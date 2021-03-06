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

package com.openexchange.groupware.dataRetrieval.config;

import java.io.Serializable;
import com.openexchange.config.ConfigurationService;

/**
 * {@link Configuration}
 *
 * @author <a href="mailto:francisco.laguna@open-xchange.com">Francisco Laguna</a>
 */
public class Configuration implements Serializable {

    private static final long serialVersionUID = 9139571663724448075L;

    public static final String ENABLE_OPTIONS_NAME = "com.openexchange.groupware.dataRetrieval.enable";
    public static final String LIFETIME_OPTIONS_NAME = "com.openexchange.groupware.dataRetrieval.lifetime";
    public static final String ISONETIME_OPTIONS_NAME = "com.openexchange.groupware.dataRetrieval.onetime";
    private static final String FORCE_PROTOCOL_NAME = "com.openexchange.groupware.dataRetrieval.forceProtocol";

    private long lifetime = -1;
    private boolean expireAfterAccess = true;
    private boolean enabled = false;
    private String forceProtocol = null;

    public Configuration(ConfigurationService service) {
        enabled = service.getBoolProperty(ENABLE_OPTIONS_NAME, false);
        String lifetime = service.getProperty(LIFETIME_OPTIONS_NAME);
        if (lifetime != null) {
            this.lifetime = Long.parseLong(lifetime.trim());
        }

        String onetime = service.getProperty(ISONETIME_OPTIONS_NAME);
        if (onetime != null) {
            expireAfterAccess = Boolean.parseBoolean(onetime.trim());
        }

        forceProtocol = service.getProperty(FORCE_PROTOCOL_NAME);
    }

    public long getLifetime() {
        return lifetime;
    }

    public boolean expiresAfterAccess() {
        return expireAfterAccess;
    }

    public boolean hasExpired(long created) {
        if (lifetime < 0) {
            return false;
        }
        return created + lifetime < System.currentTimeMillis();
    }

    public String getForcedProtocol() {
        return forceProtocol;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
