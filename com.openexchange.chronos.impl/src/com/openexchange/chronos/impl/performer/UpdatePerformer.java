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

package com.openexchange.chronos.impl.performer;

import static com.openexchange.chronos.common.CalendarUtils.contains;
import static com.openexchange.chronos.common.CalendarUtils.find;
import static com.openexchange.chronos.common.CalendarUtils.getExceptionDateUpdates;
import static com.openexchange.chronos.common.CalendarUtils.getUserIDs;
import static com.openexchange.chronos.common.CalendarUtils.hasExternalOrganizer;
import static com.openexchange.chronos.common.CalendarUtils.initRecurrenceRule;
import static com.openexchange.chronos.common.CalendarUtils.isAllDay;
import static com.openexchange.chronos.common.CalendarUtils.isOpaqueTransparency;
import static com.openexchange.chronos.common.CalendarUtils.isSeriesException;
import static com.openexchange.chronos.common.CalendarUtils.isSeriesMaster;
import static com.openexchange.chronos.common.CalendarUtils.matches;
import static com.openexchange.chronos.impl.Check.requireUpToDateTimestamp;
import static com.openexchange.tools.arrays.Collections.isNullOrEmpty;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import com.openexchange.chronos.Alarm;
import com.openexchange.chronos.Attachment;
import com.openexchange.chronos.Attendee;
import com.openexchange.chronos.AttendeeField;
import com.openexchange.chronos.CalendarUserType;
import com.openexchange.chronos.Event;
import com.openexchange.chronos.EventField;
import com.openexchange.chronos.RecurrenceId;
import com.openexchange.chronos.RecurrenceRange;
import com.openexchange.chronos.UnmodifiableEvent;
import com.openexchange.chronos.common.CalendarUtils;
import com.openexchange.chronos.common.mapping.AttendeeMapper;
import com.openexchange.chronos.common.mapping.EventMapper;
import com.openexchange.chronos.exception.CalendarExceptionCodes;
import com.openexchange.chronos.impl.CalendarFolder;
import com.openexchange.chronos.impl.Check;
import com.openexchange.chronos.impl.InternalCalendarResult;
import com.openexchange.chronos.impl.Role;
import com.openexchange.chronos.impl.Utils;
import com.openexchange.chronos.service.CalendarSession;
import com.openexchange.chronos.service.CollectionUpdate;
import com.openexchange.chronos.service.EventUpdate;
import com.openexchange.chronos.service.ItemUpdate;
import com.openexchange.chronos.service.SimpleCollectionUpdate;
import com.openexchange.chronos.storage.CalendarStorage;
import com.openexchange.exception.OXException;
import com.openexchange.groupware.tools.mappings.Mapping;
import com.openexchange.tools.arrays.Arrays;

/**
 * {@link UpdatePerformer}
 *
 * @author <a href="mailto:tobias.friedrich@open-xchange.com">Tobias Friedrich</a>
 * @since v7.10.0
 */
public class UpdatePerformer extends AbstractUpdatePerformer {

    /** <i>Meta</i>-fields of events that are always skipped when applying updated event data */
    private static final EventField[] SKIPPED_FIELDS = {
        EventField.CREATED, EventField.CREATED_BY, EventField.LAST_MODIFIED, EventField.TIMESTAMP, EventField.MODIFIED_BY, EventField.FLAGS
    };

    /**
     * Initializes a new {@link UpdatePerformer}.
     *
     * @param storage The underlying calendar storage
     * @param session The calendar session
     * @param folder The calendar folder representing the current view on the events
     * @param roles The {@link Role}s a user acts as.
     */
    public UpdatePerformer(CalendarStorage storage, CalendarSession session, CalendarFolder folder, EnumSet<Role> roles) throws OXException {
        super(storage, session, folder, roles);
    }

    /**
     * Initializes a new {@link UpdatePerformer}.
     *
     * @param storage The underlying calendar storage
     * @param session The calendar session
     * @param folder The calendar folder representing the current view on the events
     */
    public UpdatePerformer(CalendarStorage storage, CalendarSession session, CalendarFolder folder) throws OXException {
        super(storage, session, folder);
    }

    /**
     * Initializes a new {@link UpdatePerformer}, taking over the settings from another update performer.
     *
     * @param updatePerformer The update performer to take over the settings from
     */
    protected UpdatePerformer(AbstractUpdatePerformer updatePerformer) {
        super(updatePerformer);
    }

