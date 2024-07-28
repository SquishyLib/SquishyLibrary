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

import com.github.squishylib.common.logger.Logger;
import com.github.squishylib.database.example.ExampleTable;
import com.github.squishylib.database.implementation.SqliteDatabase;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;

public class Tests {

    @Test
    public void test() {
        Database database = new SqliteDatabase(
                new Logger("com.github.squishylib.database", null),
                Duration.ofSeconds(3),
                true,
                Duration.ofMillis(100),
                10,
                new File("src/test/resources/database.sqlite3")
        );
        database.connect();
        database.createTable(new ExampleTable());
        database.disconnect(false);
    }
}
