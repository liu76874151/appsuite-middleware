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

package com.openexchange.caldav.reports;

import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import com.openexchange.caldav.CaldavProtocol;
import com.openexchange.caldav.GroupwareCaldavFactory;
import com.openexchange.dav.DAVProtocol;
import com.openexchange.dav.actions.PROPFINDAction;
import com.openexchange.webdav.action.WebdavRequest;
import com.openexchange.webdav.action.WebdavResponse;
import com.openexchange.webdav.protocol.Multistatus;
import com.openexchange.webdav.protocol.Protocol;
import com.openexchange.webdav.protocol.WebdavFactory;
import com.openexchange.webdav.protocol.WebdavPath;
import com.openexchange.webdav.protocol.WebdavProtocolException;
import com.openexchange.webdav.protocol.WebdavResource;
import com.openexchange.webdav.protocol.WebdavStatus;
import com.openexchange.webdav.xml.resources.PropertiesMarshaller;
import com.openexchange.webdav.xml.resources.ResourceMarshaller;

/**
 * A {@link CaldavMultigetReport} allows clients to retrieve properties of certain named resources. It is conceptually similar to a propfind.
 *
 * @author <a href="mailto:francisco.laguna@open-xchange.com">Francisco Laguna</a>
 */
public class CaldavMultigetReport extends PROPFINDAction {

    public static final String NAMESPACE = CaldavProtocol.CAL_NS.getURI();
    public static final String NAME = "calendar-multiget";

    /**
     * Initializes a new {@link CaldavMultigetReport}.
     *
     * @param protocol The protocol
     */
    public CaldavMultigetReport(DAVProtocol protocol) {
        super(protocol);
    }

    @Override
    public void perform(WebdavRequest request, WebdavResponse response) throws WebdavProtocolException {
        /*
         * get paths of requested resources
         */
        Document requestBody = requireRequestBody(request);
        List<WebdavPath> paths = getHrefPaths(request, requestBody.getRootElement());
        /*
         * resolve & marshal requested resources
         */
        ResourceMarshaller marshaller = getMarshaller(request, requireRequestBody(request));
        PropertiesMarshaller helper = new PropertiesMarshaller(request.getURLPrefix(), request.getCharset());
        Element multistatusElement = prepareMultistatusElement();
        WebdavFactory factory = request.getFactory();
        if (GroupwareCaldavFactory.class.isInstance(factory)) {
            /*
             * batch-resolve requested resources
             */
            Multistatus<WebdavResource> multistatus = ((GroupwareCaldavFactory) factory).resolveResources(paths);
            for (int statusCode : multistatus.getStatusCodes()) {
                for (WebdavStatus<WebdavResource> status : multistatus.toIterable(statusCode)) {
                    if (null != status.getAdditional()) {
                        multistatusElement.addContent(marshaller.marshal(status.getAdditional()));
                    } else {
                        multistatusElement.addContent(new Element("response", Protocol.DAV_NS)
                            .addContent(helper.marshalHREF(status.getUrl(), false))
                            .addContent(helper.marshalStatus(status.getStatus()))
                        );
                    }
                }
            }
        } else {
            /*
             * resolve each requested resource individually
             */
            for (WebdavPath path : paths) {
                try {
                    WebdavResource resource = request.getFactory().resolveResource(path);
                    multistatusElement.addContent(marshaller.marshal(resource));
                } catch (WebdavProtocolException e) {
                    multistatusElement.addContent(new Element("response", Protocol.DAV_NS)
                        .addContent(helper.marshalHREF(path, false))
                        .addContent(helper.marshalStatus(e.getStatus()))
                    );
                }
            }
        }
        /*
         * send multistatus response
         */
        sendMultistatusResponse(response, multistatusElement);
    }

}
