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

package com.github.squishylib.database;

import com.github.squishylib.common.logger.Logger;
import com.github.squishylib.database.datatype.DataType;
import com.github.squishylib.database.field.PrimaryField;
import com.github.squishylib.database.field.RecordField;
import com.github.squishylib.database.field.RecordFieldPool;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.util.*;

public class Query {

    private final @NotNull Map<String, Object> patterns;
    private int limit;
    private String orderByKey;
    private Comparator<?> orderByComparator;

    public Query() {
        this.patterns = new LinkedHashMap<>();
        this.limit = -1;
    }

    public @NotNull Map<String, Object> getPatterns() {
        return this.patterns;
    }

    public int getLimit() {
        return this.limit;
    }

    public @Nullable String getOrderByKey() {
        return this.orderByKey;
    }

    public @Nullable Comparator<?> getOrderByComparator() {
        return this.orderByComparator;
    }

    public @NotNull Query match(@NotNull String key, @NotNull Object value) {
        this.patterns.put(key, value);
        return this;
    }

    public @NotNull Query match(@NotNull Map<RecordField, Object> map) {
        for (Map.Entry<RecordField, Object> entry : map.entrySet()) {
            this.match(entry.getKey().getName(), entry.getValue());
        }
        return this;
    }

    public @NotNull Query match(@NotNull RecordFieldPool recordFieldPool) {
        for (final Map.Entry<RecordField, Object> entry : recordFieldPool.get().entrySet()) {
            this.match(entry.getKey().getName(), entry.getValue());
        }
        return this;
    }

    public @NotNull Query limit(int limit) {
        this.limit = limit;
        return this;
    }

    public @NotNull Query orderBy(@NotNull String key, @NotNull Comparator<?> comparator) {
        this.orderByKey = key;
        this.orderByComparator = comparator;
        return this;
    }

    public @NotNull String buildSqliteWhere() {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, Object> map : this.patterns.entrySet()) {
            builder.append(map.getKey()).append(" = ? AND ");
        }

        // Delete the last " AND".
        builder.replace(builder.length() - 5, builder.length(), "");

        return builder.toString();
    }

    public void setWildCards(@NotNull PreparedStatement statement, @NotNull Logger logger, @NotNull Database.Type type) {
        try {

            int index = 1;
            for (Map.Entry<String, Object> map : this.patterns.entrySet()) {

                final DataType<?> dataType = DataType.of(map.getValue());
                final Object sqliteObject = dataType.javaToDatabaseValue(map.getValue(), type);

                logger.debug("&d| &7Set wild card &b{number} to {value} ({type}) &7 using {converter} + {old_value} ({old_type})"
                        .replace("{number}", String.valueOf(index))
                        .replace("{value}", String.valueOf(sqliteObject))
                        .replace("{type}", sqliteObject == null ? "null" : sqliteObject.getClass().getSimpleName())
                        .replace("{converter}", dataType.getClass().getSimpleName())
                        .replace("{old_value}", String.valueOf(map.getValue()))
                        .replace("{old_type}", map.getValue() == null ? "null" : map.getValue().getClass().getSimpleName())
                );

                statement.setObject(index, DataType.of(map.getValue()).javaToDatabaseValue(map.getValue(), type));
                index++;
            }

        } catch (Exception exception) {
            throw new DatabaseException(exception, this, "appendSqlite", "Unable to append a query to a statement.");
        }
    }

    public @NotNull List<Bson> buildMongoFilter() {
        List<Bson> filterList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : this.patterns.entrySet()) {
            filterList.add(Filters.eq(entry.getKey(), entry.getValue()));
        }

        return filterList;
    }
}
