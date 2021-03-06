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

package com.openexchange.rest.services.database.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import com.google.common.collect.ImmutableList;
import com.openexchange.database.Databases;
import com.openexchange.groupware.update.PerformParameters;
import com.openexchange.groupware.update.SimpleConvertUtf8ToUtf8mb4UpdateTask;
import com.openexchange.tools.update.Tools;

/**
 * {@link ServiceSchemaConvertUtf8ToUtf8mb4UpdateTask}
 *
 * @author <a href="mailto:ioannis.chouklis@open-xchange.com">Ioannis Chouklis</a>
 */
public class ServiceSchemaConvertUtf8ToUtf8mb4UpdateTask extends SimpleConvertUtf8ToUtf8mb4UpdateTask {

    /**
     * Initialises a new {@link ServiceSchemaConvertUtf8ToUtf8mb4UpdateTask}.
     */
    public ServiceSchemaConvertUtf8ToUtf8mb4UpdateTask() {
        super(ImmutableList.of("serviceSchemaVersion", "serviceSchemaMigrationLock"));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.openexchange.groupware.update.SimpleConvertUtf8ToUtf8mb4UpdateTask#before(com.openexchange.groupware.update.PerformParameters, java.sql.Connection)
     */
    @Override
    protected void before(PerformParameters params, Connection connection) throws SQLException {
        for (String t : tablesToConvert()) {
            PreparedStatement stmt = null;
            try {
                if (!Tools.tableExists(connection, t)) {
                    continue;
                }
                String alterTable = alterTable(t, getColumsToModify(connection, params.getSchema().getSchema(), t, "latin1", Collections.emptyList()), UTF8MB4_CHARSET, UTF8MB4_UNICODE_COLLATION);
                stmt = connection.prepareStatement(alterTable);
                stmt.execute();
            } finally {
                Databases.closeSQLStuff(stmt);
            }
        }
    }
}
