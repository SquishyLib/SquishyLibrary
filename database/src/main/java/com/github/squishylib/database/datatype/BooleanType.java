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

public class BooleanType implements DataType<Boolean> {

    @Override
    public boolean isType(@NotNull Object value) {
        return false;
    }

    @Override
    public @NotNull String getSqliteName() {
        return "";
    }

    @Override
    public @NotNull String toSqlite(@Nullable Boolean object) {
        return "";
    }

    @Override
    public @Nullable Boolean fromSqlite(@Nullable ResultSet results, @NotNull String fieldName) {
        return null;
    }
}
