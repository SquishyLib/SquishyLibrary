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
import com.github.squishylib.common.logger.Logger;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Represents methods every database should have to
 * interact with the stored data.
 */
public interface Database {

    /**
     * The status of the database connection.
     * <p>
     * The status of {@link DatabaseStatus#DISCONNECTED} and {@link DatabaseStatus#RECONNECTING}
     * both mean that the database is currently disconnected.
     * It is advised to use the method {@link Database#isDisconnected()}
     * for this check.
     *
     * @return The database status.
     */
    @NotNull
    DatabaseStatus getStatus();

    /**
     * The logger that the database type is using.
     *
     * @return The logger.
     */
    @NotNull
    Logger getLogger();

    /**
     * The amount of time the database will wait before
     * attempting to reconnect.
     * <p>
     * The database will only reconnect if
     * {@link Database#willReconnect()} returns true.
     *
     * @return The cooldown before reconnecting.
     */
    @NotNull
    Duration getReconnectCooldown();

    /**
     * If the database will attempt to reconnect when it
     * gets disconnected.
     *
     * @return True if the database will attempt to reconnect.
     */
    boolean willReconnect();

    /**
     * Used to attempt to connect to the database.
     * <p>
     * Returns the status of the database once it has
     * tried to connect once.
     * <p>
     * You can use the {@link CompletableFuture#waitForComplete()}
     * to wait until it has attempted to connect.
     *
     * @return The completable status.
     */
    @NotNull
    CompletableFuture<DatabaseStatus> connectAsync();

    /**
     * Used to attempt to connect to the database.
     * <p>
     * This will wait until it has finished trying to
     * connect for the first time.
     *
     * @return This instance.
     */
    default @NotNull Database connect() {
        this.connectAsync().waitForComplete();
        return this;
    }

    /**
     * Used to close the connection to the database.
     * <p>
     * Returns the status of the database once it has
     * tried to disconnect.
     * <p>
     * You can use the {@link CompletableFuture#waitForComplete()}
     * to wait until it has attempted to connect.
     *
     * @param reconnect If the database should still try to reconnect.
     * @return The completable status.
     */
    @NotNull
    CompletableFuture<DatabaseStatus> disconnectAsync(boolean reconnect);

    /**
     * Used to close the connection to the database.
     *
     * @param reconnect If it should still attempt to reconnect.
     * @return This instance.
     */
    default @NotNull Database disconnect(boolean reconnect) {
        this.disconnectAsync(reconnect).waitForComplete();
        return this;
    }

    /**
     * Used to check if the database is currently connected.
     * <p>
     * This method checks if the status is
     * {@link DatabaseStatus#CONNECTED}.
     *
     * @return True if connected.
     */
    default boolean isConnected() {
        return this.getStatus().isConnected();
    }

    /**
     * Used to check if the database is currently disconnected.
     *
     * @return True if disconnected.
     */
    default boolean isDisconnected() {
        return this.getStatus().isDisconnected();
    }

    /**
     * Used to check if the logger is outputting debug messages.
     *
     * @return True if the database is in debug mode.
     */
    default boolean isInDebugMode() {
        return this.getLogger().isInDebugMode();
    }
}
