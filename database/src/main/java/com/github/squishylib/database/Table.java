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
import com.github.squishylib.database.field.PrimaryFieldMap;
import com.github.squishylib.database.field.RecordField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This will be implemented by every table.
 *
 * @param <R> The type of record in the table.
 */
public abstract class Table<R extends Record<R>> implements TableSelection<R, Database> {

    private Database database;

    @Override
    public @NotNull Database getDatabase() {
        return this.database;
    }

    @Override
    public @NotNull TableSelection<R, Database> setDatabase(@NotNull Database database) {
        this.database = database;
        return this;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<String>> getColumnNames() {
        return this.database.createTableSelection(this).getColumnNames();
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> addColumn(@NotNull RecordField field) {
        return this.database.createTableSelection(this).addColumn(field);
    }

    @Override
    public @NotNull CompletableFuture<@Nullable R> getFirstRecord() {
        return this.database.createTableSelection(this).getFirstRecord();
    }

    @Override
    public @NotNull CompletableFuture<@Nullable R> getFirstRecord(@Nullable Query query) {
        return this.database.createTableSelection(this).getFirstRecord(query);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<R>> getRecordList() {
        return this.database.createTableSelection(this).getRecordList();
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<R>> getRecordList(@Nullable Query query) {
        return this.database.createTableSelection(this).getRecordList(query);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Integer> getAmountOfRecords() {
        return this.database.createTableSelection(this).getAmountOfRecords();
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Integer> getAmountOfRecords(@Nullable Query query) {
        return this.database.createTableSelection(this).getAmountOfRecords(query);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> insertRecord(@NotNull R record) {
        return this.database.createTableSelection(this).insertRecord(record);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> removeRecord(@NotNull R record) {
        return this.database.createTableSelection(this).removeRecord(record);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> removeAllRecords(@NotNull Query query) {
        return this.database.createTableSelection(this).removeAllRecords(query);
    }

    public interface Resolvable<R> {
        @NotNull R resolve(@NotNull R record);
    }

    /**
     * First it attempts to get the record.
     * If it doesn't exist it creates a new one.
     * <p>
     * Your changes are then applied to the record using {@link Resolvable}.
     * <p>
     * The record is then inserted into the database.
     *
     * @param primaryFieldMap The map of identifiers.
     * @param resolvable      The update that should be applied to the record.
     * @return The result of the operation.
     */
    public @NotNull CompletableFuture<@NotNull Boolean> resolveRecord(
            final @NotNull PrimaryFieldMap primaryFieldMap,
            final @NotNull Resolvable<R> resolvable
    ) {

        CompletableFuture<@NotNull Boolean> future = new CompletableFuture<>();

        new Thread(() -> {

            try {
                R record = this.getFirstRecord(new Query().match(primaryFieldMap)).waitAndGet();

                // Does the record not exist?
                if (record == null) record = this.createEmpty(primaryFieldMap);

                // Resolve the changes.
                record = resolvable.resolve(record);

                // Insert the updated record.
                this.insertRecord(record);

                // Complete the request.
                future.complete(true);

            } catch (Exception exception) {
                future.complete(false);
                throw new DatabaseException(exception, this.getClass(), "resolveRecord()", "Unable to resolve a database record update.");
            }
        });

        return future;
    }
}
