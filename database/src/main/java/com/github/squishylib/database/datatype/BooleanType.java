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

public class BooleanType implements DataType<Boolean> {

    @Override
    public boolean isType(@NotNull Object value) {
        return value instanceof Boolean;
    }

    @Override
    public @NotNull String getSqliteName() {
        return "INTEGER";
    }

    @Override
    public @NotNull String toSqlite(@Nullable Object object) {
        if (!(object instanceof Boolean)) throw new DatabaseException(this, "toSqlite", "Object is not a boolean. object=" + object);
        return Boolean.TRUE.equals(object) ? "1" : "0";
    }

    @Override
    public @Nullable Boolean fromSqlite(@Nullable ResultSet results, @NotNull String fieldName) {
        try {
            if (results == null) return null;
            final int value = results.getInt(fieldName);
            return value == 1 ? Boolean.TRUE : Boolean.FALSE;
        } catch (Exception exception) {
            throw new DatabaseException(this, "fromSqlite", "Unable to get integer from result set as a int. fieldName=" + fieldName);
        }
    }
}
