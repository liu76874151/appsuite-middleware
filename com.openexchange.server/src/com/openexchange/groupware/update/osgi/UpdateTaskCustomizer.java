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

package com.openexchange.groupware.update.osgi;

import java.util.Collection;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import com.openexchange.groupware.update.UpdateTaskProviderService;
import com.openexchange.groupware.update.UpdateTaskV2;
import com.openexchange.groupware.update.internal.DynamicSet;

/**
 * {@link UpdateTaskCustomizer} - The {@link ServiceTrackerCustomizer service tracker customizer} for update tasks.
 *
 * @author <a href="mailto:thorben.betten@open-xchange.com">Thorben Betten</a>
 */
public final class UpdateTaskCustomizer implements ServiceTrackerCustomizer<UpdateTaskProviderService, UpdateTaskProviderService> {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UpdateTaskCustomizer.class);

    private final BundleContext context;

    public UpdateTaskCustomizer(final BundleContext context) {
        super();
        this.context = context;
    }

    @Override
    public UpdateTaskProviderService addingService(final ServiceReference<UpdateTaskProviderService> reference) {
        final UpdateTaskProviderService providerService = context.getService(reference);
        final DynamicSet registry = DynamicSet.getInstance();
        // Get provider's collection
        final Collection<? extends UpdateTaskV2> collection = providerService.getUpdateTasks();
        boolean error = false;
        for (final UpdateTaskV2 task : collection) {
            if (!registry.addUpdateTask(task)) {
                LOG.error("Update task \"{}\" could not be registered.", task.getClass().getName(), new Exception());
                error = true;
                break;
            }
        }
        if (!error) {
            // Everything worked fine
            return providerService;
        }
        // Rollback
        for (final UpdateTaskV2 task : collection) {
            registry.removeUpdateTask(task);
        }
        // Nothing to track, return null
        context.ungetService(reference);
        return null;
    }

    @Override
    public void modifiedService(final ServiceReference<UpdateTaskProviderService> reference, final UpdateTaskProviderService service) {
        // Nothing to do
    }

    @Override
    public void removedService(final ServiceReference<UpdateTaskProviderService> reference, final UpdateTaskProviderService service) {
        if (null != service) {
            try {
                final DynamicSet registry = DynamicSet.getInstance();
                final UpdateTaskProviderService providerService = service;
                final Collection<? extends UpdateTaskV2> collection = providerService.getUpdateTasks();
                for (final UpdateTaskV2 task : collection) {
                    registry.removeUpdateTask(task);
                }
            } finally {
                context.ungetService(reference);
            }
        }
    }
}
