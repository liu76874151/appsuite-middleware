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

package com.openexchange.contacts.json;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.openexchange.ajax.requesthandler.AJAXActionService;
import com.openexchange.ajax.requesthandler.AJAXActionServiceFactory;
import com.openexchange.contacts.json.actions.AdvancedSearchAction;
import com.openexchange.contacts.json.actions.AllAction;
import com.openexchange.contacts.json.actions.AnniversariesAction;
import com.openexchange.contacts.json.actions.AutocompleteAction;
import com.openexchange.contacts.json.actions.BirthdaysAction;
import com.openexchange.contacts.json.actions.ContactAction;
import com.openexchange.contacts.json.actions.CopyAction;
import com.openexchange.contacts.json.actions.DeleteAction;
import com.openexchange.contacts.json.actions.GetAction;
import com.openexchange.contacts.json.actions.GetUserAction;
import com.openexchange.contacts.json.actions.GetVCardAction;
import com.openexchange.contacts.json.actions.ListAction;
import com.openexchange.contacts.json.actions.ListUserAction;
import com.openexchange.contacts.json.actions.NewAction;
import com.openexchange.contacts.json.actions.SearchAction;
import com.openexchange.contacts.json.actions.UpdateAction;
import com.openexchange.contacts.json.actions.UpdatesAction;
import com.openexchange.oauth.provider.resourceserver.annotations.OAuthModule;
import com.openexchange.server.ServiceLookup;


/**
 * {@link ContactActionFactory}
 *
 * @author <a href="mailto:steffen.templin@open-xchange.com">Steffen Templin</a>
 * @author <a href="mailto:tobias.friedrich@open-xchange.com">Tobias Friedrich</a>
 */
@OAuthModule
public class ContactActionFactory implements AJAXActionServiceFactory {

    /**
     * The read-only scope for OAuth requests
     */
    public static final String OAUTH_READ_SCOPE = "read_contacts";

    /**
     * The writable scope for OAuth requests
     */
    public static final String OAUTH_WRITE_SCOPE = "write_contacts";

    private final Map<String, ContactAction> actions;

    public ContactActionFactory(final ServiceLookup serviceLookup) {
        super();
        ImmutableMap.Builder<String, ContactAction> actions = ImmutableMap.builder();
        actions.put("get", new GetAction(serviceLookup));
        actions.put("all", new AllAction(serviceLookup));
        actions.put("list", new ListAction(serviceLookup));
        actions.put("new", new NewAction(serviceLookup));
        actions.put("delete", new DeleteAction(serviceLookup));
        actions.put("update", new UpdateAction(serviceLookup));
        actions.put("updates", new UpdatesAction(serviceLookup));
        actions.put("listuser", new ListUserAction(serviceLookup));
        actions.put("getuser", new GetUserAction(serviceLookup));
        actions.put("copy", new CopyAction(serviceLookup));
        actions.put("search", new SearchAction(serviceLookup));
        actions.put("advancedSearch", new AdvancedSearchAction(serviceLookup));
        actions.put("birthdays", new BirthdaysAction(serviceLookup));
        actions.put("anniversaries", new AnniversariesAction(serviceLookup));
        actions.put("autocomplete", new AutocompleteAction(serviceLookup));
        actions.put("getVcard", new GetVCardAction(serviceLookup));
        this.actions = actions.build();
    }

    @Override
    public AJAXActionService createActionService(final String action) {
        return actions.get(action);
    }
}
