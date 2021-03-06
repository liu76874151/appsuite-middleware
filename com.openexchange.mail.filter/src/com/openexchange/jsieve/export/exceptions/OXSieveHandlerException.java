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
 *    trademarks of the OX Software GmbH. group of companies.
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
package com.openexchange.jsieve.export.exceptions;

import com.openexchange.jsieve.export.SieveResponse;

/**
 * {@link OXSieveHandlerException}
 *
 * @author <a href="mailto:dennis.sieben@open-xchange.com">Dennis Sieben</a>
 * @author <a href="mailto:thorben.betten@open-xchange.com">Thorben Betten</a>
 */
public class OXSieveHandlerException extends Exception {

	private static final long serialVersionUID = 2990692657778743217L;

	private final String sieveHost;
	private final int sieveHostPort;
    private final SieveResponse response;
    private boolean parseError;
    private boolean authTimeoutError;

    /**
     * Initializes a new {@link OXSieveHandlerException}
     *
     * @param message The message
     * @param sieveHost The sieve host name
     * @param sieveHostPort The sieve host port
     * @param response The {@link SieveResponse}
     */
    public OXSieveHandlerException(final String message, final String sieveHost, final int sieveHostPort, SieveResponse response) {
        super(message);
        this.sieveHost = sieveHost;
        this.sieveHostPort = sieveHostPort;
        this.response = response;
    }

    /**
     * Initializes a new {@link OXSieveHandlerException}
     *
     * @param message The message
     * @param sieveHost The sieve host name
     * @param sieveHostPort The sieve host port
     * @param response The {@link SieveResponse}
     * @param cause The cause
     */
    public OXSieveHandlerException(final String message, final String sieveHost, final int sieveHostPort, SieveResponse response, Throwable cause) {
        super(message, cause);
        this.sieveHost = sieveHost;
        this.sieveHostPort = sieveHostPort;
        this.response = response;
    }

    /**
     * Sets whether this SIEVE exception is caused by a parsing error
     *
     * @param parseError The flag to set
     * @return This <code>OXSieveHandlerException</code> instance
     */
    public OXSieveHandlerException setParseError(final boolean parseError) {
        this.parseError = parseError;
        return this;
    }

    /**
     * Sets whether this SIEVE exception is caused by a timeout during authentication
     *
     * @param authTimeoutError The flag to set
     * @return This <code>OXSieveHandlerException</code> instance
     */
    public OXSieveHandlerException setAuthTimeoutError(boolean authTimeoutError) {
        this.authTimeoutError = authTimeoutError;
        return this;
    }

    /**
     * Signals whether this SIEVE exception is caused by a parsing error
     *
     * @return <code>true</code> for parse error; otherwise <code>false</code>
     */
    public boolean isParseError() {
        return parseError;
    }

    /**
     * Signals whether this SIEVE exception is caused by a timeout during authentication
     *
     * @return <code>true</code> for timeout during authentication; otherwise <code>false</code>
     */
    public boolean isAuthTimeoutError() {
        return authTimeoutError;
    }

	/**
	 * Gets the name of the sieve host for which this exception was thrown
	 *
	 * @return The name of the sieve host for which this exception was thrown
	 */
	public String getSieveHost() {
		return sieveHost;
	}

	/**
	 * Gets the port of the sieve host for which this exception was thrown
	 *
	 * @return The port of the sieve host for which this exception was thrown
	 */
	public int getSieveHostPort() {
		return sieveHostPort;
	}

	/**
     * @return the {@link SieveResponse}
     */
    public final SieveResponse getSieveResponse() {
        return response;
	}
}

