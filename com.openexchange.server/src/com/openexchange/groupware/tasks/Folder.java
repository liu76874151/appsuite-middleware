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

package com.openexchange.groupware.tasks;

import java.util.Collections;
import java.util.Set;

/**
 * Stores a folder that contains a task.
 * @author <a href="mailto:marcus@open-xchange.org">Marcus Klein</a>
 */
public class Folder {

    /**
     * An empty set of folders.
     */
    static final Set<Folder> EMPTY = Collections.emptySet();

    /**
     * Unique identifier of the folder.
     */
    private final int identifier;

    /**
     * Unique identifier of the user or <code>MAIN_FOLDER</code> if the folder
     * is the main folder of the task.
     */
    private final int user;

    /**
     * Default constructor.
     * @param folder unique identifier of the folder the task must appear in.
     * @param user unique identifier of the user or <code>0</code> if the
     * folder is the main folder of the task.
     */
    public Folder(final int folder, final int user) {
        super();
        this.identifier = folder;
        this.user = user;
    }

    /**
     * @return Returns the folder.
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * @return Returns the user.
     */
    int getUser() {
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Folder)) {
            return false;
        }
        return identifier == ((Folder) obj).identifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return identifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "TaskFolder: " + getIdentifier() + ", User: " + getUser();
    }
}
