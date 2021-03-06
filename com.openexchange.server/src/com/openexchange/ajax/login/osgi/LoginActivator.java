/*
 *
 *    OPEN-XCHANGE legal information
 *
 *    All intellectual property rights in the Software are protected by
 *    international copyright laws.
 *
 *
 *    In some countries OX, OX Open-Xchange, open xchange and OXtender
 *    as well as the corresponding Logos OX Open-Xchange and OX are registered
 *    trademarks of the OX Software GmbH group of companies.
 *    The use of the Logos is not covered by the GNU General Public License.
 *    Instead, you are allowed to use these Logos according to the terms and
 *    conditions of the Creative Commons License, Version 2.5, Attribution,
 *    Non-commercial, ShareAlike, and the interpretation of the term
 *    Non-commercial applicable to the aforementioned license is published
 *    on the web site http://www.open-xchange.com/EN/legal/index.html.
 *
 *    Please make sure that third-party modules and libraries are used
 *    according to their respective licenses.
 *
 *    Any modifications to this package must retain all copyright notices
 *    of the original copyright holder(s) for the original code used.
 *
 *    After any such modifications, the original and derivative code shall remain
 *    under the copyright of the copyright holder(s) and/or original author(s)per
 *    the Attribution and Assignment Agreement that can be located at
 *    http://www.open-xchange.com/EN/developer/. The contributing author shall be
 *    given Attribution for the derivative code and a license granting use.
 *
 *     Copyright (C) 2016-2020 OX Software GmbH
 *     Mail: info@open-xchange.com
 *
 *
 *     This program is free software; you can redistribute it and/or modify it
 *     under the terms of the GNU General Public License, Version 2 as published
 *     by the Free Software Foundation.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *     or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *     for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc., 59
 *     Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package com.openexchange.ajax.login.osgi;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.regex.Pattern;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import com.openexchange.ajax.LoginServlet;
import com.openexchange.ajax.login.RateLimiterByLogin;
import com.openexchange.ajax.login.AllowedRedirectUris;
import com.openexchange.ajax.login.LoginRequestHandler;
import com.openexchange.ajax.login.RedeemReservationLogin;
import com.openexchange.config.ConfigurationService;
import com.openexchange.dispatcher.DispatcherPrefixService;
import com.openexchange.java.Strings;
import com.openexchange.login.LoginRampUpService;
import com.openexchange.login.internal.LoginPerformer;
import com.openexchange.login.internal.format.CompositeLoginFormatter;
import com.openexchange.login.listener.LoginListener;
import com.openexchange.osgi.HousekeepingActivator;
import com.openexchange.osgi.ServiceSet;
import com.openexchange.osgi.SimpleRegistryListener;
import com.openexchange.osgi.Tools;
import com.openexchange.password.mechanism.PasswordMechRegistry;
import com.openexchange.server.services.ServerServiceRegistry;
import com.openexchange.session.reservation.Enhancer;
import com.openexchange.session.reservation.SessionReservationService;
import com.openexchange.share.ShareService;
import com.openexchange.share.groupware.ModuleSupport;
import com.openexchange.tokenlogin.TokenLoginService;

/**
 * {@link LoginActivator}
 *
 * @author <a href="mailto:marcus.klein@open-xchange.com">Marcus Klein</a>
 */
public class LoginActivator extends HousekeepingActivator {

    /** Simple class to delay initialization until needed */
    private static class LoggerHolder {
        static final Logger LOG = org.slf4j.LoggerFactory.getLogger(LoginActivator.class);
    }

    public LoginActivator() {
        super();
    }

    @Override
    protected Class<?>[] getNeededServices() {
        return new Class<?>[] { ConfigurationService.class };
    }

