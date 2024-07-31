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
public class SqliteDatabase extends DatabaseRequestQueue implements Database {

    private @NotNull DatabaseStatus status;
    private final @NotNull Logger logger;
    private final @NotNull Duration reconnectCooldown;
    private final boolean willReconnect;
    private final @NotNull Duration timeBetweenRequests;
    private final long maxRequestPending;
    private final @NotNull File file;

    private final @NotNull List<Table<?>> tableList;
    private Connection connection;

    public SqliteDatabase(@NotNull Logger logger,
                          @NotNull Duration reconnectCooldown,
                          boolean willReconnect,
                          @NotNull Duration timeBetweenRequests,
                          long maxRequestPending,
                          @NotNull File file) {

        super(timeBetweenRequests, maxRequestPending);

        this.status = DatabaseStatus.DISCONNECTED;
        this.logger = logger.extend(" [SQLiteDatabase]");
        this.reconnectCooldown = reconnectCooldown;
        this.willReconnect = willReconnect;
        this.timeBetweenRequests = timeBetweenRequests;
        this.maxRequestPending = maxRequestPending;
        this.file = file;

        this.tableList = new ArrayList<>();
    }

    @Override
    protected @NotNull Database getDatabase() {
        return this;
    }

    @Override
    protected boolean reconnectIfDisconnected() {

        try {
            // Is the database connected?
            if (this.isConnected()) return true;

            // Should the database reconnect?
            if (this.willReconnect) {
                this.connect();

                // Is the database now connected?
                if (this.isConnected()) return true;

                // Otherwise, it will try to reconnect,
                // so here we wait till connected.

                while (true) {
                    Thread.sleep(this.getReconnectCooldown().toMillis());
                    if (this.isConnected()) return true;
                }
            }

            return false;

        } catch (Exception exception) {
            throw new DatabaseException(exception, this, "reconnectIfDisconnected", "Failed to check if disconnected and reconnecting.");
        }
    }

    @Override
    public @NotNull DatabaseStatus getStatus() {
        try {

            // Check if the database is reconnecting.
            if (this.status.equals(DatabaseStatus.RECONNECTING)) return this.status;

            // Check if the connection is closed.
            if (this.connection.isClosed()) {
                this.status = DatabaseStatus.DISCONNECTED;
                return this.status;
            }

            this.status = DatabaseStatus.CONNECTED;

        } catch (SQLException exception) {
            this.status = DatabaseStatus.DISCONNECTED;
        }
        return this.status;
    }

    @Override
    public @NotNull Logger getLogger() {
        return this.logger;
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

        // Add the table to the list.
        this.tableList.add(table);

        // Does the table exist?
        if (this.hasTable(table.getName())) {

            // Create a new record.
            Record<?> record = table.createEmpty(new PrimaryFieldMap("temp"));

            // Get the fields and the current loaded fields.
            final List<RecordField> fields = record.getFieldList();
            final List<String> currentFields = table.getColumnNames().waitForComplete();

            // Get the list of missing fields.
            final List<RecordField> missingFields = fields.stream()
                    .filter(field -> !currentFields.contains(field.getName()))
                    .toList();

            // Are there no missing fields?
            if (!missingFields.isEmpty()) return this;

            // Otherwise, add the missing columns.
            missingFields.forEach(table::addColumn);
        }
        return this;
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
    public @NotNull <R extends Record> TableSelection<R, ?> createTableSelection(@NotNull Table<R> table) {
        return new SqliteTableSelection<>(this, table);
    }

    @Override
    public @NotNull CompletableFuture<DatabaseStatus> connectAsync() {

        // Create the future result.
        final CompletableFuture<DatabaseStatus> future = new CompletableFuture<>();

        new Thread(() -> {
            try {

                this.logger.info("Attempting to connect to the SQLite database.");

                // Initiate drivers.
                Class.forName("org.sqlite.JDBC");

                // Create directory.
                new File(this.file.getParent()).mkdirs();

                // Create database connection url.
                String url = this.getUrl();

                // Create a connection to the database.
                this.connection = DriverManager.getConnection(url);

                this.logger.info("Connected successfully to SQLite database.");

                // Set status and future.
                this.status = DatabaseStatus.CONNECTED;
                future.complete(this.status);


            } catch (Exception exception) {

                // Check if the database should attempt to reconnect later.
                if (this.willReconnect) {

                    // Attempt to reconnect to the database.
                    this.attemptReconnect();

                    // Set and return status.
                    this.status = DatabaseStatus.RECONNECTING;
                    future.complete(this.status);
                    throw new DatabaseException(exception, this, "connectAsync",
                            "Could not connect to SQLite database. Attempting to reconnect in "
                                    + this.reconnectCooldown.toSeconds() + "s."
                    );
                }

                // Otherwise, disconnect.
                this.status = DatabaseStatus.DISCONNECTED;
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
    public @NotNull CompletableFuture<DatabaseStatus> disconnectAsync(boolean reconnect) {

        // Create the future result.
        final CompletableFuture<DatabaseStatus> future = new CompletableFuture<>();

        new Thread(() -> {
            try {

                // Close connection.
                this.connection.close();
                this.status = DatabaseStatus.DISCONNECTED;

                // Should we reconnect?
                if (reconnect) {
                    this.status = DatabaseStatus.RECONNECTING;
                    this.connect();
                }

                // Complete future status.
                future.complete(this.status);

            } catch (Exception exception) {

                // Should we reconnect?
                if (reconnect) {
                    this.status = DatabaseStatus.RECONNECTING;
                    this.connect();
                }

                // Complete future status.
                future.complete(this.status);

                throw new DatabaseException(exception, this, "disconnectAsync",
                        "Error while disconnecting. reconnect=" + reconnect
                );
            }
        }).start();

        return future;
    }
}
