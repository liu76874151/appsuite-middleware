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

package com.openexchange.admin.reseller.rmi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.Stack;
import org.junit.After;
import org.junit.Test;
import com.openexchange.admin.reseller.rmi.dataobjects.ResellerAdmin;
import com.openexchange.admin.reseller.rmi.dataobjects.Restriction;
import com.openexchange.admin.reseller.rmi.exceptions.OXResellerException;
import com.openexchange.admin.reseller.rmi.extensions.OXContextExtensionImpl;
import com.openexchange.admin.rmi.dataobjects.Context;
import com.openexchange.admin.rmi.dataobjects.Credentials;
import com.openexchange.admin.rmi.dataobjects.User;
import com.openexchange.admin.rmi.exceptions.DuplicateExtensionException;
import com.openexchange.admin.rmi.exceptions.InvalidDataException;
import com.openexchange.admin.rmi.exceptions.StorageException;
import com.openexchange.admin.rmi.factory.ResellerAdminFactory;
import com.openexchange.admin.rmi.factory.UserFactory;

public class OXResellerInterfaceTest extends AbstractOXResellerTest {

    private Stack<Context> restrictionContexts = null;

    /**
     * Initialises a new {@link OXResellerInterfaceTest}.
     */
    public OXResellerInterfaceTest() {
        super();
    }

    @After
    public final void tearDown() throws Exception {
        final ResellerAdmin[] adms = getResellerManager().search("test*");
        for (final ResellerAdmin adm : adms) {
            getResellerManager().delete(adm);
        }
    }

    @Test
    public void testUpdateModuleAccessRestrictions() throws Exception {
        getResellerManager().updateDatabaseModuleAccessRestrictions();
    }

    @Test
    public void testCreate() throws Exception {
        ResellerAdmin adm = getResellerManager().create(ResellerAdminFactory.createResellerAdmin());
        ResellerAdmin admch = getResellerManager().create(ResellerAdminFactory.createResellerAdmin(TESTCHANGEUSER, "Test Change User"));

        assertNotNull("creation of ResellerAdmin failed", adm);
        assertNotNull("creation of ResellerAdmin failed", admch);
        assertTrue("creation of ResellerAdmin failed", adm.getId() > 0);
        assertTrue("creation of ResellerAdmin failed", admch.getId() > 0);
    }

    @Test(expected = InvalidDataException.class)
    public void testCreateMissingMandatoryFields() throws Exception {
        ResellerAdmin adm = new ResellerAdmin();
        // no displayname
        adm.setName("incomplete");
        adm.setPassword("secret");
        getResellerManager().create(adm);

        // no password
        adm.setPassword(null);
        adm.setDisplayname("Test incomplete");
        adm.setName("incomplete");
        getResellerManager().create(adm);

        // no name
        adm.setPassword("secret");
        adm.setDisplayname("Test incomplete");
        adm.setName(null);
        getResellerManager().create(adm);
    }

    @Test
    public void testCreateWithRestrictions() throws Exception {
        for (final String user : new String[] { TESTRESTRICTIONUSER, TESTRESTCHANGERICTIONUSER }) {
            ResellerAdmin adm = ResellerAdminFactory.createResellerAdmin(user, "Test Restriction User");
            adm.setRestrictions(new Restriction[] { MaxContextRestriction(), MaxContextQuotaRestriction() });
            adm = getResellerManager().create(adm);

            System.out.println(adm);

            assertNotNull("creation of ResellerAdmin failed", adm);
            assertTrue("creation of ResellerAdmin failed", adm.getId() > 0);
        }
    }

    @Test
    public void testChangeWithRestrictions() throws Exception {
        ResellerAdmin adm = ResellerAdminFactory.createResellerAdmin(TESTRESTCHANGERICTIONUSER);
        adm.setRestrictions(new Restriction[] { MaxContextRestriction(), MaxContextQuotaRestriction() });
        adm = getResellerManager().create(adm);

        adm = getResellerManager().getData(ResellerAdminFactory.createResellerAdmin(TESTRESTCHANGERICTIONUSER));
        Restriction r = getRestrictionByName(Restriction.MAX_OVERALL_CONTEXT_QUOTA_PER_SUBADMIN, adm.getRestrictions());
        assertNotNull("Restriction Restriction.MAX_CONTEXT_QUOTA not found", r);
        r.setValue("2000");
        getResellerManager().change(adm);

        adm = getResellerManager().getData(ResellerAdminFactory.createResellerAdmin(TESTRESTCHANGERICTIONUSER));
        r = getRestrictionByName(Restriction.MAX_OVERALL_CONTEXT_QUOTA_PER_SUBADMIN, adm.getRestrictions());

        assertNotNull("Restriction Restriction.MAX_CONTEXT_QUOTA not found", r);
        assertEquals("Change Restriction value failed", "2000", r.getValue());
    }

    @Test
    public void testChange() throws Exception {
        getResellerManager().create(ResellerAdminFactory.createResellerAdmin(TESTCHANGEUSER, "Test Change User"));

        ResellerAdmin adm = new ResellerAdmin(TESTCHANGEUSER);
        final String newdisp = "New Display name";
        adm.setDisplayname(newdisp);

        getResellerManager().change(adm);

        ResellerAdmin chadm = getResellerManager().getData(new ResellerAdmin(TESTCHANGEUSER));

        assertEquals("getData must return changed Displayname", adm.getDisplayname(), chadm.getDisplayname());
    }

