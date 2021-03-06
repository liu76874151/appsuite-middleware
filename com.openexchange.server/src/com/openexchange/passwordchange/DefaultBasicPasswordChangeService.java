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

package com.openexchange.passwordchange;

import com.openexchange.exception.OXException;
import com.openexchange.groupware.contexts.Context;
import com.openexchange.groupware.ldap.User;
import com.openexchange.groupware.ldap.UserImpl;
import com.openexchange.guest.GuestService;
import com.openexchange.java.Strings;
import com.openexchange.password.mechanism.PasswordDetails;
import com.openexchange.password.mechanism.PasswordMech;
import com.openexchange.password.mechanism.PasswordMechRegistry;
import com.openexchange.password.mechanism.stock.StockPasswordMechs;
import com.openexchange.server.ServiceExceptionCode;
import com.openexchange.server.services.ServerServiceRegistry;
import com.openexchange.user.UserService;

/**
 * {@link DefaultBasicPasswordChangeService}
 *
 * @author <a href="mailto:thorben.betten@open-xchange.com">Thorben Betten</a>
 * @since v7.8.0
 */
public class DefaultBasicPasswordChangeService extends BasicPasswordChangeService {

    /**
     * Initializes a new {@link DefaultBasicPasswordChangeService}.
     *
     * @param services
     */
    public DefaultBasicPasswordChangeService() {
        super();
    }

    @Override
    protected void update(PasswordChangeEvent event) throws OXException {

        Context ctx = event.getContext();

        UserService userService = ServerServiceRegistry.getInstance().getService(UserService.class);
        if (userService == null) {
            throw ServiceExceptionCode.SERVICE_UNAVAILABLE.create(UserService.class.getName());
        }
        User user = userService.getUser(event.getSession().getUserId(), ctx);
        UserImpl updatedUser = new UserImpl(user);

        prepareUserUpdate(event, user, updatedUser);

        userService.updatePassword(updatedUser, ctx);
        userService.invalidateUser(ctx, event.getSession().getUserId());

        if (updatedUser.isGuest()) {
            GuestService guestService = ServerServiceRegistry.getInstance().getService(GuestService.class);
            if (guestService != null) {
                guestService.updateGuestUser(updatedUser, ctx.getContextId());
            }
        }
    }

    /**
     * @param event
     * @param user
     * @param updatedUser
     * @throws OXException
     */
    protected void prepareUserUpdate(PasswordChangeEvent event, User user, UserImpl updatedUser) throws OXException {
        if (Strings.isEmpty(event.getNewPassword())) {
            updatedUser.setUserPassword(null);
            updatedUser.setSalt(null);
        } else {
            PasswordMechRegistry passwordMechRegistry = ServerServiceRegistry.getInstance().getService(PasswordMechRegistry.class);
            PasswordMech passwordMech = passwordMechRegistry.get(user.getPasswordMech());
            if (passwordMech == null) {
                passwordMech = StockPasswordMechs.BCRYPT.getPasswordMech();
            }
            PasswordDetails passwordDetails = passwordMech.encode(event.getNewPassword());
            updatedUser.setPasswordMech(passwordDetails.getPasswordMech());
            updatedUser.setUserPassword(passwordDetails.getEncodedPassword());
            updatedUser.setSalt(passwordDetails.getSalt());
        }
    }
}
