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

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDatabase extends DatabaseRequestQueue implements Database {

    private @NotNull DatabaseStatus status;
    private final @NotNull Logger logger;
    private final @NotNull Duration reconnectCooldown;
    private final boolean willReconnect;
    private final @NotNull Duration timeBetweenRequests;
    private final long maxRequestPending;
    private final @NotNull File file;

    private final @NotNull List<Table<?>> tableList;

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
        return null;
    }

    @Override
    public @NotNull CompletableFuture<DatabaseStatus> disconnectAsync(boolean reconnect) {
        return null;
    }
}
