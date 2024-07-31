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

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.LogRecord;

public class Formatter extends java.util.logging.Formatter {

    @Override
    public String format(LogRecord record) {
        return "\u001B[30m[" + record.getInstant().atZone(ZoneId.of("GMT+1")).format(DateTimeFormatter.ofPattern("dd/mm/yyyy hh:m")) + "] [" + record.getLevel().getName().split("")[0] + "] " + record.getMessage() + "\n";
    }
}
