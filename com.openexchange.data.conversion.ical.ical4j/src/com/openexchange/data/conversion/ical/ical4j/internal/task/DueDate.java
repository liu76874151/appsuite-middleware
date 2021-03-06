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

package com.openexchange.data.conversion.ical.ical4j.internal.task;

import static com.openexchange.data.conversion.ical.ical4j.internal.EmitterTools.toDate;
import static com.openexchange.data.conversion.ical.ical4j.internal.EmitterTools.toDateTime;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import com.openexchange.data.conversion.ical.ConversionWarning;
import com.openexchange.data.conversion.ical.Mode;
import com.openexchange.data.conversion.ical.ical4j.internal.AbstractVerifyingAttributeConverter;
import com.openexchange.data.conversion.ical.ical4j.internal.EmitterTools;
import com.openexchange.data.conversion.ical.ical4j.internal.ParserTools;
import com.openexchange.groupware.contexts.Context;
import com.openexchange.groupware.tasks.Task;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Due;

/**
 * @author Francisco Laguna <francisco.laguna@open-xchange.com>
 * @author <a href="mailto:marcus@open-xchange.org">Marcus Klein</a>
 */
public class DueDate extends AbstractVerifyingAttributeConverter<VToDo, Task> {

    public DueDate() {
        super();
    }

    @Override
    public boolean isSet(final Task task) {
        return task.containsEndDate();
    }

    @Override
    public void emit(final Mode mode, final int index, final Task task, final VToDo vToDo, final List<ConversionWarning> warnings, final Context ctx, final Object... args) {
        /*
         * emit as date or date-time depending on fulltime flag
         */
        Due due = new Due();
        if (task.getFullTime()) {
            due.setDate(toDate(task.getEndDate()));
        } else {
            due.setDate(toDateTime(mode.getZoneInfo(), task.getEndDate(), EmitterTools.extractTimezoneIfPossible(task)));
        }
        vToDo.getProperties().add(due);
    }

    @Override
    public boolean hasProperty(final VToDo vToDo) {
        return null != vToDo.getDue();
    }

    @Override
    public void parse(final int index, final VToDo vToDo, final Task task, final TimeZone timeZone, final Context ctx, final List<ConversionWarning> warnings) {
        if (task.containsEndDate()) {
            return;
        }
        final Due due = vToDo.getDue();
        if (null == due) {
            return;
        }
        final Date endDate = ParserTools.toDateConsideringDateType(due, timeZone);
        task.setEndDate(endDate);
        task.setFullTime(false == ParserTools.isDateTime(vToDo, due));
    }

}
