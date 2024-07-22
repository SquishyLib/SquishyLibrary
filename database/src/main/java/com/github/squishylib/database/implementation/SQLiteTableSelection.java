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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class SQLiteTableSelection<R extends Record> implements TableSelection<R, SQLiteDatabase> {

    private final @NotNull SQLiteDatabase database;
    private final @NotNull Table<R> table;

    public SQLiteTableSelection(final @NotNull SQLiteDatabase database, @NotNull Table<R> table) {
        this.database = database;
        this.table = table;
    }

    @Override
    public @NotNull String getName() {
        return this.table.getName();
    }

    @Override
    public @NotNull Optional<SQLiteDatabase> getDatabase() {
        return Optional.of(this.database);
    }

    @Override
    public @NotNull TableSelection<R, SQLiteDatabase> setDatabase(@NotNull SQLiteDatabase database) {
        return this;
    }

    @NotNull
    @Override
    public R createEmpty(@NotNull String identifier) {
        return this.table.createEmpty(identifier);
    }

    @Override
    public @NotNull CompletableFuture<R> getFirstRecord() {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<R> getFirstRecord(@NotNull Query query) {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<R>> getRecordList() {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<R>> getRecordList(@NotNull Query query) {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Integer> getAmountOfRecords() {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Integer> getAmountOfRecords(@NotNull Query query) {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> removeRecord(@NotNull Record record) {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> removeAllRecords(@NotNull Query query) {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> insertRecord(@NotNull Record record) {
        return null;
    }
}
