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

public class DoubleType implements DataType<Double> {

    @Override
    public @NotNull String getSqliteName() {
        return "REAL";
    }

    @Override
    public @NotNull String getMySqlName(long size) {
        return "DECIMAL(65)";
    }

    @Override
    public @Nullable Object toSqlite(@Nullable Object object) {
        if (!(object instanceof Double)) throw new DatabaseException(this, "toSqlite", "Object is not a instance of a double. object type: " + object.getClass().getName());
        return object;
    }

    @Override
    public @Nullable Object toMySql(@Nullable Object object) {
        return this.toSqlite(object);
    }

    @Override
    public @Nullable Double fromSqlite(@NotNull ResultSet results, @NotNull String fieldName) {
        try {
            return results.getDouble(fieldName);
        } catch (Exception exception) {
            throw new DatabaseException(exception, this, "fromSqlite",
                    "Unable to get the result value from the result set as a double. fieldName=" + fieldName
            );
        }
    }

    @Override
    public @Nullable Double fromMySql(@NotNull ResultSet results, @NotNull String fieldName) {
        return this.fromSqlite(results, fieldName);
    }
}