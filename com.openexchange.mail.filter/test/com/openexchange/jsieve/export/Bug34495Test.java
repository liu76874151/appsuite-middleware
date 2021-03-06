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
 *    trademarks of the OX Software GmbH. group of companies.
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

package com.openexchange.jsieve.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.StringTokenizer;
import org.junit.Before;
import org.junit.Test;
import com.openexchange.jsieve.export.exceptions.OXSieveHandlerException;


/**
 * {@link Bug34495Test}
 *
 * @author <a href="mailto:steffen.templin@open-xchange.com">Steffen Templin</a>
 */
public class Bug34495Test extends SieveHandler {


    private static final String ERROR_MSG_1 = "\"Zeile 7: Fehlender String: Hier muss entweder \\\"text:\\\" oder `\\\"` folgen\"";

    private static final String ERROR_MSG_2 = "\"Cited from RFC 5804: \\\"Client implementations should note that this may be a\r\n" +
                                              "multiline literal string with more than one error message separated\r\n" +
                                              "by CRLFs.\\\"\"";

    private static final String DELIMS = "\"\\\r\n ";

    public Bug34495Test() {
        super(null, null, null, null, 0, null, null, -1, -1);
    }


    @Before
    public void setUp() {
        AUTH = true;
        bos_sieve = new BufferedOutputStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
            }
        });
    }

     @Test
     public void testParseMessageWithQuotes() throws Exception {
        testParseMessage(ERROR_MSG_1);
    }

     @Test
     public void testParseMessageWithLineBreaks() throws Exception {
        testParseMessage(ERROR_MSG_2);
    }

    private void testParseMessage(String msg) throws Exception {
        bis_sieve = new BufferedReader(new StringReader("NO " + msg));
        boolean errorThrown = false;
        try {
            setScript("test", new byte[1], new StringBuilder());
        } catch (OXSieveHandlerException e) {
            errorThrown = true;
//          System.out.println(e.getMessage());
          int c1 = new StringTokenizer(msg, DELIMS, false).countTokens();
          int c2 = new StringTokenizer(e.getMessage(), DELIMS, false).countTokens();
          assertTrue(c1 > 1);
          assertEquals(c1, c2);
        }

        assertTrue(errorThrown);
    }

}
