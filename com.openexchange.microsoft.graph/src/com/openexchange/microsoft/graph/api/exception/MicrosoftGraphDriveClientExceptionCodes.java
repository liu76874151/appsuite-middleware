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
 *     Copyright (C) 2018-2020 OX Software GmbH
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

package com.openexchange.microsoft.graph.api.exception;

import static com.openexchange.exception.OXExceptionStrings.MESSAGE;
import com.openexchange.exception.Category;
import com.openexchange.exception.DisplayableOXExceptionCode;
import com.openexchange.exception.OXException;
import com.openexchange.exception.OXExceptionFactory;

/**
 * {@link MicrosoftGraphDriveClientExceptionCodes} - Defines the client exceptions codes
 *
 * @author <a href="mailto:ioannis.chouklis@open-xchange.com">Ioannis Chouklis</a>
 * @since v7.10.2
 */
public enum MicrosoftGraphDriveClientExceptionCodes implements DisplayableOXExceptionCode {

    /**
     * <li>The copying of file %1$s failed. There is nothing we can do about it. Try again later</li>
     * <li>The asynchronous copying of item '%1$s' failed with status code '%2$s'.</li>
     */
    ASYNC_COPY_FAILED("The asynchronous copying of item '%1$s' failed with status code '%2$s'.", MicrosoftGraphDriveClientExceptionMessages.ASYNC_COPY_FAILED, CATEGORY_ERROR, 1),
    /**
     * <li>Uploading the file '%1$s' in folder '%2$s' failed. There is nothing we can do about it. Try again later.</li>
     * <li>Uploading the file '%1$s' in folder '%2$s' failed. The API returned an empty 'uploadUrl'.</li>
     */
    UPLOAD_FAILED_EMPTY_URL("Uploading the file '%1$s' in folder '%2$s' failed. The API returned an empty 'uploadUrl'.", MicrosoftGraphDriveClientExceptionMessages.UPLOAD_FAILED, CATEGORY_ERROR, 2),
    /**
     * <li>Uploading the file '%1$s' in folder '%2$s' failed. There is nothing we can do about it. Try again later.</li>
     * <li>Uploading the file '%1$s' in folder '%2$s' failed with status code '%3$s'.</li>
     */
    UPLOAD_FAILED_STATUS_CODE("Uploading the file '%1$s' in folder '%2$s' failed with status code '%3$s'.", MicrosoftGraphDriveClientExceptionMessages.UPLOAD_FAILED, CATEGORY_ERROR, 3),
    /**
     * <li>Uploading the file '%1$s' in folder '%2$s' failed. There is nothing we can do about it. Try again later.</li>
     * <li>Uploading the file '%1$s' in folder '%2$s' failed. Reached end of stream.</li>
     */
    UPLOAD_FAILED("Uploading the file '%1$s' in folder '%2$s' failed. Reached end of stream.", MicrosoftGraphDriveClientExceptionMessages.UPLOAD_FAILED, CATEGORY_ERROR, 3),
    ;

    public static final String PREFIX = "MICROSOFT-GRAPH-DRIVE-CLIENT";

    private String message;
    private String displayMessage;
    private Category category;
    private int number;

    /**
     * Initialises a new {@link MicrosoftGraphDriveClientExceptionCodes}.
     * 
     * @param message The exception message
     * @param displayMessage The display message
     * @param category The {@link Category}
     * @param number The error number
     */
    private MicrosoftGraphDriveClientExceptionCodes(String message, Category category, int number) {
        this(message, null, category, number);
    }

    /**
     * Initialises a new {@link MicrosoftGraphDriveClientExceptionCodes}.
     * 
     * @param message The exception message
     * @param displayMessage The display message
     * @param category The {@link Category}
     * @param number The error number
     */
    private MicrosoftGraphDriveClientExceptionCodes(String message, String displayMessage, Category category, int number) {
        this.message = message;
        this.displayMessage = null != displayMessage ? displayMessage : MESSAGE;
        this.category = category;
        this.number = number;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getDisplayMessage() {
        return displayMessage;
    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    @Override
    public Category getCategory() {
        return category;
    }

    @Override
    public boolean equals(OXException e) {
        return OXExceptionFactory.getInstance().equals(this, e);
    }

    /**
     * Creates a new {@link OXException} instance pre-filled with this code's attributes.
     *
     * @return The newly created {@link OXException} instance
     */
    public OXException create() {
        return OXExceptionFactory.getInstance().create(this, new Object[0]);
    }

    /**
     * Creates a new {@link OXException} instance pre-filled with this code's attributes.
     *
     * @param args The message arguments in case of printf-style message
     * @return The newly created {@link OXException} instance
     */
    public OXException create(final Object... args) {
        return OXExceptionFactory.getInstance().create(this, (Throwable) null, args);
    }

    /**
     * Creates a new {@link OXException} instance pre-filled with this code's attributes.
     *
     * @param cause The optional initial cause
     * @param args The message arguments in case of printf-style message
     * @return The newly created {@link OXException} instance
     */
    public OXException create(final Throwable cause, final Object... args) {
        return OXExceptionFactory.getInstance().create(this, cause, args);
    }
}
