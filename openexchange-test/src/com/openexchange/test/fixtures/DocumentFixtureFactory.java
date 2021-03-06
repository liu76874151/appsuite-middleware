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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.openexchange.exception.OXException;
import com.openexchange.test.fixtures.transformators.CredentialsTransformator;

/**
 * @author Tobias Friedrich <tobias.friedrich@open-xchange.com>
 */
public class DocumentFixtureFactory implements FixtureFactory<Document> {

    private final File datapath;
    private String seleniumDataPath;
    private String seleniumSeparator;
    private final FixtureLoader fixtureLoader;

    public DocumentFixtureFactory(File datapath, FixtureLoader fixtureLoader) {
        super();
        this.datapath = datapath;
        this.fixtureLoader = fixtureLoader;
    }

    @Override
    public Fixtures<Document> createFixture(final Map<String, Map<String, String>> entries) {
        DocumentFixtures documentFixtures = new DocumentFixtures(entries, datapath, fixtureLoader);
        if (seleniumDataPath != null) {
            documentFixtures.setSeleniumConfiguration(seleniumDataPath, seleniumSeparator);
        }
        return documentFixtures;
    }

    public void setSeleniumConfiguration(String seleniumDataPath, String seleniumSeparator) {
        this.seleniumDataPath = seleniumDataPath;
        this.seleniumSeparator = seleniumSeparator;
    }

    private class DocumentFixtures extends DefaultFixtures<Document> implements Fixtures<Document> {

        private final Map<String, Map<String, String>> entries;
        private final Map<String, Fixture<Document>> knownDocuments = new HashMap<String, Fixture<Document>>();
        @SuppressWarnings("hiding")
        private final File datapath;
        @SuppressWarnings("hiding")
        private String seleniumDataPath;
        @SuppressWarnings("hiding")
        private String seleniumSeparator;

        public DocumentFixtures(final Map<String, Map<String, String>> values, File datapath, FixtureLoader fixtureLoader) {
            super(Document.class, values, fixtureLoader);
            this.entries = values;
            this.datapath = datapath;

            super.addTransformator(new CredentialsTransformator(fixtureLoader), "created_by");
        }

        @Override
        public Fixture<Document> getEntry(final String entryName) throws OXException {
            if (knownDocuments.containsKey(entryName)) {
                return knownDocuments.get(entryName);
            }
            final Map<String, String> values = entries.get(entryName);
            if (null == values) {
                throw new FixtureException("Entry with name " + entryName + " not found");
            }
            final Document document = new Document(datapath);
            apply(document, values);

            if (seleniumDataPath != null) {
                document.setSeleniumConfiguration(seleniumDataPath, seleniumSeparator);
            }

            final Fixture<Document> fixture = new Fixture<Document>(document, values.keySet().toArray(new String[values.size()]), values);
            knownDocuments.put(entryName, fixture);
            return fixture;
        }

        public void setSeleniumConfiguration(String seleniumDataPath, String seleniumSeparator) {
            this.seleniumDataPath = seleniumDataPath;
            this.seleniumSeparator = seleniumSeparator;
        }
    }
}
