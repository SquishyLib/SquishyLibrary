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

import com.github.squishylib.common.logger.Level;
import com.github.squishylib.common.logger.Logger;
import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.database.implementation.MongoDatabase;
import com.github.squishylib.database.implementation.MySqlDatabase;
import com.github.squishylib.database.implementation.SqliteDatabase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.Duration;

/**
 * <pre>{@code
 * should_reconnect_every_cycle: Boolean
 * reconnect_cooldown_millis: Long
 * will_reconnect: Boolean
 * time_between_requests_millis: Long
 * max_requests_pending: Long
 *
 * sqlite:
 *   enabled: true
 *   path: String
 * }</pre>
 */
public class DatabaseBuilder {

    public static final @NotNull String SHOULD_RECONNECT_EVERY_CYCLE = "should_reconnect_every_cycle";
    public static final @NotNull String RECONNECT_COOLDOWN_IDENTIFIER = "reconnect_cooldown_millis";
    public static final @NotNull String WILL_RECONNECT_IDENTIFIER = "will_reconnect";
    public static final @NotNull String TIME_BETWEEN_REQUESTS_IDENTIFIER = "time_between_requests_millis";
    public static final @NotNull String MAX_REQUESTS_PENDING_IDENTIFIER = "max_requests_pending";

    public static final @NotNull String SQLITE_IDENTIFIER = "sqlite";
    public static final @NotNull String SQLITE_ENABLED_IDENTIFIER = "enabled";
    public static final @NotNull String SQLITE_PATH_IDENTIFIER = "path";

    public static final @NotNull String MYSQL_IDENTIFIER = "mysql";
    public static final @NotNull String MYSQL_ENABLED_IDENTIFIER = "enabled";
    public static final @NotNull String MYSQL_CONNECTION_STRING_IDENTIFIER = "connection_string";
    public static final @NotNull String MYSQL_DATABASE_NAME_IDENTIFIER = "database_name";
    public static final @NotNull String MYSQL_USERNAME_IDENTIFIER = "username";
    public static final @NotNull String MYSQL_PASSWORD_IDENTIFIER = "password";

    public static final @NotNull String MONGO_IDENTIFIER = "mongo";
    public static final @NotNull String MONGO_ENABLED_IDENTIFIER = "enabled";
    public static final @NotNull String MONGO_CONNECTION_STRING_IDENTIFIER = "connection_string";
    public static final @NotNull String MONGO_DATABASE_NAME_IDENTIFIER = "database_name";

    private final @NotNull ConfigurationSection section;
    private @NotNull Logger logger;

    /**
     * <pre>{@code
     * reconnect_cooldown_millis: Long
     * will_reconnect: Boolean
     * time_between_requests_millis: Long
     * max_requests_pending: Long
     *
     * sqlite:
     *   enabled: true
     *   path: String
     * }</pre>
     */
    public DatabaseBuilder() {
        this.section = new MemoryConfigurationSection();
        this.logger = new Logger("com.github.squishylib.database");
    }

    /**
     * <pre>{@code
     * reconnect_cooldown_millis: Long
     * will_reconnect: Boolean
     * time_between_requests_millis: Long
     * max_requests_pending: Long
     *
     * sqlite:
     *   enabled: true
     *   path: String
     * }</pre>
     *
     * @param section The configuration section containing the database info.
     */
    public DatabaseBuilder(@NotNull ConfigurationSection section) {
        this.section = section;
        this.logger = new Logger("com.github.squishylib.database");
    }

    public @NotNull DatabaseBuilder setDebugMode(boolean isDebugMode) {
        if (isDebugMode) {
            this.getLogger().setLevel(Level.ALL);
            this.logger.setDebugForwarding(true);
        } else {
            this.getLogger().setLevel(Level.INFO);
            this.logger.setDebugForwarding(false);
        }
        return this;
    }

    public @NotNull Logger getLogger() {
        return this.logger;
    }

    public @NotNull DatabaseBuilder setLogger(@NotNull Logger logger) {
        this.logger = logger;
        return this;
    }

    public boolean getShouldReconnectEveryCycle() {
        return this.section.getBoolean(SHOULD_RECONNECT_EVERY_CYCLE, true);
    }

