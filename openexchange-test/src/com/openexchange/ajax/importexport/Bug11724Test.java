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

import static org.junit.Assert.assertFalse;
import org.junit.Test;
import com.openexchange.ajax.appointment.action.DeleteRequest;
import com.openexchange.ajax.appointment.action.GetRequest;
import com.openexchange.ajax.appointment.action.GetResponse;
import com.openexchange.ajax.framework.AJAXClient;
import com.openexchange.ajax.framework.AbstractAJAXSession;
import com.openexchange.ajax.framework.Executor;
import com.openexchange.ajax.importexport.actions.ICalImportRequest;
import com.openexchange.ajax.importexport.actions.ICalImportResponse;
import com.openexchange.groupware.importexport.ImportResult;

/**
 *
 * @author <a href="mailto:marcus@open-xchange.org">Marcus Klein</a>
 */
public final class Bug11724Test extends AbstractAJAXSession {

    /**
     * Checks if a whole day appointment is imported properly.
     */
    @Test
    public void testWholeDayAppointment() throws Throwable {
        final AJAXClient client = getClient();
        final int folderId = client.getValues().getPrivateAppointmentFolder();
        final ICalImportResponse iResponse = Executor.execute(client, new ICalImportRequest(folderId, ICAL));
        final ImportResult result = iResponse.getImports()[0];
        final int objectId = Integer.parseInt(result.getObjectId());
        try {
            final GetResponse gResponse = Executor.execute(client, new GetRequest(folderId, objectId));
            assertFalse(gResponse.hasError());
        } finally {
            Executor.execute(client, new DeleteRequest(objectId, folderId, result.getDate()));
        }
    }

    private static final String ICAL = "BEGIN:VCALENDAR\n" + "VERSION:2.0\n" + "PRODID:OPEN-XCHANGE\n" + "BEGIN:VEVENT\n" + "CLASS:PUBLIC\n" + "CREATED:20080728T200752Z\n" + "DESCRIPTION:fasel\n" + "DTSTART;VALUE=DATE:20080728\n" + "LAST-MODIFIED:20080728T200755Z\n" + "ORGANIZER:mailto:user3@oxtest41.de\n" + "DTSTAMP:20080728T200853Z\n" + "SUMMARY:ganztag\n" + "TRANSP:OPAQUE\n" + "UID:21@localhost\n" + "END:VEVENT\n" + "END:VCALENDAR\n";
}
