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

package com.openexchange.groupware.settings.impl;

import com.openexchange.exception.OXException;
import com.openexchange.groupware.contexts.Context;
import com.openexchange.groupware.ldap.User;
import com.openexchange.groupware.ldap.UserImpl;
import com.openexchange.groupware.ldap.UserStorage;
import com.openexchange.groupware.settings.IValueHandler;
import com.openexchange.groupware.settings.Setting;
import com.openexchange.server.ServiceExceptionCode;
import com.openexchange.server.services.ServerServiceRegistry;
import com.openexchange.session.Session;
import com.openexchange.user.UserService;

/**
 * This class contains the shared functions for all user settings.
 */
public abstract class AbstractUserFuncs implements IValueHandler {

    /**
     * Initializes a new {@link AbstractUserFuncs}.
     */
    protected AbstractUserFuncs() {
        super();
    }

    @Override
    public void writeValue(final Session session, final Context ctx, final User user, final Setting setting) throws OXException {
        /*
         * write to user storage
         */
        UserImpl newUser = new UserImpl(user);
        setValue(newUser, setting.getSingleValue().toString(), user);
        UserService userService = ServerServiceRegistry.getInstance().getService(UserService.class);
        if (null != userService) {
            userService.updateUser(newUser, ctx);
        } else {
            org.slf4j.LoggerFactory.getLogger(AbstractUserFuncs.class).warn(
                "Unable to access user service, updating directly via storage.", ServiceExceptionCode.absentService(UserService.class));
            UserStorage.getInstance().updateUser(newUser, ctx);
        }
        /*
         * try and set value in passed reference, too
         */
        if (UserImpl.class.isInstance(user)) {
            setValue((UserImpl)user, setting.getSingleValue().toString(), user);
        }
    }

    @Override
    public int getId() {
        return -1;
    }

    /**
     * Sets the value in passed <tt>newUser</tt>.
     *
     * @param newUser In this user object the value should be set.
     * @param value The value to set.
     * @param originalUser The original user fetched from storage
     * @throws OXException If writing of the value fails.
     */
    @SuppressWarnings("unused")
    protected void setValue(UserImpl newUser, String value, User originalUser) throws OXException {
        // Can be overwritten.
    }
}
