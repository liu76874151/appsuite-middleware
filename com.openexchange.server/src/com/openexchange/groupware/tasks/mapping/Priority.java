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

package com.openexchange.groupware.tasks.mapping;

import static com.openexchange.java.Autoboxing.I;
import static com.openexchange.java.Autoboxing.i;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import com.openexchange.groupware.tasks.Mapper;
import com.openexchange.groupware.tasks.Task;


/**
 * Methods for iterated processing of the priority of task objects.
 *
 * @author <a href="mailto:marcus.klein@open-xchange.com">Marcus Klein</a>
 * @since 7.6.1
 */
public class Priority implements Mapper<Integer> {

    public static final Priority SINGLETON = new Priority();

    private Priority() {
        super();
    }

    @Override
    public int getId() {
        return Task.PRIORITY;
    }

    @Override
    public boolean isSet(Task task) {
        return task.containsPriority();
    }

    @Override
    public String getDBColumnName() {
        return "priority";
    }

    @Override
    public String getDisplayName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void toDB(PreparedStatement stmt, int pos, Task task) throws SQLException {
        if (null == task.getPriority()) {
            stmt.setNull(pos, Types.INTEGER);
        } else {
            stmt.setInt(pos, i(task.getPriority()));
        }
    }

    @Override
    public void fromDB(ResultSet result, int pos, Task task) throws SQLException {
        final int priority = result.getInt(pos);
        if (!result.wasNull()) {
            task.setPriority(I(priority));
        }
    }

    @Override
    public boolean equals(Task task1, Task task2) {
        Integer prio1 = task1.getPriority();
        Integer prio2 = task2.getPriority();

        if (null == prio1) {
            return null == prio2;
        }
        if (null == prio2) {
            return false;
        }
        return prio1.intValue() == prio2.intValue();
    }

    @Override
    public Integer get(Task task) {
        return task.getPriority();
    }

    @Override
    public void set(Task task, Integer value) {
        task.setPriority(value);
    }
}
