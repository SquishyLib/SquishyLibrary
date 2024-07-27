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

import com.github.squishylib.common.CompletableFuture;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * This will be implemented by every table.
 *
 * @param <R> The type of record in the table.
 */
public abstract class Table<R extends Record> implements TableSelection<R, Database> {

    private Database database;

    @Override
    public @NotNull Optional<Database> getDatabase() {
        return Optional.ofNullable(database);
    }

    @Override
    public @NotNull TableSelection<R, Database> setDatabase(@NotNull Database database) {
        this.database = database;
        return this;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull R> getFirstRecord() {
        return this.database.createTableSelection(this).getFirstRecord();
    }

    @Override
    public @NotNull CompletableFuture<@NotNull R> getFirstRecord(@NotNull Query query) {
        return this.database.createTableSelection(this).getFirstRecord(query);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<R>> getRecordList() {
        return this.database.createTableSelection(this).getRecordList();
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<R>> getRecordList(@NotNull Query query) {
        return this.database.createTableSelection(this).getRecordList(query);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Integer> getAmountOfRecords() {
        return this.database.createTableSelection(this).getAmountOfRecords();
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Integer> getAmountOfRecords(@NotNull Query query) {
        return this.database.createTableSelection(this).getAmountOfRecords(query);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> insertRecord(@NotNull R record) {
        return this.database.createTableSelection(this).insertRecord(record);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> removeRecord(@NotNull Record record) {
        return this.database.createTableSelection(this).removeRecord(record);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> removeAllRecords(@NotNull Query query) {
        return this.database.createTableSelection(this).removeAllRecords(query);
    }
}
