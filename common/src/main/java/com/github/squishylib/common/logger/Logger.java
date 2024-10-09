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

package com.github.squishylib.common.logger;

import com.github.squishylib.common.indicator.Replicable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Handler;

/**
 * A logger that supports the use of color codes
 * making it easy to use colors in the console.
 * <p>
 * It also offers extending the logger with tags.
 * <p>
 * This is effectively a {@link java.util.logging.Logger} adapter.
 */
public class Logger implements Replicable<Logger> {

    private final @NotNull java.util.logging.Logger logger;

    private @Nullable String prefix;
    private boolean debugForwarding = false; // Send debug messages to info.

    /**
     * Used to create a new squishy logger adapter.
     *
     * @param logger The logger that should be used.
     * @param prefix The loggers prefix.
     */
    public Logger(@NotNull java.util.logging.Logger logger, @Nullable String prefix) {
        this.logger = logger;
        this.prefix = prefix;
    }

    /**
     * Used to create a new squishy logger adapter.
     *
     * @param logger The logger that should be used.
     */
    public Logger(@NotNull java.util.logging.Logger logger) {
        this(logger, null);
    }

    /**
     * If the name of the logger already exists this
     * will use the instance of that logger instead of
     * creating a new logger.
     * <p>
     * It is advised to use the package string as the name of the logger
     * to identify what package the logger is part of.
     *
     * @param name The name of the logger to get or create.
     */
    public Logger(@NotNull String name) {
        this(java.util.logging.Logger.getLogger(name), null);
    }

    /**
     * If the name of the logger already exists this
     * will use the instance of that logger instead of
     * creating a new logger.
     * <p>
     * It is advised to use the package string as the name of the logger
     * to identify what package the logger is part of.
     *
     * @param name   The name of the logger to get or create.
     * @param prefix The logging prefix to use.
     */
    public Logger(@NotNull String name, @Nullable String prefix) {
        this(java.util.logging.Logger.getLogger(name), prefix);
    }

    public @NotNull java.util.logging.Logger getLogger() {
        return this.logger;
    }

    public @Nullable String getPrefix() {
        return this.prefix;
    }

    /**
     * If a prefix exists, it will at a space at the end.
     * Otherwise, it will return an empty string.
     *
     * @return The formatted prefix.
     */
    public @NotNull String getPrefixFormatted() {
        return this.prefix == null ? "" : this.prefix + " ";
    }

    /**
     * This logger will add a space to the end of the prefix
     * to separate it from the message.
     *
     * @param prefix The prefix.
     * @return This instance.
     */
    public @NotNull Logger setPrefix(@Nullable String prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * The logging level determines what will be
     * outputted to the console.
     * <p>
     * For example, if set to ERROR, only errors will be outputted.
     *
     * @return The logging level.
     */
    public @NotNull java.util.logging.Level getLevel() {
        return this.logger.getLevel();
    }

    /**
     * The logging level determines what will be
     * outputted to the console.
     * <p>
     * For example, if set to ERROR, only errors will be outputted.
     *
     * @param level The logging level.
     * @return The logging level.
     */
    public @NotNull Logger setLevel(@NotNull java.util.logging.Level level) {
        this.logger.setLevel(level);
        return this;
    }

    /**
     * Should debug messages be sent on the info level?
     * <p>
     * The level of this logger should still be set to Debug
     * for debug messages to come though.
     * <p>
     * Sometimes the root logger doesn't let debug statements though,
     * so ths can be used as an alternative.
     * Otherwise, you can use {@link Logger#setRootLoggerLevel(Level)}.
     *
     * @param debugForwarding If debug messages should be sent though info.
     * @return This instance.
     */
    public @NotNull Logger setDebugForwarding(boolean debugForwarding) {
        this.debugForwarding = debugForwarding;
        return this;
    }

    /**
     * Used to check if the logger is outputting
     * debug messages.
     *
     * @return True if in debug mode.
     */
    public boolean isInDebugMode() {
        return this.logger.isLoggable(Level.DEBUG);
    }

    public @NotNull Logger error(@NotNull String message) {
        this.logger.log(Level.ERROR, ConsoleColor.parse("&c" + this.getPrefixFormatted() + message));
        return this;
    }

    public @NotNull Logger warn(@NotNull String message) {
        this.logger.log(Level.WARNING, ConsoleColor.parse("&e" + this.getPrefixFormatted() + message));
        return this;
    }

    public @NotNull Logger info(@NotNull String message) {
        this.logger.log(Level.INFO, ConsoleColor.parse("&7" + this.getPrefixFormatted() + message));
        return this;
    }

    public @NotNull Logger debug(@NotNull String message) {

        // Check if debug forwarding is enabled.
        // Debug forwarding is where debugs should be sent though the info level.
        if (debugForwarding && this.logger.isLoggable(Level.DEBUG)) {
            this.logger.log(Level.INFO, ConsoleColor.parse("&7" + this.getPrefixFormatted() + message));
        }

        this.logger.log(Level.DEBUG, ConsoleColor.parse("&7" + this.getPrefixFormatted() + message));
        return this;
    }

    /**
     * If you wish to have a space between the prefix before it,
     * you should include within the extension.
     *
     * @param prefixExtension The prefix to add to the current prefix.
     * @return A new logger with the extended prefix but linked log level.
     */
    public @NotNull Logger extend(@NotNull String prefixExtension) {
        if (this.getPrefix() == null) prefixExtension = prefixExtension.trim();
        else prefixExtension = this.getPrefix() + prefixExtension;
        return this.duplicate().setPrefix(prefixExtension);
    }

    /**
     * If you wish to have a space between the prefix before it,
     * you should include it within the prefix extension.
     * <p>
     * If you wish to have a dot between packages,
     * you should include it within the name extension.
     *
     * @param prefixExtension The prefix to add to the current prefix.
     * @param nameExtension   The name to add to the current name.
     * @return A new logger, unless the name already exists, with a new prefix.
     */
    public @NotNull Logger extendFully(@NotNull String prefixExtension, @NotNull String nameExtension) {
        if (this.getPrefix() == null) prefixExtension = prefixExtension.trim();
        else prefixExtension = this.getPrefix() + prefixExtension;

        return new Logger(
                this.getLogger().getName() + nameExtension,
                prefixExtension
        );
    }

    @Override
    public @NotNull Logger duplicate() {
        return new Logger(this.logger, this.prefix);
    }

    public static void setRootLoggerLevel(@NotNull Level level) {
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
        rootLogger.setLevel(level);
        Handler handler = rootLogger.getHandlers()[0];
        handler.setLevel(level);
    }
}
