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

package com.openexchange.ajax.chronos.itip;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import com.openexchange.ajax.chronos.factory.ICalFacotry.Method;
import com.openexchange.ajax.chronos.factory.ICalFacotry.PartStat;
import com.openexchange.ajax.chronos.factory.ICalFacotry.ProdID;
import com.openexchange.testing.httpclient.models.Analysis;
import com.openexchange.testing.httpclient.models.Attendee;
import com.openexchange.testing.httpclient.models.CalendarUser;
import com.openexchange.testing.httpclient.models.EventData;

/**
 * {@link AbstractITipReplyTest}
 *
 * @author <a href="mailto:daniel.becker@open-xchange.com">Daniel Becker</a>
 * @since v7.10.0
 */
public abstract class AbstractITipReplyTest extends AbstractITipAnalyzeTest {

    protected EventData createdEvent;

    @Override
    public void tearDown() throws Exception {
        try {
            if (null != createdEvent) {
                deleteEvent(createdEvent);
            }
        } finally {
            super.tearDown();
        }
    }


    protected void analyze(EventData event) throws Exception {
        analyze(event, CustomConsumers.UPDATE.getConsumer(), null);
    }

    protected void analyze(EventData event, Consumer<Analysis> validator, Map<String, Object> values) throws Exception {
        analyze(ProdID.OX, Method.REPLY, Collections.singletonList(event), validator, values);
    }

    protected void updateAttendeeStatus(Attendee replyingAttendee, PartStat partStat) {
        replyingAttendee.setPartStat(partStat.name().toUpperCase());
        createdEvent.setAttendees(Collections.singletonList(replyingAttendee));
    }

    protected Attendee prepareCommonAttendees(EventData event) {
        Attendee replyingAttendee = createAttendee(testUserC2, apiClientC2);
        replyingAttendee.setEntity(Integer.valueOf(0));
        // organizer gets added automatically
        event.setAttendees(Collections.singletonList(replyingAttendee));
        CalendarUser c = new CalendarUser();
        c.cn(userResponseC1.getData().getDisplayName());
        c.email(userResponseC1.getData().getEmail1());
        c.entity(Integer.valueOf(userResponseC1.getData().getId()));
        event.setOrganizer(c);
        event.setCalendarUser(c);

        return replyingAttendee;
    }

}
