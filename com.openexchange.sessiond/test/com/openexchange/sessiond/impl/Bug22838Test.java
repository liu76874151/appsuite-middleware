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

package com.openexchange.sessiond.impl;

import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.osgi.service.event.EventAdmin;
import com.openexchange.config.SimConfigurationService;
import com.openexchange.server.SimpleServiceLookup;
import com.openexchange.session.Session;
import com.openexchange.sessiond.impl.usertype.UserTypeSessiondConfigInterface;
import com.openexchange.sessiond.impl.usertype.UserTypeSessiondConfigRegistry;
import com.openexchange.sessiond.impl.usertype.UserTypeSessiondConfigRegistry.UserType;
import com.openexchange.sessionstorage.SessionStorageService;

/**
 * {@link Bug22838Test}
 *
 * @author <a href="mailto:jan.bauerdick@open-xchange.com">Jan Bauerdick</a>
 */
public class Bug22838Test {

    @Mock
    private UserTypeSessiondConfigRegistry registry;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        SimpleServiceLookup serviceLookup = new SimpleServiceLookup();
        SessionStorageService sessionStorageService = Mockito.mock(SessionStorageService.class);
        serviceLookup.add(SessionStorageService.class, sessionStorageService);
        EventAdmin eventAdmin = Mockito.mock(EventAdmin.class);
        serviceLookup.add(EventAdmin.class, eventAdmin);
        com.openexchange.sessiond.osgi.Services.setServiceLookup(serviceLookup);

        UserTypeSessiondConfigInterface sessiondConfigInterface = new UserTypeSessiondConfigInterface() {

            @Override
            public UserType getUserType() {
                return UserType.USER;
            }

            @Override
            public int getMaxSessionsPerUserType() {
                return 0;
            }
        };

        Mockito.when(registry.getConfigFor(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(sessiondConfigInterface);

        SessionHandler.init(new SessiondConfigImpl(new SimConfigurationService()), registry);
    }

    @After
    public void tearDown() throws Exception {
        SessionHandler.close();
    }

    @Test
    public void testMergeEmptyArrayWithNull() throws Exception {
        Session[] retval = SessionHandler.removeUserSessions(0, 0);
        assertEquals("Array length not 0", 0, retval.length);
    }

}
