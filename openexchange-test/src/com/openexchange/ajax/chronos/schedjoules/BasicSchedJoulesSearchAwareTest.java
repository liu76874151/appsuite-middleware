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

package com.openexchange.ajax.chronos.schedjoules;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.sql.Date;
import org.junit.Test;
import com.openexchange.ajax.chronos.factory.FindFactory;
import com.openexchange.testing.httpclient.models.FindQueryBody;
import com.openexchange.testing.httpclient.models.FindQueryResponse;
import com.openexchange.testing.httpclient.models.FindQueryResponseData;
import com.openexchange.testing.httpclient.models.FolderData;
import com.openexchange.testing.httpclient.modules.FindApi;

/**
 * {@link BasicSchedJoulesSearchAwareTest}
 *
 * @author <a href="mailto:ioannis.chouklis@open-xchange.com">Ioannis Chouklis</a>
 */
public class BasicSchedJoulesSearchAwareTest extends AbstractSchedJoulesProviderTest {

    private static final String DEFAULT_COLUMNS = "1,2,20,101,200,201,202,206,207,209,212,213,214,215,216,220,221,222,224,227,400,401,402";

    /**
     * Initialises a new {@link BasicSchedJoulesSearchAwareTest}.
     *
     * @param providerId
     */
    public BasicSchedJoulesSearchAwareTest() {
        super();
    }

    /**
     * Tests a simple search with default fields
     */
    @Test
    public void testSimpleSearch() throws Exception {
        FolderData folderData = createFolder(CALENDAR_ONE, "testSimpleSearch");
        eventManager.getAllEvents(new Date(0), new Date(1516370400), false, folderData.getId());

        FindQueryBody queryBody = FindFactory.createFindBody("Full", folderData.getId());

        FindApi findApi = defaultUserApi.getFindApi();
        FindQueryResponse response = findApi.doQuery(defaultUserApi.getSession(), "calendar", queryBody, DEFAULT_COLUMNS, null);
        assertNull(response.getErrorDesc(), response.getError());
        FindQueryResponseData responseData = response.getData();
        assertNotNull(responseData);
        assertTrue("No results found", responseData.getSize().intValue() > 0);
    }

    /**
     * Tests a simple search with defined fields
     */
    @Test
    public void testSearchWithFields() throws Exception {
        FolderData folderData = createFolder(CALENDAR_ONE, "testSearchWithFields");
        eventManager.getAllEvents(new Date(0), new Date(1516370400), false, folderData.getId());

        FindQueryBody queryBody = FindFactory.createFindBody("Full", folderData.getId());

        FindApi findApi = defaultUserApi.getFindApi();
        FindQueryResponse response = findApi.doQuery(defaultUserApi.getSession(), "calendar", queryBody, "400", null);
        assertNull(response.getErrorDesc(), response.getError());
        FindQueryResponseData responseData = response.getData();
        assertNotNull(responseData);
        assertTrue("No results found", responseData.getSize().intValue() > 0);
    }
}
