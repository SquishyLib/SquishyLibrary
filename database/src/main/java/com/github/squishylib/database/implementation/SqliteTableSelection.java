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
import com.github.squishylib.database.*;
import com.github.squishylib.database.Record;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

public class SqliteTableSelection<R extends Record<R>> implements TableSelection<R, SqliteDatabase> {

    private final @NotNull SqliteDatabase database;
    private final @NotNull Table<R> table;

    public SqliteTableSelection(final @NotNull SqliteDatabase database, @NotNull Table<R> table) {
        this.database = database;
        this.table = table;
    }

    @Override
    public @NotNull String getName() {
        return this.table.getName();
    }

    @Override
    public @NotNull String getIdentifierName() {
        return this.table.getIdentifierName();
    }

    @Override
    public @NotNull Optional<SqliteDatabase> getDatabase() {
        return Optional.of(this.database);
    }

    @Override
    public @NotNull TableSelection<R, SqliteDatabase> setDatabase(@NotNull SqliteDatabase database) {
        return this;
    }

    @NotNull
    @Override
    public R createEmpty(@NotNull String identifier) {
        return this.table.createEmpty(identifier);
    }

    @Override
    public @NotNull CompletableFuture<R> getFirstRecord() {
        return this.database.addRequest(new Request<>(() -> {

            // Create the sql statement.
            final String statement = "SELECT * FROM " + this.table.getName() + " LIMIT 1";

            try {

                // Create the prepared statement.
                PreparedStatement preparedStatement = this.database.getConnection().prepareStatement(statement);
                ResultSet results = preparedStatement.executeQuery(statement);
                preparedStatement.close();

                // Are there no results?
                if (results == null) return null;
                if (!results.next())  return null;

                return this.createEmpty(results.getString(this.getIdentifierName()))
                        .convert(results);

            } catch (Exception exception) {
                throw new DatabaseException(exception, this, "getFirstRecord", "statement=&e" + statement + "&r");
            }
        }));
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
