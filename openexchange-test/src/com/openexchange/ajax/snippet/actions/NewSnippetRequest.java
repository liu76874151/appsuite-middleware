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

package com.openexchange.ajax.snippet.actions;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.openexchange.ajax.AJAXServlet;
import com.openexchange.ajax.framework.AbstractAJAXParser;

/**
 * {@link NewSnippetRequest}
 *
 * @author <a href="mailto:ioannis.chouklis@open-xchange.com">Ioannis Chouklis</a>
 */
public class NewSnippetRequest extends AbstractSnippetRequest<NewSnippetResponse> {

    private JSONObject body;

    /**
     * Initializes a new {@link NewSnippetRequest}.
     * 
     * @param failOnError
     */
    protected NewSnippetRequest(boolean failOnError) {
        super(failOnError);
    }

    /**
     * Initializes a new {@link NewSnippetRequest}.
     * 
     * @param body
     * @param failOnError
     */
    public NewSnippetRequest(final JSONObject body, final boolean failOnError) {
        super(failOnError);
        this.body = body;
    }

    public NewSnippetRequest(final JSONObject body) {
        this(body, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.openexchange.ajax.framework.AJAXRequest#getMethod()
     */
    @Override
    public Method getMethod() {
        return Method.PUT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.openexchange.ajax.framework.AJAXRequest#getParameters()
     */
    @Override
    public Parameter[] getParameters() throws IOException, JSONException {
        List<Parameter> params = new LinkedList<Parameter>();
        params.add(new Parameter(AJAXServlet.PARAMETER_ACTION, AJAXServlet.ACTION_NEW));
        return params.toArray(new Parameter[] {});
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.openexchange.ajax.framework.AJAXRequest#getParser()
     */
    @Override
    public AbstractAJAXParser<? extends NewSnippetResponse> getParser() {
        return new NewSnippetParser(failOnError);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.openexchange.ajax.framework.AJAXRequest#getBody()
     */
    @Override
    public Object getBody() throws IOException, JSONException {
        return body;
    }

}
