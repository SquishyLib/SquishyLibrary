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

import com.github.squishylib.common.logger.Logger;
import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.database.implementation.SqliteDatabase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.crypto.Data;
import java.io.File;
import java.time.Duration;

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
public class DatabaseBuilder {

    public static final @NotNull String RECONNECT_COOLDOWN_IDENTIFIER = "reconnect_cooldown_millis";
    public static final @NotNull String WILL_RECONNECT_IDENTIFIER = "will_reconnect";
    public static final @NotNull String TIME_BETWEEN_REQUESTS_IDENTIFIER = "time_between_requests_millis";
    public static final @NotNull String MAX_REQUESTS_PENDING_IDENTIFIER = "max_requests_pending";

    public static final @NotNull String SQLITE_IDENTIFIER = "sqlite";
    public static final @NotNull String SQLITE_ENABLED_IDENTIFIER = "enabled";
    public static final @NotNull String SQLITE_PATH_IDENTIFIER = "path";

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

    public @NotNull Logger getLogger() {
        return this.logger;
    }

    public @NotNull DatabaseBuilder setLogger(@NotNull Logger logger) {
        this.logger = logger;
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

    public @NotNull Database create() {
        if (this.isSqliteEnabled()) {

            final String path = this.getSqlitePath();
            if (path == null) throw new DatabaseException(this, "create", "Path for sqlite database is not defined in the configuration.");

            return new SqliteDatabase(
                    this.getLogger(),
                    this.getReconnectCooldown(),
                    this.getWillReconnect(),
                    this.getTimeBetweenRequests(),
                    this.getMaxRequestsPending(),
                    new File(path)
            );
        }

        throw new DatabaseException(this, "create", "No database types where enabled.");
    }
}
