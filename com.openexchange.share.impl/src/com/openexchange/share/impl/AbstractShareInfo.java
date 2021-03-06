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

package com.openexchange.share.impl;

import com.openexchange.java.Strings;
import com.openexchange.share.GuestInfo;
import com.openexchange.share.ShareTarget;
import com.openexchange.share.SubfolderAwareShareInfo;

/**
 * {@link AbstractShareInfo}
 *
 * @author <a href="mailto:tobias.friedrich@open-xchange.com">Tobias Friedrich</a>
 * @since v7.8.0
 */
public abstract class AbstractShareInfo implements SubfolderAwareShareInfo {

    private final ShareTarget srcTarget;
    private final ShareTarget dstTarget;
    private final boolean includeSubfolders;

    /**
     * Initializes a new {@link AbstractShareInfo}.
     *
     * @param srcTarget The share target from the sharing users point of view
     * @param dstTarget The share target from the recipients point of view
     * @param includeSubfolders Whether sub-folders should be included in case the target is a infostore folder
     */
    protected AbstractShareInfo(ShareTarget srcTarget, ShareTarget dstTarget, boolean includeSubfolders) {
        super();
        this.srcTarget = srcTarget;
        this.dstTarget = dstTarget;
        this.includeSubfolders = includeSubfolders;
    }

    @Override
    public ShareTarget getTarget() {
        return srcTarget;
    }

    @Override
    public ShareTarget getDestinationTarget() {
        return dstTarget;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Share");
        ShareTarget target = getTarget();
        if (null != target) {
            stringBuilder.append(" [module=").append(target.getModule()).append(", folder=").append(target.getFolder());
            if (null != target.getItem()) {
                stringBuilder.append(", item=").append(target.getItem());
            }
            stringBuilder.append(']');
        }
        GuestInfo guest = getGuest();
        if (null != guest) {
            stringBuilder.append(" [recipient=").append(guest.getRecipientType()).append(", id=").append(guest.getGuestID()).append(", context=").append(guest.getContextID());
            if (Strings.isNotEmpty(guest.getEmailAddress())) {
                stringBuilder.append(", email=").append(guest.getEmailAddress());
            }
            if (Strings.isNotEmpty(guest.getDisplayName())) {
                stringBuilder.append(", name=").append(guest.getDisplayName());
            }
            if (0 < guest.getCreatedBy()) {
                stringBuilder.append(", createdBy=").append(guest.getCreatedBy());
            }
            if (Strings.isNotEmpty(guest.getBaseToken())) {
                stringBuilder.append(", token=").append(guest.getBaseToken());
            }
            stringBuilder.append(']');
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean isIncludeSubfolders() {
        return includeSubfolders;
    }

}