    /**
     * Performs the update operation.
     *
     * @param objectId The identifier of the event to update
     * @param recurrenceId The optional id of the recurrence.
     * @param updatedEventData The updated event data
     * @param clientTimestamp The client timestamp to catch concurrent modifications
     * @return The update result
     */
    public InternalCalendarResult perform(String objectId, RecurrenceId recurrenceId, Event updatedEventData, long clientTimestamp) throws OXException {
        getSelfProtection().checkEvent(updatedEventData);
        /*
         * load original event data
         */
        Event originalEvent = requireUpToDateTimestamp(loadEventData(objectId), clientTimestamp);
        /*
         * update event or event occurrence
         */
        if (null == recurrenceId && updatedEventData.containsRecurrenceId()) {
            recurrenceId = updatedEventData.getRecurrenceId();
        }
        if (null != recurrenceId) {
            if (isSeriesMaster(originalEvent)) {
                updateRecurrence(originalEvent, recurrenceId, updatedEventData);
            } else if (isSeriesException(originalEvent) && recurrenceId.equals(originalEvent.getRecurrenceId())) {
                updateEvent(originalEvent, updatedEventData);
            } else {
                throw CalendarExceptionCodes.INVALID_RECURRENCE_ID.create(String.valueOf(recurrenceId), null);
            }
        } else {
            updateEvent(originalEvent, updatedEventData);
        }
        return resultTracker.getResult();
    }

    /**
     * Updates data of an existing event recurrence and tracks the update in the underlying calendar result.
     *
     * @param originalSeriesMaster The original series master event
     * @param recurrenceId The recurrence identifier targeting the event occurrence to update
     * @param updatedEventData The updated event data
     * @param ignoredFields Additional fields to ignore during the update; {@link #SKIPPED_FIELDS} are always skipped
     */
    protected void updateRecurrence(Event originalSeriesMaster, RecurrenceId recurrenceId, Event updatedEventData, EventField... ignoredFields) throws OXException {
        recurrenceId = Check.recurrenceIdExists(session.getRecurrenceService(), originalSeriesMaster, recurrenceId);
        if (contains(originalSeriesMaster.getDeleteExceptionDates(), recurrenceId)) {
            /*
             * cannot update a delete exception
             */
            throw CalendarExceptionCodes.EVENT_RECURRENCE_NOT_FOUND.create(originalSeriesMaster.getSeriesId(), recurrenceId);
        }
        if (null != recurrenceId.getRange()) {
            /*
             * update "this and future" recurrences; first split the series at this recurrence
             */
            Check.recurrenceRangeMatches(recurrenceId, RecurrenceRange.THISANDFUTURE);
            new SplitPerformer(this).perform(originalSeriesMaster.getSeriesId(), recurrenceId.getValue(), null, originalSeriesMaster.getTimestamp());
            /*
             * reload the (now splitted) series event & apply the update, taking over a new recurrence rule as needed
             */
            Event updatedMasterEvent = loadEventData(originalSeriesMaster.getId());
            updatedEventData = applyRecurrenceRuleAfterSplit(originalSeriesMaster, updatedMasterEvent, updatedEventData);
            updateEvent(updatedMasterEvent, updatedEventData, EventField.ID, EventField.RECURRENCE_ID, EventField.DELETE_EXCEPTION_DATES, EventField.CHANGE_EXCEPTION_DATES);
        } else if (contains(originalSeriesMaster.getChangeExceptionDates(), recurrenceId)) {
            /*
             * update for existing change exception, perform update, touch master & track results
             */
            Check.recurrenceRangeMatches(recurrenceId, null);
            Event originalExceptionEvent = loadExceptionData(originalSeriesMaster, recurrenceId);
            updateEvent(originalExceptionEvent, updatedEventData, ignoredFields);
            touch(originalSeriesMaster.getSeriesId());
            resultTracker.trackUpdate(originalSeriesMaster, loadEventData(originalSeriesMaster.getId()));
        } else {
            /*
             * update for new change exception; prepare & insert a plain exception first, based on the original data from the master
             */
            Map<Integer, List<Alarm>> seriesMasterAlarms = storage.getAlarmStorage().loadAlarms(originalSeriesMaster);
            Event newExceptionEvent = prepareException(originalSeriesMaster, recurrenceId);
            Map<Integer, List<Alarm>> newExceptionAlarms = prepareExceptionAlarms(seriesMasterAlarms);
            Check.quotaNotExceeded(storage, session);
            storage.getEventStorage().insertEvent(newExceptionEvent);
            storage.getAttendeeStorage().insertAttendees(newExceptionEvent.getId(), originalSeriesMaster.getAttendees());
            storage.getAttachmentStorage().insertAttachments(session.getSession(), folder.getId(), newExceptionEvent.getId(), originalSeriesMaster.getAttachments());
            insertAlarms(newExceptionEvent, newExceptionAlarms, true);
            newExceptionEvent = loadEventData(newExceptionEvent.getId());
            resultTracker.trackCreation(newExceptionEvent, originalSeriesMaster);
            /*
             * perform the update on the newly created change exception
             * - recurrence rule is forcibly ignored during update to satisfy UsmFailureDuringRecurrenceTest.testShouldFailWhenTryingToMakeAChangeExceptionASeriesButDoesNot()
             * - sequence number is also ignored (since possibly incremented implicitly before)
             */
            updateEvent(newExceptionEvent, updatedEventData, EventField.ID, EventField.RECURRENCE_RULE, EventField.SEQUENCE);
            Event updatedExceptionEvent = loadEventData(newExceptionEvent.getId());
            /*
             * add change exception date to series master & track results
             */
            resultTracker.rememberOriginalEvent(originalSeriesMaster);
            addChangeExceptionDate(originalSeriesMaster, recurrenceId, CalendarUtils.isInternal(originalSeriesMaster.getOrganizer(), CalendarUserType.INDIVIDUAL));
            Event updatedMasterEvent = loadEventData(originalSeriesMaster.getId());
            resultTracker.trackUpdate(originalSeriesMaster, updatedMasterEvent);
            /*
             * reset alarm triggers for series master event and new change exception
             */
            storage.getAlarmTriggerStorage().deleteTriggers(updatedMasterEvent.getId());
            storage.getAlarmTriggerStorage().insertTriggers(updatedMasterEvent, seriesMasterAlarms);
            storage.getAlarmTriggerStorage().deleteTriggers(updatedExceptionEvent.getId());
            storage.getAlarmTriggerStorage().insertTriggers(updatedExceptionEvent, storage.getAlarmStorage().loadAlarms(updatedExceptionEvent));
        }
    }