    @Test
    public void testChangeName() throws Exception {
        getResellerManager().create(ResellerAdminFactory.createResellerAdmin(TESTCHANGEUSER, "Test Change User"));

        ResellerAdmin adm = getResellerManager().getData(new ResellerAdmin(TESTCHANGEUSER));
        adm.setName(CHANGEDNAME);
        getResellerManager().change(adm);
        ResellerAdmin newadm = new ResellerAdmin();
        newadm.setId(adm.getId());
        ResellerAdmin chadm = getResellerManager().getData(newadm);
        assertEquals("getData must return changed name", adm.getName(), chadm.getName());
    }

    @Test(expected = StorageException.class)
    public void testChangeNameWithoutID() throws Exception {
        ResellerAdmin adm = new ResellerAdmin();
        adm.setName(CHANGEDNAME + "new");
        getResellerManager().change(adm);
    }

    @Test
    public void testGetData() throws Exception {
        final ResellerAdmin adm = ResellerAdminFactory.createResellerAdmin();

        getResellerManager().create(adm);

        final ResellerAdmin dbadm = getResellerManager().getData(new ResellerAdmin(TESTUSER));

        assertEquals("getData returned wrong data", adm.getName(), dbadm.getName());
        assertEquals("getData returned wrong data", adm.getDisplayname(), dbadm.getDisplayname());
    }

    @Test
    public void testGetDataBug19102() throws Exception {
        final ResellerAdmin adm = ResellerAdminFactory.createResellerAdmin();

        getResellerManager().create(adm);

        // add some restrictions to adm
        adm.setRestrictions(new Restriction[] { new Restriction(Restriction.MAX_OVERALL_USER_PER_SUBADMIN, "2") });
        final ResellerAdmin dbadm = getResellerManager().getData(adm);
        // and check whether they are still there after getData call
        assertNull("there must be no restrictions set", dbadm.getRestrictions());
    }

    @Test
    public void testGetDataWithRestrictions() throws Exception {
        final ResellerAdmin adm = ResellerAdminFactory.createResellerAdmin(TESTRESTRICTIONUSER, "Test Restriction User");
        adm.setRestrictions(new Restriction[] { MaxContextRestriction(), MaxContextQuotaRestriction() });
        getResellerManager().create(adm);

        final ResellerAdmin dbadm = getResellerManager().getData(adm);

        Restriction[] res = dbadm.getRestrictions();
        assertNotNull("ResellerAdmin must contain Restrictions", res);

        boolean foundmaxctx = getRestrictionByName(Restriction.MAX_CONTEXT_PER_SUBADMIN, res) == null ? false : true;
        boolean foundmaxctxquota = getRestrictionByName(Restriction.MAX_OVERALL_CONTEXT_QUOTA_PER_SUBADMIN, res) == null ? false : true;

        assertTrue(MaxContextQuotaRestriction().getName() + " must be contained in ResellerAdmin", foundmaxctx);
        assertTrue(MaxContextRestriction().getName() + " must be contained in ResellerAdmin", foundmaxctxquota);
        assertEquals("getData returned wrong data", adm.getName(), dbadm.getName());
        assertEquals("getData returned wrong data", adm.getDisplayname(), dbadm.getDisplayname());
    }

    @Test
    public void testRestrictionsToContext() throws Exception {
        getResellerManager().create(ResellerAdminFactory.createResellerAdmin());
        restrictionContexts = new Stack<Context>();
        for (final Credentials creds : new Credentials[] { superAdminCredentials, TestUserCredentials() }) {

            User oxadmin = UserFactory.createContextAdmin();
            Context ctx1 = new Context();
            ctx1.setMaxQuota(100000L);

            try {
                ctx1.addExtension(new OXContextExtensionImpl(new Restriction[] { MaxUserPerContextRestriction() }));
            } catch (final DuplicateExtensionException e) {
                // cannot occur on a newly created context
                e.printStackTrace();
            }
            final Context ctx = getContextManager().create(ctx1, oxadmin, creds);
            restrictionContexts.push(ctx);
        }

        for (final Credentials creds : new Credentials[] { TestUserCredentials(), superAdminCredentials }) {
            final Context ctx = restrictionContexts.pop();

            Restriction[] res = getResellerManager().getContextRestrictions(ctx);
            assertNotNull("Context restrictions must not be null", res);
            assertEquals("Context restrictions must contain one restriction", 1, res.length);
            assertEquals("Restriction value does not match expected value", MaxUserPerContextRestriction().getValue(), res[0].getValue());
            deleteContext(ctx, creds);
        }
    }

    @Test
    public void testDeleteContextOwningSubadmin() throws Exception {
        getResellerManager().create(ResellerAdminFactory.createResellerAdmin("owned"));
        final Context ctx = createContext(new Credentials("owned", "secret"));

        boolean deleteFailed = false;
        try {
            getResellerManager().delete(ResellerAdminFactory.createResellerAdmin("owned"));
        } catch (OXResellerException e) {
            deleteFailed = true;
        }
        assertTrue("deletion of ResellerAdmin must fail", deleteFailed);

        deleteContext(ctx, new Credentials("owned", "secret"));
        getResellerManager().delete(ResellerAdminFactory.createResellerAdmin("owned"));
    }

    @Test
    public void testDeleteByID() throws Exception {
        ResellerAdmin adm = getResellerManager().create(ResellerAdminFactory.createResellerAdmin());
        adm = getResellerManager().getData(adm);
        adm.setName(null);

        getResellerManager().delete(adm);
    }

}
