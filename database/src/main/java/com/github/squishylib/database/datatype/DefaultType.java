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
import java.util.HashMap;
import java.util.Map;

public class DefaultType implements DataType<Object> {

    @Override
    public @NotNull String getSqliteName() {
        return "TEXT";
    }

    @Override
    public @NotNull String getMySqlName(long size) {
        return "LONGTEXT";
    }

    @Override
    public @Nullable Object toSqlite(@Nullable Object object) {
        final Map<String, Object> map = new HashMap<>();
        map.put("value", object);
        return new Gson().toJson(map);
    }

    @Override
    public @Nullable Object toMySql(@Nullable Object object) {
        return toSqlite(object);
    }

    @Override
    public @Nullable Object fromSqlite(@NotNull ResultSet results, @NotNull String fieldName) {
        try {
            return new Gson().fromJson(results.getString(fieldName), Map.class).get("value");
        } catch (Exception exception) {
            throw new DatabaseException(exception, this, "fromSqlite",
                    "Unable to get the result value from the result set as a string. fieldName=" + fieldName
            );
        }
    }

    @Override
    public @Nullable Object fromMySql(@NotNull ResultSet results, @NotNull String fieldName) {
        return fromSqlite(results, fieldName);
    }
}
