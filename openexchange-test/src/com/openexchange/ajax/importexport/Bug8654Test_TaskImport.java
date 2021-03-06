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

package com.openexchange.ajax.importexport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.ByteArrayInputStream;
import org.junit.Test;
import com.openexchange.ajax.importexport.actions.ICalImportRequest;
import com.openexchange.ajax.importexport.actions.ICalImportResponse;
import com.openexchange.ajax.task.ManagedTaskTest;
import com.openexchange.groupware.tasks.Task;

/**
 * {@link Bug8654Test_TaskImport}
 *
 * @author <a href="mailto:Jan-Oliver.Huhn@open-xchange.com">Jan-Oliver Huhn</a>
 * @since v7.10.0
 */
public class Bug8654Test_TaskImport extends ManagedTaskTest {

    final String ical =
        "BEGIN:VCALENDAR\n"
        + "VERSION:2.0\n"
        + "X-WR-CALNAME:Test\n"
        + "PRODID:-//Apple Computer\\, Inc//iCal 2.0//EN\n"
        + "X-WR-RELCALID:F1D0AAC4-A28F-41E1-9FA8-83546CE7D7B8\n"
        + "X-WR-TIMEZONE:Europe/Berlin\n" + "CALSCALE:GREGORIAN\n"
        + "METHOD:PUBLISH\n"
        + "BEGIN:VTIMEZONE\n"
        + "TZID:Europe/Berlin\n"
        + "LAST-MODIFIED:20070801T101420Z\n"
        + "BEGIN:DAYLIGHT\n"
        + "DTSTART:20070325T010000\n"
        + "TZOFFSETTO:+0200\n"
        + "TZOFFSETFROM:+0000\n"
        + "TZNAME:CEST\n"
        + "END:DAYLIGHT\n"
        + "BEGIN:STANDARD\n"
        + "DTSTART:20071028T030000\n"
        + "TZOFFSETTO:+0100\n"
        + "TZOFFSETFROM:+0200\n"
        + "TZNAME:CET\n"
        + "END:STANDARD\n"
        + "END:VTIMEZONE\n"
        + "BEGIN:VTODO\n"
        + "PRIORITY:5\n"
        + "DTSTAMP:20070801T101401Z\n"
        + "UID:C037CF41-BB61-4BF8-A77A-459D2B754A32\n"
        + "SEQUENCE:4\n"
        + "URL;VALUE=URI:http://www.open-xchange.com\n"
        + "DTSTART;TZID=Europe/Berlin:20070801T000000\n"
        + "SUMMARY:Teste Task Import von OX\n"
        + "DESCRIPTION:Test\\, ob die Aufgaben auch ankommen.\n"
        + "END:VTODO\n"
        + "END:VCALENDAR";

    @Test
    public void testTaskImport() throws Exception {
        final ICalImportRequest request = new ICalImportRequest(folderID, new ByteArrayInputStream(ical.toString().getBytes(com.openexchange.java.Charsets.UTF_8)), false);
        ICalImportResponse response = getClient().execute(request);
        assertEquals(1, response.getImports().length);

        String objectId = response.getImports()[0].getObjectId();
        Task task = ttm.getTaskFromServer(folderID, Integer.parseInt(objectId));
        assertNotNull(task);
        assertEquals("Teste Task Import von OX", task.getTitle());
    }

}
