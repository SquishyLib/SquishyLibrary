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
import com.github.squishylib.database.Query;
import com.github.squishylib.database.Record;
import com.github.squishylib.database.datatype.DataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordFieldPool {

    private final @NotNull Map<RecordField, Object> map;
    private final @Nullable String defaultValue;

    public RecordFieldPool() {
        this.map = new HashMap<>();
        this.defaultValue = null;
    }

    public RecordFieldPool(final @Nullable String defaultValue) {
        this.map = new HashMap<>();
        this.defaultValue = defaultValue;
    }

    public @NotNull Map<RecordField, Object> get() {
        return this.map;
    }

    public @Nullable Object get(final @NotNull String fieldName) {
        for (final Map.Entry<RecordField, Object> entry : map.entrySet()) {
            if (entry.getKey().getName().equals(fieldName)) return entry.getValue();
        }
        return this.defaultValue;
    }

    public @NotNull String getString(final @NotNull String fieldName) {
        final Object value = get(fieldName);
        if (!(value instanceof String)) {
            throw new DatabaseException(this, "getString", "The primary key is ether null or not a string. &efieldName=" + fieldName + " value=" + get(fieldName));
        }
        return (String) value;
    }

    public @NotNull RecordFieldPool set(final @NotNull String fieldName, final @NotNull Object value, final long maxSize) {
        this.map.put(new RecordField(fieldName, DataType.of(value), maxSize), value);
        return this;
    }

    public @NotNull RecordFieldPool set(final @NotNull RecordField field, final @NotNull Object value) {
        this.map.put(field, value);
        return this;
    }

    public @NotNull RecordFieldPool addAll(final @NotNull Map<RecordField, Object> map) {
        this.map.putAll(map);
        return this;
    }

    public @NotNull RecordFieldPool addAll(final @NotNull Query query) {
        for (Map.Entry<String, Object> entry : query.getPatterns().entrySet()) {
            this.map.put(
                    new RecordField(entry.getKey(), DataType.of(entry.getValue().getClass())),
                    entry.getValue()
            );
        }
        return this;
    }

    public @NotNull RecordFieldPool onlyPrimaryKeys() {
        final HashMap<RecordField, Object> temp = new HashMap<>(this.map);

        for (final Map.Entry<RecordField, Object> entry : temp.entrySet()) {
            if (entry.getKey() instanceof PrimaryField) continue;
            this.map.remove(entry.getKey(), entry.getValue());
        }
        return this;
    }
}
