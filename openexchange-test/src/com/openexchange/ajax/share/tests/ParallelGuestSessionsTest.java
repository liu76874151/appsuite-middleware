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

package com.openexchange.ajax.share.tests;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import com.openexchange.ajax.folder.actions.EnumAPI;
import com.openexchange.ajax.folder.actions.OCLGuestPermission;
import com.openexchange.ajax.folder.actions.UpdateRequest;
import com.openexchange.ajax.framework.AJAXSession;
import com.openexchange.ajax.share.GuestClient;
import com.openexchange.ajax.share.ShareTest;
import com.openexchange.ajax.share.actions.ExtendedPermissionEntity;
import com.openexchange.groupware.container.FolderObject;
import com.openexchange.server.impl.OCLPermission;
import com.openexchange.share.notification.ShareNotificationService.Transport;
import com.openexchange.share.recipient.RecipientType;

/**
 * {@link ParallelGuestSessionsTest}
 *
 * @author <a href="mailto:tobias.friedrich@open-xchange.com">Tobias Friedrich</a>
 */
public class ParallelGuestSessionsTest extends ShareTest {

    @Test
    public void testParallelInvitedGuestSessions() throws Exception {
        int module = randomModule();
        testParallelGuestSessions(randomFolderAPI(), module, getDefaultFolder(module), randomGuestPermission(RecipientType.GUEST, module));
    }

    @Test
    public void testParallelAnonymousGuestSessions() throws Exception {
        int module = randomModule();
        testParallelGuestSessions(randomFolderAPI(), module, getDefaultFolder(module), randomGuestPermission(RecipientType.ANONYMOUS, module));
    }

    private void testParallelGuestSessions(EnumAPI api, int module, int parent, OCLGuestPermission guestPermission) throws Exception {
        /*
         * create private folder
         */
        FolderObject folder = insertPrivateFolder(api, module, parent);
        /*
         * update folder, add permission for guest
         */
        folder.addPermission(guestPermission);
        folder = updateFolder(api, folder, new RequestCustomizer<UpdateRequest>() {

            @Override
            public void customize(UpdateRequest request) {
                request.setCascadePermissions(false);
                request.setNotifyPermissionEntities(Transport.MAIL);
            }
        });
        /*
         * /*
         * check permissions
         */
        OCLPermission matchingPermission = null;
        for (OCLPermission permission : folder.getPermissions()) {
            if (permission.getEntity() != getClient().getValues().getUserId()) {
                matchingPermission = permission;
                break;
            }
        }
        assertNotNull("No matching permission in created folder found", matchingPermission);
        checkPermissions(guestPermission, matchingPermission);
        /*
         * discover & check guest
         */
        ExtendedPermissionEntity guest = discoverGuestEntity(api, module, folder.getObjectID(), matchingPermission.getEntity());
        checkGuestPermission(guestPermission, guest);
        /*
         * check access to share, using the same ajax session as the sharing user
         */
        String shareURL = discoverShareURL(guest);
        assertNotNull("ShareURL couldn't be discovered.", shareURL);
        AJAXSession sharedSession = getSession();
        String oldSessionID = sharedSession.getId();
        try {
            sharedSession.setId(null);
            GuestClient guestClient = new GuestClient(sharedSession, shareURL, guestPermission.getRecipient(), true, false);
            guestClient.checkShareModuleAvailable();
            guestClient.checkShareAccessible(guestPermission);
        } finally {
            // restore sharing user's session ID for teardown
            sharedSession.setId(oldSessionID);
        }
        /*
         * re-discover guest, using the sharing user's session, thus verifying the old session is still alive, too
         */
        guest = discoverGuestEntity(api, module, folder.getObjectID(), matchingPermission.getEntity());
        checkGuestPermission(guestPermission, guest);
    }

}
