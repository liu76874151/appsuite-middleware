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

package com.openexchange.mailaccount;

import static com.openexchange.mailaccount.Constants.MAIL_PROTOCOL_GUARD_GUEST;
import java.util.Map;
import com.openexchange.session.Session;


/**
 * {@link MailAccounts} - Utility class for mail account.
 *
 * @author <a href="mailto:thorben.betten@open-xchange.com">Thorben Betten</a>
 * @since v7.6.1
 */
public final class MailAccounts {

    /**
     * Initializes a new {@link MailAccounts}.
     */
    private MailAccounts() {
        super();
    }

    /**
     * Gets the transport authentication information from given mail account.
     *
     * @param mailAccount The mail account
     * @param fallback The fall-back value
     * @return The transport authentication information or <code>fallback</code>
     */
    public static TransportAuth getTransportAuthFrom(MailAccount mailAccount, TransportAuth fallback) {
        if (null == mailAccount) {
            return fallback;
        }
        Map<String, String> properties = mailAccount.getProperties();
        if (null == properties) {
            return fallback;
        }
        TransportAuth transportAuth = TransportAuth.transportAuthFor(properties.get("transport.auth"));
        return null == transportAuth ? fallback : transportAuth;
    }

    /**
     * Checks for a guest session
     *
     * @param session The session to check
     * @return <code>true</code> for a guest session; otherwise <code>false</code>
     */
    public static boolean isGuest(Session session) {
        return null != session && Boolean.TRUE.equals(session.getParameter(Session.PARAM_GUEST));
    }

    /**
     * Checks if account is a guest account
     *
     * @param account The mail account to check
     * @return <code>true</code> for a guest account; otherwise <code>false</code>
     */
    public static boolean isGuestAccount(MailAccount account) {
        return null == account ? false : MAIL_PROTOCOL_GUARD_GUEST.equals(account.getMailProtocol());
    }

    /**
     * Checks if account is a guest account
     *
     * @param account The account to check
     * @return <code>true</code> for a guest account; otherwise <code>false</code>
     */
    public static boolean isGuestAccount(Account account) {
        return null == account ? false : Constants.NAME_GUARD_GUEST.equals(account.getName());
    }

}
