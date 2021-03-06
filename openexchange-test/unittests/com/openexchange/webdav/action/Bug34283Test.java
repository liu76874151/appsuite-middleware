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

package com.openexchange.webdav.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.ByteArrayInputStream;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import com.openexchange.java.Charsets;
import com.openexchange.webdav.protocol.Protocol;
import com.openexchange.webdav.protocol.WebdavCollection;
import com.openexchange.webdav.protocol.WebdavPath;
import com.openexchange.webdav.protocol.WebdavResource;

/**
 * {@link Bug34283Test}
 *
 * @author <a href="mailto:tobias.friedrich@open-xchange.com">Tobias Friedrich</a>
 */
public class Bug34283Test extends ActionTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        /*
         * create child resources
         */
        WebdavCollection collection = factory.resolveCollection(testCollection);
        for (int i = 0; i < 30; i++) {
            byte[] bytes = UUID.randomUUID().toString().getBytes(Charsets.UTF_8);
            WebdavResource resource = collection.resolveResource(new WebdavPath("child_" + i + ".test"));
            resource.setContentType("text/html");
            resource.putBodyAndGuessLength(new ByteArrayInputStream(bytes));
            resource.create();
        }
    }

    @Test
    public void testMarshallingLimit() throws Exception {
        /*
         * prepare propfind request
         */
        MockWebdavRequest request = new MockWebdavRequest(factory, "http://localhost/");
        request.setBodyAsString("<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + "<D:propfind xmlns:D=\"DAV:\"><D:prop><D:displayname/></D:prop></D:propfind>");
        request.setHeader("depth", "1");
        request.setUrl(testCollection);
        MockWebdavResponse response = new MockWebdavResponse();
        Protocol protocol = new Protocol() {

            @Override
            public int getRecursiveMarshallingLimit() {
                return 25;
            }
        };
        new WebdavPropfindAction(protocol).perform(request, response);
        /*
         * check response for HTTP 507
         */
        assertEquals("Wrong response status", Protocol.SC_MULTISTATUS, response.getStatus());
        assertTrue("No HTTP 507 in response", response.getResponseBodyAsString().contains("HTTP/1.1 507"));
    }

}
