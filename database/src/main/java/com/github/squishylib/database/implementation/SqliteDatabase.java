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
import com.github.squishylib.database.field.PrimaryFieldMap;
import com.github.squishylib.database.field.RecordField;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * A database that is represented by a local file.
 */
public class SqliteDatabase extends RequestQueueDatabase {

    protected @NotNull Database.Status status;
    protected final @NotNull Logger logger;
    protected final boolean shouldReconnectEveryCycle;
    protected final @NotNull Duration reconnectCooldown;
    protected final boolean willReconnect;
    protected final @NotNull Duration timeBetweenRequests;
    protected final long maxRequestPending;
    protected final @NotNull File file;

    protected final @NotNull List<Table<?>> tableList;
    protected Connection connection;

    public SqliteDatabase(@NotNull Logger logger,
                          boolean shouldReconnectEveryCycle,
                          @NotNull Duration reconnectCooldown,
                          boolean willReconnect,
                          @NotNull Duration timeBetweenRequests,
                          long maxRequestPending,
                          @NotNull File file) {

        this.status = Status.DISCONNECTED;
        this.logger = logger.extend(" [SqliteDatabase]");
        this.shouldReconnectEveryCycle = shouldReconnectEveryCycle;
        this.reconnectCooldown = reconnectCooldown;
        this.willReconnect = willReconnect;
        this.timeBetweenRequests = timeBetweenRequests;
        this.maxRequestPending = maxRequestPending;
        this.file = file;

        this.tableList = new ArrayList<>();
        this.logger.debug("Initialized sqlite database class.");
    }

    @Override
    public @NotNull Type getType() {
        return Type.SQLITE;
    }

    @Override
    public @NotNull Database.Status getStatus() {
        try {

            // Check if the database is reconnecting.
            if (this.status.equals(Status.RECONNECTING)) return this.status;

            // Check if the connection is closed.
            if (this.connection.isClosed()) {
                this.status = Status.DISCONNECTED;
                return this.status;
            }

            // Otherwise, status is connected.
            this.status = Status.CONNECTED;

        } catch (SQLException exception) {
            this.status = Status.DISCONNECTED;
        }

        return this.status;
    }

    @Override
    public @NotNull Database setStatus(@NotNull Database.Status status) {
        this.status = status;
        return this;
    }

    @Override
    public @NotNull Logger getLogger() {
        return this.logger;
    }

    @Override
    public boolean shouldReconnectEveryCycle() {
        return this.shouldReconnectEveryCycle;
    }

    @Override
    public @NotNull Duration getReconnectCooldown() {
        return this.reconnectCooldown;
    }

    @Override
    public boolean willReconnect() {
        return this.willReconnect;
    }

    @Override
    public @NotNull Duration getTimeBetweenRequests() {
        return this.timeBetweenRequests;
    }

    @Override
    public long getMaxRequestsPending() {
        return this.maxRequestPending;
    }

    @Override
    public @NotNull List<Table<?>> getTableList() {
        return this.tableList;
    }

    @Override
    public int getAmountOfTables() {
        return this.tableList.size();
    }

    public @NotNull String getUrl() {
        return "jdbc:sqlite:" + this.file.getAbsolutePath();
    }

    public @NotNull Connection getConnection() {
        return this.connection;
    }

    @Override
    public @NotNull Database createTable(@NotNull Table<?> table) {

        // Create this methods logger.
        final Logger tempLogger = this.logger.extend(" &b.createTable() &7SqliteDatabase.java:195 table=" + table.getName() + "&7");
        tempLogger.debug("Creating table.");

        // Link the table to this database.
        table.setDatabase(this);

        // Add the table to the list.
        this.tableList.add(table);

        // Does the table already exist in the database?
        if (this.hasTable(table.getName())) {

            tempLogger.debug("Table already exists in database.");

            // The fields that are missing from the databases table.
            final List<RecordField> missingFields = this.getMissingFields(table);

            // Are there no missing fields?
            if (missingFields.isEmpty()) {
                tempLogger.debug("No missing fields.");
                return this;
            }

            tempLogger.debug("Missing fields=" + missingFields.stream().map(RecordField::getName).toList());

            // Otherwise, add the missing columns.
            missingFields.forEach(field -> {
                tempLogger.debug("Adding missing field &e" + field.getName() + ".");
                table.addColumn(field).waitAndGet();
            });
            return this;
        }

        // Create the sql create table statement.
        final String statement = this.createTableStatement(table);

        tempLogger.debug("Executing create table statement &b" + statement);

        try {

            // Execute the statement.
            PreparedStatement preparedStatement = this.connection.prepareStatement(statement);
            preparedStatement.execute();
            return this;

        } catch (Exception exception) {
            throw new DatabaseException(exception, this, "createTable", "Failed to create the table &7" + table.getName() + ". &estatement=" + statement);
        }
    }

