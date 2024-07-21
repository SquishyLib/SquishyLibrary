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

package com.github.squishylib.common.test;

import com.github.squishylib.common.logger.Logger;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class LoggerTests {

    @Test
    public void testLoggerExtensions() throws IOException {

        // Create a new logger.
        Logger test1 = new Logger("com.github.smuddgge.squishy.common.test", "[Test1]");

        // Extend the logger.
        Logger test2 = test1.extend(" [Test2]");

        // Print to the console.
        test2.info("-");
    }
}
