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

package com.openexchange.datamining;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * {@link Tools}
 *
 * @author <a href="mailto:karsten.will@open-xchange.com">Karsten Will</a>
 */
public final class Tools {

    static String humanReadableBytes(String string) {
        String returnString = "";
        BigInteger number = new BigInteger(string);
        if (number.equals(new BigInteger("9999999999")) || Integer.parseInt(string) == Integer.MAX_VALUE) {
            returnString = "INFINITE";
        } else if (number.equals(new BigInteger("1"))) {
            returnString = "1Byte";
        } else if (number.compareTo(new BigInteger("1000000000")) >= 0) {
            returnString = number.divide(new BigInteger("1000000000")).toString() + "GB";
        } else if (number.compareTo(new BigInteger("1000000")) >= 0) {
            returnString = number.divide(new BigInteger("1000000")).toString() + "MB";
        } else if (number.compareTo(new BigInteger("1000")) >= 0) {
            returnString = number.divide(new BigInteger("1000")).toString() + "KB";
        }
        return returnString;
    }

    /**
     * Get a time stamp of 30 days back in milliseconds
     * 
     * @return The time stamp
     */
    static String calculate30DaysBack() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -30);
        return String.valueOf(cal.getTime().getTime());
    }

}