    public @NotNull DatabaseBuilder setShouldReconnectEveryCycle(boolean shouldReconnectEveryCycle) {
        this.section.set(SHOULD_RECONNECT_EVERY_CYCLE, shouldReconnectEveryCycle);
        return this;
    }

    public @NotNull Duration getReconnectCooldown() {
        return Duration.ofMillis(this.section.getLong(RECONNECT_COOLDOWN_IDENTIFIER, 500));
    }

    public @NotNull DatabaseBuilder setReconnectCooldown(@NotNull Duration reconnectCooldown) {
        this.section.set(RECONNECT_COOLDOWN_IDENTIFIER, reconnectCooldown.toMillis());
        return this;
    }

    public boolean getWillReconnect() {
        return this.section.getBoolean(WILL_RECONNECT_IDENTIFIER, false);
    }

    public @NotNull DatabaseBuilder setWillReconnect(boolean willReconnect) {
        this.section.set(WILL_RECONNECT_IDENTIFIER, willReconnect);
        return this;
    }

    public @NotNull Duration getTimeBetweenRequests() {
        return Duration.ofMillis(this.section.getLong(TIME_BETWEEN_REQUESTS_IDENTIFIER, 500));
    }

    public @NotNull DatabaseBuilder setTimeBetweenRequests(@NotNull Duration timeBetweenRequests) {
        this.section.set(TIME_BETWEEN_REQUESTS_IDENTIFIER, timeBetweenRequests.toMillis());
        return this;
    }

    public long getMaxRequestsPending() {
        return this.section.getLong(MAX_REQUESTS_PENDING_IDENTIFIER, 500);
    }

    public @NotNull DatabaseBuilder setMaxRequestsPending(long maxRequestsPending) {
        this.section.set(MAX_REQUESTS_PENDING_IDENTIFIER, maxRequestsPending);
        return this;
    }

    public boolean isSqliteEnabled() {
        return this.section.getBoolean(SQLITE_IDENTIFIER + "." + SQLITE_ENABLED_IDENTIFIER, false);
    }

    public @NotNull DatabaseBuilder setSqliteEnabled(boolean sqliteEnabled) {
        this.section.set(SQLITE_IDENTIFIER + "." + SQLITE_ENABLED_IDENTIFIER, sqliteEnabled);
        return this;
    }

    public @Nullable String getSqlitePath() {
        return this.section.getSection(SQLITE_IDENTIFIER).getString(SQLITE_PATH_IDENTIFIER);
    }

    public @NotNull DatabaseBuilder setSqlitePath(@Nullable String path) {
        this.section.set(SQLITE_IDENTIFIER + "." + SQLITE_PATH_IDENTIFIER, path);
        return this;
    }

    public boolean isMySqlEnabled() {
        return this.section.getSection(MYSQL_IDENTIFIER).getBoolean(MYSQL_ENABLED_IDENTIFIER, false);
    }

    public @NotNull DatabaseBuilder setMySqlEnabled(boolean mySqlEnabled) {
        this.section.getSection(MYSQL_IDENTIFIER).set(MYSQL_ENABLED_IDENTIFIER, mySqlEnabled);
        return this;
    }

    public @Nullable String getMySqlConnectionString() {
        return this.section.getSection(MYSQL_IDENTIFIER).getString(MYSQL_CONNECTION_STRING_IDENTIFIER);
    }

    public @NotNull DatabaseBuilder setMySqlConnectionString(@Nullable String connectionString) {
        this.section.getSection(MYSQL_IDENTIFIER).set(MYSQL_CONNECTION_STRING_IDENTIFIER, connectionString);
        return this;
    }

    public @Nullable String getMySqlDatabaseName() {
        return this.section.getSection(MYSQL_IDENTIFIER).getString(MYSQL_DATABASE_NAME_IDENTIFIER);
    }

    public @NotNull DatabaseBuilder setMySqlDatabaseName(@Nullable String databaseName) {
        this.section.getSection(MYSQL_IDENTIFIER).set(MYSQL_DATABASE_NAME_IDENTIFIER, databaseName);
        return this;
    }

    public @Nullable String getMySqlUsername() {
        return this.section.getSection(MYSQL_IDENTIFIER).getString(MYSQL_USERNAME_IDENTIFIER);
    }

