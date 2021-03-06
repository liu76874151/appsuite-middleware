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

package com.openexchange.chronos.ical.ical4j.mapping.alarm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.openexchange.chronos.Alarm;
import com.openexchange.chronos.ical.ical4j.mapping.ICalMapping;
import net.fortuna.ical4j.extensions.caldav.property.Acknowledged;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VAlarm;

/**
 * {@link AlarmMappings}
 *
 * @author <a href="mailto:tobias.friedrich@open-xchange.com">Tobias Friedrich</a>
 * @since v7.10.0
 */
public class AlarmMappings {

	/**
	 * Holds a collection of all known alarm mappings.
	 */
	public static List<ICalMapping<VAlarm, Alarm>> ALL = Collections.<ICalMapping<VAlarm, Alarm>>unmodifiableList(Arrays.asList(
		new TriggerMapping(),
		new UidMapping(),
		new ActionMapping(),
		new AcknowledgedMapping(),
        new DescriptionMapping(),
        new RelatedToMapping(),
        new RepeatMapping(),
        new SummaryMapping(),
        new AttachmentMapping(),
        new AttendeeMapping(),
        new ExtendedPropertiesMapping(
            Property.SUMMARY, Property.REPEAT, Property.DURATION, Property.TRIGGER, Property.UID, Property.ACTION,
            Acknowledged.PROPERTY_NAME, Property.DESCRIPTION, Property.RELATED_TO, Property.ATTACH, Property.ATTENDEE
        )
	));

    /**
     * Initializes a new {@link AlarmMappings}.
     */
	private AlarmMappings() {
		super();
	}

}
