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

package com.openexchange.imagetransformation;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import com.openexchange.osgi.annotation.SingletonService;

/**
 * {@link ImageMetadataService} - A service for providing several meta-data information for an image.
 *
 * @author <a href="mailto:thorben.betten@open-xchange.com">Thorben Betten</a>
 * @since v7.8.4
 */
@SingletonService
public interface ImageMetadataService {

    /**
     * Gets the {@link Dimension dimension} for specified image data.
     *
     * @param imageStream The image data
     * @param mimeType The image MIME type
     * @param name The image name
     * @return The dimension
     * @throws IOException If dimension cannot be returned
     */
    Dimension getDimensionFor(InputStream imageStream, String mimeType, String name) throws IOException;

    /**
     * Gets the meta-data for specified image data.
     *
     * @param imageStream The image data
     * @param mimeType The image MIME type
     * @param name The image name
     * @param options The options to consider when retrieving image's meta-data
     * @return The meta-data
     * @throws IOException If meta-data cannot be returned
     */
    ImageMetadata getMetadataFor(InputStream imageStream, String mimeType, String name, ImageMetadataOptions options) throws IOException;

}
