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
import com.github.squishylib.database.DatabaseException;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class DoubleType implements DataType<Double> {

    @Override
    public @NotNull String getTypeName(Database.@NotNull Type type, long maxSize) {
        return switch (type) {
            case SQLITE -> "REAL";
            case MYSQL -> "DECIMAL(65)";
            default -> "Database type not supported.";
        };
    }

    @Override
    public @Nullable Object javaToDatabaseValue(@Nullable Object value, Database.@NotNull Type type) {
        if (value instanceof Double) return value;

        // Otherwise the object is not a boolean.
        throw new DatabaseException(this, "javaToDatabaseValue",
                "Value is not the correct type. The correct type is {correct} and it was {type}"
                        .replace("{correct}", this.getClass().getSimpleName())
                        .replace("{type}", (value == null ? "null" : value.getClass().getName()))
        );
    }

    @Override
    public @Nullable Double databaseValueToJava(@NotNull ResultSet resultSet, @NotNull String fieldName, Database.@NotNull Type type) {
        try {

            return resultSet.getDouble(fieldName);

        } catch (Exception exception) {
            throw new DatabaseException(this, "databaseValueToJava",
                    "Failed to convert field {field} into {type}."
                            .replace("{field}", fieldName)
                            .replace("{type}", this.getClass().getSimpleName())
            );
        }
    }
}