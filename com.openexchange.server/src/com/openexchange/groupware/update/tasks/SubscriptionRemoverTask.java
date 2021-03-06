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

import static com.openexchange.tools.sql.DBUtils.tablesExist;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.openexchange.database.Databases;
import com.openexchange.exception.OXException;
import com.openexchange.groupware.update.Attributes;
import com.openexchange.groupware.update.PerformParameters;
import com.openexchange.groupware.update.TaskAttributes;
import com.openexchange.groupware.update.UpdateConcurrency;
import com.openexchange.groupware.update.UpdateExceptionCodes;
import com.openexchange.groupware.update.UpdateTaskV2;
import com.openexchange.java.Autoboxing;


/**
 * {@link SubscriptionRemoverTask}
 *
 * @author <a href="mailto:francisco.laguna@open-xchange.com">Francisco Laguna</a>
 */
public class SubscriptionRemoverTask implements UpdateTaskV2 {
    
    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionRemoverTask.class);

    private final List<String> subscriptionSourceIds;

    /**
     * Initializes a new {@link SubscriptionRemoverTask}.
     *
     * @param subscriptionSourceId The identifier
     */
    public SubscriptionRemoverTask(final String subscriptionSourceId) {
        super();
        this.subscriptionSourceIds = Collections.singletonList(subscriptionSourceId);
    }

    /**
     * Initializes a new {@link SubscriptionRemoverTask}.
     *
     * @param subscriptionSourceIds A list of identifier
     */
    public SubscriptionRemoverTask(final List<String> subscriptionSourceIds) {
        super();
        this.subscriptionSourceIds = subscriptionSourceIds;
    }

    /**
     * Initializes a new {@link SubscriptionRemoverTask}.
     *
     * @param subscriptionSourceIds A list of identifier
     */
    public SubscriptionRemoverTask(String... subscriptionSourceIds) {
        super();
        this.subscriptionSourceIds = Arrays.asList(subscriptionSourceIds);
    }

    @Override
    public TaskAttributes getAttributes() {
        return new Attributes(UpdateConcurrency.BACKGROUND);
    }

    @Override
    public String[] getDependencies() {
        return new String[0];
    }
    
    /* @formatter:off */
    private static final String SQL = "DELETE sub, conf_string, conf_bool FROM subscriptions AS sub "
                                    + "LEFT JOIN genconf_attributes_strings AS conf_string ON sub.configuration_id = conf_string.id AND sub.cid = conf_string.cid "
                                    + "LEFT JOIN genconf_attributes_bools as conf_bool ON sub.configuration_id = conf_bool.id AND sub.cid = conf_bool.cid "
                                    + "WHERE sub.source_id = ?;";
    /* @formatter:on */

    @Override
    public void perform(final PerformParameters params) throws OXException {
        Connection con = params.getConnection();
        int rollback = 0;
        PreparedStatement stmt = null;
        try {
            if (!tablesExist(con, "subscriptions", "genconf_attributes_strings", "genconf_attributes_bools")) {
                return;
            }

            con.setAutoCommit(false);
            rollback = 1;

            stmt = con.prepareStatement(SQL);
            for (String id : subscriptionSourceIds) {
                stmt.setString(1, id);
                stmt.addBatch();
            }
            int[] deleted = stmt.executeBatch();
            con.commit();
            
            Integer result = Autoboxing.I(Arrays.stream(deleted).reduce(Integer::sum).getAsInt());
            LOG.info("Removed {} invalid subscriptions.", result);
            rollback = 2;
        } catch (SQLException e) {
            throw UpdateExceptionCodes.SQL_PROBLEM.create(e, e.getMessage());
        } catch (RuntimeException e) {
            throw UpdateExceptionCodes.OTHER_PROBLEM.create(e, e.getMessage());
        } finally {
            Databases.closeSQLStuff(stmt);
            if (rollback > 0) {
                if (rollback == 1) {
                    Databases.rollback(con);
                }
                Databases.autocommit(con);
            }
        }
    }

    /* This is a SQL Test Case for the poor. Do this in an empty database to check the validity of the sql statement */
    /*
    CREATE TABLE `subscriptions` (
        `id` INT4 unsigned NOT NULL,
        `cid` INT4 unsigned NOT NULL,
        `user_id` INT4 unsigned NOT NULL,
        `configuration_id` INT4 unsigned NOT NULL,
        `source_id` varchar(255) collate utf8_unicode_ci NOT NULL,
        `folder_id` varchar(255) collate utf8_unicode_ci NOT NULL,
        `last_update` bigint(20) unsigned NOT NULL,
        `enabled` tinyint(1) NOT NULL default '1',
        `created` bigint(20) NOT NULL default '0',
        `lastModified` bigint(20) NOT NULL default '0',
        PRIMARY KEY  (`cid`,`id`)
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

      CREATE TABLE `genconf_attributes_strings` (
        `cid` INT4 unsigned NOT NULL,
        `id` INT4 unsigned NOT NULL,
        `name` varchar(100) collate utf8_unicode_ci default NULL,
        `value` varchar(256) collate utf8_unicode_ci default NULL,
        `widget` varchar(256) collate utf8_unicode_ci default NULL,
        KEY `cid` (`cid`,`id`,`name`)
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

      CREATE TABLE `genconf_attributes_bools` (
        `cid` INT4 unsigned NOT NULL,
        `id` INT4 unsigned NOT NULL,
        `name` varchar(100) collate utf8_unicode_ci default NULL,
        `value` tinyint(1) default NULL,
        `widget` varchar(256) collate utf8_unicode_ci default NULL,
        KEY `cid` (`cid`,`id`,`name`)
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

      INSERT INTO subscriptions (id, cid, user_id, source_id, configuration_id, folder_id, last_update) VALUES (1,1,1,'com.openexchange.removeMe', 2, 12, 0);
      INSERT INTO subscriptions (id, cid, user_id, source_id, configuration_id, folder_id, last_update) VALUES (2,1,1,'com.openexchange.keepMe'  , 3, 12, 0);
      INSERT INTO subscriptions (id, cid, user_id, source_id, configuration_id, folder_id, last_update) VALUES (1,2,1,'com.openexchange.keepMe'  , 2, 12, 0);
      INSERT INTO subscriptions (id, cid, user_id, source_id, configuration_id, folder_id, last_update) VALUES (2,2,1,'com.openexchange.removeMe', 3, 12, 0);

      INSERT INTO genconf_attributes_strings (id, cid, name, value) VALUES (2, 1, "action", "remove");
      INSERT INTO genconf_attributes_strings (id, cid, name, value) VALUES (3, 1, "action", "keep");
      INSERT INTO genconf_attributes_strings (id, cid, name, value) VALUES (2, 2, "action", "keep");
      INSERT INTO genconf_attributes_strings (id, cid, name, value) VALUES (3, 2, "action", "remove");

      INSERT INTO genconf_attributes_bools (id, cid, name, value) VALUES (2, 1, "action", 0);
      INSERT INTO genconf_attributes_bools (id, cid, name, value) VALUES (3, 1, "action", 1);
      INSERT INTO genconf_attributes_bools (id, cid, name, value) VALUES (2, 2, "action", 1);
      INSERT INTO genconf_attributes_bools (id, cid, name, value) VALUES (3, 2, "action", 0);

      SELECT * FROM subscriptions;
      SELECT * FROM genconf_attributes_strings;
      SELECT * FROM genconf_attributes_bools;

      DELETE subscriptions, genconf_attributes_strings, genconf_attributes_bools FROM subscriptions, genconf_attributes_strings, genconf_attributes_bools WHERE subscriptions.source_id = 'com.openexchange.removeMe' AND genconf_attributes_strings.id = subscriptions.configuration_id AND genconf_attributes_bools.id = subscriptions.configuration_id AND genconf_attributes_strings.cid = subscriptions.cid AND genconf_attributes_bools.cid = subscriptions.cid;

      SELECT * FROM subscriptions;
      SELECT * FROM genconf_attributes_strings;
      SELECT * FROM genconf_attributes_bools;

      DROP TABLE subscriptions;
      DROP TABLE genconf_attributes_strings
      DROP TABLE genconf_attributes_bools
    */


}
