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

package com.openexchange.groupware.update.tasks;

import static com.openexchange.database.Databases.autocommit;
import static com.openexchange.database.Databases.rollback;
import java.sql.Connection;
import java.sql.SQLException;
import com.openexchange.exception.OXException;
import com.openexchange.groupware.update.PerformParameters;
import com.openexchange.groupware.update.UpdateExceptionCodes;
import com.openexchange.groupware.update.UpdateTaskAdapter;
import com.openexchange.tools.update.Column;
import com.openexchange.tools.update.Tools;

/**
 * {@link CalendarRevokeAddConfirmPerOccurrenceTask} - Extends those calendar tables that carry confirmation information by "occurrence" column.
 *
 * @author <a href="mailto:thorben.betten@open-xchange.com">Thorben Betten</a>
 */
public class CalendarRevokeAddConfirmPerOccurrenceTask extends UpdateTaskAdapter {

    /**
     * Initializes a new {@link CalendarRevokeAddConfirmPerOccurrenceTask}.
     */
    public CalendarRevokeAddConfirmPerOccurrenceTask() {
        super();
    }

    @Override
    public String[] getDependencies() {
        return new String[] { CalendarAddConfirmPerOccurrenceTask.class.getName(), CalendarRevokeAddNewPrimaryKeyForConfirmPerOccurrence.class.getName() };
    }

    @Override
    public void perform(final PerformParameters params) throws OXException {
        Connection con = params.getConnection();
        int rollback = 0;
        try {
            con.setAutoCommit(false);
            rollback = 1;

            final Column occurrenceColumn = new Column("occurrence", "INT(10) unsigned NOT NULL DEFAULT '0'");

            Tools.checkAndDropColumns(con, "prg_dates_members", occurrenceColumn);
            Tools.checkAndDropColumns(con, "del_dates_members", occurrenceColumn);

            Tools.checkAndDropColumns(con, "dateExternal", occurrenceColumn);
            Tools.checkAndDropColumns(con, "delDateExternal", occurrenceColumn);

            con.commit();
            rollback = 2;
        } catch (final SQLException e) {
            throw UpdateExceptionCodes.SQL_PROBLEM.create(e, e.getMessage());
        } finally {
            if (rollback > 0) {
                if (rollback==1) {
                    rollback(con);
                }
                autocommit(con);
            }
        }
    }

}
