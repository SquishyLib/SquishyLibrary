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
import com.github.squishylib.database.field.ForeignField;
import com.github.squishylib.database.field.PrimaryField;
import com.github.squishylib.database.field.RecordField;
import com.github.squishylib.database.field.RecordFieldPool;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * A sql style database that is run on a server.
 */
public class MySqlDatabase extends RequestQueueDatabase {

    private @NotNull Database.Status status;
    private final @NotNull Logger logger;
    private final boolean shouldReconnectEveryCycle;
    private final @NotNull Duration reconnectCooldown;
    private final boolean willReconnect;
    private final @NotNull Duration timeBetweenRequests;
    private final long maxRequestPending;
    private final @NotNull String connectionString;
    private final @NotNull String databaseName;
    private final @NotNull String username;
    private final @NotNull String password;

    private final @NotNull List<Table<?>> tableList;
    private Connection connection;

    public MySqlDatabase(@NotNull Logger logger,
                         boolean shouldReconnectEveryCycle,
                         @NotNull Duration reconnectCooldown,
                         boolean willReconnect,
                         @NotNull Duration timeBetweenRequests,
                         long maxRequestPending,
                         @NotNull String connectionString,
                         @NotNull String databaseName,
                         @NotNull String username,
                         @NotNull String password) {

        this.status = Status.DISCONNECTED;
        this.logger = logger.extend(" [MySqlDatabase]");
        this.shouldReconnectEveryCycle = shouldReconnectEveryCycle;
        this.reconnectCooldown = reconnectCooldown;
        this.willReconnect = willReconnect;
        this.timeBetweenRequests = timeBetweenRequests;
        this.maxRequestPending = maxRequestPending;

        this.connectionString = connectionString;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;

        this.tableList = new ArrayList<>();
        this.logger.debug("Initialized mysql database class.");
    }

