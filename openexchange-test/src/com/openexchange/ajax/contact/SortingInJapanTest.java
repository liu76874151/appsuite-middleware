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

package com.openexchange.ajax.contact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.openexchange.ajax.config.actions.GetRequest;
import com.openexchange.ajax.config.actions.SetRequest;
import com.openexchange.ajax.config.actions.Tree;
import com.openexchange.groupware.container.Contact;
import com.openexchange.java.Strings;

public class SortingInJapanTest extends AbstractManagedContactTest {

    private String originalLocale;

    public SortingInJapanTest() {
        super();
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        originalLocale = getClient().execute(new GetRequest(Tree.Language)).getString();
        if (Strings.isEmpty(originalLocale)) {
            fail("no locale found");
        }
        getClient().execute(new SetRequest(Tree.Language, "ja-JP"));
    }

    @Override
    @After
    public void tearDown() throws Exception {
        try {
            if (null != originalLocale) {
                getClient().execute(new SetRequest(Tree.Language, originalLocale));
            }
        } finally {
            super.tearDown();
        }
    }

    @Test
    public void testCustomSortingForJapan() {
        /*
         * generate test contacts on server
         */
        Contact[] orderedContacts = new Contact[] { generateContact("\u30a1"), generateContact("\u30a3"), generateContact("\u30a6"), generateContact("\u30ac"), generateContact("#*+$&& ASCII Art"), generateContact("012345"), generateContact("AAAAA"), generateContact("Hans Dampf"), generateContact("Max Mustermann"),
        };
        List<Contact> unorderedContacts = new ArrayList<Contact>(Arrays.asList(orderedContacts));
        Collections.shuffle(unorderedContacts);
        cotm.newActionMultiple(unorderedContacts.toArray(new Contact[unorderedContacts.size()]));
        /*
         * get all contacts
         */
        Contact[] receivedContacts = cotm.allAction(folderID);
        assertNotNull("no contacts received", receivedContacts);
        assertEquals("wrong number of contacts received", orderedContacts.length, receivedContacts.length);
        /*
         * check sort order
         */
        for (int i = 0; i < receivedContacts.length; i++) {
            assertEquals("contact order wrong", orderedContacts[i].getSurName(), receivedContacts[i].getSurName());
        }
    }

}
