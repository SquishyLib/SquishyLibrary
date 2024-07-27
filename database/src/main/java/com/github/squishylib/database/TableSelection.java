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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Represents a table interface.
 * Contains methods used to interact with a table.
 *
 * @param <R> The record type the table contains.
 * @param <D> The database connection that is being used.
 */
public interface TableSelection<R extends Record, D extends Database> {

    /**
     * Used to get the name of the table.
     * <p>
     * This should never be a consumer inputted value.
     * It should be a final string set by the administrator.
     * <p>
     * Otherwise, you may be vulnerable to sql injections.
     * <p>
     * All other values, for example, query's for records
     * should be secure enough to use consumer inputted values.
     * This is because in sql databases the prepared statement
     * will be used.
     *
     * @return The name of the table.
     */
    @NotNull
    String getName();

    /**
     * The name of the identifier filed used in records.
     * This will become the primary key.
     *
     * @return The identifier's name.
     */
    @NotNull String getIdentifierName();

    /**
     * Used to get the database this table selection
     * instance is linked to.
     *
     * @return The linked database.
     */
    @NotNull
    Optional<D> getDatabase();

    /**
     * Used to link this table selection to a database.
     * <p>
     * This is the database instance that will be used to
     * run the database actions.
     *
     * @param database The database to link.
     * @return This instance.
     */
    @NotNull
    TableSelection<R, D> setDatabase(@NotNull D database);

    /**
     * Used to create an empty record.
     * This will be used when getting a record from the database.
     *
     * @return The empty record.
     */
    @NotNull
    R createEmpty(@NotNull String identifier);

    /**
     * Requests the first record from this
     * table within the database.
     * <p>
     * This will return null if the record doesn't exist.
     * <p>
     * It's advised to check if the future was also canceled.
     *
     * @return The first record in this table.
     */
    @NotNull
    CompletableFuture<@Nullable R> getFirstRecord();

    /**
     * Requests the first record from this
     * table within the database given a query.
     * <p>
     * This will return null if the record doesn't exist.
     * <p>
     * It's advised to check if the future was also canceled.
     *
     * @return The first record in this table.
     */
    @NotNull
    CompletableFuture<@Nullable R> getFirstRecord(@NotNull Query query);

    /**
     * Requests the list of records within this table.
     * <p>
     * This will return empty if there are no records.
     * <p>
     * It's advised to check if the future was also canceled.
     *
     * @return The optional list.
     */
    @NotNull
    CompletableFuture<@NotNull List<R>> getRecordList();

    /**
     * Requests the list of records within this table
     * given a query.
     * <p>
     * This will return empty if there are no records.
     * <p>
     * It's advised to check if the future was also canceled.
     *
     * @return The optional list.
     */
    @NotNull
    CompletableFuture<@NotNull List<R>> getRecordList(@NotNull Query query);

    /**
     * Requests the amount of records in this table.
     * <p>
     * It's advised to check if the future was also canceled.
     *
     * @return The optional list.
     */
    @NotNull
    CompletableFuture<@NotNull Integer> getAmountOfRecords();

    /**
     * Requests the amount of records in this table
     * given a query.
     * <p>
     * It's advised to check if the future was also canceled.
     *
     * @return The optional list.
     */
    @NotNull
    CompletableFuture<@NotNull Integer> getAmountOfRecords(@NotNull Query query);

    /**
     * Used to insert a record into the database.
     * <p>
     * This will return false if the request was canceled.
     * <p>
     * It's advised to check if the future was also canceled.
     *
     * @return The optional list.
     */
    @NotNull
    CompletableFuture<@NotNull Boolean> insertRecord(@NotNull R record);

    /**
     * Used to remove a record from this table.
     * <p>
     * This will return false if the request was canceled.
     * <p>
     * It's advised to check if the future was also canceled.
     *
     * @return The optional list.
     */
    @NotNull
    CompletableFuture<@NotNull Boolean> removeRecord(@NotNull Record record);

    /**
     * Used to remove a record from this table.
     * <p>
     * This will return false if the request was canceled.
     * <p>
     * It's advised to check if the future was also canceled.
     *
     * @return The optional list.
     */
    @NotNull
    CompletableFuture<@NotNull Boolean> removeAllRecords(@NotNull Query query);
}