    @Override
    public @NotNull Type getType() {
        return Type.MYSQL;
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
    public @NotNull Database setStatus(@NotNull Status status) {
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

    public @NotNull Connection getConnection() {
        return this.connection;
    }

    @Override
    public @NotNull Database createTable(@NotNull Table<?> table) {

        // Create this methods logger.
        final Logger tempLogger = this.logger.extend(" &b.createTable() &7MySqlDatabase.java:160 table=" + table.getName() + "&7");
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
        final Logger tempLogger = this.logger.extend(" &b.getMissingFields() &7MySqlDatabase.java:215 table=" + table.getName() + "&7");

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
        Record<?> record = table.createEmptyRecord(new RecordFieldPool("temp generated record for create table statement"));

        // Loop though primary keys.
        record.getPrimaryFieldList().forEach(primaryField -> builder.append(
                "`{key}` {type} PRIMARY KEY,"
                        .replace("{key}", primaryField.getName())
                        .replace("{type}", primaryField.getType().getTypeName(Type.MYSQL, primaryField.getMaxSize()))
        ));

        // Loop though fields.
        record.getFieldList().stream()
                .filter(field -> !(field instanceof PrimaryField) && !(field instanceof ForeignField))
                .forEach(field -> builder.append(
                        "`{key}` {type},"
                                .replace("{key}", field.getName())
                                .replace("{type}", field.getType().getTypeName(Type.MYSQL, field.getMaxSize()))
                ));

        // Loop though foreign keys.
        record.getForeignFieldList().forEach(foreignField -> builder.append(
                "`{key}` {type} REFERENCES {reference}({reference_field}),"
                        .replace("{key}", foreignField.getName())
                        .replace("{type}", foreignField.getType().getTypeName(Type.MYSQL, foreignField.getMaxSize()))
                        .replace("{reference}", foreignField.getForeignTableName())
                        .replace("{reference_field}", foreignField.getForeignName())
        ));

        builder.deleteCharAt(builder.length() - 1);
        builder.append(");");

        return builder.toString();
    }

    @Override
    public <T extends Table<?>> @NotNull T getTable(@NotNull Class<T> clazz) {
        for (final Table<?> table : this.tableList) {
            if (clazz.isAssignableFrom(table.getClass())) return (T) table;
        }

        throw new DatabaseException(this, "getTable", "Table was not registered with the database: " + clazz.getName()
                + ". Please use database.createTable(Table<?> table) before trying to get the table instance."
        );
    }

    @Override
    public boolean hasTable(@NotNull String tableName) {
        try {

            // Create the statement
            String statement = "SHOW TABLES LIKE '{table}'"
                    .replace("{table}", tableName);

            // Execute the statement.
            ResultSet resultSet = this.connection.prepareStatement(statement).executeQuery();

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
        return new MySqlTableSelection<>(this, table);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> drop() {
        return this.addRequest(new Request<>(() -> {

            // Create this requests logger.
            final Logger tempLogger = this.getLogger().extend(" &b.drop() &7MySqlDatabase.java:315");

            // Create the sql statement.
            final String statement = "DROP DATABASE {database};"
                    .replace("{database}", this.databaseName);

            try {

                // Create the prepared statement.
                tempLogger.debug("&d⎡ &7Executing statement &b" + statement);
                PreparedStatement preparedStatement = this.getConnection().prepareStatement(statement);
                preparedStatement.execute();
                preparedStatement.close();
                tempLogger.debug("&d⎣ &7Success &btrue");

                // Disconnect.
                this.disconnect(false);
                return true;

            } catch (Exception exception) {
                throw new DatabaseException(exception, this, "getAmountOfRecords", "statement=&e" + statement + "&r");
            }
        }));
    }

    @Override
    public @NotNull CompletableFuture<Status> connectAsync() {

        // Create this methods logger.
        final Logger tempLogger = this.logger.extend(" &bconnectAsync() &7MySqlDatabase.java:308");
        tempLogger.debug("Connecting to the database. currentStatus=" + this.status);

        // Create the future result.
        final CompletableFuture<Status> future = new CompletableFuture<>();

        new Thread(() -> {
            try {

                this.logger.info("Attempting to connect to the &bmysql database.");

                // Create database connection url.
                String url = "jdbc:mysql://{address}/?user={username}&password={password}"
                        .replace("{address}", this.connectionString)
                        .replace("{username}", this.username)
                        .replace("{password}", this.password);
                tempLogger.debug("Connecting with url &b" + url);

                // Create a connection to the database.
                this.connection = DriverManager.getConnection("jdbc:mysql://{connection_string}/?user={username}&password={password}"
                        .replace("{connection_string}", this.connectionString)
                        .replace("{username}", this.username)
                        .replace("{password}", this.password)
                );
                tempLogger.debug("Connected to mysql database.");

                // Ensure database is created.
                String statement = "CREATE DATABASE IF NOT EXISTS {name}".replace("{name}", this.databaseName);
                tempLogger.debug("Ensuring database is created. &bstatement=" + statement);
                this.connection.createStatement().executeUpdate(statement);

                // Close the connection.
                this.connection.close();
                tempLogger.debug("Reconnecting but this time to the specific database.");

                // Create the specific database url.
                String url2 = "jdbc:mysql://{connection_string}/{database}"
                        .replace("{connection_string}", this.connectionString)
                        .replace("{database}", this.databaseName);
                tempLogger.debug("Connecting with url &b" + url2);

                // Connect to the specific database.
                this.connection = DriverManager.getConnection(url2, this.username, this.password);
                this.logger.info("Connected successfully to the &bmysql database.");

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
                            "Could not connect to mysql database. Attempting to reconnect in "
                                    + this.reconnectCooldown.toSeconds() + "s."
                    );
                }

                tempLogger.debug("The field willReconnect is false, disconnected.");

                // Otherwise, disconnect.
                this.status = Status.DISCONNECTED;
                future.complete(this.status);
                throw new DatabaseException(exception, this, "connectAsync",
                        "Could not connect to mysql database. Reconnecting is set to false."
                );
            }
        }).start();

        return future;
    }

    @Override
    public void closeConnection() throws Exception {
        this.connection.close();
    }
}
