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

package com.openexchange.find.basic.calendar;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.dmfs.rfc5545.DateTime;
import com.openexchange.chronos.Event;
import com.openexchange.chronos.common.CalendarUtils;
import com.openexchange.java.util.TimeZones;

/**
 * {@link RankedEventComparator}
 *
 * A comparator that ranks events by how close their start date is to today. There is an additional
 * degression factor that causes events in the past to be less relevant than upcoming ones.
 *
 * @author <a href="mailto:tobias.friedrich@open-xchange.com">Tobias Friedrich</a>
 * @since v7.10.0
 */
public class RankedEventComparator implements Comparator<Event>, Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 5180481713172279479L;

    /** Milliseconds per day */
    private static final long MILLIS_PER_DAY = TimeUnit.DAYS.toMillis(1);

    /** The degression factor that is applied to the ranking of dates that are before the relative date */
    private static final double DEGRESSION_FACTOR = 1.1;

    private final long relativeTo;
    private final TimeZone timeZone;

    /**
     * Initializes a new {@link RankedEventComparator}, using "now" as relative date.
     *
     * @param timeZone The timezone to consider for <i>floating</i> dates, i.e. the actual 'perspective' of the comparison, or
     *            <code>null</code> to fall back to UTC
     */
    public RankedEventComparator(TimeZone timeZone) {
        this(new Date(), timeZone);
    }

    /**
     * Initializes a new {@link RankedEventComparator}, using the supplied relative date.
     *
     * @param relativeTo The relative date to use for comparisons.
     * @param timeZone The timezone to consider for <i>floating</i> dates, i.e. the actual 'perspective' of the comparison, or
     *            <code>null</code> to fall back to UTC
     */
    public RankedEventComparator(Date relativeTo, TimeZone timeZone) {
        super();
        this.relativeTo = relativeTo.getTime();
        this.timeZone = null != timeZone ? timeZone : TimeZones.UTC;
    }

    @Override
    public int compare(Event event1, Event event2) {
        DateTime date1 = null != event1 ? event1.getStartDate() : null;
        DateTime date2 = null != event2 ? event2.getStartDate() : null;
        if (date1 == date2) {
            return 0;
        } else if (null == date1) {
            return 1;
        } else if (null == date2) {
            return -1;
        } else {
            double ranking1 = calculateRating(date1, relativeTo, timeZone);
            double ranking2 = calculateRating(date2, relativeTo, timeZone);
            return Double.compare(ranking1, ranking2);
        }
    }

    private static double calculateRating(DateTime dateTime, long relativeTo, TimeZone timeZone) {
        long time = dateTime.isFloating() ? CalendarUtils.getDateInTimeZone(dateTime, timeZone) : dateTime.getTimestamp();
        if (time > relativeTo) {
            return Math.pow(((double)(time - relativeTo)) / MILLIS_PER_DAY, 2.0);
        } else {
            return Math.pow(DEGRESSION_FACTOR * (relativeTo - time) / MILLIS_PER_DAY, 2.0);
        }
    }

}
