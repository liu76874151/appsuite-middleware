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
 *     Copyright (C) 2016-2020 OX Software GmbH.
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

package com.openexchange.mailfilter.json.ajax.json.mapper.parser.test;

import java.util.ArrayList;
import java.util.List;
import org.apache.jsieve.SieveException;
import org.json.JSONException;
import org.json.JSONObject;
import com.openexchange.exception.OXException;
import com.openexchange.jsieve.commands.TestCommand;
import com.openexchange.jsieve.commands.TestCommand.Commands;
import com.openexchange.mailfilter.json.ajax.json.mapper.parser.CommandParser;
import com.openexchange.tools.session.ServerSession;

/**
 * {@link DateTestCommandParser} parses date sieve tests.
 *
 * @author <a href="mailto:kevin.ruthmann@open-xchange.com">Kevin Ruthmann</a>
 * @author <a href="mailto:ioannis.chouklis@open-xchange.com">Ioannis Chouklis</a>
 * @since v7.8.4
 */
public class DateTestCommandParser extends AbstractDateTestCommandParser implements CommandParser<TestCommand> {

    /**
     * Initialises a new {@link DateTestCommandParser}.
     */
    public DateTestCommandParser() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.openexchange.mailfilter.json.ajax.json.mapper.parser.CommandParser#parse(org.json.JSONObject, com.openexchange.tools.session.ServerSession)
     */
    @Override
    public TestCommand parse(JSONObject jsonObject, ServerSession session) throws JSONException, SieveException, OXException {
        String commandName = Commands.DATE.getCommandName();
        final List<Object> argList = new ArrayList<Object>();

        parseZone(argList, jsonObject, session, commandName);
        parseComparisonTag(argList, jsonObject, commandName);
        parseHeader(argList, jsonObject, commandName);
        parseDatePart(argList, jsonObject, commandName);

        return new TestCommand(TestCommand.Commands.DATE, argList, new ArrayList<TestCommand>());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.openexchange.mailfilter.json.ajax.json.mapper.parser.CommandParser#parse(org.json.JSONObject, java.lang.Object)
     */
    @Override
    public void parse(JSONObject jsonObject, TestCommand command) throws JSONException, OXException {
        parseZone(jsonObject, command);
        parseComparisonTag(jsonObject, command);
        parseHeader(jsonObject, command);
        parseDatePart(jsonObject, command);
    }
}
