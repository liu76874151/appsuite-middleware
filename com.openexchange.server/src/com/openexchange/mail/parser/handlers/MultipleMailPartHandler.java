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

package com.openexchange.mail.parser.handlers;

import static com.openexchange.mail.parser.MailMessageParser.generateFilename;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.openexchange.exception.OXException;
import com.openexchange.mail.MailExceptionCode;
import com.openexchange.mail.config.MailProperties;
import com.openexchange.mail.dataobjects.MailMessage;
import com.openexchange.mail.dataobjects.MailPart;
import com.openexchange.mail.dataobjects.UUEncodedAttachmentMailPart;
import com.openexchange.mail.mime.ContentType;
import com.openexchange.mail.mime.MimeDefaultSession;
import com.openexchange.mail.mime.MimeMailException;
import com.openexchange.mail.mime.MimeType2ExtMap;
import com.openexchange.mail.mime.MimeTypes;
import com.openexchange.mail.mime.converters.MimeMessageConverter;
import com.openexchange.mail.mime.datasource.MessageDataSource;
import com.openexchange.mail.parser.ContentProvider;
import com.openexchange.mail.parser.MailMessageHandler;
import com.openexchange.mail.parser.MailMessageParser;
import com.openexchange.mail.uuencode.UUEncodedPart;

/**
 * {@link MultipleMailPartHandler} - Looks for multiple mail parts by sequence IDs.
 *
 * @author <a href="mailto:thorben.betten@open-xchange.com">Thorben Betten</a>
 */
