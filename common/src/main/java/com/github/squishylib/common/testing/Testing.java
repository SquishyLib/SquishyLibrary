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

package com.github.squishylib.common.testing;

import com.github.squishylib.common.logger.Formatter;
import com.github.squishylib.common.logger.Level;

import java.util.logging.Handler;

public class Testing {

    public static void setupConsoleLogger() {

        // Set up root logger.
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
        rootLogger.setLevel(Level.ALL);
        Handler handler = rootLogger.getHandlers()[0];
        handler.setLevel(Level.ALL);
        handler.setFormatter(new Formatter());
    }
}