    /**
     * Updates data of an existing event.
     *
     * @param originalEvent The original, plain event data
     * @param eventData The updated event data
     * @param ignoredFields Additional fields to ignore during the update; {@link #SKIPPED_FIELDS} are always skipped
     * @return The updated event
     */
    protected Event updateEvent(Event originalEvent, Event eventData, EventField... ignoredFields) throws OXException {
        /*
         * check if folder view on event is allowed as needed
         */
        boolean assumeExternalOrganizerUpdate = assumeExternalOrganizerUpdate(originalEvent, eventData);
        if (false == assumeExternalOrganizerUpdate) {
            Check.eventIsInFolder(originalEvent, folder);
        }
        /*
         * handle new delete exceptions from the calendar user's point of view beforehand
         */
        if (isSeriesMaster(originalEvent) && eventData.containsDeleteExceptionDates() && 
            false == hasExternalOrganizer(originalEvent) && false == deleteRemovesEvent(originalEvent)) {
            if (updateDeleteExceptions(originalEvent, eventData)) {
                originalEvent = loadEventData(originalEvent.getId());
            }
            /*
             * consider as handled, so ignore delete exception dates later on
             */
            ignoredFields = null != ignoredFields ? Arrays.add(ignoredFields, EventField.DELETE_EXCEPTION_DATES) : new EventField[] { EventField.DELETE_EXCEPTION_DATES };
        }
        /*
         * prepare event update & check conflicts as needed
         */
        List<Event> originalChangeExceptions = isSeriesMaster(originalEvent) ? loadExceptionData(originalEvent) : null;
        Event originalSeriesMasterEvent = isSeriesException(originalEvent) ? loadEventData(originalEvent.getSeriesId()) : null;
        EventUpdateProcessor eventUpdate = new EventUpdateProcessor(
            session, folder, originalEvent, originalChangeExceptions, originalSeriesMasterEvent, eventData, timestamp, Arrays.add(SKIPPED_FIELDS, ignoredFields));
        if (needsConflictCheck(eventUpdate)) {
            Check.noConflicts(storage, session, eventUpdate.getUpdate(), eventUpdate.getAttendeeUpdates().previewChanges());
        }
        /*
         * recursively perform pending deletions of change exceptions if required, checking permissions as needed
         */
        for (Event removedException : eventUpdate.getExceptionUpdates().getRemovedItems()) {
            requireDeletePermissions(removedException);
            delete(removedException);
        }
        /*
         * update event data in storage, checking permissions as required
         */
        storeEventUpdate(originalEvent, eventUpdate.getDelta(), eventUpdate.getUpdatedFields(), assumeExternalOrganizerUpdate);
        storeAttendeeUpdates(originalEvent, eventUpdate.getAttendeeUpdates(), assumeExternalOrganizerUpdate);
        storeAttachmentUpdates(originalEvent, eventUpdate.getAttachmentUpdates(), assumeExternalOrganizerUpdate);
        /*
         * update passed alarms for calendar user, apply default alarms for newly added internal user attendees
         */
        if (eventData.containsAlarms()) {
            Event updatedEvent = loadEventData(originalEvent.getId());
            List<Alarm> originalAlarms = storage.getAlarmStorage().loadAlarms(originalEvent, calendarUserId);
            if(originalChangeExceptions != null) {

                List<Event> copies = new ArrayList<>(originalChangeExceptions.size());
                for(Event eve: originalChangeExceptions) {
                    copies.add(EventMapper.getInstance().copy(eve, null, EventMapper.getInstance().getAssignedFields(eve)));
                }
                
                List<Event> exceptionsWithAlarms = storage.getUtilities().loadAdditionalEventData(calendarUserId, copies, null);
                Map<Event, List<Alarm>> alarmsToUpdate = AlarmUpdateProcessor.getUpdatedExceptions(originalAlarms, eventData.getAlarms(), exceptionsWithAlarms);
                for (Entry<Event, List<Alarm>> toUpdate : alarmsToUpdate.entrySet()) {
                    updateAlarms(toUpdate.getKey(), calendarUserId, toUpdate.getKey().getAlarms(), toUpdate.getValue());
                }
            }
            updateAlarms(updatedEvent, calendarUserId, originalAlarms, eventData.getAlarms());
        }
        for (int userId : getUserIDs(eventUpdate.getAttendeeUpdates().getAddedItems())) {
            List<Alarm> defaultAlarm = isAllDay(eventUpdate.getUpdate()) ? session.getConfig().getDefaultAlarmDate(userId) : session.getConfig().getDefaultAlarmDateTime(userId);
            if (null != defaultAlarm) {
                insertAlarms(eventUpdate.getUpdate(), userId, defaultAlarm, true);
            }
        }
        /*
         * recursively perform pending updates of change exceptions if required
         */
        for (ItemUpdate<Event, EventField> updatedException : eventUpdate.getExceptionUpdates().getUpdatedItems()) {
            updateEvent(updatedException.getOriginal(), updatedException.getUpdate());
        }
        /*
         * track update result & update any stored alarm triggers of all users if required
         */
        Event updatedEvent = loadEventData(originalEvent.getId());
        if (originalEvent.getId().equals(updatedEvent.getId()) && Objects.equals(originalEvent.getRecurrenceId(), updatedEvent.getRecurrenceId())) {
            resultTracker.trackUpdate(originalEvent, updatedEvent);
        } else {
            resultTracker.trackDeletion(originalEvent);
            resultTracker.trackCreation(updatedEvent);
        }
        Map<Integer, List<Alarm>> alarms;
        if (eventUpdate.containsAnyChangeOf(new EventField[] { EventField.START_DATE, EventField.END_DATE })) {
            storage.getAlarmTriggerStorage().deleteTriggers(originalEvent.getId());
            alarms = storage.getAlarmStorage().loadAlarms(updatedEvent);
        } else {
            alarms = storage.getAlarmStorage().loadAlarms(updatedEvent);
            storage.getAlarmTriggerStorage().deleteTriggers(originalEvent.getId());
        }
        storage.getAlarmTriggerStorage().insertTriggers(updatedEvent, alarms);
        return updatedEvent;
    }

