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

package com.openexchange.drive.json.action;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.openexchange.ajax.requesthandler.AJAXActionService;
import com.openexchange.ajax.requesthandler.AJAXActionServiceFactory;
import com.openexchange.exception.OXException;

/**
 * {@link DriveActionFactory}
 *
 * @author <a href="mailto:tobias.friedrich@open-xchange.com">Tobias Friedrich</a>
 */
public class DriveActionFactory implements AJAXActionServiceFactory {

    private final Map<String, AJAXActionService> actions;

    public DriveActionFactory() {
        super();
        ImmutableMap.Builder<String, AJAXActionService> actions = ImmutableMap.builder();
        actions.put("syncfolders", new SyncFoldersAction());
        actions.put("syncfiles", new SyncFilesAction());
        actions.put("upload", new UploadAction());
        actions.put("download", new DownloadAction());
        actions.put("listen", new ListenAction());
        actions.put("quota", new QuotaAction());
        actions.put("settings", new SettingsAction());
        actions.put("subscribe", new SubscribeAction());
        actions.put("unsubscribe", new UnsubscribeAction());
        actions.put("updateToken", new UpdateTokenAction());
        actions.put("fileMetadata", new FileMetadataAction());
        actions.put("directoryMetadata", new DirectoryMetadataAction());
        actions.put("jump", new JumpAction());
        actions.put("subfolders", new SubfoldersAction());
        actions.put("getLink", new GetLinkAction());
        actions.put("updateLink", new UpdateLinkAction());
        actions.put("deleteLink", new DeleteLinkAction());
        actions.put("sendLink", new SendLinkAction());
        actions.put("updateFile", new UpdateFileAction());
        actions.put("updateFolder", new UpdateFolderAction());
        actions.put("getFile", new GetFileAction());
        actions.put("getFolder", new GetFolderAction());
        actions.put("shares", new SharesAction());
        actions.put("notify", new NotifyAction());
        actions.put("autocomplete", new AutocompleteAction());
        actions.put("trashStats", new TrashStatsAction());
        actions.put("emptyTrash", new EmptyTrashAction());
        actions.put("moveFile", new MoveFileAction());
        actions.put("moveFolder", new MoveFolderAction());
        actions.put("trashContents", new TrashContentsAction());
        actions.put("deleteFromTrash", new DeleteFromTrashAction());
        actions.put("restoreFromTrash", new RestoreFromTrashAction());
        this.actions = actions.build();
    }

    @Override
    public AJAXActionService createActionService(String action) throws OXException {
        return actions.get(action);
    }

}
