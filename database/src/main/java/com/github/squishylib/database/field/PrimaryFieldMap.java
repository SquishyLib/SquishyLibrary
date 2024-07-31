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

package com.github.squishylib.database.field;

import com.github.squishylib.database.DatabaseException;
import com.github.squishylib.database.datatype.DefaultType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PrimaryFieldMap {

    private final @NotNull Map<PrimaryField, Object> map;
    private final @Nullable String defaultValue;

    public PrimaryFieldMap(final @Nullable String defaultValue) {
        this.map = new HashMap<>();
        this.defaultValue = defaultValue;
    }

    public PrimaryFieldMap(final @Nullable String defaultValue, final @NotNull Map<PrimaryField, Object> map) {
        this.map = map;
        this.defaultValue = defaultValue;
    }

    public @NotNull Map<PrimaryField, Object> get() {
        return this.map;
    }

    public @Nullable Object get(@NotNull final String fieldName) {
        for (final Map.Entry<PrimaryField, Object> entry : map.entrySet()) {
            if (entry.getKey().getName().equals(fieldName)) return entry.getValue();
        }
        return this.defaultValue;
    }

    public @NotNull String getString(@NotNull final String fieldName) {
        final Object value = get(fieldName);
        if (!(value instanceof String)) {
            throw new DatabaseException(this, "getString", "The primary key is ether null or not a string. &efieldName=" + fieldName + " value=" + get(fieldName));
        }
        return (String) value;
    }

    public @Nullable PrimaryFieldMap set(@NotNull final String fieldName, @NotNull final Object value) {
        this.map.put(new PrimaryField(fieldName, new DefaultType()), value);
        return this;
    }
}
