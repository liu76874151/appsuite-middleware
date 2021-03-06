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

package com.openexchange.find.facet;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.Serializable;


/**
 * @author <a href="mailto:steffen.templin@open-xchange.com">Steffen Templin</a>
 * @since v7.6.0
 */
public class Option implements Serializable{

    private static final long serialVersionUID = 5585827431039480470L;

    private final String id;

    private final String name;

    private final Filter filter;

    /**
     * Convenience method that takes a localizable string to construct an according
     * {@link SimpleDisplayItem}.
     * @param id
     *   The unique id of this option within a {@link FacetValue}.
     * @param name
     *   The localizable display name of this option (shown within a client).
     * @param filter
     *   The filter.
     */
    public static Option newInstance(String id, String name, Filter filter) {
        checkNotNull(id);
        checkNotNull(name);
        checkNotNull(filter);
        return new Option(id, name, filter);
    }

    private Option(String id, String name, Filter filter) {
        super();
        this.id = id;
        this.name = name;
        this.filter = filter;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Filter getFilter() {
        return filter;
    }

}
