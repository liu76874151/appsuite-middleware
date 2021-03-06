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

package com.openexchange.contact.vcard.impl.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import com.openexchange.config.ConfigurationService;
import com.openexchange.contact.vcard.VCardService;
import com.openexchange.contact.vcard.impl.internal.DefaultVCardService;
import com.openexchange.contact.vcard.impl.internal.VCardParametersFactoryImpl;
import com.openexchange.contact.vcard.impl.internal.VCardServiceLookup;
import com.openexchange.exception.OXException;
import com.openexchange.imagetransformation.ImageTransformationService;
import com.openexchange.osgi.HousekeepingActivator;
import com.openexchange.version.VersionService;

/**
 * {@link ContactVCardActivator}
 *
 * @author <a href="mailto:tobias.friedrich@open-xchange.com">Tobias Friedrich</a>
 */
public class ContactVCardActivator extends HousekeepingActivator {

    @Override
    protected Class<?>[] getNeededServices() {
        return EMPTY_CLASSES;
    }

    @Override
    protected Class<?>[] getOptionalServices() {
        return new Class[] { VersionService.class };
    }

    @Override
    protected void startBundle() throws Exception {
        VCardServiceLookup.set(this);
        final VCardParametersFactoryImpl vCardParametersFactory = new VCardParametersFactoryImpl();
        final BundleContext context = this.context;
        track(ConfigurationService.class, new ServiceTrackerCustomizer<ConfigurationService, ConfigurationService>() {

            @Override
            public ConfigurationService addingService(ServiceReference<ConfigurationService> reference) {
                ConfigurationService configService = context.getService(reference);
                try {
                    vCardParametersFactory.reinitialize(configService);
                } catch (OXException e) {
                    org.slf4j.LoggerFactory.getLogger(ContactVCardActivator.class).error("Error during reinitialization: {}", e.getMessage(), e);
                }
                return configService;
            }

            @Override
            public void modifiedService(ServiceReference<ConfigurationService> reference, ConfigurationService service) {
                // no
            }

            @Override
            public void removedService(ServiceReference<ConfigurationService> reference, ConfigurationService service) {
                context.ungetService(reference);
            }}
        );
        trackService(ImageTransformationService.class);
        openTrackers();
        registerService(VCardService.class, new DefaultVCardService(vCardParametersFactory));
    }

    @Override
    protected void stopBundle() throws Exception {
        VCardServiceLookup.set(null);
        closeTrackers();
        super.stopBundle();
    }

}

