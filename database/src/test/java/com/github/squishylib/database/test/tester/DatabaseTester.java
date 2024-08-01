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

package com.github.squishylib.database.test.tester;

import com.github.squishylib.common.logger.Logger;
import com.github.squishylib.common.testing.ResultChecker;
import com.github.squishylib.common.testing.Testing;
import com.github.squishylib.database.Database;
import com.github.squishylib.database.DatabaseBuilder;
import com.github.squishylib.database.test.example.ExampleRecord;
import com.github.squishylib.database.test.example.ExampleTable;
import org.jetbrains.annotations.NotNull;

public class DatabaseTester {

    private final @NotNull DatabaseBuilder builder;
    private final @NotNull Logger logger;

    public DatabaseTester(@NotNull DatabaseBuilder builder) {
        this.builder = builder;
        this.logger = builder.getLogger();

        // Set up console logger.
        Testing.setupConsoleLogger();
    }

    public void testAll() {
        this.testConnection();
        this.testReconnection();
        this.testCreateTable();
        this.testInsertAndGetFirst();
    }

    public void testConnection() {
        this.logger.info("&aRunning test: &ftestConnection");
        final Database database = this.builder.create().connect();
        new ResultChecker("testConnection")
                .expect(database.isConnected(), "database.isConnected()");
    }

    public void testReconnection() {
        this.logger.info("&aRunning test: &ftestReconnection");
        final Database database = this.builder.create().connect();
        database.disconnect(true);
        new ResultChecker("testReconnection")
                .expect(database.isConnected(), "database.isConnected()");
    }

    public void testCreateTable() {
        this.logger.info("&aRunning test: &ftestCreateTable");
        final Database database = this.builder.create().connect();
        database.createTable(new ExampleTable());
        new ResultChecker("testCreateTable")
                .expect(database.isConnected(), "database.isConnected()")
                .expect(database.getAmountOfTables() == 1, "database.getTable(ExampleTable.class) != null")
                .expect(database.getTable(ExampleTable.class).getColumnNames().waitForComplete().size() == 2, "Are there the correct number of columns?");
    }

    public void testInsertAndGetFirst() {
        this.logger.info("&aRunning test: &ftestInsertAndGetFirst");
        final Database database = this.builder.create().connect().createTable(new ExampleTable());
        database.getTable(ExampleTable.class).insertRecord(
                new ExampleRecord("testInsertAndGetFirst").setString("testInsertAndGetFirst")
        );
        ExampleRecord record = database.getTable(ExampleTable.class).getFirstRecord().waitForComplete();
        new ResultChecker("testInsertAndGetFirst")
                .expect(record != null, "record != null")
                .expect(record.getIdentifier().equals("testInsertAndGetFirst"), "record.getIdentifier().equals(\"testInsertAndGetFirst\")")
                .expect(record.getString().equals("testInsertAndGetFirst"), "record.getString().equals(\"testInsertAndGetFirst\")");
    }
}
