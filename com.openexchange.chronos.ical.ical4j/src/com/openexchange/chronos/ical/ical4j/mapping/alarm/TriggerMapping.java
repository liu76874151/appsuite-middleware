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

import java.util.List;
import com.openexchange.chronos.Alarm;
import com.openexchange.chronos.Trigger;
import com.openexchange.chronos.Trigger.Related;
import com.openexchange.chronos.common.AlarmUtils;
import com.openexchange.chronos.ical.ICalParameters;
import com.openexchange.chronos.ical.ical4j.mapping.AbstractICalMapping;
import com.openexchange.exception.OXException;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VAlarm;

/**
 * {@link TriggerMapping}
 *
 * @author <a href="mailto:tobias.friedrich@open-xchange.com">Tobias Friedrich</a>
 * @since v7.10.0
 */
public class TriggerMapping extends AbstractICalMapping<VAlarm, Alarm> {

    /**
     * Initializes a new {@link TriggerMapping}.
     */
	public TriggerMapping() {
		super();
	}

	@Override
	public void export(Alarm object, VAlarm component, ICalParameters parameters, List<OXException> warnings) {
		Trigger value = object.getTrigger();
		if (null == value) {
			removeProperties(component, Property.TRIGGER);
		} else {
			net.fortuna.ical4j.model.property.Trigger property = component.getTrigger();
			if (null == property) {
				property = new net.fortuna.ical4j.model.property.Trigger();
				component.getProperties().add(property);
			}
			if (null != value.getDateTime()) {
				/*
				 * export as absolute date-time
				 */
				property.setDateTime(new DateTime(value.getDateTime()));
			} else if (null != value.getDuration()) {
				/*
				 * export as relative duration
				 */
				property.setDuration(new Dur(value.getDuration()));
				property.getParameters().removeAll(Parameter.RELATED);
				if (Related.END.equals(value.getRelated())) {
					property.getParameters().add(net.fortuna.ical4j.model.parameter.Related.END);
				} else if (Related.START.equals(value.getRelated())) {
					property.getParameters().add(net.fortuna.ical4j.model.parameter.Related.START);
				}
			}
		}
	}

	@Override
	public void importICal(VAlarm component, Alarm object, ICalParameters parameters, List<OXException> warnings) {
		net.fortuna.ical4j.model.property.Trigger property = component.getTrigger();
		if (null == property) {
			object.setTrigger(null);
		} else {
			Trigger value = new Trigger();
			if (null != property.getDateTime()) {
				/*
				 * import as absolute date-time
				 */
				value.setDateTime(property.getDateTime());
				value.setDuration(null);
				value.setRelated(null);
			} else if (null != property.getDuration()) {
				/*
				 * import as relative duration
				 */
				value.setDateTime(null);
                Dur dur = property.getDuration();
                value.setDuration(AlarmUtils.getDuration(dur.isNegative(), dur.getWeeks(), dur.getDays(), dur.getHours(), dur.getMinutes(), dur.getSeconds()));
				Parameter parameter = property.getParameter(Parameter.RELATED);
				if (null != parameter) {
					if (net.fortuna.ical4j.model.parameter.Related.END.equals(parameter)) {
						value.setRelated(Related.END);
                    } else if (net.fortuna.ical4j.model.parameter.Related.START.equals(parameter)) {
						value.setRelated(Related.START);
					} else {
						addConversionWarning(warnings, "TRIGGER", "Invalid \"RELATED\" parameter:" + parameter);
					}
				} else {
					value.setRelated(null);
				}
			}
			object.setTrigger(value);
		}
	}

}
