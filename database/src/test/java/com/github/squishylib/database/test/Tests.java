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

package com.github.squishylib.database.test;

import com.github.squishylib.database.DatabaseBuilder;
import com.github.squishylib.database.test.tester.DatabaseTester;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class Tests {

    @Test
    public void testSqlite() throws InterruptedException {
        DatabaseTester tester = new DatabaseTester(new DatabaseBuilder()
                .setReconnectCooldown(Duration.ofMillis(1000))
                .setWillReconnect(true)
                .setTimeBetweenRequests(Duration.ofMillis(500))
                .setMaxRequestsPending(20)
                .setSqliteEnabled(true)
                .setSqlitePath("src/test/resources/database.sqlite3")
                .setDebugMode(true)
        );
        tester.testAll();
    }
}
