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

import com.github.squishylib.database.DatabaseException;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.util.Map;

public class DefaultType implements DataType<Map<String, Object>> {

    @Override
    public boolean isType(@NotNull Object value) {
        return value instanceof Map;
    }

    @Override
    public @NotNull String getSqliteName() {
        return "TEXT";
    }

    @Override
    public @NotNull String toSqlite(@Nullable Map<String, Object> object) {
        return new Gson().toJson(object);
    }

    @SuppressWarnings("all")
    @Override
    public @Nullable Map<String, Object> fromSqlite(@Nullable ResultSet results, @NotNull String fieldName) {
        try {
            return new Gson().fromJson(results.getString(fieldName), Map.class);
        } catch (Exception exception) {
            throw new DatabaseException(exception, this, "fromSqlite",
                    "Unable to get the result value from the result set. fieldName=" + fieldName
            );
        }
    }
}
