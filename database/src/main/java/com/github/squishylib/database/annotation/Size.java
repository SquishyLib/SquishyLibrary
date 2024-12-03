/*
 * Java configuration and database library.
 * Copyright (C) 2024  Smuddgge
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.squishylib.database.annotation;

import com.github.squishylib.common.annotation.Paired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the maximum size of a field.
 * <p>
 * By default, it will assign the maximum amount of space.
 * Therefore, this annotation is suggested to save storage space.
 * <pre>
 * {@code
 * String: Maximum amount of characters.
 * Integer: Largest integer.
 * }
 * <p>
 * Defaults to {@link Integer#MAX_VALUE}.
 * <p>
 * This annotation must be paired with the
 * {@link Field} annotation.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Paired(Field.class)
public @interface Size {

    long DEFAULT_VALUE = Integer.MAX_VALUE;

    /**
     * The maximum size of the field.
     * <pre>
     * {@code
     * String: Maximum amount of characters.
     * Integer: Largest integer.
     * }
     * <p>
     * Defaults to {@link Integer#MAX_VALUE}.
     *
     * @return The maximum size of the field.
     */
    long value() default DEFAULT_VALUE;
}
