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

package com.github.squishylib.database.datatype;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;

/**
 * Represents a data type that can be converted to and from
 * different database types.
 *
 * @param <T> The type of object in java.
 */
public interface DataType<T> {

    /**
     * Get the data types name that can be
     * used in a sqlite database.
     *
     * @return The sqlite data type name.
     */
    @NotNull
    String getSqliteName();

    /**
     * Get the data types name that can be
     * used in a mysql database.
     *
     * @param size The maximum size of the data type.
     * @return The mysql data type name.
     */
    @NotNull
    String getMySqlName(long size);

    /**
     * Converts an object into the sqlite type.
     * <p>
     * For example a boolean would be converted into
     * an integer type as there are no booleans in sqlite.
     *
     * @param object The object to convert.
     * @return The converted object.
     */
    @Nullable
    Object toSqlite(@Nullable Object object);

    /**
     * Converts an object into the mysql type.
     * <p>
     * For example a boolean would be converted into
     * an TINYINT(2) type as there are no booleans in mysql.
     *
     * @param object The object to convert.
     * @return The converted object.
     */
    @Nullable
    Object toMySql(@Nullable Object object);

    /**
     * Converts a sqlite result back into the datatype.
     *
     * @param results   The result set.
     * @param fieldName The field's name.
     * @return The original type.
     */
    @Nullable
    T fromSqlite(@NotNull ResultSet results, @NotNull String fieldName);

    /**
     * Converts a mysql result back into the datatype.
     *
     * @param results   The result set.
     * @param fieldName The field's name.
     * @return The original type.
     */
    @Nullable
    T fromMySql(@NotNull ResultSet results, @NotNull String fieldName);

    /**
     * Used to get the datatype class of a java type
     * <p>
     * This should not be used when converting a database
     * type back into a java type as they could be different.
     *
     * @param type The class type.
     * @return The data type.
     */
    static @NotNull DataType<?> of(@NotNull Class<?> type) {
        return switch (type.getName()) {
            case "boolean", "java.land.Boolean" -> new BooleanType();
            case "string", "java.lang.String" -> new StringType();
            default -> new DefaultType();
        };
    }
}
