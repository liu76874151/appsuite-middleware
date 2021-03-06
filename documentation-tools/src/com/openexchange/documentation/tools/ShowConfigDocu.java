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

package com.openexchange.documentation.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import com.openxchange.documentation.tools.internal.ConfigDocu;
import com.openxchange.documentation.tools.internal.Property;

/**
 * {@link ShowConfigDocu} is a CLT which provides access to the config documentation.
 *
 * @author <a href="mailto:kevin.ruthmann@open-xchange.com">Kevin Ruthmann</a>
 * @since v7.10.0
 */
@SuppressWarnings("static-access")
public class ShowConfigDocu {

    private static final Options options = new Options();
    private static final String DEFAULT_FOLDER = "/opt/open-xchange/documentation/etc";

    private static final String SEARCH_OPTION = "s";
    private static final String TAG_OPTION = "t";
    private static final String KEY_OPTION = "k";
    private static final String HELP_OPTION = "h";
    private static final String ANSI_OPTION = "n";
    private static final String ONLY_KEY_OPTION = "o";
    private static final String PRINT_TAG_OPTION = "p";

    private static final Comparator<String> IGNORE_CASE_COMP = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    };

    static {
        options.addOption(Option.builder(HELP_OPTION).longOpt("help").hasArg(false).desc("Prints this usage.").required(false).build());
        options.addOption(Option.builder(TAG_OPTION).longOpt("tag").hasArg().numberOfArgs(1).desc("If set only properties with the given tag are returned.").required(false).build());
        options.addOption(Option.builder(SEARCH_OPTION).longOpt("search").hasArg().numberOfArgs(1).desc("If set only properties which match the given search term are returned.").required(false).build());
        options.addOption(Option.builder(KEY_OPTION).longOpt("key").hasArg().numberOfArgs(1).desc("If set only properties with a key which contains the given term are returned.").required(false).build());
        options.addOption(Option.builder(ONLY_KEY_OPTION).longOpt("only-key").hasArg(false).desc("Prints only the key for each property.").required(false).build());
        options.addOption(Option.builder(PRINT_TAG_OPTION).longOpt("print-tags").hasArg(false).desc("Prints a list of available tags.").required(false).build());
        options.addOption(Option.builder(ANSI_OPTION).longOpt("no-color").hasArg(false).desc("Removes ansi color formatting.").required(false).build());
    }

    private static final String CONFIG_CASCADE_TAG  = "Config Cascade";
    private static final String RELOADABLE_TAG  = "Reloadable";

    public static void main(String[] args) {

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine parse = parser.parse(options, args);
            if(parse.hasOption(HELP_OPTION)) {
                printUsage(1);
            }

            File yamlFolder = new File(DEFAULT_FOLDER);
            if (!yamlFolder.exists() || !yamlFolder.isDirectory()) {
                System.out.println("Unable to find the config documentation. The folder containing the docu doesn't exists ('"+DEFAULT_FOLDER+"').");
                System.exit(2);
            }

            try {
                ConfigDocu configDocu = new ConfigDocu(yamlFolder);

                if(parse.hasOption(PRINT_TAG_OPTION)) {
                    Set<String> tags = configDocu.getTags();
                    tags.add(CONFIG_CASCADE_TAG);
                    tags.add(RELOADABLE_TAG);
                    printTags(tags);
                    System.exit(0);
                }

                boolean onlyKey = parse.hasOption(ONLY_KEY_OPTION);

                List<Property> props = null;
                if(parse.hasOption(TAG_OPTION)) {
                    String tag = parse.getOptionValue(TAG_OPTION);
                    if(tag == null) {
                        props = configDocu.getProperties();
                    }
                    switch(tag) {
                        case RELOADABLE_TAG:
                            props = configDocu.getProperties();
                            props = props.stream()
                                .filter(prop -> prop.isReloadable())
                                .collect(Collectors.toList());
                            break;
                        case CONFIG_CASCADE_TAG:
                            props = configDocu.getProperties();
                            props = props.stream()
                                .filter(prop -> prop.isConfigcascadeAware())
                                .collect(Collectors.toList());
                            break;
                        default:
                            props = configDocu.getProperties(tag);
                            break;
                    }

                } else {
                    props = configDocu.getProperties();
                }

                if(parse.hasOption(KEY_OPTION)) {
                    String term = parse.getOptionValue(KEY_OPTION);
                    props = props.stream()
                        .filter(prop -> prop.getKey().contains(term))
                        .collect(Collectors.toList());
                }
                if(parse.hasOption(SEARCH_OPTION)) {
                    String term = parse.getOptionValue(SEARCH_OPTION);
                    props = props.stream()
                        .filter(prop -> prop.contains(term))
                        .collect(Collectors.toList());
                }

                boolean useAnsi = true;
                if(parse.hasOption(ANSI_OPTION)) {
                    useAnsi = Boolean.valueOf(parse.getOptionValue(ANSI_OPTION));
                }

                printProperties(props, useAnsi, onlyKey);
            } catch (FileNotFoundException e) {
                handleError(e);
            }
        } catch (ParseException e1) {
            handleError(e1);
        }
    }

    /**
     * Prints the given tags as a list
     *
     * @param tags The tags
     */
    private static void printTags(Set<String> tags) {
        List<String> sortedTags = new ArrayList<>(tags);
        Collections.sort(sortedTags, IGNORE_CASE_COMP);
        System.out.println("Available tags:\n");
        for(String tag : sortedTags) {
            System.out.println(tag);
        }

    }

    private static void printProperties(List<Property> props, boolean useAnsi, boolean onlyKey) {
        if (useAnsi) {
            System.out.println(ANSI_BOLD + "Properties:" + ANSI_RESET);
        } else {
            System.out.println("Properties:");
        }
        for (Property prop : props) {
            printProperty(prop, null, useAnsi, onlyKey);
            if (!onlyKey) {
                System.out.println("\n" + format("-------------------------------------------------", ANSI_RED, useAnsi) + "\n");
            }
        }
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_FRAME = "\u001B[51m";

    private static void printProperty(Property prop, String currentValue, boolean useAnsi, boolean onlyKey) {
        printKeyValue("Key", prop.getKey(), useAnsi);
        if(onlyKey) {
            return;
        }
        System.out.println(format("Description", ANSI_RED, useAnsi)+":");
        System.out.println(formatDescription(prop.getDescription(), useAnsi));
        printKeyValue("Default", prop.getDefaultValue(), true, useAnsi);
        if (currentValue != null) {
            printKeyValue("Configured value", currentValue, true, useAnsi);
        }
        printKeyValue("File", prop.getFile(), useAnsi);
        printKeyValue("Version", prop.getVersion(), useAnsi);
        printKeyValue("Package", prop.getPackageName(), useAnsi);
        System.out.print(format("Tags", ANSI_RED, useAnsi)+": ");
        boolean first = true;
        if (prop.isConfigcascadeAware() || prop.isReloadable()) {
            if (prop.isConfigcascadeAware()) {
                System.out.print(CONFIG_CASCADE_TAG);
                if (prop.isReloadable()) {
                    System.out.print(format(" | ", ANSI_RED, useAnsi) + RELOADABLE_TAG);
                }
            } else {
                System.out.print(RELOADABLE_TAG);
            }
            first = false;
        }
        for(String tag: prop.getTags()) {
            if(first) {
                System.out.print(tag);
                first = false;
            } else {
                System.out.print(format(" | ", ANSI_RED, useAnsi)+tag);
            }
        }
    }

    private static String formatDescription(String value, boolean useAnsi) {
        if(useAnsi) {
            value = value.replaceAll("<code>|\\[\\[", ANSI_FRAME+" ");
            value = value.replaceAll("</code>|\\]\\]", " "+ANSI_RESET);
            value = value.replaceAll("<b>", ANSI_BOLD);
            value = value.replaceAll("</b>", ANSI_RESET);
        } else {
            value = value.replaceAll("\\[\\[|\\]\\]", "");
        }
        value = value.replaceAll("<li>", "-");
        value = value.replaceAll("\\<[^>]*>", "");
        return "  " + value.replaceAll("\n", "\n  ");
    }

    private static String format(String value, String color, boolean useAnsi) {
        if(useAnsi) {
            return color+value+ANSI_RESET;
        }
        return value;
    }

    private static void printKeyValue(String key, String value, boolean useAnsi) {
        printKeyValue(key, value, false, useAnsi);
    }

    private static void printKeyValue(String key, String value, boolean printEmptyValue, boolean useAnsi) {
        if(value!=null) {
            System.out.println(format(key, ANSI_RED, useAnsi)+": "+value);
        } else if(printEmptyValue) {
            System.out.println(format(key, ANSI_RED, useAnsi)+": ");
        }
    }

    /**
     * Prints an error message with exit code 3
     *
     * @param e The error
     */
    private static final void handleError(Exception e){
        System.err.println("An error occured: " + e.getMessage());
        System.exit(3);
    }

    /**
     * Print usage
     *
     * @param exitCode
     */
    private static final void printUsage(int exitCode) {
        HelpFormatter hf = new HelpFormatter();
        hf.setWidth(120);
        hf.printHelp("showconfigdocu", options);
        System.exit(exitCode);
    }
}
