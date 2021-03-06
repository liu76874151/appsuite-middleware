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

package com.openexchange.annotation;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be applied to a package, type, method or constructor in order to
 * define that contained entities for which a null annotation is otherwise lacking
 * should be considered as {@link NonNull @NonNull}. Entities affected by
 * <code>@NonNullByDefault</code> are:
 * <ul>
 * <li>method return values</li>
 * <li>parameters of a method or constructor</li>
 * <li>fields.</li>
 * </ul>
 * Local variables are <em>not</em> affected.
 * <dl>
 * <dt>Canceling a default</dt>
 * <dd>By using a <code>@NonNullByDefault</code> annotation with the argument <code>false</code>,
 * a default from any enclosing scope can be canceled for the element being annotated.
 * <dt>Nested defaults</dt>
 * <dd>If a <code>@NonNullByDefault</code>
 * annotation is used within the scope of another <code>@NonNullByDefault</code>
 * annotation, the innermost annotation defines the
 * default applicable at any given position (depending on the parameter {@link #value()}).</dd>
 * </dl>
 * Note that for applying an annotation to a package, a file by the name
 * <code>package-info.java</code> is used.
 *
 * @since 7.4.0
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ PACKAGE, TYPE, METHOD, CONSTRUCTOR })
public @interface NonNullByDefault {
	/**
	 * When parameterized with <code>false</code>, the annotation specifies that the current element should not apply
	 * any default to un-annotated types.
	 */
	boolean value() default true;
}
