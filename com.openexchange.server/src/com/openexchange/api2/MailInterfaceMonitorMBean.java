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



package com.openexchange.api2;

/**
 * @author <a href="mailto:thorben.betten@open-xchange.com">Thorben Betten</a>
 *
 */
public interface MailInterfaceMonitorMBean {

	/**
	 *
	 * @return the number of active connections which were opened from
	 *         <code>Store</code>, <code>Folder</code> or
	 *         <code>Transport</code> instances
	 */
	int getNumActive();

	/**
	 * @return the average use time of most important IMAP commands
	 */
	double getAvgUseTime();

	/**
	 * @return the longest time of most important IMAP commands
	 */
	long getMaxUseTime();

	/**
	 * Resets the maximum use time.
	 */
	void resetMaxUseTime();

	/**
	 * @return the minimal use time of most important IMAP commands
	 */
	long getMinUseTime();

	/**
	 * Resets the minimum use time.
	 */
	void resetMinUseTime();

	/**
	 * @return the number of broken connections
	 */
	int getNumBrokenConnections();

	/**
	 * Resets the number of broken connections
	 *
	 */
	void resetNumBrokenConnections();

	/**
	 *
	 * @return the number of timed-out connections
	 */
	int getNumTimeoutConnections();

	/**
	 * Resets the number of timed-out connections
	 *
	 */
	void resetNumTimeoutConnections();

	/**
	 *
	 * @return the number of successfull logins
	 */
	int getNumSuccessfulLogins();

	/**
	 * Resets the number of successful logins
	 *
	 */
	void resetNumSuccessfulLogins();

	/**
	 *
	 * @return the number of failed logins
	 */
	int getNumFailedLogins();

	/**
	 * Resets the number of failed logins
	 *
	 */
	void resetNumFailedLogins();

	/**
	 * @return the occurrences of unsupported encoding exceptions as a
	 *         comma-separated string of the form:
	 *         <code>[encoding1]: [num1] times, [encoding2]: [num2] times, ...</code>
	 */
	String getUnsupportedEncodingExceptions();

}