    private @NotNull List<RecordField> getMissingFields(@NotNull Table<?> table) {

        // Create this methods logger.
        final Logger tempLogger = this.logger.extend(" &b.getMissingFields() &7SqliteDatabase.java:248 table=" + table.getName() + "&7");

        // The fields that should be included.
        final List<RecordField> fields = table.getFieldList();

        tempLogger.debug("Fields that should be included= " + fields.stream().map(RecordField::getName).toList());

        // The current fields.
        final List<String> currentFields = table.getColumnNames().waitAndGet();

        tempLogger.debug("Current fields= " + currentFields);

        // The missing fields.
        return fields.stream()
                .filter(field -> !currentFields.contains(field.getName()))
                .toList();
    }

    private @NotNull String createTableStatement(@NotNull Table<?> table) {
        StringBuilder builder = new StringBuilder(
                "CREATE TABLE IF NOT EXISTS `" + table.getName() + "` ("
        );

        // Create a new record.
        Record<?> record = table.createEmpty(new PrimaryFieldMap("temp"));

        // Loop though primary keys.
        record.getPrimaryFieldList().forEach(primaryField -> builder.append(
                "`{key}` {type} PRIMARY KEY,"
                        .replace("{key}", primaryField.getName())
                        .replace("{type}", primaryField.getType().getSqliteName())
        ));

        // Loop though fields.
        record.getFieldList().stream()
                .filter(field -> !builder.toString().contains(field.getName()))
                .forEach(field -> builder.append(
                        "`{key}` {type},"
                                .replace("{key}", field.getName())
                                .replace("{type}", field.getType().getSqliteName())
                ));

        // Loop though foreign keys.
        record.getForeignFieldList().forEach(foreignField -> builder.append(
                "`{key}` {type} REFERENCES {reference}({reference_field}),"
                        .replace("{key}", foreignField.getName())
                        .replace("{type}", foreignField.getType().getSqliteName())
                        .replace("{reference}", foreignField.getForeignTableName())
                        .replace("{reference_field}", foreignField.getForeignName())
        ));

        builder.deleteCharAt(builder.length() - 1);
        builder.append(");");

        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Table<?>> @NotNull T getTable(@NotNull Class<T> clazz) {
        for (final Table<?> table : this.tableList) {
            if (clazz.isAssignableFrom(table.getClass())) return (T) table;
        }

        throw new DatabaseException(this, "getTable", "Table was not registered with the database: " + clazz.getName()
                + ". Please use database.createTable(Table<?> table) before trying to get the table isntance."
        );
    }

    @Override
    public boolean hasTable(@NotNull String tableName) {
        try {

            // Get the table.
            DatabaseMetaData metaData = this.connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, tableName, null);

            // Check if the result is null.
            if (resultSet == null) return false;
            if (!resultSet.next()) return false;

            // Check if there is a table.
            return resultSet.getRow() > 0;

        } catch (Exception exception) {
            throw new DatabaseException(exception, this, "hasTable", "Unable to check if the table " + tableName + " exists.");
        }
    }

    @Override
    public @NotNull <R extends Record<R>> TableSelection<R, ?> createTableSelection(@NotNull Table<R> table) {
        return new SqliteTableSelection<>(this, table);
    }

