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

package com.openexchange.contact.storage.rdb.internal;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.openexchange.i18n.I18nService;

/**
 * {@link Translator}
 *
 * @author <a href="mailto:tobias.friedrich@open-xchange.com">Tobias Friedrich</a>
 */
public class Translator {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Translator.class);
    private static final Translator INSTANCE = new Translator();

    private final Map<Locale, I18nService> services = new ConcurrentHashMap<Locale, I18nService>();

    /**
     * Gets the translator instance.
     *
     * @return The instance.
     */
    public static Translator getInstance() {
        return INSTANCE;
    }

    /**
     * Translates a string.
     *
     * @param locale The target locale
     * @param toTranslate The string to translate
     * @return The translated string
     */
    public String translate(Locale locale, String toTranslate) {
        Locale loc = null == locale ? Locale.US : locale;
        I18nService service = services.get(loc);
        if (null == service) {
            return toTranslate;
        }
        if (!service.hasKey(toTranslate)) {
            LOG.debug("I18n service for locale {} has no translation for \"{}\".", loc, toTranslate);
            return toTranslate;
        }
        return service.getLocalized(toTranslate);
    }

    /**
     * Adds an {@link I18nService}.
     *
     * @param service The service to add
     */
    public void addService(I18nService service) {
        if (null != services.put(service.getLocale(), service)) {
            LOG.warn("Another i18n translation service found for {}", service.getLocale());
        }
    }

    /**
     * Removes a previously added {@link I18nService}.
     *
     * @param service The service to remove
     */
    public void removeService(I18nService service) {
        if (null == services.remove(service.getLocale())) {
            LOG.warn("Unknown i18n translation service shut down for {}", service.getLocale());
        }
    }

    /**
     * Initializes a new {@link Translator}.
     */
    private Translator() {
        super();
    }

}
