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

package com.github.squishylib.database.implementation;

import com.github.squishylib.common.CompletableFuture;
import com.github.squishylib.database.Query;
import com.github.squishylib.database.Record;
import com.github.squishylib.database.Table;
import com.github.squishylib.database.TableSelection;
import com.github.squishylib.database.field.PrimaryFieldMap;
import com.github.squishylib.database.field.RecordField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class MySqlTableSelection<R extends Record<R>> implements TableSelection<R, MySqlDatabase> {

    private final @NotNull MySqlDatabase database;
    private final @NotNull Table<R> table;

    public MySqlTableSelection(final @NotNull MySqlDatabase database, @NotNull Table<R> table) {
        this.database = database;
        this.table = table;
    }

    @Override
    public @NotNull String getName() {
        return this.table.getName();
    }

    @Override
    public @NotNull Optional<MySqlDatabase> getDatabase() {
        return Optional.of(this.database);
    }

    @Override
    public @NotNull TableSelection<R, MySqlDatabase> setDatabase(@NotNull MySqlDatabase database) {
        return null;
    }

    @Override
    public @NotNull R createEmpty(@NotNull PrimaryFieldMap identifiers) {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<String>> getColumnNames() {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> addColumn(@NotNull RecordField field) {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@Nullable R> getFirstRecord() {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@Nullable R> getFirstRecord(@Nullable Query query) {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<R>> getRecordList() {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<R>> getRecordList(@Nullable Query query) {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Integer> getAmountOfRecords() {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Integer> getAmountOfRecords(@Nullable Query query) {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> insertRecord(@NotNull R record) {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> removeRecord(@NotNull R record) {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> removeAllRecords(@NotNull Query query) {
        return null;
    }
}
