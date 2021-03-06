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

package com.openexchange.calendar.json.compat;

import java.util.TimeZone;
import org.json.JSONException;
import org.json.JSONObject;
import com.openexchange.ajax.fields.AppointmentFields;
import com.openexchange.ajax.fields.CalendarFields;
import com.openexchange.ajax.fields.CommonFields;
import com.openexchange.ajax.parser.CalendarParser;
import com.openexchange.exception.OXException;

/**
 * moved from com.openexchange.ajax.parser.AppointmentParser
 *
 * AppointmentParser
 *
 * @author <a href="mailto:sebastian.kauss@open-xchange.com">Sebastian Kauss</a>
 */
public class AppointmentParser extends CalendarParser {

    protected AppointmentParser() {
        super();
    }

    public AppointmentParser(final TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public AppointmentParser(final boolean parseAll, final TimeZone timeZone) {
        this.parseAll = parseAll;
        this.timeZone = timeZone;
    }

    public void parse(final Appointment appointmentobject, final JSONObject jsonobject) throws OXException {
        try {
            parseElementAppointment(appointmentobject, jsonobject);
        } catch (final OXException exc) {
            throw exc;
        } catch (final Exception exc) {
            throw new OXException(exc);
        }
    }

    protected void parseElementAppointment(final Appointment appointmentobject, final JSONObject jsonobject) throws JSONException, OXException {
        boolean isFullTime = jsonobject.has(CalendarFields.FULL_TIME) && parseBoolean(jsonobject, CalendarFields.FULL_TIME);

        if (jsonobject.has(CalendarFields.START_DATE)) {
            if (isFullTime) {
                appointmentobject.setStartDate(parseDate(jsonobject, CalendarFields.START_DATE));
            } else {
                appointmentobject.setStartDate(parseTime(jsonobject, CalendarFields.START_DATE, timeZone));
            }
        }

        if (jsonobject.has(CalendarFields.END_DATE)) {
            if (isFullTime) {
                appointmentobject.setEndDate(parseDate(jsonobject, CalendarFields.END_DATE));
            } else {
                appointmentobject.setEndDate(parseTime(jsonobject, CalendarFields.END_DATE, timeZone));
            }
        }

        if (jsonobject.has(AppointmentFields.SHOW_AS)) {
            appointmentobject.setShownAs(parseInt(jsonobject, AppointmentFields.SHOW_AS));
        }

        if (jsonobject.has(AppointmentFields.LOCATION)) {
            appointmentobject.setLocation(parseString(jsonobject, AppointmentFields.LOCATION));
        }

        if (jsonobject.has(CommonFields.COLORLABEL)) {
            appointmentobject.setLabel(parseInt(jsonobject, CommonFields.COLORLABEL));
        }

        if (jsonobject.has(CalendarFields.ALARM)) {
            appointmentobject.setAlarm(parseInt(jsonobject, CalendarFields.ALARM));
        }

        if (jsonobject.has(AppointmentFields.IGNORE_CONFLICTS)) {
            appointmentobject.setIgnoreConflicts(parseBoolean(jsonobject, AppointmentFields.IGNORE_CONFLICTS));
        }

        if (jsonobject.has(AppointmentFields.IGNORE_OUTDATED_SEQUENCE)) {
            appointmentobject.setIgnoreOutdatedSequence(parseBoolean(jsonobject, AppointmentFields.IGNORE_OUTDATED_SEQUENCE));
        }

        if (jsonobject.has(AppointmentFields.TIMEZONE)) {
            appointmentobject.setTimezone(parseString(jsonobject, AppointmentFields.TIMEZONE));
        }

        if (jsonobject.has(CalendarFields.RECURRENCE_START)) {
            appointmentobject.setRecurringStart(parseDate(jsonobject, CalendarFields.RECURRENCE_START).getTime());
        }

        if (parseAll) {
            if (jsonobject.has(CalendarFields.CHANGE_EXCEPTIONS)) {
                appointmentobject.setChangeExceptions(parseJSONDateArray(jsonobject, CalendarFields.CHANGE_EXCEPTIONS));
            }
            if (jsonobject.has(CalendarFields.DELETE_EXCEPTIONS)) {
                appointmentobject.setDeleteExceptions(parseJSONDateArray(jsonobject, CalendarFields.DELETE_EXCEPTIONS));
            }
        }

        parseElementCalendar(appointmentobject, jsonobject);
    }
}
