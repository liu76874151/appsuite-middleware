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

package com.openexchange.oauth.provider.impl.groupware;

import com.openexchange.database.AbstractCreateTableImpl;


/**
 * {@link AuthCodeCreateTableService}
 *
 * @author <a href="mailto:thorben.betten@open-xchange.com">Thorben Betten</a>
 */
public final class AuthCodeCreateTableService extends AbstractCreateTableImpl {

    private static final String TABLE_AUTH_CODE = "authCode";

    private static final String CREATE_AUTH_CODE = "CREATE TABLE `"+TABLE_AUTH_CODE+"` (" +
        " `code` varchar(64) NOT NULL," +
        " `cid` INT4 unsigned NOT NULL," +
        " `user` INT4 unsigned NOT NULL," +
        " `clientId` varchar(255) NOT NULL," +
        " `redirectURI` varchar(767) NOT NULL," +
        " `scope` varchar(512) NOT NULL," +
        " `nanos` BIGINT(64) NOT NULL," +
        " PRIMARY KEY (`code`)," +
        " KEY `userIndex` (`cid`,`user`)" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

    /**
     * Gets the table names.
     *
     * @return The table names.
     */
    public static String[] getTablesToCreate() {
        return new String[] { TABLE_AUTH_CODE };
    }

    /**
     * Gets the CREATE-TABLE statements.
     *
     * @return The CREATE statements
     */
    public static String[] getCreateStmts() {
        return new String[] { CREATE_AUTH_CODE };
    }

    /**
     * Initializes a new {@link AuthCodeCreateTableService}.
     */
    public AuthCodeCreateTableService() {
        super();
    }

    @Override
    public String[] requiredTables() {
        return NO_TABLES;
    }

    @Override
    public String[] tablesToCreate() {
        return getTablesToCreate();
    }

    @Override
    protected String[] getCreateStatements() {
        return getCreateStmts();
    }

}
