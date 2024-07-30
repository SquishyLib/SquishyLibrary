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

    boolean isType(@NotNull Object value);

    @NotNull
    String getSqliteName();

    @NotNull
    String toSqlite(@Nullable T object);

    @Nullable
    T fromSqlite(@Nullable ResultSet results, @NotNull String fieldName);

    static @NotNull DataType<?> of(@NotNull Class<?> type) {
        return switch (type.getName()) {
            case "boolean", "java.land.Boolean" -> new BooleanType();
            default -> new DefaultType();
        };
    }
}
