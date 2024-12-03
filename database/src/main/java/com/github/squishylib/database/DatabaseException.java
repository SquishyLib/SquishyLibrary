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

import com.github.squishylib.common.logger.ConsoleColor;
import org.jetbrains.annotations.NotNull;

public class DatabaseException extends RuntimeException {

    public DatabaseException(@NotNull final Object clazzInstance, @NotNull final String method, @NotNull final String reason) {
        System.out.println(ConsoleColor.parse("\n&7----------------------------------------------------------------------" +
                "\n&7class= &c" + clazzInstance.getClass().getName() +
                "\n&7method= &c" + method +
                "\n&7reason= &c" + reason
        ));
    }

    public DatabaseException(@NotNull final Exception exception, @NotNull final Object clazzInstance, @NotNull final String method, @NotNull final String reason) {
        super(exception);
        System.out.println(ConsoleColor.parse("&7----------------------------------------------------------------------" +
                "\n&7class= &c" + clazzInstance.getClass().getName() +
                "\n&7method= &c" + method +
                "\n&7reason= &c" + reason
        ));
    }
}
