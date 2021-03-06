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

package com.openexchange.chronos.storage.rdb.resilient;

import java.util.List;
import java.util.Map;
import com.openexchange.chronos.Attendee;
import com.openexchange.chronos.AttendeeField;
import com.openexchange.chronos.exception.ProblemSeverity;
import com.openexchange.chronos.storage.AttendeeStorage;
import com.openexchange.exception.OXException;
import com.openexchange.server.ServiceLookup;

/**
 * {@link RdbAttendeeStorage}
 *
 * @author <a href="mailto:tobias.friedrich@open-xchange.com">Tobias Friedrich</a>
 * @since v7.10.0
 */
public class RdbAttendeeStorage extends RdbResilientStorage implements AttendeeStorage {

    private final AttendeeStorage delegate;

    /**
     * Initializes a new {@link RdbAttendeeStorage}.
     *
     * @param services A service lookup reference
     * @param delegate The delegate storage
     * @param handleTruncations <code>true</code> to automatically handle data truncation warnings, <code>false</code>, otherwise
     * @param handleIncorrectStrings <code>true</code> to automatically handle incorrect string warnings, <code>false</code>, otherwise
     * @param unsupportedDataThreshold The threshold defining up to which severity unsupported data errors can be ignored, or <code>null</code> to not ignore any
     *            unsupported data error at all
     */
    public RdbAttendeeStorage(ServiceLookup services, AttendeeStorage delegate, boolean handleTruncations, boolean handleIncorrectStrings, ProblemSeverity unsupportedDataThreshold) {
        super(services, handleTruncations, handleIncorrectStrings);
        this.delegate = delegate;
        setUnsupportedDataThreshold(unsupportedDataThreshold, delegate);
    }

    @Override
    public List<Attendee> loadAttendees(String eventId) throws OXException {
        return delegate.loadAttendees(eventId);
    }

    @Override
    public Map<String, List<Attendee>> loadAttendees(String[] eventIds) throws OXException {
        return delegate.loadAttendees(eventIds);
    }

    @Override
    public Map<String, List<Attendee>> loadAttendees(String[] eventIds, Boolean internal) throws OXException {
        return delegate.loadAttendees(eventIds, internal);
    }

    @Override
    public Map<String, Integer> loadAttendeeCounts(String[] eventIds, Boolean internal) throws OXException {
        return delegate.loadAttendeeCounts(eventIds, internal);
    }

    @Override
    public Map<String, Attendee> loadAttendee(String[] eventIds, Attendee attendee, AttendeeField[] fields) throws OXException {
        return delegate.loadAttendee(eventIds, attendee, fields);
    }

    @Override
    public Map<String, List<Attendee>> loadAttendeeTombstones(String[] eventIds) throws OXException {
        return delegate.loadAttendeeTombstones(eventIds);
    }

    @Override
    public void deleteAttendees(String eventId) throws OXException {
        delegate.deleteAttendees(eventId);
    }

    @Override
    public void deleteAttendees(List<String> eventIds) throws OXException {
        delegate.deleteAttendees(eventIds);
    }

    @Override
    public void deleteAttendees(String eventId, List<Attendee> attendees) throws OXException {
        delegate.deleteAttendees(eventId, attendees);
    }

    @Override
    public boolean deleteAllAttendees() throws OXException {
        return delegate.deleteAllAttendees();
    }

    @Override
    public void insertAttendees(String eventId, List<Attendee> attendees) throws OXException {
        runWithRetries(() -> delegate.insertAttendees(eventId, attendees), f -> handleObjects(eventId, attendees, f));
    }

    @Override
    public void insertAttendees(Map<String, List<Attendee>> attendeesByEventId) throws OXException {
        runWithRetries(() -> delegate.insertAttendees(attendeesByEventId), f -> handleObjectsPerEventId(attendeesByEventId, f));
    }

    @Override
    public void updateAttendees(String eventId, List<Attendee> attendees) throws OXException {
        runWithRetries(() -> delegate.updateAttendees(eventId, attendees), f -> handleObjects(eventId, attendees, f));
    }

    @Override
    public void updateAttendee(String eventId, Attendee attendee) throws OXException {
        runWithRetries(() -> delegate.updateAttendee(eventId, attendee), f -> handle(eventId, attendee, f));
    }

    @Override
    public void insertAttendeeTombstone(String eventId, Attendee attendee) throws OXException {
        runWithRetries(() -> delegate.insertAttendeeTombstone(eventId, attendee), f -> handle(eventId, attendee, f));
    }

    @Override
    public void insertAttendeeTombstones(String eventId, List<Attendee> attendees) throws OXException {
        runWithRetries(() -> delegate.insertAttendeeTombstones(eventId, attendees), f -> handleObjects(eventId, attendees, f));
    }

    @Override
    public void insertAttendeeTombstones(Map<String, List<Attendee>> attendeesByEventId) throws OXException {
        runWithRetries(() -> delegate.insertAttendeeTombstones(attendeesByEventId), f -> handleObjectsPerEventId(attendeesByEventId, f));
    }

}
