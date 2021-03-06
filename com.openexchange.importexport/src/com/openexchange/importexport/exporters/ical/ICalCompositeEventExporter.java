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

package com.openexchange.importexport.exporters.ical;

import static com.openexchange.java.Autoboxing.I;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.collect.Lists;
import com.openexchange.ajax.container.ThresholdFileHolder;
import com.openexchange.chronos.Event;
import com.openexchange.chronos.EventField;
import com.openexchange.chronos.ical.StreamedCalendarExport;
import com.openexchange.chronos.provider.composition.IDBasedCalendarAccess;
import com.openexchange.chronos.service.CalendarParameters;
import com.openexchange.chronos.service.EventID;
import com.openexchange.exception.OXException;
import com.openexchange.importexport.exceptions.ImportExportExceptionCodes;
import com.openexchange.importexport.osgi.ImportExportServices;
import com.openexchange.java.Streams;
import com.openexchange.java.Strings;
import com.openexchange.tools.session.ServerSession;

/**
 * {@link ICalCompositeEventExporter}
 *
 * @author <a href="mailto:Jan-Oliver.Huhn@open-xchange.com">Jan-Oliver Huhn</a>
 * @since v7.10.0
 */
public class ICalCompositeEventExporter extends AbstractICalEventExporter {

    /** The event fields used for the export */
    private static final EventField[] EXPORTED_FIELDS = com.openexchange.tools.arrays.Arrays.remove(EventField.values(), EventField.ALARMS, EventField.ATTACHMENTS, EventField.FLAGS);

    public ICalCompositeEventExporter(String folderId, Map<String, List<String>> batchIds) {
        super(folderId, batchIds);
    }

    @Override
    protected ThresholdFileHolder exportFolderData(ServerSession session, OutputStream out) throws OXException {
        /*
         * get identifiers of all events in exported folder
         */
        IDBasedCalendarAccess calendarAccess = getCalendarAccess(session);
        calendarAccess.set(CalendarParameters.PARAMETER_FIELDS, new EventField[] { EventField.FOLDER_ID, EventField.ID, EventField.START_DATE, EventField.END_DATE });
        calendarAccess.set(CalendarParameters.PARAMETER_EXPAND_OCCURRENCES, Boolean.FALSE);
        int limit = getExportLimit(session);
        if (-1 < limit) {
            calendarAccess.set(CalendarParameters.PARAMETER_RIGHT_HAND_LIMIT, I(limit));
        }

        List<EventID> eventIDs = new LinkedList<>();
        Set<String> timezoneIDs = new HashSet<>();
        parseEvents(calendarAccess.getEventsInFolder(getFolderId()), eventIDs, timezoneIDs);

        /*
         * prepare export
         */
        calendarAccess = getCalendarAccess(session);
        calendarAccess.set(CalendarParameters.PARAMETER_FIELDS, EXPORTED_FIELDS);
        calendarAccess.set(CalendarParameters.PARAMETER_EXPAND_OCCURRENCES, Boolean.FALSE);

        if (null != out) {
            stream(session, out, calendarAccess, eventIDs, timezoneIDs);
            return null;
        }

        boolean success = false;
        ThresholdFileHolder sink = null;
        try {
            sink = new ThresholdFileHolder();
            stream(session, sink.asOutputStream(), calendarAccess, eventIDs, timezoneIDs);
            success = true;
            return sink;
        } finally {
            if (false == success) {
                Streams.close(sink);
            }
        }
    }

    @Override
    protected ThresholdFileHolder exportBatchData(ServerSession session, OutputStream out) throws OXException {
        List<EventID> eventIds = convertBatchDataToEventIds();
        if (eventIds.size() == 1) {
            return exportChronosEvents(Collections.singletonList(getCalendarAccess(session).getEvent(eventIds.get(0))), out, session);
        } else {
            return exportChronosEvents(getCalendarAccess(session).getEvents(eventIds), out, session);
        }
    }

    private IDBasedCalendarAccess getCalendarAccess(ServerSession session) throws OXException {
        return ImportExportServices.getIDBasedCalendarAccessFactory().createAccess(session);
    }

    private void stream(ServerSession session, OutputStream out, IDBasedCalendarAccess calendarAccess, List<EventID> eventIDs, Set<String> timezoneIDs) throws OXException {
        /*
         * load full event data in chunks & add to export
         */
        try (StreamedCalendarExport streamedExport = ImportExportServices.getICalService().getStreamedExport(out, null)) {
            streamedExport.writeMethod("PUBLISH");
            String name = extractName(session, getFolderId());
            if (Strings.isNotEmpty(name)) {
                streamedExport.writeCalendarName(extractName(session, getFolderId()));
            }
            streamedExport.writeTimeZones(timezoneIDs);
            for (List<EventID> chunk : Lists.partition(eventIDs, 100)) {
                /*
                 * serialize calendar
                 */
                streamedExport.writeEvents(prepareForExport(calendarAccess.getEvents(chunk)));
            }
            streamedExport.finish();
        } catch (IOException e) {
            throw ImportExportExceptionCodes.IOEXCEPTION.create(e);
        }
    }
    
    /**
     * Gets the full identifiers for the supplied events holding the folder and file identifiers
     * and get all used timezones
     * 
     * @param eventsInFolder The events to get the full identifiers for
     * @param eventIDs A {@link List} to write the {@link EventID}s to
     * @param timezoneIDs A {@link Set} to write the timezones to
     */
    private static void parseEvents(List<Event> eventsInFolder, List<EventID> eventIDs, Set<String> timezoneIDs) {
        if (null == eventsInFolder) {
            return;
        }
        for (Event event : eventsInFolder) {
            eventIDs.add(new EventID(event.getFolderId(), event.getId()));
            addTimeZone(timezoneIDs, event.getStartDate());
            addTimeZone(timezoneIDs, event.getEndDate());
        }
    }

    private static boolean addTimeZone(Set<String> timeZones, org.dmfs.rfc5545.DateTime dateTime) {
        if (null != dateTime && false == dateTime.isFloating() && null != dateTime.getTimeZone() && false == "UTC".equals(dateTime.getTimeZone().getID())) {
            return timeZones.add(dateTime.getTimeZone().getID());
        }
        return false;
    }

}
