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

import com.github.squishylib.common.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigurationException extends RuntimeException {

    public static long lastErrorTimeStamp;

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

        Logger logger = new Logger("com.github.squishylib.configuration");
        logger.error("source: &f" + source);
        if (reason != null) logger.error("reason: &f" + reason);
        if (helpMessage != null) logger.error("&7" + String.join("\n&7", helpMessage));
        logger.error("&c");

        for (StackTraceElement element : exception.getStackTrace()) {
            logger.error("[Trace] " + element.getMethodName() + ":" + element.getLineNumber());
        }
    }
}

