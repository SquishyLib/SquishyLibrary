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

import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum ConsoleColor {
    RESET("\033[0m", "&r"),
    WHITE("\033[0m", "&f"),
    GRAY("\033[0;37m", "&7", "&8"),
    RED("\033[0;31m", "&c", "&4"),
    YELLOW("\033[0;33m", "&e", "&6"),
    GREEN("\033[0;32m", "&a", "&2"),
    BLUE("\033[0;34m", "&b", "&9"),
    CYAN("\033[0;36m", "&3"),
    PURPLE("\033[0;35m", "&5", "&d");

    private final @NotNull String code;
    private final @NotNull List<String> patterns;

    /**
     * Used to create a console color.
     *
     * @param code     The instance of the color code to replace with.
     * @param patterns The patterns that will be replaced with the color code.
     */
    ConsoleColor(@NotNull String code, @NotNull String... patterns) {
        this.code = code;
        this.patterns = List.of(patterns);
    }

    public @NotNull String getCode() {
        return this.code;
    }

    public @NotNull List<String> getPatternList() {
        return this.patterns;
    }

    @Override
    public String toString() {
        return this.getCode();
    }

    /**
     * Used to parse the colors in a string.
     * Converts the {@link ConsoleColor#patterns}
     * to the java color code.
     *
     * @param string The instance of a string.
     * @return The parsed string.
     */
    public static @NotNull String parse(@NotNull String string) {

        // Loop though all colors.
        for (ConsoleColor color : ConsoleColor.values()) {

            // Loop though all patterns.
            for (String pattern : color.getPatternList()) {
                string = string.replace(pattern, color.getCode());
            }
        }

        return string + "\033[0m";
    }
}
