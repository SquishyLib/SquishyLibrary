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

/**
 * Similar levels to {@link java.util.logging.Level}
 * but with updated names.
 */
public class Level extends java.util.logging.Level {

    private static final String defaultBundle = "sun.util.logging.resources.logging";

    public static final Level OFF = new Level("OFF", Integer.MAX_VALUE, defaultBundle);

    public static final Level ERROR = new Level("ERROR",1000, defaultBundle);

    public static final Level WARNING = new Level("WARNING", 900, defaultBundle);

    public static final Level INFO = new Level("INFO", 800, defaultBundle);

    public static final Level DEBUG = new Level("DEBUG", 500, defaultBundle);

    public static final Level ALL = new Level("ALL", Integer.MIN_VALUE, defaultBundle);

    protected Level(String name, int value, String resourceBundleName) {
        super(name, value, resourceBundleName);
    }
}
