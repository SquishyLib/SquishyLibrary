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

/**
 * A logger where you can use color codes
 * to write color to the console easily.
 * <p>
 * It also offers extending the logger with tags.
 * <p>
 * This is effectively a {@link java.util.logging.Logger} adapter.
 */
public class Logger implements Replicable<Logger> {

    private final @NotNull java.util.logging.Logger logger;

    private @Nullable String prefix;

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
    public Logger(@NotNull String name, @NotNull String prefix) {
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
     * Used to check if the logger is outputting
     * debug messages.
     *
     * @return True if in debug mode.
     */
    public boolean isInDebugMode() {
        return this.logger.isLoggable(Level.DEBUG);
    }

    public @NotNull Logger error(@NotNull String message) {
        this.logger.log(Level.ERROR, ConsoleColor.parse(this.getPrefixFormatted() + message));
        return this;
    }

    public @NotNull Logger warn(@NotNull String message) {
        this.logger.log(Level.WARNING, ConsoleColor.parse(this.getPrefixFormatted() + message));
        return this;
    }

    public @NotNull Logger info(@NotNull String message) {
        this.logger.log(Level.INFO, ConsoleColor.parse(this.getPrefixFormatted() + message));
        return this;
    }

    public @NotNull Logger debug(@NotNull String message) {
        this.logger.log(Level.DEBUG, ConsoleColor.parse(this.getPrefixFormatted() + message));
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
        return this.duplicate().setPrefix(this.getPrefix() + prefixExtension);
    }

    /**
     * If you wish to have a space between the prefix before it,
     * you should include it within the prefix extension.
     * <p>
     * If you wish to have a dot between packages,
     * you should include it within the name extension.
     *
     * @param prefixExtension The prefix to add to the current prefix.
     * @param nameExtension The name to add to the current name.
     * @return A new logger, unless the name already exists, with a new prefix.
     */
    public @NotNull Logger extendFully(@NotNull String prefixExtension, @NotNull String nameExtension) {
        return new Logger(
                this.getLogger().getName() + nameExtension,
                this.getPrefixFormatted() + prefixExtension
        );
    }

    @Override
    public @NotNull Logger duplicate() {
        return new Logger(this.logger, this.prefix);
    }
}
