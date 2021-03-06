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

package com.openexchange.database;

import java.sql.Connection;
import com.openexchange.exception.OXException;

/**
 * If your bundle needs to create database tables for working properly this service must be implemented. Its method are called if a new
 * schema for contexts is created. The order of executing {@link CreateTableService} instances is calculated by the string arrays given from
 * the methods {@link #requiredTables()} and {@link #tablesToCreate()}. The {@link #perform(Connection)} method should then create the
 * tables needed for your bundle.
 *
 * The table must be created in its newest version. <code>com.openexchange.groupware.update.UpdateTask</code>s are not executed after all
 * tables have been created and the schema
 * is marked in that way that all {@link UpdateTask}s have already been executed.
 *
 * @author <a href="mailto:marcus.klein@open-xchange.com">Marcus Klein</a>
 */
public interface CreateTableService {

    /**
     * This method should return all names of those table that have to exist before the {@link #perform(Connection)} method is called.
     * For instance if a table has foreign keys constraints to other tables.
     *
     * @return An array with table names that have to exist before the {@link #perform(Connection)} method is called.
     */
    String[] requiredTables();

    /**
     * This method must return all names of those tables that shall be created during call of the {@link #perform(Connection)} method.
     *
     * @return An array with table names that are created during call of the {@link #perform(Connection)} method.
     */
    String[] tablesToCreate();

    /**
     * The implementation of this method should create the required tables on the given database connection. The connection is already
     * configured to use the correct schema. The given connection is already in a transaction. Do not modify the transaction state of the
     * connection.
     *
     * @param con writable connection in a transaction state.
     * @throws OXException should be thrown if creating the table fails.
     */
    void perform(Connection con) throws OXException;

}
