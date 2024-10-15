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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;

/**
 * Represents methods every database should have to
 * interact with the stored data.
 */
public interface Database {

    /**
     * The different kinds of database connections.
     * Each database instance will be connected to one of these.
     */
    enum Type {
        SQLITE,
        MYSQL
    }

    /**
     * Represents the connection status of a database.
     * Weather or not it is connected, disconnected or reconnecting.
     */
    enum Status {
        CONNECTED,
        DISCONNECTED,
        RECONNECTING;

        public boolean isConnected() {
            return this == CONNECTED;
        }

        public boolean isDisconnected() {
            return this == DISCONNECTED || this == RECONNECTING;
        }
    }

    /**
     * The database type that is being used
     * behind the scenes!
     *
     * @return The type of database.
     */
    @NotNull Type getType();

    /**
     * The status of the database connection.
     * <p>
     * The status of {@link Status#DISCONNECTED} and {@link Status#RECONNECTING}
     * both mean that the database is currently disconnected.
     * It is advised to use the method {@link Database#isDisconnected()}
     * for this check.
     *
     * @return The database status.
     */
    @NotNull
    Database.Status getStatus();

    /**
     * This method should only be used by the library.
     * If you are trying to disconnect use the
     * {@link Database#disconnect(boolean)} method.
     *
     * @param status The status to set the database.
     * @return This instance.
     */
    @ApiStatus.Internal
    @NotNull
    Database setStatus(@NotNull Database.Status status);

    /**
     * The logger that the database type is using.
     *
     * @return The logger.
     */
    @NotNull
    Logger getLogger();

    /**
     * If the database should actively disconnect and reconnect if there
     * have not been any requests in a while.
     *
     * @return True if the database should be disconnected and
     * reconnected every cycle.
     */
    boolean shouldReconnectEveryCycle();

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
     * The time between requests that are getting sent
     * to the database.
     * <p>
     * This should be a low value, primarily used to let the
     * database catch up.
     * <p>
     * This client will still however wait for a request to be
     * completed before sending a new one.
     *
     * @return The duration between requests.
     */
    @NotNull
    Duration getTimeBetweenRequests();

    /**
     * The maximum amount of requests that are allowed to be
     * waiting at one time.
     *
     * @return The maximum requests pending.
     */
    long getMaxRequestsPending();

    /**
     * Used to get the list of tables registered with
     * this database instance.
     *
     * @return The list of tables.
     */
    @NotNull
    List<Table<?>> getTableList();

    /**
     * Used to get the amount of tables registered with this database.
     *
     * @return The amount of tables.
     */
    int getAmountOfTables();

    /**
     * Used to create a table if it doesn't exist and register it
     * with this database instance.
     *
     * @param table The instance of the table.
     * @return This instance.
     */
    @NotNull
    Database createTable(@NotNull Table<?> table);

    /**
     * Used to get a registered table.
     *
     * @param clazz The table class type.
     * @param <T>   The table type.
     * @return The registered table.
     * @throws DatabaseException If the table was not registered.
     */
    <T extends Table<?>> @NotNull T getTable(@NotNull Class<T> clazz);

    /**
     * Used to check the database if a table has been created.
     *
     * @param tableName The table name to check.
     * @return True if the database contains the table.
     */
    boolean hasTable(@NotNull String tableName);

    /**
     * Used to create a table selection implementation
     * for a table instance.
     *
     * @param table The instance of a table.
     * @param <R>   The type of record being using.
     * @return The table selection implementation.
     */
    <R extends Record<R>> @NotNull TableSelection<R, ?> createTableSelection(@NotNull Table<R> table);

    /**
     * This will delete the database and close the connection.
     *
     * @return True if successful.
     */
    @NotNull CompletableFuture<Boolean> drop();

    /**
     * Used to attempt to connect to the database.
     * <p>
     * Returns the status of the database once it has
     * tried to connect once.
     * <p>
     * You can use the {@link CompletableFuture#waitAndGet()}
     * to wait until it has attempted to connect.
     *
     * @return The completable status.
     */
    @NotNull
    CompletableFuture<Status> connectAsync();

    /**
     * Used to attempt to connect to the database.
     * <p>
     * This will wait until it has finished trying to
     * connect for the first time.
     *
     * @return This instance.
     */
    default @NotNull Database connect() {
        this.connectAsync().waitAndGet();
        return this;
    }

    /**
     * Should only be used by the library to access
     * the close connection method.
     */
    @ApiStatus.Internal
    void closeConnection() throws Exception;

