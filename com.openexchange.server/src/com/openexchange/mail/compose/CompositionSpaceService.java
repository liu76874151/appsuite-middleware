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

package com.openexchange.mail.compose;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import com.openexchange.ajax.requesthandler.AJAXRequestData;
import com.openexchange.exception.OXException;
import com.openexchange.groupware.upload.StreamedUploadFileIterator;
import com.openexchange.mail.MailPath;
import com.openexchange.mail.usersetting.UserSettingMail;
import com.openexchange.osgi.annotation.SingletonService;
import com.openexchange.session.Session;

/**
 * {@link CompositionSpaceService} - The composition space service.
 *
 * @author <a href="mailto:thorben.betten@open-xchange.com">Thorben Betten</a>
 * @since v7.10.2
 */
@SingletonService
public interface CompositionSpaceService {

    /**
     * Transports the mail resulting from specified composition space.
     *
     * @param compositionSpaceId The identifier of the composition space describing the mail to transport
     * @param mailSettings The user's mail settings
     * @param requestData The request data
     * @param warnings The optional collection to add possible warnings to
     * @param deleteAfterTransport Whether the composition space is supposed to be deleted after transport
     * @param session The session providing user information
     * @return The path to the sent mail or <code>null</code>
     * @throws OXException If transport fails
     */
    MailPath transportCompositionSpace(UUID compositionSpaceId, UserSettingMail mailSettings, AJAXRequestData requestData, List<OXException> warnings, boolean deleteAfterTransport, Session session) throws OXException;

    /**
     * Saves given composition space to an appropriate draft mail.
     *
     * @param compositionSpaceId The identifier of the composition space to save as draft mail
     * @param deleteAfterSave Whether the composition space is supposed to be deleted after saving as draft mail
     * @param session The session providing user information
     * @return The path to the draft mail
     * @throws OXException If conversion fails
     */
    MailPath saveCompositionSpaceToDraftMail(UUID compositionSpaceId, boolean deleteAfterSave, Session session) throws OXException;

    /**
     * Opens a new composition space for composing a message according to given parameters.
     *
     * @param session The session providing user information
     * @return The opened composition space
     * @throws OXException If no composition space can be opened
     */
    CompositionSpace openCompositionSpace(OpenCompositionSpaceParameters parameters, Session session) throws OXException;

    /**
     * Closes specified composition space and drops all associated resources.
     *
     * @param compositionSpaceId The composition space identifier
     * @param session The session
     * @return <code>true</code> if such a composition space has been successfully closed; otherwise <code>false</code>
     * @throws OXException If closing/deleting composition space fails
     */
    boolean closeCompositionSpace(UUID compositionSpaceId, Session session) throws OXException;

    /**
     * Closes those composition spaces associated with given session, which are idle for longer than given max. idle time.
     *
     * @param maxIdleTime The max. idle time in milliseconds
     * @param session The session
     * @throws OXException If composition spaces cannot be closed/deleted
     */
    void closeExpiredCompositionSpaces(long maxIdleTimeMillis, Session session) throws OXException;

    /**
     * Gets the composition space associated with given identifier.
     *
     * @param compositionSpaceId The composition space identifier
     * @param session The session
     * @return The composition space
     * @throws OXException If composition space cannot be returned
     */
    CompositionSpace getCompositionSpace(UUID compositionSpaceId, Session session) throws OXException;

    /**
     * Gets the identifiers of all available composition spaces associated with given session.
     *
     * @param fields The fields to set in returned composition spaces
     * @param session The session
     * @return The composition space identifiers
     * @throws OXException If composition spaces cannot be returned
     */
    List<CompositionSpace> getCompositionSpaces(MessageField[] fields, Session session) throws OXException;

    /**
     * Updates the composition space associated with given identifier.
     *
     * @param compositionSpaceId The composition space identifier
     * @param messageDescription The message description providing the changes to apply
     * @param session The session
     * @return The composition space
     * @throws OXException If composition space cannot be returned
     */
    CompositionSpace updateCompositionSpace(UUID compositionSpaceId, MessageDescription messageDescription, Session session) throws OXException;

    /**
     * Adds new attachments from upload to given composition space.
     *
     * @param compositionSpaceId The composition space identifier
     * @param attachmentId The identifier of the attachment to replace
     * @param uploadedAttachments The uploaded attachments
     * @param disposition The disposition
     * @param session The session
     * @return The replaced attachments
     * @throws OXException If replacing the attachment fails
     */
    Attachment replaceAttachmentInCompositionSpace(UUID compositionSpaceId, UUID attachmentId, StreamedUploadFileIterator uploadedAttachments, String disposition, Session session) throws OXException;

    /**
     * Adds new attachments from upload to given composition space.
     *
     * @param compositionSpaceId The composition space identifier
     * @param uploadedAttachments The uploaded attachments
     * @param disposition The disposition
     * @param session The session
     * @return The added attachments
     * @throws OXException If adding the attachments fails
     */
    List<Attachment> addAttachmentToCompositionSpace(UUID compositionSpaceId, StreamedUploadFileIterator uploadedAttachments, String disposition, Session session) throws OXException;

    /**
     * Adds a new attachment to given composition space.
     *
     * @param compositionSpaceId The composition space identifier
     * @param attachment The attachment description
     * @param data The attachment's data
     * @param disposition The disposition
     * @param session The session
     * @return The added attachment
     * @throws OXException If adding the attachment fails
     */
    Attachment addAttachmentToCompositionSpace(UUID compositionSpaceId, AttachmentDescription attachment, InputStream data, Session session) throws OXException;

    /**
     * Adds user's vCard as attachment to given composition space.
     *
     * @param compositionSpaceId The composition space identifier
     * @param session The session
     * @return The added vCard attachment
     * @throws OXException If adding the vCard fails
     */
    Attachment addVCardToCompositionSpace(UUID compositionSpaceId, Session session) throws OXException;

    /**
     * Adds given contact's vCard as attachment to given composition space.
     *
     * @param compositionSpaceId The composition space identifier
     * @param contactId The identifier of the contact
     * @param folderId The identifier of the folder in which the contact resides
     * @param session The session
     * @return The added vCard attachment
     * @throws OXException If adding the vCard fails
     */
    Attachment addContactVCardToCompositionSpace(UUID compositionSpaceId, String contactId, String folderId, Session session) throws OXException;

    /**
     * Adds the attachments from the original mail (e.g. on reply) to denoted composition space
     *
     * @param compositionSpaceId The composition space identifier
     * @param session The session
     * @return The added attachments
     * @throws OXException If adding attachments fails
     */
    List<Attachment> addOriginalAttachmentsToCompositionSpace(UUID compositionSpaceId, Session session) throws OXException;

    /**
     * Deletes the specified attachment from given composition space.
     *
     * @param compositionSpaceId The composition space identifier
     * @param attachmentId The identifier of the attachment to delete
     * @param session The session
     * @throws OXException If deletion fails
     */
    void deleteAttachment(UUID compositionSpaceId, UUID attachmentId, Session session) throws OXException;

    /**
     * Gets the specified attachment from given composition space.
     *
     * @param compositionSpaceId The composition space identifier
     * @param attachmentId The identifier of the attachment to delete
     * @param session The session
     * @return The attachment
     * @throws OXException If retrieval fails
     */
    Attachment getAttachment(UUID compositionSpaceId, UUID attachmentId, Session session) throws OXException;

}