    public @NotNull DatabaseBuilder setMySqlUsername(@Nullable String username) {
        this.section.getSection(MYSQL_IDENTIFIER).set(MYSQL_USERNAME_IDENTIFIER, username);
        return this;
    }

    public @Nullable String getMySqlPassword() {
        return this.section.getSection(MYSQL_IDENTIFIER).getString(MYSQL_PASSWORD_IDENTIFIER);
    }

    public @NotNull DatabaseBuilder setMySqlPassword(@Nullable String password) {
        this.section.getSection(MYSQL_IDENTIFIER).set(MYSQL_PASSWORD_IDENTIFIER, password);
        return this;
    }

    public boolean isMongoEnabled() {
        return this.section.getSection(MONGO_IDENTIFIER).getBoolean(MONGO_ENABLED_IDENTIFIER, false);
    }

    public @NotNull DatabaseBuilder setMongoEnabled(boolean mongoEnabled) {
        this.section.getSection(MONGO_IDENTIFIER).set(MONGO_ENABLED_IDENTIFIER, mongoEnabled);
        return this;
    }

    public @Nullable String getMongoConnectionString() {
        return this.section.getSection(MONGO_IDENTIFIER).getString(MONGO_CONNECTION_STRING_IDENTIFIER);
    }

    public @NotNull DatabaseBuilder setMongoConnectionString(@Nullable String connectionString) {
        this.section.getSection(MONGO_IDENTIFIER).set(MONGO_CONNECTION_STRING_IDENTIFIER, connectionString);
        return this;
    }

    public @Nullable String getMongoDatabaseName() {
        return this.section.getSection(MONGO_IDENTIFIER).getString(MONGO_DATABASE_NAME_IDENTIFIER);
    }

    public @NotNull DatabaseBuilder setMongoDatabaseName(@Nullable String databaseName) {
        this.section.getSection(MONGO_IDENTIFIER).set(MONGO_DATABASE_NAME_IDENTIFIER, databaseName);
        return this;
    }

    public @NotNull Database create() {

        if (this.isSqliteEnabled()) {

            final String path = this.getSqlitePath();
            if (path == null)
                throw new DatabaseException(this, "create", "Path for sqlite database is not defined in the configuration.");

            return new SqliteDatabase(
                    this.getLogger(),
                    this.getShouldReconnectEveryCycle(),
                    this.getReconnectCooldown(),
                    this.getWillReconnect(),
                    this.getTimeBetweenRequests(),
                    this.getMaxRequestsPending(),
                    new File(path)
            );
        }

        if (this.isMySqlEnabled()) {

            final String connectionString = this.getMySqlConnectionString();
            final String databaseName = this.getMySqlDatabaseName();
            final String username = this.getMySqlUsername();
            final String password = this.getMySqlPassword();

            if (connectionString == null) {
                throw new DatabaseException(this, "create", "Connection string for mysql database is not defined.");
            }
            if (databaseName == null) {
                throw new DatabaseException(this, "create", "Database name for mysql database is not defined.");
            }
            if (username == null) {
                throw new DatabaseException(this, "create", "Username for mysql database is not defined.");
            }
            if (password == null) {
                throw new DatabaseException(this, "create", "Password for mysql database is not defined.");
            }

            return new MySqlDatabase(
                    this.getLogger(),
                    this.getShouldReconnectEveryCycle(),
                    this.getReconnectCooldown(),
                    this.getWillReconnect(),
                    this.getTimeBetweenRequests(),
                    this.getMaxRequestsPending(),
                    connectionString,
                    databaseName,
                    username,
                    password
            );
        }

        if (this.isMongoEnabled()) {

            final String connectionString = this.getMongoConnectionString();
            final String databaseName = this.getMongoDatabaseName();

            if (connectionString == null) {
                throw new DatabaseException(this, "create", "Connection string for mongo database is not defined.");
            }

            if (databaseName == null) {
                throw new DatabaseException(this, "create", "Database name for mongo database is not defined.");
            }

            return new MongoDatabase(
                    this.getLogger(),
                    this.getShouldReconnectEveryCycle(),
                    this.getReconnectCooldown(),
                    this.getWillReconnect(),
                    this.getTimeBetweenRequests(),
                    this.getMaxRequestsPending(),
                    connectionString,
                    databaseName
            );
        }

        throw new DatabaseException(this, "create", "No database types where enabled.");
    }
}
