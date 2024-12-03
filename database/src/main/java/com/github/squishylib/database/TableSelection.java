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
import com.github.squishylib.database.field.ForeignField;
import com.github.squishylib.database.field.PrimaryField;
import com.github.squishylib.database.field.RecordField;
import com.github.squishylib.database.field.RecordFieldPool;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Represents a table interface.
 * Contains methods used to interact with a table.
 *
 * @param <R> The record type the table contains.
 * @param <D> The database connection that is being used.
 */
public interface TableSelection<R extends Record<?>, D extends Database> {

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
     * Used to get the database this table selection
     * instance is linked to.
     *
     * @return The linked database.
     */
    @NotNull
    D getDatabase();

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
     * @param pool The pool of record fields with values.
     * @return The empty record.
     */
    @NotNull
    R createEmptyRecord(@NotNull RecordFieldPool pool);

    default @NotNull R createRecord(@NotNull ResultSet resultSet, @NotNull Database.Type type) throws SQLException {
        return (R) this.createEmptyRecord(this.getFieldPool(resultSet)).convert(resultSet, type);
    }

    /**
     * Used to get the field names currently in
     * the table.
     *
     * @return The column names.
     */
    @NotNull
    CompletableFuture<@NotNull List<String>> getColumnNames();

    /**
     * Used to add a new column to the table.
     *
     * @param field The column info.
     * @return True if the column has been added successfully.
     */
    @NotNull
    CompletableFuture<@NotNull Boolean> addColumn(@NotNull RecordField field);

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
    default @NotNull
    CompletableFuture<@Nullable R> getFirstRecord() {
        return this.getFirstRecord(null);
    }

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
    CompletableFuture<@Nullable R> getFirstRecord(@Nullable Query query);

    /**
     * Requests the list of records within this table.
     * <p>
     * This will return empty if there are no records.
     * <p>
     * It's advised to check if the future was also canceled.
     *
     * @return The optional list.
     */
    default @NotNull
    CompletableFuture<@NotNull List<R>> getRecordList() {
        return this.getRecordList(null);
    }

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
    CompletableFuture<@NotNull List<R>> getRecordList(@Nullable Query query);

    /**
     * Requests the amount of records in this table.
     * <p>
     * It's advised to check if the future was also canceled.
     *
     * @return The optional list.
     */
    default @NotNull
    CompletableFuture<@NotNull Integer> getAmountOfRecords() {
        return this.getAmountOfRecords(null);
    }

    /**
     * Requests the amount of records in this table
     * given a query.
     * <p>
     * It's advised to check if the future was also canceled.
     *
     * @return The optional list.
     */
    @NotNull
    CompletableFuture<@NotNull Integer> getAmountOfRecords(@Nullable Query query);

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
    default @NotNull
    CompletableFuture<@NotNull Boolean> removeRecord(@NotNull R record) {
        return this.removeAllRecords(new Query().match(record.getFieldPool().onlyPrimaryKeys()));
    }

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

    default @NotNull List<RecordField> getFieldList() {
        return this.createEmptyRecord(new RecordFieldPool("temp generated to get record data")).getFieldList();
    }

    default @NotNull List<String> getFieldNameList() {
        return this.createEmptyRecord(new RecordFieldPool("temp generated to get record data")).getFieldNameList();
    }

    default @NotNull List<PrimaryField> getPrimaryFieldList() {
        return this.createEmptyRecord(new RecordFieldPool("temp generated to get record data")).getPrimaryFieldList();
    }

    default @NotNull List<ForeignField> getForeignFieldList() {
        return this.createEmptyRecord(new RecordFieldPool("temp generated to get record data")).getForeignFieldList();
    }

    default @NotNull RecordFieldPool getFieldPool(@NotNull ResultSet results) {
        RecordFieldPool map = new RecordFieldPool();

        for (RecordField field : this.getFieldList()) {
            try {
                map.set(field.getName(), results.getObject(field.getName()), field.getMaxSize());
            } catch (Exception exception) {
                throw new DatabaseException(exception, this, "getPrimaryFieldMap(ResultSet)", "Unable tp get field from result set. field=" + field);
            }
        }

        return map;
    }

    default @NotNull RecordFieldPool getFieldPool(@NotNull Document document) {
        RecordFieldPool map = new RecordFieldPool();

        for (RecordField field : this.getPrimaryFieldList()) {
            try {
                map.set(field.getName(), document.get(field.getName()),  field.getMaxSize());
            } catch (Exception exception) {
                throw new DatabaseException(exception, this, "getPrimaryFieldMap(Document)", "Unable tp get field from result set. field=" + field);
            }
        }

        return map;
    }
}
