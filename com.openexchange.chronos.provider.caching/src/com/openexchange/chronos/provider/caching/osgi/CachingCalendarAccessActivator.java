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

package com.openexchange.chronos.provider.caching.osgi;

import static org.slf4j.LoggerFactory.getLogger;
import com.openexchange.chronos.provider.account.AdministrativeCalendarAccountService;
import com.openexchange.chronos.provider.account.CalendarAccountService;
import com.openexchange.chronos.provider.caching.internal.Services;
import com.openexchange.chronos.service.CalendarEventNotificationService;
import com.openexchange.chronos.service.CalendarUtilities;
import com.openexchange.chronos.service.RecurrenceService;
import com.openexchange.chronos.storage.CalendarStorageFactory;
import com.openexchange.config.lean.LeanConfigurationService;
import com.openexchange.context.ContextService;
import com.openexchange.conversion.ConversionService;
import com.openexchange.database.DatabaseService;
import com.openexchange.osgi.HousekeepingActivator;

/**
 *
 * {@link CachingCalendarAccessActivator}
 *
 * @author <a href="mailto:martin.schneider@open-xchange.com">Martin Schneider</a>
 * @since v7.10.0
 */
public class CachingCalendarAccessActivator extends HousekeepingActivator {

    /**
     * Initializes a new {@link CachingCalendarAccessActivator}.
     */
    public CachingCalendarAccessActivator() {
        super();
    }

    @Override
    protected Class<?>[] getNeededServices() {
        //@formatter:off
        return new Class<?>[] { CalendarStorageFactory.class, DatabaseService.class, ContextService.class,
            CalendarUtilities.class, CalendarAccountService.class, AdministrativeCalendarAccountService.class,
            RecurrenceService.class, LeanConfigurationService.class, ConversionService.class, CalendarEventNotificationService.class };
        //@formatter:on
    }

    @Override
    protected void startBundle() throws Exception {
        try {
            getLogger(CachingCalendarAccessActivator.class).info("starting bundle {}", context.getBundle().getSymbolicName());
            Services.setServiceLookup(this);

            openTrackers();
        } catch (Exception e) {
            getLogger(CachingCalendarAccessActivator.class).error("error starting {}", context.getBundle().getSymbolicName(), e);
            throw e;
        }
    }

    @Override
    protected void stopBundle() throws Exception {
        getLogger(CachingCalendarAccessActivator.class).info("stopping bundle {}", context.getBundle().getSymbolicName());
        Services.setServiceLookup(null);
        super.stopBundle();
    }

}
