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

package com.openexchange.mail.compose.json.osgi;

import javax.servlet.ServletException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import com.openexchange.ajax.requesthandler.ResultConverter;
import com.openexchange.ajax.requesthandler.osgiservice.AJAXModuleActivator;
import com.openexchange.dispatcher.DispatcherPrefixService;
import com.openexchange.file.storage.composition.IDBasedFileAccessFactory;
import com.openexchange.mail.compose.CompositionSpaceService;
import com.openexchange.mail.compose.json.MailComposeActionFactory;
import com.openexchange.mail.compose.json.converter.AttachmentJSONResultConverter;
import com.openexchange.mail.compose.json.converter.CompositionSpaceJSONResultConverter;
import com.openexchange.mail.compose.json.rest.MailComposeRestServlet;
import com.openexchange.osgi.SimpleRegistryListener;
import com.openexchange.threadpool.ThreadPoolService;

/**
 * {@link MailComposeJSONActivator} - Activator for the mail compose JSON interface.
 *
 * @author <a href="mailto:thorben.betten@open-xchange.com">Thorben Betten</a>
 */
public class MailComposeJSONActivator extends AJAXModuleActivator {

    @Override
    protected Class<?>[] getNeededServices() {
        return new Class<?>[] { CompositionSpaceService.class, ThreadPoolService.class, DispatcherPrefixService.class,
            IDBasedFileAccessFactory.class };
    }

    @Override
    protected void startBundle() throws Exception {
        final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MailComposeJSONActivator.class);
        registerModule(new MailComposeActionFactory(this), MailComposeActionFactory.getModule());
        registerService(ResultConverter.class, new CompositionSpaceJSONResultConverter());
        registerService(ResultConverter.class, new AttachmentJSONResultConverter());

        DispatcherPrefixService dispatcherPrefixService = getService(DispatcherPrefixService.class);
        final String prefix = dispatcherPrefixService.getPrefix();
        track(HttpService.class, new SimpleRegistryListener<HttpService>() {

            @Override
            public void added(final ServiceReference<HttpService> ref, final HttpService service) {
                try {
                    service.registerServlet(prefix + "mail/compose", new MailComposeRestServlet(prefix), null, null);
                } catch (final ServletException e) {
                    log.error("Servlet registration failed: {}", MailComposeRestServlet.class.getName(), e);
                } catch (final NamespaceException e) {
                    log.error("Servlet registration failed: {}", MailComposeRestServlet.class.getName(), e);
                }
            }

            @Override
            public void removed(final ServiceReference<HttpService> ref, final HttpService service) {
                service.unregister(prefix + "mail/compose");
            }
        });
        openTrackers();
        log.info("Bundle successfully started: com.openexchange.mail.compose.json");
    }

    @Override
    protected void stopBundle() throws Exception {
        super.stopBundle();
        org.slf4j.LoggerFactory.getLogger(MailComposeJSONActivator.class).info("Bundle stopped: com.openexchange.mail.compose.json");
    }
}
