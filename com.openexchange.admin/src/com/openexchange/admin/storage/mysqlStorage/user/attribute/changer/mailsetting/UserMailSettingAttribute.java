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

package com.openexchange.admin.storage.mysqlStorage.user.attribute.changer.mailsetting;

import com.openexchange.admin.storage.mysqlStorage.user.attribute.changer.Attribute;

/**
 * {@link UserMailSettingAttribute}
 *
 * @author <a href="mailto:ioannis.chouklis@open-xchange.com">Ioannis Chouklis</a>
 * @since v7.10.1
 */
public enum UserMailSettingAttribute implements Attribute {

    BITS("permission-bits", "bits", Integer.class),
    SEND_ADDRESS("default-sender-address", "send_addr", String.class),
    STD_DRAFTS("standard-drafts-folder-name", "std_drafts", String.class),
    STD_SENT("standard-sent-folder-name", "std_sent", String.class),
    STD_SPAM("standard-spam-folder-name", "std_spam", String.class),
    STD_TRASH("standard-drafts-folder-name", "std_trash", String.class),
    CONFIRMED_SPAM("standard-spam-folder-name", "confirmed_spam", String.class),
    CONFIRMED_HAM("standard-ham-folder-name", "confirmed_ham", String.class),
    UPLOAD_QUOTA("total-upload-file-size-limit", "upload_quota", Integer.class),
    UPLOAD_QUOTA_PER_FILE("upload-file-size-limit-per-file", "upload_quota_per_file", Integer.class),
    ;

    private final String sqlFieldName;
    private final Class<?> originalType;
    private static final String TABLE_NAME = "user_setting_mail";
    private final String attributeName;

    /**
     * 
     * Initialises a new {@link UserMailSettingAttribute}.
     * 
     * @param sqlFieldNames the names of the attribute
     */
    private UserMailSettingAttribute(String attributeName, String sqlFieldName, Class<?> originalType) {
        this.attributeName = attributeName;
        this.sqlFieldName = sqlFieldName;
        this.originalType = originalType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.openexchange.admin.storage.mysqlStorage.user.attribute.changer.Attribute#getSQLFieldName()
     */
    @Override
    public String getSQLFieldName() {
        return sqlFieldName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.openexchange.admin.storage.mysqlStorage.user.attribute.changer.Attribute#getSQLTableName()
     */
    @Override
    public String getSQLTableName() {
        return TABLE_NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.openexchange.admin.storage.mysqlStorage.user.attribute.changer.Attribute#getOriginalType()
     */
    @Override
    public Class<?> getOriginalType() {
        return originalType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.openexchange.admin.storage.mysqlStorage.user.attribute.changer.Attribute#getName()
     */
    @Override
    public String getName() {
        return attributeName;
    }
}
