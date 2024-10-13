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
import com.github.squishylib.database.*;
import com.github.squishylib.database.Record;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * A sql style database that is run on a server.
 */
public class MySqlDatabase extends RequestQueueDatabase {

    protected @NotNull Database.Status status;
    protected final @NotNull Logger logger;
    protected final boolean shouldReconnectEveryCycle;
    protected final @NotNull Duration reconnectCooldown;
    protected final boolean willReconnect;
    protected final @NotNull Duration timeBetweenRequests;
    protected final long maxRequestPending;
    protected final @NotNull String connectionString;
    protected final @NotNull String databaseName;
    protected final @NotNull String username;
    protected final @NotNull String password;

    protected final @NotNull List<Table<?>> tableList;
    protected Connection connection;

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

    // TODO

    @Override
    public @NotNull Database createTable(@NotNull Table<?> table) {
        return null;
    }

    @Override
    public <T extends Table<?>> @NotNull T getTable(@NotNull Class<T> clazz) {
        return null;
    }

    @Override
    public boolean hasTable(@NotNull String tableName) {
        return false;
    }

    @Override
    public @NotNull <R extends Record<R>> TableSelection<R, ?> createTableSelection(@NotNull Table<R> table) {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<Status> connectAsync() {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<Status> disconnectAsync(boolean reconnect) {
        return null;
    }
}