public final class MultipleMailPartHandler implements MailMessageHandler {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MultipleMailPartHandler.class);

    private static final class TextMailPart extends MailPart {

        private static final long serialVersionUID = 5622318721711740585L;

        private final String text;

        private transient DataSource dataSource;

        /**
         * @param text The text content
         */
        public TextMailPart(final String text, final ContentType contentType) {
            super();
            this.text = text;
            setSize(text.length());
            if (contentType.getCharsetParameter() == null) {
                contentType.setCharsetParameter(MailProperties.getInstance().getDefaultMimeCharset());
            }
            setContentType(contentType);
        }

        private DataSource getDataSource() {
            if (null == dataSource) {
                dataSource = new MessageDataSource(text, getContentType());
            }
            return dataSource;
        }

        @Override
        public Object getContent() throws OXException {
            return text;
        }

        @Override
        public DataHandler getDataHandler() throws OXException {
            return new DataHandler(getDataSource());
        }

        @Override
        public int getEnclosedCount() throws OXException {
            return NO_ENCLOSED_PARTS;
        }

        @Override
        public MailPart getEnclosedMailPart(final int index) throws OXException {
            return null;
        }

        @Override
        public InputStream getInputStream() throws OXException {
            try {
                return getDataSource().getInputStream();
            } catch (final IOException e) {
                if ("com.sun.mail.util.MessageRemovedIOException".equals(e.getClass().getName()) || (e.getCause() instanceof MessageRemovedException)) {
                    throw MailExceptionCode.MAIL_NOT_FOUND_SIMPLE.create(e);
                }
                throw MailExceptionCode.IO_ERROR.create(e, e.getMessage());
            }
        }

        @Override
        public void loadContent() {
            // Nothing to do
        }

        @Override
        public void prepareForCaching() {
            // Nothing to do
        }

    }

    private final Set<String> ids;

    private final Map<String, MailPart> mailParts;

    private final boolean errorOnAbsence;

    /**
     * Initializes a new empty {@link MultipleMailPartHandler}.
     *
     * @param errorOnAbsence <code>true</code> to throw an error if one or more parts could not be found; otherwise <code>false</code>
     */
    public MultipleMailPartHandler(final boolean errorOnAbsence) {
        super();
        this.ids = new HashSet<>();
        mailParts = new HashMap<>();
        this.errorOnAbsence = errorOnAbsence;
    }

    /**
     * Initializes a new {@link MultipleMailPartHandler}.
     *
     * @param ids The sequence IDs to look for
     * @param errorOnAbsence <code>true</code> to throw an error if one or more parts could not be found; otherwise <code>false</code>
     */
    public MultipleMailPartHandler(final String[] ids, final boolean errorOnAbsence) {
        super();
        if (null == ids) {
            this.ids = new HashSet<>(0);
        } else {
            this.ids = new HashSet<>(Arrays.asList(ids));
        }
        mailParts = new HashMap<>(this.ids.size());
        this.errorOnAbsence = errorOnAbsence;
    }

    /**
     * Initializes a new {@link MultipleMailPartHandler}.
     *
     * @param ids The sequence IDs to look for
     * @param errorOnAbsence <code>true</code> to throw an error if one or more parts could not be found; otherwise <code>false</code>
     */
    public MultipleMailPartHandler(final Set<String> ids, final boolean errorOnAbsence) {
        super();
        if (null == ids) {
            this.ids = new HashSet<>(0);
        } else {
            this.ids = new HashSet<>(ids);
        }
        mailParts = new HashMap<>(this.ids.size());
        this.errorOnAbsence = errorOnAbsence;
    }

    /**
     * Sets sequence IDs.
     * <p>
     * Remaining mail parts are cleared.
     *
     * @param ids The sequence IDs
     */
    public void setSequenceIDs(final String[] ids) {
        this.ids.clear();
        if (null != ids) {
            this.ids.addAll(Arrays.asList(ids));
        }
        mailParts.clear();
    }

    /**
     * Sets sequence IDs.
     * <p>
     * Remaining mail parts are cleared.
     *
     * @param ids The sequence IDs
     */
    public void setSequenceIDs(final Set<String> ids) {
        this.ids.clear();
        if (null != ids) {
            this.ids.addAll(ids);
        }
        mailParts.clear();
    }

    @Override
    public boolean handleAttachment(final MailPart part, final boolean isInline, final String baseContentType, final String fileName, final String id) throws OXException {
        return handleAttachmentOrImage(part, baseContentType, isInline, id);
    }

    @Override
    public boolean handleMultipartEnd(final MailPart mp, final String id) throws OXException {
        return true;
    }

    @Override
    public boolean handleBccRecipient(final InternetAddress[] recipientAddrs) throws OXException {
        return true;
    }

    @Override
    public boolean handleCcRecipient(final InternetAddress[] recipientAddrs) throws OXException {
        return true;
    }

    @Override
    public boolean handleColorLabel(final int colorLabel) throws OXException {
        return true;
    }

    @Override
    public boolean handleContentId(final String contentId) throws OXException {
        return true;
    }

    @Override
    public boolean handleFrom(final InternetAddress[] fromAddrs) throws OXException {
        return true;
    }

    @Override
    public boolean handleHeaders(final int size, final Iterator<Entry<String, String>> iter) throws OXException {
        return true;
    }

    @Override
    public boolean handleImagePart(final MailPart part, final String imageCID, final String baseContentType, final boolean isInline, final String fileName, final String id) throws OXException {
        return handleAttachmentOrImage(part, baseContentType, isInline, id);
    }

    private boolean handleAttachmentOrImage(final MailPart part, final String baseContentType, final boolean isInline, final String id) {
        if (ids.contains(id)) {
            mailParts.put(id, part);
            if (!isInline) {
                checkFilename(part, id, baseContentType);
            }
            return (ids.size() > mailParts.size());
        }
        return true;
    }

    @Override
    public boolean handleInlineHtml(final ContentProvider htmlContent, final ContentType contentType, final long size, final String fileName, final String id) throws OXException {
        if (ids.contains(id)) {
            final MailPart mailPart = new TextMailPart(htmlContent.getContent(), contentType);
            mailPart.setContentType(contentType);
            mailPart.setSize(size);
            mailPart.setFileName(fileName);
            mailPart.setSequenceId(id);
            mailParts.put(id, mailPart);
            return (ids.size() > mailParts.size());
        }
        return true;
    }

    @Override
    public boolean handleInlinePlainText(final String plainTextContent, final ContentType contentType, final long size, final String fileName, final String id) throws OXException {
        if (ids.contains(id)) {
            final MailPart mailPart = new TextMailPart(plainTextContent, contentType);
            mailPart.setContentType(contentType);
            mailPart.setSize(size);
            mailPart.setFileName(fileName);
            mailPart.setSequenceId(id);
            mailParts.put(id, mailPart);
            return (ids.size() > mailParts.size());
        }
        return true;
    }

    @Override
    public boolean handleInlineUUEncodedAttachment(final UUEncodedPart part, final String id) throws OXException {
        if (ids.contains(id)) {
            final MailPart mailPart = new UUEncodedAttachmentMailPart(part);
            String ct = MimeType2ExtMap.getContentType(part.getFileName());
            if (ct == null || ct.length() == 0) {
                ct = MimeTypes.MIME_APPL_OCTET;
            }
            mailPart.setContentType(ct);
            mailPart.setSize(part.getFileSize());
            mailPart.setFileName(part.getFileName());
            mailPart.setSequenceId(id);
            mailParts.put(id, mailPart);
            return (ids.size() > mailParts.size());
        }
        return true;
    }

    @Override
    public boolean handleInlineUUEncodedPlainText(final String decodedTextContent, final ContentType contentType, final int size, final String fileName, final String id) throws OXException {
        return handleInlinePlainText(decodedTextContent, contentType, size, fileName, id);
    }

    @Override
    public void handleMessageEnd(final MailMessage msg) throws OXException {
        if (errorOnAbsence && ids.size() > mailParts.size()) {
            for (final String id : ids) {
                if (!mailParts.containsKey(id)) {
                    throw MailExceptionCode.ATTACHMENT_NOT_FOUND.create(id, Long.valueOf(msg.getMailId()), msg.getFolder());
                }
            }
        }
    }

    @Override
    public boolean handleMultipart(final MailPart mp, final int bodyPartCount, final String id) throws OXException {
        return true;
    }

    @Override
    public boolean handleNestedMessage(final MailPart mailPart, final String id) throws OXException {
        if (ids.contains(id)) {
            mailParts.put(id, mailPart);
            return (ids.size() > mailParts.size());
        }
        final Object content = mailPart.getContent();
        final MailMessage nestedMail;
        if (content instanceof MailMessage) {
            nestedMail = (MailMessage) content;
        } else if (content instanceof InputStream) {
            try {
                nestedMail = MimeMessageConverter.convertMessage(new MimeMessage(
                    MimeDefaultSession.getDefaultSession(),
                    (InputStream) content));
            } catch (final MessagingException e) {
                throw MimeMailException.handleMessagingException(e);
            }
        } else {
            LOG.error("Ignoring nested message. Cannot handle part's content which should be a RFC822 message according to its content type: {}", (null == content ? "null" : content.getClass().getSimpleName()));
            return true;
        }
        final MultipleMailPartHandler nestedHandler = new MultipleMailPartHandler(ids, errorOnAbsence);
        new MailMessageParser().parseMailMessage(nestedMail, nestedHandler, id);
        this.mailParts.putAll(nestedHandler.getMailParts0());
        return (ids.size() > mailParts.size());
    }

    @Override
    public boolean handlePriority(final int priority) throws OXException {
        return true;
    }

    @Override
    public boolean handleMsgRef(final String msgRef) throws OXException {
        return true;
    }

    @Override
    public boolean handleDispositionNotification(final InternetAddress dispositionNotificationTo, final boolean seen) throws OXException {
        return true;
    }

    @Override
    public boolean handleReceivedDate(final Date receivedDate) throws OXException {
        return true;
    }

    @Override
    public boolean handleSentDate(final Date sentDate) throws OXException {
        return true;
    }

    @Override
    public boolean handleSpecialPart(final MailPart part, final String baseContentType, final String fileName, final String id) throws OXException {
        return handleAttachment(
            part,
            (!Part.ATTACHMENT.equalsIgnoreCase(part.getContentDisposition().getDisposition()) && part.getFileName() == null),
            baseContentType,
            fileName,
            id);
    }

    @Override
    public boolean handleSubject(final String subject) throws OXException {
        return true;
    }

    @Override
    public boolean handleSystemFlags(final int flags) throws OXException {
        return true;
    }

    @Override
    public boolean handleToRecipient(final InternetAddress[] recipientAddrs) throws OXException {
        return true;
    }

    @Override
    public boolean handleUserFlags(final String[] userFlags) throws OXException {
        return true;
    }

    /**
     * Gets the identified mail parts matching given sequence IDs
     *
     * @return The identified mail part matching given sequence IDs
     */
    public Map<String, MailPart> getMailParts() {
        return new HashMap<>(mailParts);
    }

    private Map<String, MailPart> getMailParts0() {
        return mailParts;
    }

    private static void checkFilename(final MailPart mailPart, final String id, final String baseMimeType) {
        if (mailPart.getFileName() == null) {
            mailPart.setFileName(generateFilename(id, baseMimeType));
        }
    }

}
