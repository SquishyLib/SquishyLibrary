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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * A database that is represented by a local file.
 */
public class SQLiteDatabase extends DatabaseRequestQueue implements Database {

    private @NotNull DatabaseStatus status;
    private final @NotNull Logger logger;
    private final @NotNull Duration reconnectCooldown;
    private final boolean willReconnect;
    private final @NotNull Duration timeBetweenRequests;
    private final long maxRequestPending;
    private final @NotNull File file;

    private final @NotNull List<Table<?>> tableList;
    private Connection connection;

    public SQLiteDatabase(@NotNull Logger logger,
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
    public @NotNull DatabaseStatus getStatus() {
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
        this.tableList.add(table);
        return this;
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
    public @NotNull <R extends Record> TableSelection<R, ?> createTableSelection(@NotNull Table<R> table) {
        return new SQLiteTableSelection<>(this, table);
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
                    this.connectAsync();
                }

                // Complete future status.
                future.complete(this.status);

            } catch (Exception exception) {

                // Should we reconnect?
                if (reconnect) {
                    this.status = DatabaseStatus.RECONNECTING;
                    this.connectAsync();
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