    @Override
    public @NotNull CompletableFuture<Status> connectAsync() {

        // Create this methods logger.
        final Logger tempLogger = this.logger.extend(" &bconnectAsync() &7SqliteDatabase.java:346");
        tempLogger.debug("Connecting to the database. currentStatus=" + this.status);

        // Create the future result.
        final CompletableFuture<Status> future = new CompletableFuture<>();

        new Thread(() -> {
            try {

                this.logger.info("Attempting to connect to the &bsqlite database.");
                tempLogger.debug("Initiating drivers.");

                // Initiate drivers.
                Class.forName("org.sqlite.JDBC");

                // Create directory.
                tempLogger.debug("Creating parent directories.");
                new File(this.file.getParent()).mkdirs();

                // Create database connection url.
                String url = this.getUrl();
                tempLogger.debug("Connecting with url &b" + url);

                // Create a connection to the database.
                this.connection = DriverManager.getConnection(url);

                this.logger.info("Connected successfully to the &bsqlite database.");

                // Set status and future.
                this.status = Status.CONNECTED;
                future.complete(this.status);


            } catch (Exception exception) {

                tempLogger.debug("Exception occurred while trying to connect.");

                // Check if the database should attempt to reconnect later.
                if (this.willReconnect) {

                    tempLogger.debug("Attempting to reconnect in &b" + this.reconnectCooldown.toMillis() + "ms.");

                    // Attempt to reconnect to the database.
                    this.attemptReconnect();

                    // Set and return status.
                    this.status = Status.RECONNECTING;
                    future.complete(this.status);
                    throw new DatabaseException(exception, this, "connectAsync",
                            "Could not connect to SQLite database. Attempting to reconnect in "
                                    + this.reconnectCooldown.toSeconds() + "s."
                    );
                }

                tempLogger.debug("The field willReconnect is false, disconnected.");

                // Otherwise, disconnect.
                this.status = Status.DISCONNECTED;
                future.complete(this.status);
                throw new DatabaseException(exception, this, "connectAsync",
                        "Could not connect to SQLite database. Reconnecting is set to false."
                );
            }
        }).start();

        return future;
    }

    private void attemptReconnect() {
        new Thread(() -> {
            try {

                // Wait cooldown.
                Thread.sleep(this.reconnectCooldown.toMillis());

                // Attempt to reconnect.
                this.connectAsync();

            } catch (Exception exception) {
                throw new DatabaseException(exception, this, "attemptReconnect",
                        "Error occurred while waiting to reconnect to the database."
                );
            }
        }).start();
    }

    @Override
    public @NotNull CompletableFuture<Status> disconnectAsync(boolean reconnect) {

        // Create this methods logger.
        final Logger tempLogger = this.logger.extend(" &b.disconnectAsync() &7SqliteDatabase.java:433");
        tempLogger.debug("Disconnecting from the database. &bcurrentStatus=" + this.status + " reconnect=" + reconnect);

        // Create the future result.
        final CompletableFuture<Status> future = new CompletableFuture<>();

        new Thread(() -> {
            try {

                if (this.status.equals(Status.RECONNECTING)) {
                    throw new DatabaseException(this, "disconnectAsync", "Attempted to disconnect when reconnecting.");
                }

                // Check if already disconnected.
                if (this.isDisconnected() && status.equals(Status.DISCONNECTED)) {
                    tempLogger.debug("Already disconnected.");

                } else {
                    // Close connection.
                    this.connection.close();
                    this.status = Status.DISCONNECTED;
                    tempLogger.debug("Connection closed.");
                }

                // Should we reconnect?
                if (reconnect) {
                    tempLogger.debug("Reconnecting.");
                    this.status = Status.RECONNECTING;
                    this.connect();
                }

                // Complete future status.
                future.complete(this.status);

            } catch (Exception exception) {

                tempLogger.debug("An error occurred while disconnecting.");

                // Should we reconnect?
                if (reconnect) {
                    tempLogger.debug("Reconnecting.");
                    this.status = Status.RECONNECTING;
                    this.connect();
                }

                // Complete future status.
                future.complete(this.status);

                throw new DatabaseException(exception, this, "disconnectAsync",
                        "Error while disconnecting. &ereconnect=" + reconnect
                );
            }
        }).start();

        return future;
    }
}