    @Override
    protected void startBundle() throws Exception {
        final BundleContext context = this.context;
        class ServerServiceRegistryTracker<S> implements ServiceTrackerCustomizer<S, S> {

            private final Class<? extends S> clazz;

            ServerServiceRegistryTracker(Class<? extends S> clazz) {
                super();
                this.clazz = clazz;
            }

            @Override
            public S addingService(final ServiceReference<S> reference) {
                final S service = context.getService(reference);
                ServerServiceRegistry.getInstance().addService(clazz, service);
                return service;
            }

            @Override
            public void modifiedService(final ServiceReference<S> reference, final S service) {
                // Nothing
            }

            @Override
            public void removedService(final ServiceReference<S> reference, final S service) {
                context.ungetService(reference);
                ServerServiceRegistry.getInstance().removeService(clazz);
            }
        }
        track(ShareService.class, new ServerServiceRegistryTracker<ShareService>(ShareService.class));
        track(PasswordMechRegistry.class, new ServerServiceRegistryTracker<PasswordMechRegistry>(PasswordMechRegistry.class));
        track(ModuleSupport.class, new ServerServiceRegistryTracker<ModuleSupport>(ModuleSupport.class));

        ServiceSet<LoginRampUpService> rampUp = new ServiceSet<LoginRampUpService>();
        track(LoginRampUpService.class, rampUp);

        LoginServletRegisterer loginServletRegisterer = new LoginServletRegisterer(context, rampUp);
        final RedeemReservationLogin redeemReservationLogin = new RedeemReservationLogin();
        loginServletRegisterer.addLoginRequestHandler(LoginServlet.ACTION_REDEEM_RESERVATION, redeemReservationLogin);

        Filter filter = Tools.generateServiceFilter(context,
            ConfigurationService.class,
            HttpService.class,
            DispatcherPrefixService.class,
            LoginRequestHandler.class);
        rememberTracker(new ServiceTracker<Object, Object>(context, filter, loginServletRegisterer));

        track(TokenLoginService.class, new TokenLoginCustomizer(context));
        track(SessionReservationService.class, new SessionReservationCustomizer(context));
        track(Enhancer.class, new SimpleRegistryListener<Enhancer>() {
            @Override
            public void added(ServiceReference<Enhancer> ref, Enhancer service) {
                redeemReservationLogin.addEnhancer(service);
            }

            @Override
            public void removed(ServiceReference<Enhancer> ref, Enhancer service) {
                redeemReservationLogin.removeEnhancer(service);
            }
        });
        openTrackers();

        final ConfigurationService configurationService = getService(ConfigurationService.class);
        final String loginFormat = configurationService.getProperty("com.openexchange.ajax.login.formatstring.login");
        final String logoutFormat = configurationService.getProperty("com.openexchange.ajax.login.formatstring.logout");
        LoginPerformer.setLoginFormatter(new CompositeLoginFormatter(loginFormat, logoutFormat));

        com.openexchange.tools.servlet.http.Tools.setConfigurationService(configurationService);

        // Login name rate limiter
        boolean rateLimitByLogin = configurationService.getBoolProperty("com.openexchange.ajax.login.rateLimitByLogin.enabled", false);
        if (rateLimitByLogin) {
            String propPermits = "com.openexchange.ajax.login.rateLimitByLogin.permits";
            String propTimeFrame = "com.openexchange.ajax.login.rateLimitByLogin.timeFrameInSeconds";
            int permits = configurationService.getIntProperty(propPermits, 3);
            long timeFrameInSeconds = configurationService.getIntProperty(propTimeFrame, 30);
            if (permits > 0 && timeFrameInSeconds > 0) {
                Dictionary<String, Object> properties = new Hashtable<String, Object>(2);
                properties.put(Constants.SERVICE_RANKING, Integer.valueOf(999));
                registerService(LoginListener.class, new RateLimiterByLogin(permits, timeFrameInSeconds), properties);
            } else {
                LoggerHolder.LOG.warn("Value configured for \"{}\" and/or \"{}\" property must be positive. Rate limiting by login name is effectively disabled!", propPermits, propTimeFrame);
            }
        }

        // Allowed paths for redirects
        String allowedRedirectUris = configurationService.getProperty("com.openexchange.ajax.login.allowedRedirectURIsOnLoginError");
        if (Strings.isNotEmpty(allowedRedirectUris)) {
            String[] wildcardPatterns = Strings.splitByComma(allowedRedirectUris);
            AllowedRedirectUris whitelist = AllowedRedirectUris.getInstance();
            for (String wildcardPattern : wildcardPatterns) {
                if (Strings.isNotEmpty(wildcardPattern)) {
                    String wcp = Strings.unquote(wildcardPattern.trim());
                    int starPos = wcp.indexOf('*');
                    int qmarPos = wcp.indexOf('?');
                    if (starPos < 0 && qmarPos < 0) {
                        whitelist.add(new AllowedRedirectUris.IgnoreCaseExactUriMatcher(wcp));
                    } else {
                        int mlen = wcp.length() - 1;
                        if (mlen > 0 && ((starPos >= mlen && qmarPos >= mlen) || (starPos == mlen && qmarPos < 0) || (qmarPos == mlen && starPos < 0))) {
                            whitelist.add(new AllowedRedirectUris.IgnoreCasePrefixUriMatcher(wcp.substring(0, mlen)));
                        } else {
                            Pattern pattern = Pattern.compile(Strings.wildcardToRegex(wcp), Pattern.CASE_INSENSITIVE);
                            whitelist.add(new AllowedRedirectUris.PatternUriMatcher(pattern));
                        }
                    }
                }
            }
        }

    }

}
