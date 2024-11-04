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
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MongoDatabase extends RequestQueueDatabase {

    private @NotNull Database.Status status;
    private final @NotNull Logger logger;
    private final boolean shouldReconnectEveryCycle;
    private final @NotNull Duration reconnectCooldown;
    private final boolean willReconnect;
    private final @NotNull Duration timeBetweenRequests;
    private final long maxRequestPending;
    private final @NotNull String connectionString;
    private final @NotNull String databaseName;

    private final @NotNull List<Table<?>> tableList;
    private MongoClient client;
    private com.mongodb.client.MongoDatabase database;

    public MongoDatabase(@NotNull Logger logger,
                         boolean shouldReconnectEveryCycle,
                         @NotNull Duration reconnectCooldown,
                         boolean willReconnect,
                         @NotNull Duration timeBetweenRequests,
                         long maxRequestPending,
                         @NotNull String connectionString,
                         @NotNull String databaseName) {

        this.status = Status.DISCONNECTED;
        this.logger = logger.extend(" [MongoDatabase]");
        this.shouldReconnectEveryCycle = shouldReconnectEveryCycle;
        this.reconnectCooldown = reconnectCooldown;
        this.willReconnect = willReconnect;
        this.timeBetweenRequests = timeBetweenRequests;
        this.maxRequestPending = maxRequestPending;

        this.connectionString = connectionString;
        this.databaseName = databaseName;

        this.tableList = new ArrayList<>();
        this.logger.debug("Initialized mongo database class.");
    }

    @Override
    public @NotNull Type getType() {
        return Type.MONGO;
    }

    @Override
    public @NotNull Database.Status getStatus() {
        try {

            // Check if the database is reconnecting.
            if (this.status.equals(Status.RECONNECTING)) return this.status;

            // Check if the connection is closed by running some
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document commandResult = database.runCommand(command);

            // Otherwise, status is connected.
            this.status = Status.CONNECTED;

        } catch (Exception exception) {
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

    public @NotNull com.mongodb.client.MongoDatabase getDatabase() {
        return this.database;
    }

    @Override
    public @NotNull Database createTable(@NotNull Table<?> table) {

        // Create this methods logger.
        final Logger tempLogger = this.logger.extend(" &b.createTable() &7MongoDatabase.java:1 table=" + table.getName() + "&7");
        tempLogger.debug("Creating table.");

        // Link the table to this database.
        table.setDatabase(this);

        // Add the table to the list.
        this.tableList.add(table);

        // Does the table already exist in the database?
        if (this.hasTable(table.getName())) {
            tempLogger.debug("Table already exists in database.");
            return this;
        }

        try {
            tempLogger.debug("Creating table: " + table.getName());
            this.database.createCollection(table.getName());
            tempLogger.debug("Table has been created.");
        } catch (Exception exception) {
            throw new DatabaseException(exception, this, "createTable", "Failed to create the table &7" + table.getName() + ".");
        }

        return this;
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
        AtomicBoolean flag = new AtomicBoolean(false);
        this.database.listCollectionNames().forEach(name -> {
            if (tableName.equalsIgnoreCase(name)) flag.set(true);
        });
        return flag.get();
    }

    @Override
    public @NotNull <R extends Record<R>> TableSelection<R, ?> createTableSelection(@NotNull Table<R> table) {
        return new MongoTableSelection<>(this, table);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> drop() {
        return this.addRequest(new Request<>(() -> {
            this.database.drop();
            return true;
        }));
    }

    @Override
    public @NotNull CompletableFuture<Status> connectAsync() {

        // Create this methods logger.
        final Logger tempLogger = this.logger.extend(" &bconnectAsync() &7MongoDatabase.java:1");
        tempLogger.debug("Connecting to the database. currentStatus=" + this.status);

        // Create the future result.
        final CompletableFuture<Status> future = new CompletableFuture<>();

        new Thread(() -> {
            try {

                this.logger.info("Attempting to connect to the &bmongo database.");

                // Create a client with the connection string.
                tempLogger.debug("Connecting to client with connection string &b" + this.connectionString);
                this.client = MongoClients.create(this.connectionString);

                // Create if it doesn't exist and get.
                this.database = this.client.getDatabase(this.databaseName);
                this.logger.info("Connected successfully to the &bmongo database.");

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
                            "Could not connect to mongo database. Attempting to reconnect in "
                                    + this.reconnectCooldown.toSeconds() + "s."
                    );
                }

                tempLogger.debug("The field willReconnect is false, disconnected.");

                // Otherwise, disconnect.
                this.status = Status.DISCONNECTED;
                future.complete(this.status);
                throw new DatabaseException(exception, this, "connectAsync",
                        "Could not connect to mongo database. Reconnecting is set to false."
                );
            }
        }).start();

        return future;
    }

    @Override
    public void closeConnection() throws Exception {
        this.client.close();
    }
}
