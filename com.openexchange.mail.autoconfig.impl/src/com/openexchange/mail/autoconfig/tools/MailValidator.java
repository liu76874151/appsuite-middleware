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

package com.openexchange.mail.autoconfig.tools;

import static com.openexchange.java.Autoboxing.I;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.openexchange.config.ConfigurationService;
import com.openexchange.java.Strings;
import com.openexchange.net.ssl.SSLSocketFactoryProvider;
import com.sun.mail.smtp.SMTPTransport;

/**
 * {@link MailValidator}
 *
 * @author <a href="mailto:martin.herfurth@open-xchange.com">Martin Herfurth</a>
 */
public class MailValidator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MailValidator.class);

    private static final int DEFAULT_CONNECT_TIMEOUT = 1000;
    private static final int DEFAULT_TIMEOUT = 10000;

    /**
     * Validates for successful authentication against specified IMAP server.
     *
     * @param host The IMAP host
     * @param port The IMAP port
     * @param connectMode The connect mode to use
     * @param user The login
     * @param pwd The password
     * @return <code>true</code> for successful authentication, otherwise <code>false</code> for failed authentication
     */
    public static boolean validateImap(String host, int port, ConnectMode connectMode, String user, String pwd) {
        Store store = null;
        try {
            SSLSocketFactoryProvider factoryProvider = Services.getService(SSLSocketFactoryProvider.class);
            String socketFactoryClass = factoryProvider.getDefault().getClass().getName();
            Properties props = new Properties();
            if (ConnectMode.SSL == connectMode) {
                props.put("mail.imap.socketFactory.class", socketFactoryClass);
            } else if (ConnectMode.STARTTLS == connectMode) {
                props.put("mail.imap.starttls.required", Boolean.TRUE);
                props.put("mail.imap.ssl.trust", "*");
            } else {
                props.put("mail.imap.ssl.socketFactory.class", socketFactoryClass);
                props.put("mail.imap.ssl.socketFactory.port", I(port));
                props.put("mail.imap.starttls.enable", Boolean.TRUE);
                props.put("mail.imap.ssl.trust", "*");
                {
                    final ConfigurationService configuration = Services.getService(ConfigurationService.class);
                    final String sslProtocols = configuration.getProperty("com.openexchange.imap.ssl.protocols", "SSLv3 TLSv1").trim();
                    props.put("mail.imap.ssl.protocols", sslProtocols);
                }
                {
                    final ConfigurationService configuration = Services.getService(ConfigurationService.class);
                    final String cipherSuites = configuration.getProperty("com.openexchange.imap.ssl.ciphersuites", "").trim();
                    if (Strings.isNotEmpty(cipherSuites)) {
                        props.put("mail.imap.ssl.ciphersuites", cipherSuites);
                    }
                }
            }
            props.put("mail.imap.socketFactory.fallback", "false");
            props.put("mail.imap.connectiontimeout", I(DEFAULT_CONNECT_TIMEOUT));
            props.put("mail.imap.timeout", I(DEFAULT_TIMEOUT));
            props.put("mail.imap.socketFactory.port", I(port));
            Session session = Session.getInstance(props, null);
            store = session.getStore("imap");
            store.connect(host, port, user, pwd);
            closeSafe(store);
            store = null;
        } catch (AuthenticationFailedException e) {
            return false;
        } catch (MessagingException e) {
            return false;
        } finally {
            closeSafe(store);
        }
        return true;
    }

    /**
     * Validates for successful authentication against specified POP3 server.
     *
     * @param host The POP3 host
     * @param port The POP3 port
     * @param connectMode The connect mode to use
     * @param user The login
     * @param pwd The password
     * @return <code>true</code> for successful authentication, otherwise <code>false</code> for failed authentication
     */
    public static boolean validatePop3(String host, int port, ConnectMode connectMode, String user, String pwd) {
        Store store = null;
        try {
            Properties props = new Properties();
            SSLSocketFactoryProvider factoryProvider = Services.getService(SSLSocketFactoryProvider.class);
            String socketFactoryClass = factoryProvider.getDefault().getClass().getName();
            if (ConnectMode.SSL == connectMode) {
                props.put("mail.pop3.socketFactory.class", socketFactoryClass);
            } else if (ConnectMode.STARTTLS == connectMode) {
                props.put("mail.pop3.starttls.required", Boolean.TRUE);
                props.put("mail.pop3.ssl.trust", "*");
            } else {
                props.put("mail.pop3.ssl.socketFactory.class", socketFactoryClass);
                props.put("mail.pop3.ssl.socketFactory.port", I(port));
                props.put("mail.pop3.starttls.enable", Boolean.TRUE);
                props.put("mail.pop3.ssl.trust", "*");
                {
                    final ConfigurationService configuration = Services.getService(ConfigurationService.class);
                    final String sslProtocols = configuration.getProperty("com.openexchange.pop3.ssl.protocols", "SSLv3 TLSv1").trim();
                    props.put("mail.pop3.ssl.protocols", sslProtocols);
                }
                {
                    final ConfigurationService configuration = Services.getService(ConfigurationService.class);
                    final String cipherSuites = configuration.getProperty("com.openexchange.pop3.ssl.ciphersuites", "").trim();
                    if (Strings.isNotEmpty(cipherSuites)) {
                        props.put("mail.pop3.ssl.ciphersuites", cipherSuites);
                    }
                }
            }
            props.put("mail.pop3.socketFactory.fallback", "false");
            props.put("mail.pop3.socketFactory.port", I(port));
            props.put("mail.pop3.connectiontimeout", I(DEFAULT_CONNECT_TIMEOUT));
            props.put("mail.pop3.timeout", I(DEFAULT_TIMEOUT));
            Session session = Session.getInstance(props, null);
            store = session.getStore("pop3");
            store.connect(host, port, user, pwd);
            closeSafe(store);
            store = null;
        } catch (AuthenticationFailedException e) {
            return false;
        } catch (MessagingException e) {
            return false;
        } finally {
            closeSafe(store);
        }
        return true;
    }

    /**
     * Validates for successful authentication against specified SMTP server.
     *
     * @param host The SMTP host
     * @param port The SMTP port
     * @param connectMode The connect mode to use
     * @param user The login
     * @param pwd The password
     * @return <code>true</code> for successful authentication, otherwise <code>false</code> for failed authentication
     */
    public static boolean validateSmtp(String host, int port, ConnectMode connectModes, String user, String pwd) {
        return validateSmtp(host, port, connectModes, user, pwd, null);
    }

    /**
     * Validates for successful authentication against specified SMTP server.
     *
     * @param host The SMTP host
     * @param port The SMTP port
     * @param connectMode The connect mode to use
     * @param user The login
     * @param pwd The password
     * @param optProperties The optional container for arbitrary properties
     * @return <code>true</code> for successful authentication, otherwise <code>false</code> for failed authentication
     */
    public static boolean validateSmtp(String host, int port, ConnectMode connectMode, String user, String pwd, Map<String, Object> optProperties) {
        Transport transport = null;
        try {
            SSLSocketFactoryProvider factoryProvider = Services.getService(SSLSocketFactoryProvider.class);
            String socketFactoryClass = factoryProvider.getDefault().getClass().getName();
            Properties props = new Properties();
            if (ConnectMode.SSL == connectMode) {
                props.put("mail.smtp.socketFactory.class", socketFactoryClass);
            } else if (ConnectMode.STARTTLS == connectMode) {
                props.put("mail.smtp.starttls.required", Boolean.TRUE);
                props.put("mail.smtp.ssl.trust", "*");
            } else {
                props.put("mail.smtp.ssl.socketFactory.class", socketFactoryClass);
                props.put("mail.smtp.ssl.socketFactory.port", I(port));
                props.put("mail.smtp.starttls.enable", Boolean.TRUE);
                props.put("mail.smtp.ssl.trust", "*");
                {
                    final ConfigurationService configuration = Services.getService(ConfigurationService.class);
                    final String sslProtocols = configuration.getProperty("com.openexchange.smtp.ssl.protocols", "SSLv3 TLSv1").trim();
                    props.put("mail.smtp.ssl.protocols", sslProtocols);
                }
                {
                    final ConfigurationService configuration = Services.getService(ConfigurationService.class);
                    final String cipherSuites = configuration.getProperty("com.openexchange.smtp.ssl.ciphersuites", "").trim();
                    if (Strings.isNotEmpty(cipherSuites)) {
                        props.put("mail.smtp.ssl.ciphersuites", cipherSuites);
                    }
                }
            }
            props.put("mail.smtp.socketFactory.port", I(port));
            //props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.connectiontimeout", I(DEFAULT_CONNECT_TIMEOUT));
            props.put("mail.smtp.timeout", I(DEFAULT_TIMEOUT));
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.put("mail.smtp.auth", "true");
            Session session = Session.getInstance(props, null);
            transport = session.getTransport("smtp");
            transport.connect(host, port, user, pwd);

            if (null != optProperties) {
                final SMTPTransport smtpTransport = (SMTPTransport) transport;
                if (!smtpTransport.supportsExtension("AUTH") && !smtpTransport.supportsExtension("AUTH=LOGIN")) {
                    // No authentication mechanism supported
                    optProperties.put("smtp.auth-supported", Boolean.FALSE);
                }
            }

            closeSafe(transport);
            transport = null;
        } catch (AuthenticationFailedException e) {
            return false;
        } catch (MessagingException e) {
            return false;
        } finally {
            closeSafe(transport);
        }
        return true;
    }

    // ------------------------------------------------------- Connect tests ---------------------------------------------------------

    /**
     * Checks if a (SSL) socket connection can be established to the specified IMAP end-point (host & port)
     *
     * @param host The IMAP host
     * @param port The IMAP port
     * @param secure Whether to create an SSL socket or a plain one.
     * @return <code>true</code> if such a socket could be successfully linked to the given IMAP end-point; otherwise <code>false</code>
     */
    public static boolean tryImapConnect(String host, int port, boolean secure) {
        return tryConnect(host, port, secure, "A11 LOGOUT\r\n");
    }

    /**
     * Checks if a (SSL) socket connection can be established to the specified SMTP end-point (host & port)
     *
     * @param host The SMTP host
     * @param port The SMTP port
     * @param secure Whether to create an SSL socket or a plain one.
     * @return <code>true</code> if such a socket could be successfully linked to the given SMTP end-point; otherwise <code>false</code>
     */
    public static boolean trySmtpConnect(String host, int port, boolean secure) {
        return tryConnect(host, port, secure, "QUIT\r\n");
    }

    /**
     * Checks if a (SSL) socket connection can be established to the specified POP3 end-point (host & port)
     *
     * @param host The POP3 host
     * @param port The POP3 port
     * @param secure Whether to create an SSL socket or a plain one.
     * @return <code>true</code> if such a socket could be successfully linked to the given POP3 end-point; otherwise <code>false</code>
     */
    public static boolean tryPop3Connect(String host, int port, boolean secure) {
        return tryConnect(host, port, secure, "QUIT\r\n");
    }
    
    private static boolean tryConnect(String host, int port, boolean secure, String closePhrase) {
        try (Socket s = secure ? Services.getService(SSLSocketFactoryProvider.class).getDefault().createSocket() : new Socket()) {
            /*
             * Set connect timeout
             */
            s.connect(new InetSocketAddress(host, port), DEFAULT_CONNECT_TIMEOUT);
            s.setSoTimeout(DEFAULT_TIMEOUT);
            InputStream in = s.getInputStream();
            OutputStream out = s.getOutputStream();
            if (null == in || null == out) {
                return false;
            }
            /*
             * Read IMAP server greeting on connect
             */
            boolean eol = false;
            boolean skipLF = false;
            int i = -1;
            while (!eol && ((i = in.read()) != -1)) {
                final char c = (char) i;
                if (c == '\r') {
                    eol = true;
                    skipLF = true;
                } else if (c == '\n') {
                    eol = true;
                    skipLF = false;
                }
                // else; Ignore
            }

            /*
             * Consume final LF
             */
            if (skipLF && -1 == in.read()) {
                LOGGER.trace("Final LF should have been read but the end of the stream was already reached.");
            }

            out.write(closePhrase.getBytes(StandardCharsets.ISO_8859_1));
            out.flush();
        } catch (Exception e) {
            LOGGER.trace("Unable to connect.", e);
            return false;
        }
        return true;
    }

    private static void closeSafe(AutoCloseable s) {
        if (s != null) {
            try {
                s.close();
            } catch (Exception e) {
                // Ignore
                LOGGER.trace("Unable to close resource.", e);
            }
        }
    }
}