    /**
     * Determines if an incoming event update can be treated as initiated by the (external) organizer of a scheduling object resource or
     * not. If yes, certain checks may be skipped, i.e. the existence check in the calendar user's folder, or the check against allowed
     * attendee changes.
     * <p/>
     * An update is considered as <i>organizer-update</i> under certain circumstances, particularly:
     * <ul>
     * <li>the event has an <i>external</i> organizer</li>
     * <li>the organizer matches in the original and in the updated event</li>
     * <li>the unique identifier matches in the original and in the updated event</li>
     * <li>the updated event's sequence number is not smaller than the sequence number of the original event</li>
     * </ul>
     *
     * @param originalEvent The original event
     * @param updatedEvent The updated event
     * @return <code>true</code> if an external organizer update can be assumed, <code>false</code>, otherwise
     * @see <a href="https://bugs.open-xchange.com/show_bug.cgi?id=29566#c12">Bug 29566</a>,
     *      <a href="https://bugs.open-xchange.com/show_bug.cgi?id=23181"/>Bug 23181</a>
     */
    private boolean assumeExternalOrganizerUpdate(Event originalEvent, Event updatedEvent) {
        if (hasExternalOrganizer(originalEvent) && matches(originalEvent.getOrganizer(), updatedEvent.getOrganizer()) &&
            Objects.equals(originalEvent.getUid(), updatedEvent.getUid()) && updatedEvent.getSequence() >= originalEvent.getSequence()) {
            return true;
        }
        return false;
    }

