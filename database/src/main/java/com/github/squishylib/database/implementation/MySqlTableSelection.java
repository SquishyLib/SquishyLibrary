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
import com.github.squishylib.common.logger.Logger;
import com.github.squishylib.database.Record;
import com.github.squishylib.database.*;
import com.github.squishylib.database.field.RecordField;
import com.github.squishylib.database.field.RecordFieldPool;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public @NotNull MySqlDatabase getDatabase() {
        return this.database;
    }

    @Override
    public @NotNull TableSelection<R, MySqlDatabase> setDatabase(@NotNull MySqlDatabase database) {
        return this;
    }

    @Override
    public @NotNull R createEmptyRecord(@NotNull RecordFieldPool pool) {
        return this.table.createEmptyRecord(pool);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<String>> getColumnNames() {
        return this.database.addRequest(new Request<>(() -> {

            // Create this requests logger.
            final Logger tempLogger = this.database.getLogger().extend(" &b.getColumnNames() &7MySqlTableSelection.java:68");

            // Create the sql statement.
            final String statement = "SHOW columns FROM {table};"
                    .replace("{table}", this.table.getName());

            try {

                tempLogger.debug("&d⎡ &7Executing statement &b" + statement);

                // Create the prepared statement.
                PreparedStatement preparedStatement = this.database.getConnection().prepareStatement(statement);
                ResultSet results = preparedStatement.executeQuery();

                // Are there no results?
                if (results == null) {
                    tempLogger.debug("&d⎣ &7Result was null.");
                    preparedStatement.close();
                    return null;
                }

                // Create name list.
                List<String> columnNames = new ArrayList<>();

                // Loop though results.
                while (results.next()) {
                    final String columnName = results.getString("Field");
                    columnNames.add(columnName);
                    tempLogger.debug("&d│ &7Added column name &b" + columnName);
                }

                tempLogger.debug("&d⎣ &7Final result is &b" + columnNames);
                preparedStatement.close();
                return columnNames;

            } catch (Exception exception) {
                throw new DatabaseException(exception, this, "getFirstRecord", "statement=&e" + statement + "&r");
            }
        }));
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> addColumn(@NotNull RecordField field) {
        return this.database.addRequest(new Request<>(() -> {

            // Create this requests logger.
            final Logger tempLogger = this.database.getLogger().extend(" &b.addColumn(field) &7MySqlTableSelection.java:114");

            // Create statement.
            final String statement = "ALTER TABLE {table} ADD COLUMN {key} {type};"
                    .replace("{table}", this.getName())
                    .replace("{key}", field.getName())
                    .replace("{type}", field.getType().getTypeName(Database.Type.MYSQL, field.getMaxSize()));

            try {

                // Execute statement.
                tempLogger.debug("&d⎡ &7Executing statement &b" + statement);
                PreparedStatement preparedStatement = this.database.getConnection().prepareStatement(statement);
                boolean success = preparedStatement.execute();

                preparedStatement.close();
                tempLogger.debug("&d⎣ &7Success &b" + success);
                return success;

            } catch (Exception exception) {
                throw new DatabaseException(exception, this, "addColumn", "statement=&e" + statement + "&r");
            }
        }));
    }

    @Override
    public @NotNull CompletableFuture<@Nullable R> getFirstRecord(@Nullable Query query) {
        return this.database.addRequest(new Request<>(() -> this.getFirstRecordSync(query)));
    }

    private @Nullable R getFirstRecordSync(@Nullable Query query) {

        // Create this requests logger.
        final Logger tempLogger = this.database.getLogger().extend(" &b.getFirstRecordSync(query) &7MySqlTableSelection.java:144");

        // Create the sql statement.
        final String statement = (query == null ? "SELECT * FROM {table} LIMIT 1;" : "SELECT * FROM {table} WHERE {where} LIMIT 1;")
                .replace("{table}", this.table.getName())
                .replace("{where}", query == null ? "" : query.buildSqliteWhere());

        try {

            // Create the prepared statement.
            tempLogger.debug("&d⎡ &7Executing statement &b" + statement);
            PreparedStatement preparedStatement = this.database.getConnection().prepareStatement(statement);
            if (query != null) query.setWildCards(preparedStatement, tempLogger, Database.Type.MYSQL);
            ResultSet results = preparedStatement.executeQuery();

            // Are there no results?
            if (results == null || !results.next()) {
                tempLogger.debug("&d⎣ &7Result was null.");
                preparedStatement.close();
                return null;
            }

            R record = this.createRecord(results, Database.Type.MYSQL);

            preparedStatement.close();
            tempLogger.debug("&d⎣ &7Final result is &b" + record.convertToMap());
            return record;

        } catch (Exception exception) {
            throw new DatabaseException(exception, this, "getFirstRecordSync", "statement=&e" + statement + "&r");
        }
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<R>> getRecordList(@Nullable Query query) {
        return this.database.addRequest(new Request<>(() -> {

            // Create this requests logger.
            final Logger tempLogger = this.database.getLogger().extend(" &b.getRecordList(query) &7MySqlTableSelection.java:186");

            // Create the sql statement.
            final String statement = (query == null ? "SELECT * FROM {table};" : "SELECT * FROM {table} WHERE {where};")
                    .replace("{table}", this.table.getName())
                    .replace("{where}", query == null ? "" : query.buildSqliteWhere());

            try {

                // Create the prepared statement.
                tempLogger.debug("&d⎡ &7Executing statement &b" + statement);
                PreparedStatement preparedStatement = this.database.getConnection().prepareStatement(statement);
                if (query != null) query.setWildCards(preparedStatement, tempLogger, Database.Type.MYSQL);
                ResultSet results = preparedStatement.executeQuery();

                // Are there no results?
                if (results == null) {
                    tempLogger.debug("&d⎣ &7Result was null.");
                    preparedStatement.close();
                    return null;
                }

                // Create the list of records.
                List<R> recordList = new ArrayList<>();

                // Loop though all records.
                while (results.next()) {
                    R record = this.createRecord(results, Database.Type.MYSQL);
                    recordList.add(record);
                    tempLogger.debug("&d│ &7Added record &b" + record);
                }

                preparedStatement.close();
                tempLogger.debug("&d⎣ &7Amount of records added &b" + recordList.size());
                return recordList;

            } catch (Exception exception) {
                throw new DatabaseException(exception, this, "getRecordList", "statement=&e" + statement + "&r");
            }
        }));
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Integer> getAmountOfRecords(@Nullable Query query) {
        return this.database.addRequest(new Request<>(() -> {

            // Create this requests logger.
            final Logger tempLogger = this.database.getLogger().extend(" &b.getAmountOfRecords(query) &7MySqlTableSelection.java:233");

            // Create the sql statement.
            final String statement = (query == null ? "SELECT COUNT(*) AS amount FROM {table};" : "SELECT COUNT(*) AS amount FROM {table} WHERE {where};")
                    .replace("{table}", this.table.getName())
                    .replace("{where}", query == null ? "" : query.buildSqliteWhere());

            try {

                // Create the prepared statement.
                tempLogger.debug("&d⎡ &7Executing statement &b" + statement);
                PreparedStatement preparedStatement = this.database.getConnection().prepareStatement(statement);
                if (query != null) query.setWildCards(preparedStatement, tempLogger, Database.Type.MYSQL);
                ResultSet results = preparedStatement.executeQuery();


                // Are there no results?
                if (results == null || !results.next()) {
                    tempLogger.debug("&d⎣ &7Result was null.");
                    preparedStatement.close();
                    return 0;
                }

                int amount = results.getInt("amount");
                preparedStatement.close();
                tempLogger.debug("&d⎣ &7Amount of records &b" + amount);
                return amount;

            } catch (Exception exception) {
                throw new DatabaseException(exception, this, "getAmountOfRecords", "statement=&e" + statement + "&r");
            }
        }));
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> insertRecord(@NotNull R record) {
        return this.database.addRequest(new Request<>(() -> {

            // Check if the record already exists.
            final R temp = this.getFirstRecordSync(new Query().match(record.getFieldPool().onlyPrimaryKeys()));

            // If empty add record.
            if (temp == null) return this.addRecord(record);

            // Otherwise, update record.
            return this.updateRecord(record);
        }));
    }

    private boolean addRecord(@NotNull R record) {

        // Create this requests logger.
        final Logger tempLogger = this.database.getLogger().extend(" &b.addRecord() &7MySqlTableSelection.java:289");

        // Get the list of fields.
        final Map<RecordField, Object> map = record.getFieldValues();
        tempLogger.debug("&d⎡ &7Field map &b" + map);

        // Build the statement.
        StringBuilder builder = new StringBuilder("INSERT INTO `{table}` ("
                .replace("{table}", this.table.getName()));

        // Append record fields and delete the last ", ".
        map.keySet().forEach(field -> builder.append(field.getName()).append(", "));
        builder.replace(builder.length() - 2, builder.length(), "");

        // Add next part.
        builder.append(") VALUES (");

        // Add the wild cards and delete the last ", ".
        map.keySet().forEach(field -> builder.append("?, "));
        builder.replace(builder.length() - 2, builder.length(), "");

        // Finish the statement.
        builder.append(");");

        try {

            // Create the prepared statement.
            PreparedStatement statement = this.database.getConnection().prepareStatement(builder.toString());

            // Set the wild cards.
            new Query().match(map).setWildCards(statement, tempLogger, Database.Type.MYSQL);

            tempLogger.debug("&d⎣ &7Execute statement &b" + builder);
            boolean success = statement.execute();
            statement.close();
            return success;

        } catch (Exception exception) {
            throw new DatabaseException(exception, this, "addRecord", "statement=&e" + builder + "&r");
        }
    }

    private boolean updateRecord(@NotNull R record) {

        // Create this requests logger.
        final Logger tempLogger = this.database.getLogger().extend(" &b.updateRecord() &7MySqlTableSelection.java:339");

        // Get the list of fields.
        final Map<RecordField, Object> map = record.getFieldValues();
        tempLogger.debug("&d⎡ &7Field map &b" + map);

        // Create the statement.
        StringBuilder builder = new StringBuilder("UPDATE `{table}` SET "
                .replace("{table}", this.table.getName()));

        // Add each condition.
        map.forEach((field, value) -> builder.append("{field} = ?, "
                .replace("{field}", field.getName())));

        // Remove the last ", ".
        builder.replace(builder.length() - 2, builder.length(), "");
        Query recordQuery = new Query().match(record.getFieldPool().onlyPrimaryKeys());
        builder.append(" WHERE {where};".replace(
                "{where}",
                recordQuery.buildSqliteWhere()
        ));

        try {

            // Create the prepared statement.
            PreparedStatement statement = this.database.getConnection().prepareStatement(builder.toString());

            // Set the wild cards.
            int index = 1;
            for (final Map.Entry<RecordField, Object> entry : map.entrySet()) {
                final Object value = entry.getKey().getType().javaToDatabaseValue(entry.getValue(), Database.Type.MYSQL);
                tempLogger.debug("&d│ &7Set wild card &b" + index + " to " + value);
                statement.setObject(index, value);
                index++;
            }
            for (final Map.Entry<String, Object> entry : recordQuery.getPatterns().entrySet()) {
                tempLogger.debug("&d│ &7Set wild card &b" + index + " to " + entry.getValue());
                statement.setObject(index, entry.getValue());
                index++;
            }

            tempLogger.debug("&d⎣ &7Execute statement &b" + builder);
            boolean success = statement.execute();
            statement.close();
            return success;

        } catch (Exception exception) {
            throw new DatabaseException(exception, this, "updateRecord", "statement=&e" + builder + "&r");
        }
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> removeAllRecords(@NotNull Query query) {
        return this.database.addRequest(new Request<>(() -> {

            // Create this requests logger.
            final Logger tempLogger = this.database.getLogger().extend(" &b.removeAllRecords(query) &7MySqlTableSelection.java:390");

            // Create the sql statement.
            final String statement = "DELETE FROM {table} WHERE {where};"
                    .replace("{table}", this.table.getName())
                    .replace("{where}", query.buildSqliteWhere());

            try {

                // Create the prepared statement.
                tempLogger.debug("&d⎡ &7Executing statement &b" + statement);
                PreparedStatement preparedStatement = this.database.getConnection().prepareStatement(statement);
                query.setWildCards(preparedStatement, tempLogger, Database.Type.MYSQL);
                boolean success = preparedStatement.execute();

                preparedStatement.close();
                tempLogger.debug("&d⎣ &7Success &b" + success);
                return success;

            } catch (Exception exception) {
                throw new DatabaseException(exception, this, "getAmountOfRecords", "statement=&e" + statement + "&r");
            }
        }));
    }
}