    /**
     * Used to close the connection to the database.
     * <p>
     * Returns the status of the database once it has
     * tried to disconnect.
     * <p>
     * You can use the {@link CompletableFuture#waitAndGet()}
     * to wait until it has attempted to connect.
     *
     * @param reconnect If the database should still try to reconnect.
     * @return The completable status.
     */
    default @NotNull CompletableFuture<Status> disconnectAsync(boolean reconnect) {

        // Create this methods logger.
        final Logger tempLogger = this.getLogger().extend(" &b.disconnectAsync() &7Database.java:248");
        tempLogger.debug("Disconnecting from the database. &bcurrentStatus=" + this.getStatus() + " reconnect=" + reconnect);

        // Create the future result.
        final CompletableFuture<Status> future = new CompletableFuture<>();

        new Thread(() -> {
            try {

                if (this.getStatus().equals(Status.RECONNECTING)) {
                    throw new DatabaseException(this, "disconnectAsync", "Attempted to disconnect when reconnecting.");
                }

                // Check if already disconnected.
                if (this.isDisconnected() && this.getStatus().equals(Status.DISCONNECTED)) {
                    tempLogger.debug("Already disconnected.");

                } else {
                    // Close connection.
                    this.closeConnection();
                    this.setStatus(Status.DISCONNECTED);
                    tempLogger.debug("Connection closed.");
                }

                // Should we reconnect?
                if (reconnect) {
                    tempLogger.debug("Reconnecting.");
                    this.setStatus(Status.RECONNECTING);
                    this.connect();
                }

                // Complete future status.
                future.complete(this.getStatus());

            } catch (Exception exception) {

                tempLogger.debug("An error occurred while disconnecting.");

                // Should we reconnect?
                if (reconnect) {
                    tempLogger.debug("Reconnecting.");
                    this.setStatus(Status.RECONNECTING);
                    this.connect();
                }

                // Complete future status.
                future.complete(this.getStatus());

                throw new DatabaseException(exception, this, "disconnectAsync",
                        "Error while disconnecting. &ereconnect=" + reconnect
                );
            }
        }).start();

        return future;
    }

    /**
     * Used to close the connection to the database.
     *
     * @param reconnect If it should still attempt to reconnect.
     * @return This instance.
     */
    default @NotNull Database disconnect(boolean reconnect) {
        this.disconnectAsync(reconnect).waitAndGet();
        return this;
    }

    /**
     * Used to check if the database is currently connected.
     * <p>
     * This method checks if the status is
     * {@link Status#CONNECTED}.
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

    /**
     * Used to reconnect to the database if the connection was closed.
     * <p>
     * This will stop the current thread until complete.
     *
     * @return True if connected.
     */
    default boolean reconnectIfDisconnected() {

        // Create this methods logger.
        final Logger tempLogger = this.getLogger().extend(" &b.reconnectIfDisconnected() &Database.java:263 &7type=" + this.getType().name());

        try {

            tempLogger.debug("Checking if connected to the database. result=" + this.isConnected());

            // Is the database connected?
            if (this.isConnected()) return true;

            // Should the database reconnect?
            if (this.willReconnect()) {

                tempLogger.debug("Attempting to reconnect.");

                // Attempt to reconnect.
                this.setStatus(Status.RECONNECTING);
                this.connect();

                // Is the database now connected?
                if (this.isConnected()) {
                    tempLogger.debug("Database is now connected.");
                    return true;
                }

                // Otherwise, it will try to reconnect,
                // so here we wait till it has reconnected.
                while (true) {

                    tempLogger.debug("Checking if connected in &b" + this.getReconnectCooldown().toMillis() + "ms.");

                    // Wait the cooldown time.
                    Thread.sleep(this.getReconnectCooldown().toMillis());

                    // Check if connected.
                    if (this.isConnected()) {
                        tempLogger.debug("Database is now connected.");
                        return true;
                    }
                }
            }

            return false;

        } catch (Exception exception) {
            throw new DatabaseException(exception, this, "reconnectIfDisconnected", "Failed to check if disconnected and reconnecting.");
        }
    }

    default void attemptReconnect() {
        new Thread(() -> {
            try {

                // Wait cooldown.
                Thread.sleep(this.getReconnectCooldown().toMillis());

                // Attempt to reconnect.
                this.connectAsync();

            } catch (Exception exception) {
                throw new DatabaseException(exception, this, "attemptReconnect",
                        "Error occurred while waiting to reconnect to the database."
                );
            }
        }).start();
    }
}
