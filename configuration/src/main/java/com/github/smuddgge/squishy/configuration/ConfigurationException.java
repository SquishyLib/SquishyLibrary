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

package com.github.smuddgge.squishy.configuration;

import com.github.smuddgge.squishy.common.logger.ConsoleColor;
import org.jetbrains.annotations.NotNull;

public class ConfigurationException extends RuntimeException {

    public ConfigurationException(@NotNull final Object clazzInstance, @NotNull final String method, @NotNull final String reason) {
        super(ConsoleColor.parse("\n&7----------------------------------------------------------------------" +
                "\n&7class= &c" + clazzInstance.getClass().getName() +
                (clazzInstance instanceof Configuration configuration ? "\n&7path= &c " + configuration.getPath() : "") +
                "\n&7method= &c" + method +
                "\n&7reason= &c" + reason
        ));
    }

    public ConfigurationException(@NotNull final Exception exception, @NotNull final Object clazzInstance, @NotNull final String method, @NotNull final String reason) {
        super(ConsoleColor.parse("\n&7----------------------------------------------------------------------" +
                "\n&7class= &c" + clazzInstance.getClass().getName() +
                "\n&7method= &c" + method +
                "\n&7reason= &c" + reason +
                "\n&7exception= &c" + exception
        ), exception);
    }
}
