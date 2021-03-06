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

package com.openexchange.config.osgi;

import java.rmi.Remote;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ManagedService;
import com.openexchange.config.ConfigurationService;
import com.openexchange.config.ForcedReloadable;
import com.openexchange.config.Reloadable;
import com.openexchange.config.cascade.ConfigProviderService;
import com.openexchange.config.internal.ConfigProviderServiceImpl;
import com.openexchange.config.internal.ConfigurationImpl;
import com.openexchange.config.internal.filewatcher.FileWatcher;
import com.openexchange.config.mbean.ConfigReloadMBean;
import com.openexchange.config.mbean.ConfigReloadMBeanImpl;
import com.openexchange.config.rmi.RemoteConfigurationService;
import com.openexchange.config.rmi.impl.RemoteConfigurationServiceImpl;
import com.openexchange.management.ManagementService;
import com.openexchange.management.osgi.HousekeepingManagementTracker;
import com.openexchange.osgi.HousekeepingActivator;

/**
 * {@link ConfigActivator} - Activator for <code>com.openexchange.configread</code> bundle
 *
 * @author <a href="mailto:thorben.betten@open-xchange.com">Thorben Betten</a>
 */
public final class ConfigActivator extends HousekeepingActivator {

    private ServiceReference<ManagedService> managedServiceReference;

    /**
     * Default constructor
     */
    public ConfigActivator() {
        super();
    }

    @Override
    protected Class<?>[] getNeededServices() {
        return EMPTY_CLASSES;
    }

    @Override
    protected synchronized void startBundle() throws Exception {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConfigActivator.class);
        logger.info("starting bundle: com.openexchange.configread");
        try {
            ConfigProviderTracker configProviderServiceTracker = new ConfigProviderTracker(context);
            ConfigurationImpl configService = new ConfigurationImpl(configProviderServiceTracker.getReinitQueue());
            ConfigurationImpl.setConfigReference(configService);
            registerService(ConfigurationService.class, configService, null);

            {
                Dictionary<String, Object> properties = new Hashtable<>(2);
                properties.put("scope", "server");
                ConfigProviderServiceImpl configProviderServiceImpl = new ConfigProviderServiceImpl(configService);
                configService.setConfigProviderServiceImpl(configProviderServiceImpl);
                registerService(ConfigProviderService.class, configProviderServiceImpl, properties);
            }

            // Web Console stuff
            {
                Collection<ServiceReference<ManagedService>> serviceReferences = context.getServiceReferences(ManagedService.class, null);
                boolean found = false;
                for (ServiceReference<ManagedService> reference : serviceReferences) {
                    if ("org.apache.felix.webconsole.internal.servlet.OsgiManager".equals(reference.getProperty(Constants.SERVICE_PID))) {
                        found = true;
                        ManagedService managedService = context.getService(reference);
                        ManagedServiceTracker.configureWebConsole(managedService, configService);
                        managedServiceReference = reference;
                        break;
                    }
                }

                if (!found) {
                    rememberTracker(new ManagedServiceTracker(context, configService));
                }
            }

            // Register RMI stub
            Dictionary<String, Object> props = new Hashtable<>(2);
            props.put("RMIName", RemoteConfigurationService.RMI_NAME);
            registerService(Remote.class, new RemoteConfigurationServiceImpl(configService), props);

            // Add & open service trackers
            track(Reloadable.class, new ReloadableServiceTracker(context, configService));
            track(ForcedReloadable.class, new ForcedReloadableServiceTracker(context, configService));
            track(ManagementService.class, new HousekeepingManagementTracker(context, ConfigReloadMBean.class.getName(), ConfigReloadMBean.DOMAIN, new ConfigReloadMBeanImpl(ConfigReloadMBean.class, configService)));
            track(ConfigProviderService.class, configProviderServiceTracker);
            openTrackers();
        } catch (Throwable t) {
            logger.error("", t);
            throw t instanceof Exception ? (Exception) t : new Exception(t);
        }
    }

    @Override
    protected synchronized void stopBundle() throws Exception {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConfigActivator.class);
        logger.info("stopping bundle: com.openexchange.configread");
        try {

            ServiceReference<ManagedService> reference = managedServiceReference;
            if (null != reference) {
                context.ungetService(reference);
                managedServiceReference = null;
            }

            super.stopBundle();
            FileWatcher.dropTimer();
            ConfigurationImpl.setConfigReference(null);
        } catch (Throwable t) {
            logger.error("", t);
            throw t instanceof Exception ? (Exception) t : new Exception(t);
        }
    }

}
