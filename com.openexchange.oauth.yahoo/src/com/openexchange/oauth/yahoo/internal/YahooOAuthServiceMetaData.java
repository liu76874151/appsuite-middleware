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

package com.openexchange.oauth.yahoo.internal;

import java.util.Collection;
import java.util.Collections;
import org.scribe.builder.api.Api;
import org.scribe.model.Verb;
import com.openexchange.http.deferrer.DeferringURLService;
import com.openexchange.oauth.HostInfo;
import com.openexchange.oauth.KnownApi;
import com.openexchange.oauth.impl.AbstractExtendedScribeAwareOAuthServiceMetaData;
import com.openexchange.oauth.yahoo.YahooOAuthScope;
import com.openexchange.server.ServiceLookup;
import com.openexchange.session.Session;

/**
 * {@link YahooOAuthServiceMetaData}
 *
 * @author <a href="mailto:karsten.will@open-xchange.com">Karsten Will</a>
 * @author <a href="mailto:thorben.betten@open-xchange.com">Thorben Betten</a>
 * @author <a href="mailto:ioannis.chouklis@open-xchange.com">Ioannis Chouklis</a>
 */
public class YahooOAuthServiceMetaData extends AbstractExtendedScribeAwareOAuthServiceMetaData {

    private static final String IDENTITY_URL = "https://social.yahooapis.com/v1/me/guid?format=json";
    private static final String IDENTITY_FIELD_NAME = "value";

    public YahooOAuthServiceMetaData(ServiceLookup services) {
        super(services, KnownApi.YAHOO, YahooOAuthScope.values());
    }

    @Override
    public String modifyCallbackURL(String callbackUrl, HostInfo currentHost, Session session) {
        DeferringURLService deferrer = services.getService(DeferringURLService.class);
        if (deferrer == null || false == deferrer.isDeferrerURLAvailable(session.getUserId(), session.getContextId())) {
            return callbackUrl;
        }
        return deferrer.getDeferredURL(callbackUrl, session.getUserId(), session.getContextId());
    }

    @Override
    public Class<? extends Api> getScribeService() {
        return YahooApi2.class;
    }

    @Override
    public boolean needsRequestToken() {
        return false;
    }

    @Override
    protected String getPropertyId() {
        return "yahoo";
    }

    @Override
    protected Collection<OAuthPropertyID> getExtraPropertyNames() {
        return Collections.singletonList(OAuthPropertyID.redirectUrl);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.openexchange.oauth.impl.OAuthIdentityAware#getIdentityURL()
     */
    @Override
    public String getIdentityURL(String accessToken) {
        return IDENTITY_URL;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.openexchange.oauth.impl.OAuthIdentityAware#getIdentityMethod()
     */
    @Override
    public Verb getIdentityHTTPMethod() {
        return Verb.GET;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.openexchange.oauth.impl.OAuthIdentityAware#getIdentityPattern()
     */
    @Override
    public String getIdentityFieldName() {
        return IDENTITY_FIELD_NAME;
    }
}
