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

package com.openexchange.test.fixtures;

import java.util.HashMap;
import java.util.Map;
import com.openexchange.exception.OXException;
import com.openexchange.groupware.container.FolderObject;
import com.openexchange.test.fixtures.transformators.FolderModuleTransformator;
import com.openexchange.test.fixtures.transformators.FolderTypeTransformator;

/**
 * {@link FolderFixtureFactory}
 *
 * @author <a href="mailto:francisco.laguna@open-xchange.com">Francisco Laguna</a>
 */
public class FolderFixtureFactory implements FixtureFactory<FolderObject> {

    private final FixtureLoader fixtureLoader;

    public FolderFixtureFactory(FixtureLoader fixtureLoader) {
        this.fixtureLoader = fixtureLoader;
    }

    @Override
    public Fixtures<FolderObject> createFixture(Map<String, Map<String, String>> entries) {
        return new FolderFixtures(entries, fixtureLoader);
    }

    private class FolderFixtures extends DefaultFixtures<FolderObject> {

        private final Map<String, Map<String, String>> entries;

        private final Map<String, Fixture<FolderObject>> folders = new HashMap<String, Fixture<FolderObject>>();

        public FolderFixtures(Map<String, Map<String, String>> values, FixtureLoader fixtureLoader) {
            super(FolderObject.class, values, fixtureLoader);
            addTransformator(new FolderModuleTransformator(), "module");
            addTransformator(new FolderTypeTransformator(), "type");
            this.entries = values;
        }

        @Override
        public Fixture<FolderObject> getEntry(String entryName) throws OXException {
            if (folders.containsKey(entryName)) {
                return folders.get(entryName);
            }
            final Map<String, String> values = entries.get(entryName);
            if (null == values) {
                throw new FixtureException("Entry with name " + entryName + " not found");
            }
            final FolderObject folder = new FolderObject();
            apply(folder, values);
            final Fixture<FolderObject> fixture = new Fixture<FolderObject>(folder, values.keySet().toArray(new String[values.size()]), values);
            folders.put(entryName, fixture);
            return fixture;
        }

    }

}
