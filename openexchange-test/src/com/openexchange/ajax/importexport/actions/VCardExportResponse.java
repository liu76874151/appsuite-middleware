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

package com.openexchange.ajax.importexport.actions;

import java.util.Date;
import org.apache.http.HttpResponse;
import com.openexchange.ajax.framework.AbstractAJAXResponse;
import com.openexchange.exception.OXException;
import com.openexchange.exception.OXException.ProblematicAttribute;

public class VCardExportResponse extends AbstractAJAXResponse {

    private String vCard;
    
    private final HttpResponse response;

    /**
     * @param response
     */
    public VCardExportResponse(HttpResponse response) {
        super(null);
        this.response = response;
    }

    /**
     * @param iCal the iCal to set
     */
    public void setVCard(final String vCard) {
        this.vCard = vCard;
    }

    /**
     * @return the iCal
     */
    public String getVCard() {
        return vCard;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getData() {
        return getVCard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OXException getException() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProblematicAttribute[] getProblematics() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getTimestamp() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasError() {
        throw new UnsupportedOperationException();
    }    
    
    /**
     * @return response the http response
     */
    public HttpResponse getHttpResponse() {
        return response;
    }
}
