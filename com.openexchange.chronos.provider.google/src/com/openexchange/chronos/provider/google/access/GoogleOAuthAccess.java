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

package com.openexchange.chronos.provider.google.access;

import java.io.IOException;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpResponseException;
import com.google.api.services.calendar.Calendar;
import com.openexchange.chronos.exception.CalendarExceptionCodes;
import com.openexchange.chronos.provider.google.osgi.Services;
import com.openexchange.exception.OXException;
import com.openexchange.google.api.client.GoogleApiClients;
import com.openexchange.oauth.OAuthAccount;
import com.openexchange.oauth.OAuthService;
import com.openexchange.oauth.access.AbstractOAuthAccess;
import com.openexchange.oauth.access.OAuthAccess;
import com.openexchange.oauth.access.OAuthClient;
import com.openexchange.oauth.scope.OXScope;
import com.openexchange.session.Session;

/**
 *
 * {@link GoogleOAuthAccess}
 *
 * @author <a href="mailto:kevin.ruthmann@open-xchange.com">Kevin Ruthmann</a>
 * @since v7.10.0
 */
//FIXME: Maybe consolidate with GoogleDriveOAuthAccess
public class GoogleOAuthAccess extends AbstractOAuthAccess {

    private final int accountId;

    /**
     * Initializes a new {@link GoogleOAuthAccess}.
     */
    public GoogleOAuthAccess(int accountId, Session session) {
        super(session);
        this.accountId = accountId;
    }

    @Override
    public void initialize() throws OXException {
        synchronized (this) {
            // Grab Google OAuth account
            int oauthAccountId = getAccountId();
            OAuthAccount oauthAccount = GoogleApiClients.getGoogleAccount(oauthAccountId, getSession(), false);
            setOAuthAccount(oauthAccount);

            {
                OAuthAccount newAccount = GoogleApiClients.ensureNonExpiredGoogleAccount(oauthAccount, getSession());
                if (null != newAccount) {
                    oauthAccount = newAccount;
                    setOAuthAccount(newAccount);
                }
            }
            verifyAccount(oauthAccount, Services.getService(OAuthService.class), OXScope.calendar_ro);

            // Generate appropriate credentials for it
            GoogleCredential credentials = GoogleApiClients.getCredentials(oauthAccount, getSession());

            // Establish Calendar instance
            setOAuthClient(new OAuthClient<Calendar>(new Calendar.Builder(credentials.getTransport(), credentials.getJsonFactory(), credentials).setApplicationName(GoogleApiClients.getGoogleProductName(getSession())).build(), getOAuthAccount().getToken()));
        }
    }

    @Override
    public boolean ping() throws OXException {
        Calendar client = this.<Calendar> getClient().client;
        try {
            client.calendarList().list().execute();
            return true;
        } catch (final HttpResponseException e) {
            if (401 == e.getStatusCode() || 403 == e.getStatusCode()) {
                return false;
            }
            throw CalendarExceptionCodes.UNEXPECTED_ERROR.create(e.getMessage());
        } catch (final IOException e) {
            throw CalendarExceptionCodes.IO_ERROR.create(e, e.getMessage());
        } catch (final RuntimeException e) {
            throw CalendarExceptionCodes.UNEXPECTED_ERROR.create(e.getMessage());
        }
    }

    @Override
    public int getAccountId() throws OXException {
      return accountId;
    }

    @Override
    public OAuthAccess ensureNotExpired() throws OXException {
        if (isExpired()) {
            synchronized (this) {
                if (isExpired()) {
                    initialize();
                }
            }
        }
        return this;
    }
}
