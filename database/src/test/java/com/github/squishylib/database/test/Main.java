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
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

public class Main {

    @Test
    public void testSqlite() {
        UUID uuid = UUID.randomUUID();

        DatabaseTester tester = new DatabaseTester(new DatabaseBuilder()
                .setReconnectCooldown(Duration.ofMillis(100))
                .setShouldReconnectEveryCycle(true)
                .setWillReconnect(true)
                .setTimeBetweenRequests(Duration.ofMillis(1))
                .setMaxRequestsPending(20)

                .setSqliteEnabled(true)
                .setSqlitePath("src/test/resources/{uuid}.sqlite3".replace("{uuid}", uuid.toString()))

                .setDebugMode(true)
        );
        tester.testAll();
        tester.drop();
    }

    @Test
    public void testMySql() {
        DatabaseTester tester = new DatabaseTester(new DatabaseBuilder()
                .setReconnectCooldown(Duration.ofMillis(100))
                .setShouldReconnectEveryCycle(true)
                .setWillReconnect(true)
                .setTimeBetweenRequests(Duration.ofMillis(1))
                .setMaxRequestsPending(20)

                .setMySqlEnabled(true)
                .setMySqlConnectionString("localhost:3306")
                .setMySqlDatabaseName("database" + UUID.randomUUID().toString().substring(0, 5))
                .setMySqlUsername("admin")
                .setMySqlPassword("123")

                .setDebugMode(true)
        );
        tester.testAll();
        tester.drop();
    }

    @Test
    public void testMongo() {
        DatabaseTester tester = new DatabaseTester(new DatabaseBuilder()
                .setReconnectCooldown(Duration.ofMillis(100))
                .setShouldReconnectEveryCycle(true)
                .setWillReconnect(true)
                .setTimeBetweenRequests(Duration.ofMillis(1))
                .setMaxRequestsPending(20)

                .setMySqlEnabled(true)
                .setMySqlConnectionString("localhost:3306")
                .setMySqlDatabaseName("database" + UUID.randomUUID().toString().substring(0, 5))
                .setMySqlUsername("admin")
                .setMySqlPassword("123")

                .setDebugMode(true)
        );
        tester.testAll();
        tester.drop();
    }
}
