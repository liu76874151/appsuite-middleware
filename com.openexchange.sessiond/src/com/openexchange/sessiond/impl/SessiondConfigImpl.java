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

package com.openexchange.sessiond.impl;

import static com.openexchange.java.Autoboxing.I;
import static com.openexchange.java.Autoboxing.L;
import static com.openexchange.sessiond.SessiondProperty.SESSIOND_AUTOLOGIN;
import com.openexchange.config.ConfigTools;
import com.openexchange.config.ConfigurationService;
import com.openexchange.sessiond.impl.usertype.UserTypeSessiondConfigInterface;

/**
 * {@link SessiondConfigImpl} - The default {@link UserTypeSessiondConfigInterface} implementation.
 *
 * @author <a href="mailto:sebastian.kauss@open-xchange.org">Sebastian Kauss</a>
 * @author <a href="mailto:thorben.betten@open-xchange.com">Thorben Betten</a>
 */
public class SessiondConfigImpl implements SessiondConfigInterface {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SessiondConfigImpl.class);

    private static final long SHORT_CONTAINER_LIFE_TIME = 6L * 60L * 1000L; // 6 minutes
    private static final long LONG_CONTAINER_LIFE_TIME = 60L * 60L * 1000L; // 1 hour

    private final int maxSession;
    private final int maxSessionsPerClient;
    private final long sessionShortLifeTime;
    private final long randomTokenTimeout;
    private final long longLifeTime;
    private final boolean autoLogin;
    private final boolean asyncPutToSessionStorage;
    private final String obfuscationKey;

    /**
     * Initializes a new {@link SessiondConfigImpl}.
     *
     * @param conf The configuration service
     */
    public SessiondConfigImpl(ConfigurationService conf) {
        super();
        maxSession = conf.getIntProperty("com.openexchange.sessiond.maxSession", 50000);
        LOG.debug("Sessiond property: com.openexchange.sessiond.maxSession={}", I(maxSession));

        maxSessionsPerClient = conf.getIntProperty("com.openexchange.sessiond.maxSessionPerClient", 0);
        LOG.debug("Sessiond property: com.openexchange.sessiond.maxSessionPerClient={}", I(maxSessionsPerClient));

        long sessionShortLifeTime = conf.getIntProperty("com.openexchange.sessiond.sessionDefaultLifeTime", ((int) (60L * 60L * 1000L)));
        String tmp = conf.getProperty("com.openexchange.sessiond.sessionLongLifeTime", "1W");
        long longLifeTime = ConfigTools.parseTimespan(tmp);
        if (sessionShortLifeTime < SHORT_CONTAINER_LIFE_TIME) {
            sessionShortLifeTime = SHORT_CONTAINER_LIFE_TIME;
        }
        if (longLifeTime < LONG_CONTAINER_LIFE_TIME) {
            longLifeTime = LONG_CONTAINER_LIFE_TIME;
        }
        if (longLifeTime < sessionShortLifeTime) {
            longLifeTime = sessionShortLifeTime;
        }
        this.sessionShortLifeTime = sessionShortLifeTime;
        this.longLifeTime = longLifeTime;
        LOG.debug("Sessiond property: com.openexchange.sessiond.sessionDefaultLifeTime={}", L(this.sessionShortLifeTime));
        LOG.debug("Sessiond property: com.openexchange.sessiond.sessionLongLifeTime={}", L(this.longLifeTime));

        tmp = conf.getProperty("com.openexchange.sessiond.randomTokenTimeout", Integer.toString(30000));
        randomTokenTimeout = ConfigTools.parseTimespan(tmp);
        LOG.debug("Sessiond property: com.openexchange.sessiond.randomTokenTimeout={}", L(randomTokenTimeout));

        tmp = conf.getProperty(SESSIOND_AUTOLOGIN.getPropertyName(), SESSIOND_AUTOLOGIN.getDefaultValue());
        autoLogin = Boolean.parseBoolean(tmp.trim());

        tmp = conf.getProperty("com.openexchange.sessiond.asyncPutToSessionStorage", "true");
        asyncPutToSessionStorage = Boolean.parseBoolean(tmp.trim());

        obfuscationKey = conf.getProperty("com.openexchange.sessiond.encryptionKey", "auw948cz,spdfgibcsp9e8ri+<#qawcghgifzign7c6gnrns9oysoeivn");
    }

    @Override
    public boolean isAsyncPutToSessionStorage() {
        return asyncPutToSessionStorage;
    }

    @Override
    public long getSessionContainerTimeout() {
        return SHORT_CONTAINER_LIFE_TIME;
    }

    @Override
    public int getNumberOfSessionContainers() {
        return (int) (sessionShortLifeTime / SHORT_CONTAINER_LIFE_TIME);
    }

    @Override
    public int getMaxSessions() {
        return maxSession;
    }

    @Override
    public int getMaxSessionsPerClient() {
        return maxSessionsPerClient;
    }

    @Override
    public long getLifeTime() {
        return sessionShortLifeTime;
    }

    @Override
    public long getRandomTokenTimeout() {
        return randomTokenTimeout;
    }

    @Override
    public long getLongLifeTime() {
        return longLifeTime;
    }

    @Override
    public int getNumberOfLongTermSessionContainers() {
        long retval = (longLifeTime - sessionShortLifeTime) / LONG_CONTAINER_LIFE_TIME;
        return (int) ((retval < 1) ? 1 : retval);
    }

    @Override
    public boolean isAutoLogin() {
        return autoLogin;
    }

    @Override
    public long getLongTermSessionContainerTimeout() {
        return LONG_CONTAINER_LIFE_TIME;
    }

    @Override
    public String getObfuscationKey() {
        return obfuscationKey;
    }
}
