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
 *     Copyright (C) 2018-2020 OX Software GmbH
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

package com.openexchange.rest.client.v2.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.openexchange.exception.OXException;
import com.openexchange.java.Streams;
import com.openexchange.rest.client.exception.RESTExceptionCodes;
import com.openexchange.rest.client.v2.RESTResponse;

/**
 * {@link JsonRESTResponseBodyParser}
 *
 * @author <a href="mailto:ioannis.chouklis@open-xchange.com">Ioannis Chouklis</a>
 * @since v7.10.1
 */
public class JsonRESTResponseBodyParser implements RESTResponseBodyParser {

    private static final String CHARSET = "UTF-8";
    private final Set<String> contentTypes;

    /**
     * Initialises a new {@link JsonRESTResponseBodyParser}.
     */
    public JsonRESTResponseBodyParser() {
        super();
        contentTypes = Collections.singleton("application/json");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.openexchange.rest.client.v2.parser.RESTResponseBodyParser#parse(org.apache.http.HttpResponse, com.openexchange.rest.client.v2.RESTResponse)
     */
    @Override
    public void parse(HttpResponse httpResponse, RESTResponse restResponse) throws OXException {
        try (InputStream inputStream = Streams.bufferedInputStreamFor(httpResponse.getEntity().getContent())) {
            String string = Streams.stream2string(inputStream, CHARSET);
            char c = string.charAt(0);
            switch (c) {
                case '{':
                    restResponse.setResponseBody(new JSONObject(string));
                    return;
                case '[':
                    restResponse.setResponseBody(new JSONArray(string));
                    return;
                default:
                    throw RESTExceptionCodes.JSON_ERROR.create("Unexpected start token detected '" + c + "'");
            }
        } catch (IOException e) {
            throw RESTExceptionCodes.IO_ERROR.create(e, e.getMessage());
        } catch (JSONException e) {
            throw RESTExceptionCodes.JSON_ERROR.create(e, e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.openexchange.rest.client.RESTResponseBodyParser#getContentTypes()
     */
    @Override
    public Set<String> getContentTypes() {
        return contentTypes;
    }
}
