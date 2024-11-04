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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;

public class LongType implements DataType<Long> {

    @Override
    public @NotNull String getSqliteName() {
        return "INTEGER";
    }

    @Override
    public @NotNull String getMySqlName(long size) {
        if (size <= 64 && size > 0) {
            return "BIT(64)";
        }
        if (size <= 32767 && size >= -32768) {
            return "SMALLINT(255)";
        }

        return "BIGINT(255)";
    }

    @Override
    public @Nullable Object toSqlite(@Nullable Object object) {
        if (!(object instanceof Long)) throw new DatabaseException(this, "toSqlite", "Object is not a instance of a long. object type: " + object.getClass().getName());
        return object;
    }

    @Override
    public @Nullable Object toMySql(@Nullable Object object) {
        return this.toSqlite(object);
    }

    @Override
    public @Nullable Long fromSqlite(@NotNull ResultSet results, @NotNull String fieldName) {
        try {
            return results.getLong(fieldName);
        } catch (Exception exception) {
            throw new DatabaseException(exception, this, "fromSqlite",
                    "Unable to get the result value from the result set as a long. fieldName=" + fieldName
            );
        }
    }

    @Override
    public @Nullable Long fromMySql(@NotNull ResultSet results, @NotNull String fieldName) {
        return this.fromSqlite(results, fieldName);
    }
}
