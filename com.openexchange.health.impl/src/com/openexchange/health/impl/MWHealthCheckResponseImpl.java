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

package com.openexchange.health.impl;

import java.util.Map;
import com.openexchange.health.MWHealthCheckResponse;
import com.openexchange.health.MWHealthState;

/**
 * {@link MWHealthCheckResponseImpl}
 *
 * @author <a href="mailto:jan.bauerdick@open-xchange.com">Jan Bauerdick</a>
 * @since v7.10.1
 */
public class MWHealthCheckResponseImpl implements MWHealthCheckResponse {

    private final String name;
    private final Map<String, Object> data;
    private final MWHealthState state;

    public MWHealthCheckResponseImpl(String name, Map<String, Object> data, MWHealthState state) {
        super();
        this.name = name;
        this.data = data;
        this.state = state;
    }

    public MWHealthCheckResponseImpl(String name, Map<String, Object> data, boolean state) {
        super();
        this.name = name;
        this.data = data;
        this.state = state ? MWHealthState.UP : MWHealthState.DOWN;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public MWHealthState getState() {
        return state;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": ").append(MWHealthState.UP.equals(state) ? "UP" : "DOWN");
        // Don't include detailed data for now
        //        if (null != data && data.size() > 0) {
        //            sb.append(", ").append("data: [");
        //            for (Map.Entry<String, Object> entry : data.entrySet()) {
        //                sb.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
        //            }
        //            sb.deleteCharAt(sb.length() - 1);
        //            sb.append("]");
        //        }
        return sb.toString();
    }

}
