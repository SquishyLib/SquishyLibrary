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

public class DefaultType implements DataType<Object> {

    @Override
    public @NotNull String getTypeName(Database.@NotNull Type type, long maxSize) {
        return switch (type) {
            case SQLITE -> "TEXT";
            case MYSQL -> "LONGTEXT";
            default -> "Database type not supported.";
        };
    }

    @Override
    public @Nullable Object javaToDatabaseValue(@Nullable Object value, Database.@NotNull Type type) {
        final Map<String, Object> map = new HashMap<>();
        map.put("value", value);
        return new Gson().toJson(map);
    }

    @Override
    public @Nullable Object databaseValueToJava(@NotNull ResultSet resultSet, @NotNull String fieldName, Database.@NotNull Type type) {
        try {

            return new Gson().fromJson(
                    resultSet.getString(fieldName),
                    Map.class
            ).get("value");

        } catch (Exception exception) {
            try {
                throw new DatabaseException(exception, this, "databaseValueToJava",
                        "Failed to convert field &e{field} &cwith value &e\"{value}\" &cinto &e{type}&c."
                                .replace("{field}", fieldName)
                                .replace("{value}", resultSet.getString(fieldName))
                                .replace("{type}", this.getClass().getSimpleName())
                );
            } catch (Exception exception2) {
                throw new DatabaseException(exception2, this, "databaseValueToJava",
                        "Failed to convert field &e{field} &cwith incorrect value into {type}."
                                .replace("{field}", fieldName)
                                .replace("{type}", this.getClass().getSimpleName())
                );
            }
        }
    }
}
