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

package com.openexchange.rest.client.endpointpool;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.HttpClient;
import com.openexchange.exception.OXException;
import com.openexchange.osgi.annotation.SingletonService;

/**
 * {@link EndpointManagerFactory} - A factory service for new end-point managers.
 *
 * @author <a href="mailto:thorben.betten@open-xchange.com">Thorben Betten</a>
 * @since v7.8.1
 */
@SingletonService
public interface EndpointManagerFactory {

    /**
     * Creates a new end-point manager for the specified end-point URIs.
     *
     * @param endpointUris The end-point URIs
     * @param httpClient The associated HTTP client
     * @param availableStrategy The strategy to decide whether an end-point is available or not
     * @param heartbeatInterval The heart-beat interval to check if a black-listed end-point URI is available again
     * @param timeUnit The time unit for the heart-beat interval
     * @return A new end-point manager
     * @throws OXException If a new end-point manager cannot be returned
     * @throws IllegalArgumentException If passed URIs are empty or invalid
     */
    EndpointManager createEndpointManagerByUris(List<URI> endpointUris, HttpClient httpClient, EndpointAvailableStrategy availableStrategy, long heartbeatInterval, TimeUnit timeUnit) throws OXException;

    /**
     * Creates a new end-point manager for the specified end-point URIs.
     *
     * @param endpoints The end-points
     * @param httpClient The associated HTTP client
     * @param availableStrategy The strategy to decide whether an end-point is available or not
     * @param heartbeatInterval The heart-beat interval to check if a black-listed end-point URI is available again
     * @param timeUnit The time unit for the heart-beat interval
     * @return A new end-point manager
     * @throws OXException If a new end-point manager cannot be returned
     * @throws IllegalArgumentException If passed URIs are empty or invalid
     */
    EndpointManager createEndpointManager(List<String> endpoints, HttpClient httpClient, EndpointAvailableStrategy availableStrategy, long heartbeatInterval, TimeUnit timeUnit) throws OXException;

}
