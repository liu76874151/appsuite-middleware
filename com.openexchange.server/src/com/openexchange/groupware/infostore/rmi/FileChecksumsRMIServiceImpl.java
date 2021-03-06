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

package com.openexchange.groupware.infostore.rmi;

import java.rmi.RemoteException;
import java.util.List;
import com.openexchange.database.DatabaseService;
import com.openexchange.exception.OXException;
import com.openexchange.filestore.QuotaFileStorageService;
import com.openexchange.groupware.infostore.utils.FileMD5SumHelper;
import com.openexchange.server.services.ServerServiceRegistry;

/**
 * {@link FileChecksumsRMIServiceImpl}
 *
 * @author <a href="mailto:ioannis.chouklis@open-xchange.com">Ioannis Chouklis</a>
 * @since v7.10.1
 */
public class FileChecksumsRMIServiceImpl implements FileChecksumsRMIService {

    /**
     * Initialises a new {@link FileChecksumsRMIServiceImpl}.
     */
    public FileChecksumsRMIServiceImpl() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.openexchange.groupware.infostore.rmi.FileChecksumsRMIService#listFilesWithoutChecksumInContext(int)
     */
    @Override
    public List<String> listFilesWithoutChecksumInContext(int contextId) throws RemoteException {
        try {
            return FileMD5SumHelper.toString(contextId, getMD5SumHelper().listMissingInContext(contextId));
        } catch (OXException e) {
            throw new RemoteException(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.openexchange.groupware.infostore.rmi.FileChecksumsRMIService#listFilesWithoutChecksumInDatabase(int)
     */
    @Override
    public List<String> listFilesWithoutChecksumInDatabase(int databaseId) throws RemoteException {
        try {
            return FileMD5SumHelper.toString(getMD5SumHelper().listMissingInDatabase(databaseId));
        } catch (OXException e) {
            throw new RemoteException(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.openexchange.groupware.infostore.rmi.FileChecksumsRMIService#listAllFilesWithoutChecksum()
     */
    @Override
    public List<String> listAllFilesWithoutChecksum() throws RemoteException {
        try {
            return FileMD5SumHelper.toString(getMD5SumHelper().listAllMissing());
        } catch (OXException e) {
            throw new RemoteException(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.openexchange.groupware.infostore.rmi.FileChecksumsRMIService#calculateMissingChecksumsInContext(int)
     */
    @Override
    public List<String> calculateMissingChecksumsInContext(int contextId) throws RemoteException {
        try {
            return FileMD5SumHelper.toString(contextId, getMD5SumHelper().calculateMissingInContext(contextId));
        } catch (OXException e) {
            throw new RemoteException(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.openexchange.groupware.infostore.rmi.FileChecksumsRMIService#calculateMissingChecksumsInDatabase(int)
     */
    @Override
    public List<String> calculateMissingChecksumsInDatabase(int databaseId) throws RemoteException {
        try {
            return FileMD5SumHelper.toString(getMD5SumHelper().calculateMissingInDatabase(databaseId));
        } catch (OXException e) {
            throw new RemoteException(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.openexchange.groupware.infostore.rmi.FileChecksumsRMIService#calculateAllMissingChecksums()
     */
    @Override
    public List<String> calculateAllMissingChecksums() throws RemoteException {
        try {
            return FileMD5SumHelper.toString(getMD5SumHelper().calculateAllMissing());
        } catch (OXException e) {
            throw new RemoteException(e.getMessage(), e);
        }
    }

    /**
     * Retrieves the MD5SumHelper
     * 
     * @return The MD5SumHelper
     * @throws OXException if the helper is absent
     */
    private FileMD5SumHelper getMD5SumHelper() throws OXException {
        DatabaseService dbService = ServerServiceRegistry.getServize(DatabaseService.class, true);
        QuotaFileStorageService fsService = ServerServiceRegistry.getServize(QuotaFileStorageService.class, true);
        return new FileMD5SumHelper(dbService, fsService);
    }
}
