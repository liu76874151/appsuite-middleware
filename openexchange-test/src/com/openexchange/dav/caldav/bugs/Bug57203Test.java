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

package com.openexchange.dav.caldav.bugs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import com.openexchange.dav.StatusCodes;
import com.openexchange.dav.SyncToken;
import com.openexchange.dav.caldav.CalDAVTest;
import com.openexchange.dav.caldav.ICalResource;
import com.openexchange.groupware.calendar.TimeTools;
import com.openexchange.groupware.container.Appointment;

/**
 * {@link Bug57203Test}
 *
 * category can not be removed from appointment
 *
 * @author <a href="mailto:tobias.friedrich@open-xchange.com">Tobias Friedrich</a>
 * @since v7.10.0
 */
public class Bug57203Test extends CalDAVTest {

    @Test
    public void testRemoveCategories() throws Exception {
        /*
         * fetch sync token for later synchronization
         */
        SyncToken syncToken = new SyncToken(fetchSyncToken());
        /*
         * create appointment on server
         */
        String uid = randomUID();
        String summary = "test";
        String location = "ort";
        String categories = "hund,gurke,autobahn";
        Date start = TimeTools.D("next tuesday at 12:30");
        Date end = TimeTools.D("next tuesday at 13:45");
        Appointment appointment = generateAppointment(start, end, uid, summary, location);
        appointment.setCategories(categories);
        rememberForCleanUp(create(appointment));
        /*
         * verify appointment on client
         */
        Map<String, String> eTags = syncCollection(syncToken).getETagsStatusOK();
        assertTrue("no resource changes reported on sync collection", 0 < eTags.size());
        List<ICalResource> calendarData = calendarMultiget(eTags.keySet());
        ICalResource iCalResource = assertContains(uid, calendarData);
        assertNotNull("No VEVENT in iCal found", iCalResource.getVEvent());
        assertEquals("SUMMARY wrong", summary, iCalResource.getVEvent().getSummary());
        assertEquals("CATEGORIES wrong", categories, iCalResource.getVEvent().getPropertyValue("CATEGORIES"));
        /*
         * update appointment on client, remove categories
         */
        categories = null;
        iCalResource.getVEvent().removeProperties("CATEGORIES");
        assertEquals("response code wrong", StatusCodes.SC_CREATED, putICalUpdate(iCalResource));
        /*
         * verify appointment on server
         */
        appointment = getAppointment(uid);
        assertNotNull("appointment not found on server", appointment);
        assertEquals("Title wrong", summary, appointment.getTitle());
        assertEquals("Start wrong", start, appointment.getStartDate());
        assertEquals("End wrong", end, appointment.getEndDate());
        assertNull("Categories wrong", appointment.getCategories());
        /*
         * verify appointment on client
         */
        iCalResource = get(uid);
        assertNotNull("No VEVENT in iCal found", iCalResource.getVEvent());
        assertEquals("UID wrong", uid, iCalResource.getVEvent().getUID());
        assertEquals("SUMMARY wrong", summary, iCalResource.getVEvent().getSummary());
        assertNull("CATEGORIES wrong", iCalResource.getVEvent().getProperty("CATEGORIES"));
    }

}
