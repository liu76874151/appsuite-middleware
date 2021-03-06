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
 *     Copyright (C) 2018-2020 OX Software GmbH
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

package com.openexchange.file.storage.oauth.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import com.openexchange.file.storage.FileStorageAccountManagerProvider;
import com.openexchange.oauth.OAuthServiceMetaData;
import com.openexchange.server.ServiceLookup;

/**
 * {@link AbstractCloudStorageOAuthServiceMetaDataRegisterer}
 *
 * @author <a href="mailto:ioannis.chouklis@open-xchange.com">Ioannis Chouklis</a>
 * @since v7.10.1
 */
public abstract class AbstractCloudStorageOAuthServiceMetaDataRegisterer implements ServiceTrackerCustomizer<OAuthServiceMetaData, OAuthServiceMetaData> {

    private final BundleContext context;
    private final ServiceLookup services;
    private final String identifier;
    private volatile ServiceTracker<FileStorageAccountManagerProvider, FileStorageAccountManagerProvider> tracker;

    /**
     * Initialises a new {@link AbstractCloudStorageOAuthServiceMetaDataRegisterer}.
     * 
     * @param services TODO
     */
    public AbstractCloudStorageOAuthServiceMetaDataRegisterer(BundleContext context, ServiceLookup services, String identifier) {
        super();
        this.context = context;
        this.services = services;
        this.identifier = identifier;
    }

    /**
     * @return
     */
    protected abstract ServiceTrackerCustomizer<FileStorageAccountManagerProvider, FileStorageAccountManagerProvider> getRegisterer();

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
     */
    @Override
    public OAuthServiceMetaData addingService(ServiceReference<OAuthServiceMetaData> reference) {
        OAuthServiceMetaData oAuthServiceMetaData = getContext().getService(reference);
        if (false == identifier.equals(oAuthServiceMetaData.getId())) {
            // Not of interest
            getContext().ungetService(reference);
            return null;
        }
        ServiceTracker<FileStorageAccountManagerProvider, FileStorageAccountManagerProvider> tracker = new ServiceTracker<FileStorageAccountManagerProvider, FileStorageAccountManagerProvider>(getContext(), FileStorageAccountManagerProvider.class, getRegisterer());
        this.tracker = tracker;
        tracker.open();
        return oAuthServiceMetaData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
     */
    @Override
    public void modifiedService(ServiceReference<OAuthServiceMetaData> reference, OAuthServiceMetaData service) {
        // nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
     */
    @Override
    public void removedService(ServiceReference<OAuthServiceMetaData> reference, OAuthServiceMetaData service) {
        if (null == service) {
            return;
        }
        OAuthServiceMetaData oAuthServiceMetaData = service;
        if (identifier.equals(oAuthServiceMetaData.getId())) {
            ServiceTracker<FileStorageAccountManagerProvider, FileStorageAccountManagerProvider> tracker = this.tracker;
            if (null != tracker) {
                tracker.close();
                this.tracker = null;
            }
        }
        getContext().ungetService(reference);
    }

    /**
     * Gets the context
     *
     * @return The context
     */
    public BundleContext getContext() {
        return context;
    }

    /**
     * Gets the services
     *
     * @return The services
     */
    public ServiceLookup getServiceLookup() {
        return services;
    }
}
