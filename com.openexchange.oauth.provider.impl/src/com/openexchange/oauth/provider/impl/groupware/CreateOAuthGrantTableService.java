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
 * {@link CreateOAuthGrantTableService}
 *
 * @author <a href="mailto:steffen.templin@open-xchange.com">Steffen Templin</a>
 * @since v7.8.0
 */
public final class CreateOAuthGrantTableService extends AbstractCreateTableImpl {

    private static final String CREATE_GRANT_TABLE = "CREATE TABLE `oauth_grant` (" +
        " `cid` INT4 unsigned NOT NULL," +
        " `user` INT4 unsigned NOT NULL," +
        " `refresh_token` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL," +
        " `access_token` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL," +
        " `client` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL," +
        " `expiration_date` BIGINT(64) NOT NULL," +
        " `scopes` VARCHAR(767) NOT NULL," +
        " `creation_date` BIGINT(64) NOT NULL," +
        " `last_modified` BIGINT(64) NOT NULL," +
        " PRIMARY KEY (refresh_token)," +
        " UNIQUE KEY `access_token` (access_token)," +
        " KEY `client` (client)" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

    /**
     * Initializes a new {@link CreateOAuthGrantTableService}.
     */
    public CreateOAuthGrantTableService() {
        super();
    }

    /**
     * Gets the table names.
     *
     * @return The table names.
     */
    public static String[] getTablesToCreate() {
        return new String[] { "oauth_grant" };
    }

    /**
     * Gets the CREATE-TABLE statements.
     *
     * @return The CREATE statements
     */
    public static String[] getCreateStmts() {
        return new String[] { CREATE_GRANT_TABLE };
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
