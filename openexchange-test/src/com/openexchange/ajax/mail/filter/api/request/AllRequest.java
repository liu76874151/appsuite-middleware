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

package com.openexchange.ajax.mail.filter.api.request;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import com.openexchange.ajax.AJAXServlet;
import com.openexchange.ajax.mail.filter.api.parser.AllParser;
import com.openexchange.ajax.mail.filter.api.response.AllResponse;
import com.openexchange.groupware.tasks.Task;

/**
 * {@link AllRequest}. Contains the data for a mail filter all request.
 *
 * @author <a href="mailto:sebastian.kauss@open-xchange.org">Sebastian Kauss</a>
 * @author <a href="mailto:ioannis.chouklis@open-xchange.com">Ioannis Chouklis</a>
 */
public class AllRequest extends AbstractMailFilterRequest<AllResponse> {

    public static final int[] GUI_COLUMNS = new int[] { Task.OBJECT_ID };

    @SuppressWarnings("hiding")
    private final boolean failOnError;

    private String userName = null;

    /**
     * Default constructor.
     */
    public AllRequest() {
        this(true);
    }

    /**
     * Default constructor.
     */
    public AllRequest(final boolean failOnError) {
        super();
        this.failOnError = failOnError;
    }

    public AllRequest(final String userName, final boolean failOnError) {
        super();
        this.userName = userName;
        this.failOnError = failOnError;
    }

    public AllRequest(final String userName) {
        this(userName, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getBody() throws JSONException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Method getMethod() {
        return Method.GET;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Parameter[] getParameters() {
        final List<Parameter> params = new ArrayList<Parameter>();
        params.add(new Parameter(AJAXServlet.PARAMETER_ACTION, AJAXServlet.ACTION_LIST));
        if (userName != null) {
            params.add(new Parameter("username", userName));
        }
        return params.toArray(new Parameter[params.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AllParser getParser() {
        return new AllParser(failOnError);
    }
}