    /**
     * Gets a value indicating whether conflict checks should take place along with the update or not.
     *
     * @param eventUpdate The event update to evaluate
     * @param attendeeHelper The attendee helper for the update
     * @return <code>true</code> if conflict checks should take place, <code>false</code>, otherwise
     */
    private boolean needsConflictCheck(EventUpdate eventUpdate) throws OXException {
        if (Utils.coversDifferentTimePeriod(eventUpdate.getOriginal(), eventUpdate.getUpdate())) {
            /*
             * (re-)check conflicts if event appears in a different time period
             */
            return true;
        }
        if (eventUpdate.getUpdatedFields().contains(EventField.TRANSP) && false == isOpaqueTransparency(eventUpdate.getOriginal())) {
            /*
             * (re-)check conflicts if transparency is now opaque
             */
            return true;
        }
        if (0 < eventUpdate.getAttendeeUpdates().getAddedItems().size()) {
            /*
             * (re-)check conflicts if there are new attendees
             */
            return true;
        }
        return false;
    }

    private boolean updateDeleteExceptions(Event originalEvent, Event updatedEvent) throws OXException {
        if (isSeriesMaster(originalEvent) && false == isNullOrEmpty(updatedEvent.getDeleteExceptionDates())) {
            if (deleteRemovesEvent(originalEvent)) {
                /*
                 * "real" delete exceptions for all attendees, take over as-is during normal update routine
                 */
                return false;
            }
            /*
             * check for newly indicated delete exceptions, from the calendar user's point of view
             */
            Attendee userAttendee = find(originalEvent.getAttendees(), calendarUserId);
            SortedSet<RecurrenceId> originalDeleteExceptionDates = Utils.applyExceptionDates(storage, originalEvent, calendarUserId).getDeleteExceptionDates();
            SimpleCollectionUpdate<RecurrenceId> exceptionDateUpdates = getExceptionDateUpdates(originalDeleteExceptionDates, updatedEvent.getDeleteExceptionDates());
            if (0 < exceptionDateUpdates.getRemovedItems().size() || null == userAttendee) {
                throw CalendarExceptionCodes.FORBIDDEN_CHANGE.create(originalEvent.getId(), EventField.DELETE_EXCEPTION_DATES);
            }
            if (0 < exceptionDateUpdates.getAddedItems().size()) {
                for (RecurrenceId newDeleteException : exceptionDateUpdates.getAddedItems()) {
                    RecurrenceId recurrenceId = Check.recurrenceIdExists(session.getRecurrenceService(), originalEvent, newDeleteException);
                    if (contains(originalEvent.getChangeExceptionDates(), recurrenceId)) {
                        /*
                         * remove attendee from existing change exception
                         */
                        Event originalExceptionEvent = loadExceptionData(originalEvent, recurrenceId);
                        delete(originalExceptionEvent, userAttendee);
                    } else {
                        /*
                         * creation of new delete exception for this attendee
                         */
                        deleteFromRecurrence(originalEvent, recurrenceId, userAttendee);
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Persists event data updates in the underlying calendar storage, verifying that the current user has appropriate write permissions
     * in order to do so.
     *
     * @param originalEvent The event being updated
     * @param deltaEvent The delta event providing the updated event data
     * @param updatedFields The actually updated fields
     * @param assumeExternalOrganizerUpdate <code>true</code> if an external organizer update can be assumed, <code>false</code>, otherwise
     * @return <code>true</code> if there were changes, <code>false</code>, otherwise
     */
    private boolean storeEventUpdate(Event originalEvent, Event deltaEvent, Set<EventField> updatedFields, boolean assumeExternalOrganizerUpdate) throws OXException {
        HashSet<EventField> updatedEventFields = new HashSet<EventField>(updatedFields);
        updatedEventFields.removeAll(java.util.Arrays.asList(EventField.ATTACHMENTS, EventField.ALARMS, EventField.ATTENDEES));
        if (updatedEventFields.isEmpty()) {
            return false;
        }
        boolean realChange = false;
        for (EventField updatedField : updatedEventFields) {
            if (Arrays.contains(SKIPPED_FIELDS, updatedField)) {
                continue;
            }
            realChange = true;
            break;
        }
        if (realChange) {
            requireWritePermissions(originalEvent, assumeExternalOrganizerUpdate);
        } else {
            requireWritePermissions(originalEvent, session.getEntityResolver().prepareUserAttendee(calendarUserId), assumeExternalOrganizerUpdate);
        }
        storage.getEventStorage().updateEvent(deltaEvent);
        return true;
    }

    /**
     * Persists attendee updates in the underlying calendar storage, verifying that the current user has appropriate write permissions
     * in order to do so.
     *
     * @param originalEvent The event the attendees are updated for
     * @param attachmentUpdates The attendee updates to persist
     * @param assumeExternalOrganizerUpdate <code>true</code> if an external organizer update can be assumed, <code>false</code>, otherwise
     * @return <code>true</code> if there were changes, <code>false</code>, otherwise
     */
    private boolean storeAttendeeUpdates(Event originalEvent, CollectionUpdate<Attendee, AttendeeField> attendeeUpdates, boolean assumeExternalOrganizerUpdate) throws OXException {
        if (attendeeUpdates.isEmpty()) {
            return false;
        }
        /*
         * perform attendee deletions
         */
        List<Attendee> removedItems = attendeeUpdates.getRemovedItems();
        if (0 < removedItems.size()) {
            requireWritePermissions(originalEvent, removedItems, assumeExternalOrganizerUpdate);
            storage.getEventStorage().insertEventTombstone(storage.getUtilities().getTombstone(originalEvent, timestamp, calendarUser));
            storage.getAttendeeStorage().insertAttendeeTombstones(originalEvent.getId(), storage.getUtilities().getTombstones(removedItems));
            storage.getAttendeeStorage().deleteAttendees(originalEvent.getId(), removedItems);
            storage.getAlarmStorage().deleteAlarms(originalEvent.getId(), getUserIDs(removedItems));
        }
        /*
         * perform attendee updates
         */
        List<? extends ItemUpdate<Attendee, AttendeeField>> updatedItems = attendeeUpdates.getUpdatedItems();
        if (0 < updatedItems.size()) {
            List<Attendee> attendeesToUpdate = new ArrayList<Attendee>(updatedItems.size());
            for (ItemUpdate<Attendee, AttendeeField> attendeeToUpdate : updatedItems) {
                requireWritePermissions(originalEvent, attendeeToUpdate, assumeExternalOrganizerUpdate);
                Attendee originalAttendee = attendeeToUpdate.getOriginal();
                Attendee newAttendee = AttendeeMapper.getInstance().copy(originalAttendee, null, (AttendeeField[]) null);
                AttendeeMapper.getInstance().copy(attendeeToUpdate.getUpdate(), newAttendee, AttendeeField.RSVP, AttendeeField.HIDDEN, AttendeeField.COMMENT, AttendeeField.PARTSTAT, AttendeeField.ROLE, AttendeeField.EXTENDED_PARAMETERS);
                attendeesToUpdate.add(newAttendee);
            }
            storage.getAttendeeStorage().updateAttendees(originalEvent.getId(), attendeesToUpdate);
        }
        /*
         * perform attendee inserts
         */
        if (0 < attendeeUpdates.getAddedItems().size()) {
            requireWritePermissions(originalEvent, assumeExternalOrganizerUpdate);
            storage.getAttendeeStorage().insertAttendees(originalEvent.getId(), attendeeUpdates.getAddedItems());
        }
        return true;
    }

    /**
     * Persists attachment updates in the underlying calendar storage, verifying that the current user has appropriate write permissions
     * in order to do so.
     *
     * @param originalEvent The event the attachments are updated for
     * @param attachmentUpdates The attachment updates to persist
     * @param assumeExternalOrganizerUpdate <code>true</code> if an external organizer update can be assumed, <code>false</code>, otherwise
     * @return <code>true</code> if there were changes, <code>false</code>, otherwise
     */
    private boolean storeAttachmentUpdates(Event originalEvent, SimpleCollectionUpdate<Attachment> attachmentUpdates, boolean assumeExternalOrganizerUpdate) throws OXException {
        if (attachmentUpdates.isEmpty()) {
            return false;
        }
        requireWritePermissions(originalEvent, assumeExternalOrganizerUpdate);
        if (0 < attachmentUpdates.getRemovedItems().size()) {
            storage.getAttachmentStorage().deleteAttachments(session.getSession(), folder.getId(), originalEvent.getId(), attachmentUpdates.getRemovedItems());
        }
        if (0 < attachmentUpdates.getAddedItems().size()) {
            Check.attachmentsAreVisible(session, storage, attachmentUpdates.getAddedItems());
            storage.getAttachmentStorage().insertAttachments(session.getSession(), folder.getId(), originalEvent.getId(), attachmentUpdates.getAddedItems());
        }
        return true;
    }

    /**
     * Selects the recurrence rule to use for the event update after a split has been performed on a recurring event series. This may be
     * necessary when the rule's <code>COUNT</code> attribute was modified during the split operation.
     *
     * @param originalSeriesMaster The original series master event (before the split)
     * @param updatedSeriesMaster The updated series master event (after the split)
     * @param clientUpdate The updated event data as passed by the client
     * @return The (possibly modified) updated event data to take over
     */
    private static Event applyRecurrenceRuleAfterSplit(Event originalSeriesMaster, Event updatedSeriesMaster, Event clientUpdate) throws OXException {
        Mapping<? extends Object, Event> rruleMapping = EventMapper.getInstance().get(EventField.RECURRENCE_RULE);
        if (false == rruleMapping.isSet(clientUpdate) || rruleMapping.equals(updatedSeriesMaster, clientUpdate)) {
            /*
             * rrule not modified, nothing to do
             */
            return clientUpdate;
        }
        if (null == clientUpdate.getRecurrenceRule() || rruleMapping.equals(originalSeriesMaster, updatedSeriesMaster)) {
            /*
             * rrule is removed or was not changed by split, so take over new rrule from client as-is
             */
            return clientUpdate;
        }
        /*
         * rrule was modified during split, merge a possibly updated count value with client's rrule
         */
        RecurrenceRule updatedRule = initRecurrenceRule(updatedSeriesMaster.getRecurrenceRule());
        if (null != updatedRule.getCount()) {
            RecurrenceRule clientRule = initRecurrenceRule(clientUpdate.getRecurrenceRule());
            RecurrenceRule originalRule = initRecurrenceRule(originalSeriesMaster.getRecurrenceRule());
            if (null != clientRule.getCount() && clientRule.getCount().equals(originalRule.getCount())) {
                clientRule.setCount(updatedRule.getCount().intValue());
                clientUpdate = EventMapper.getInstance().copy(clientUpdate, null, (EventField[]) null);
                clientUpdate.setRecurrenceRule(clientRule.toString());
                return new UnmodifiableEvent(clientUpdate);
            }
        }
        /*
         * stick with client-supplied data, otherwise
         */
        return clientUpdate;
    }

}
