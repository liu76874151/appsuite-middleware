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

package com.openexchange.chronos.alarm.mail.impl;

import static com.openexchange.java.Autoboxing.I;
import static com.openexchange.java.Autoboxing.L;
import com.openexchange.config.lean.DefaultProperty;
import com.openexchange.config.lean.Property;

/**
 * {@link MailAlarmConfig}
 *
 * @author <a href="mailto:kevin.ruthmann@open-xchange.com">Kevin Ruthmann</a>
 * @since v7.10.1
 */
public class MailAlarmConfig {

    private static final String PREFIX = "com.openexchange.calendar.alarm.mail.";

    /**
     * Defines the time in milliseconds an alarm mail should be send out before the trigger time.
     * With this property the admin can configure the average time needed by the mail system to send out the mail.
     * This way the mail should usually be send out on time and not a few seconds late.
     */
    public static final Property MAIL_SHIFT = DefaultProperty.valueOf(PREFIX + "time.shift", I(0));

    /**
     * Enabled or disables mail alarms.
     */
    public static final Property MAIL_ENABLED = DefaultProperty.valueOf(PREFIX + "enabled", Boolean.TRUE);

    /**
     * The amount of mails allowed to be sent in a given time-frame (see {@link #MAIL_LIMIT_TIME_FRAME})
     */
    public static final Property MAIL_LIMIT_AMOUNT = DefaultProperty.valueOf(PREFIX + "limit.amount", I(-1));

    /**
     * The timeframe in milliseconds used for the amount limit (see {@link #MAIL_LIMIT_AMOUNT}
     */
    public static final Property MAIL_LIMIT_TIME_FRAME = DefaultProperty.valueOf(PREFIX + "limit.timeframe", L(60000l));

}
