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

package com.openexchange.subscribe.google.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.openexchange.oauth.KnownApi;
import com.openexchange.oauth.OAuthAccountDeleteListener;
import com.openexchange.oauth.OAuthServiceMetaData;
import com.openexchange.oauth.association.spi.OAuthAccountAssociationProvider;
import com.openexchange.server.ServiceLookup;
import com.openexchange.subscribe.SubscribeService;
import com.openexchange.subscribe.google.GoogleContactsSubscribeService;
import com.openexchange.subscribe.google.groupware.GoogleSubscriptionsOAuthAccountDeleteListener;
import com.openexchange.subscribe.google.oauth.GoogleContactsOAuthAccountAssociationProvider;

/**
 * {@link OAuthServiceMetaDataRegisterer}
 *
 * @author <a href="mailto:ioannis.chouklis@open-xchange.com">Ioannis Chouklis</a>
 * @since 7.10.1
 */
public class OAuthServiceMetaDataRegisterer implements ServiceTrackerCustomizer<OAuthServiceMetaData, OAuthServiceMetaData> {

    private final String oauthIdentifier;
    private final ServiceLookup services;
    private final BundleContext context;
    private volatile ServiceRegistration<SubscribeService> contactsRegistration;
    private volatile ServiceRegistration<OAuthAccountDeleteListener> deleteListenerRegistration;
    private volatile ServiceRegistration<OAuthAccountAssociationProvider> associationProviderRegistration;

    /**
     * Initialises a new {@link OAuthServiceMetaDataRegisterer}.
     *
     * @param services The service look-up
     * @param context The bundle context
     */
    public OAuthServiceMetaDataRegisterer(ServiceLookup services, BundleContext context) {
        super();
        this.services = services;
        this.context = context;
        oauthIdentifier = KnownApi.GOOGLE.getServiceId();
    }

    @Override
    public OAuthServiceMetaData addingService(ServiceReference<OAuthServiceMetaData> ref) {
        Logger logger = LoggerFactory.getLogger(OAuthServiceMetaDataRegisterer.class);
        OAuthServiceMetaData oAuthServiceMetaData = context.getService(ref);
        if (oauthIdentifier.equals(oAuthServiceMetaData.getId())) {
            logger.info("Registering Google Contact subscription services.");
            GoogleContactsSubscribeService subscribeService = new GoogleContactsSubscribeService(oAuthServiceMetaData, services);
            contactsRegistration = context.registerService(SubscribeService.class, subscribeService, null);
            deleteListenerRegistration = context.registerService(OAuthAccountDeleteListener.class, new GoogleSubscriptionsOAuthAccountDeleteListener(subscribeService, services), null);

            if (associationProviderRegistration == null) {
                associationProviderRegistration = context.registerService(OAuthAccountAssociationProvider.class, new GoogleContactsOAuthAccountAssociationProvider(services, subscribeService), null);
            }
        }
        return oAuthServiceMetaData;
    }

    @Override
    public void modifiedService(ServiceReference<OAuthServiceMetaData> arg0, OAuthServiceMetaData arg1) {
        // nothing
    }

    @Override
    public void removedService(ServiceReference<OAuthServiceMetaData> ref, OAuthServiceMetaData service) {
        Logger logger = LoggerFactory.getLogger(OAuthServiceMetaDataRegisterer.class);
        if (service.getId().equals(oauthIdentifier)) {
            logger.info("Unregistering Google Contacts subscription services.");

            {
                ServiceRegistration<SubscribeService> registration = this.contactsRegistration;
                if (null != registration) {
                    registration.unregister();
                    this.contactsRegistration = null;
                }
            }
            {
                ServiceRegistration<OAuthAccountDeleteListener> registration = this.deleteListenerRegistration;
                if (null != registration) {
                    registration.unregister();
                    this.deleteListenerRegistration = null;
                }
            }
            {
                ServiceRegistration<OAuthAccountAssociationProvider> registration = this.associationProviderRegistration;
                if (null != registration) {
                    registration.unregister();
                    this.associationProviderRegistration = null;
                }
            }
        }
        context.ungetService(ref);
    }
}
