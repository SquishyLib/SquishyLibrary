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

package com.github.squishylib.configuration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationException extends RuntimeException {

    private static Logger logger;
    private static long lastErrorTimeStamp;

    /**
     * @param exception   The optional instance of the exception.
     * @param source      For example: Leaf.get()
     * @param reason      If there is a specific reason.
     * @param helpMessage A way of solving the problem.
     */
    public ConfigurationException(@Nullable Exception exception, @NotNull final String source, @Nullable final String reason, @Nullable final String... helpMessage) {

        // Stop lots of errors.
        // Solve the first one first.
        if (lastErrorTimeStamp != -1 && System.currentTimeMillis() - lastErrorTimeStamp < 1000) {
            return;
        }

        lastErrorTimeStamp = System.currentTimeMillis();

        if (logger == null) {
            System.out.println("Squishy library logger is null!");
            return;
        }

        logger.severe("source: " + source);
        if (reason != null) logger.severe("reason: " + reason);
        if (helpMessage != null) logger.severe(String.join("\n", helpMessage));

        for (StackTraceElement element : exception.getStackTrace()) {
            logger.severe("[Trace] " + element.getMethodName() + ":" + element.getLineNumber());
        }
    }

    public static void useLogger(Logger logger) {
        ConfigurationException.logger = logger;
    }

    public static Logger getLogger() {
        return ConfigurationException.logger;
    }
}

