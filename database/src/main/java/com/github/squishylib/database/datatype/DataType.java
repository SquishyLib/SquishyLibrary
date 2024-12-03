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

import com.github.squishylib.database.Database;
import com.github.squishylib.database.annotation.Size;
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
     * The data type's name given the type of database.
     * <p>
     * For example, in Sqlite a String would be type TEXT
     * and in mysql it would potentially be VARCHAR(65535).
     *
     * @param type    The type of database.
     * @param maxSize The max size needed for the data type.
     * @return The data type's name.
     */
    @NotNull
    String getTypeName(@NotNull Database.Type type, long maxSize);

    /**
     * The data type's name given the type of database.
     * <p>
     * For example, in Sqlite a String would be type TEXT
     * and in mysql it would potentially be VARCHAR(65535).
     * <p>
     * The max size of the data type will be
     * {@link Size#DEFAULT_VALUE}.
     *
     * @param type The type of database.
     * @return The data type's name.
     */
    default @NotNull String getTypeName(@NotNull Database.Type type) {
        return this.getTypeName(type, Size.DEFAULT_VALUE);
    }

    /**
     * Converts a java object into an appropriate
     * format for the database.
     * <p>
     * For example, Sqlite doesn't support booleans out the box so
     * the boolean data type would be converted into a 1 or 0.
     *
     * @param value The java value.
     * @param type  The database type.
     * @return The converted value that is suitable for the database.
     */
    @Nullable
    Object javaToDatabaseValue(@Nullable Object value, @NotNull Database.Type type);

    /**
     * Converts a database value back into the java value.
     * <p>
     * For example, as the sqlite database doesn't support booleans,
     * and they are being represented as integers. This method will
     * convert them back into a boolean.
     *
     * @param resultSet The sql result set containing the values.
     * @param fieldName The name of the field to extract.
     * @param type      The database type.
     * @return The java data type.
     */
    @Nullable
    T databaseValueToJava(@NotNull ResultSet resultSet, @NotNull String fieldName, @NotNull Database.Type type);

    /**
     * Used to get the datatype class of a java type.
     * <p>
     * This should not be used when converting a database
     * type back into a java type as they could be different.
     *
     * @param type The class type.
     * @return The data type.
     */
    static @NotNull DataType<?> of(@NotNull Class<?> type) {
        return switch (type.getSimpleName()) {
            case "boolean", "Boolean", "java.land.Boolean" -> new BooleanType();
            case "string", "String", "java.lang.String" -> new StringType();
            case "int", "Integer", "java.lang.Integer" -> new IntegerType();
            case "long", "Long", "java.lang.Long" -> new LongType();
            case "float", "Float", "java.lang.Float" -> new FloatType();
            case "double", "Double", "java.lang.Double" -> new DoubleType();
            default -> new DefaultType();
        };
    }

    /**
     * Used to get the datatype class of a java type.
     * <p>
     * This should not be used when converting a database
     * type back into a java type as they could be different.
     *
     * @param object The instance of the object.
     * @return The data type.
     */
    static @NotNull DataType<?> of(@Nullable Object object) {
        if (object instanceof Boolean) return new BooleanType();
        if (object instanceof String) return new StringType();
        if (object instanceof Integer) return new IntegerType();
        if (object instanceof Long) return new LongType();
        if (object instanceof Float) return new FloatType();
        if (object instanceof Double) return new DoubleType();
        return new DefaultType();
    }
}
