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
 * Used to indicate if a field is linked to
 * another table.
 * <p>
 * This is mainly used, so you can match the create table
 * statement with your database table.
 * <p>
 * This annotation must be paired with the
 * {@link Field} annotation.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Paired(Field.class)
public @interface Foreign {

    /**
     * The table name that this foreign
     * key is linked to.
     *
     * @return The table where this field exists.
     */
    String table();

    /**
     * The name of the field in the foreign table.
     *
     * @return The columns name.
     */
    String tableField();
}
