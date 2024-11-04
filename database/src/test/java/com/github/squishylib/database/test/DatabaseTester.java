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

import com.github.squishylib.common.logger.Logger;
import com.github.squishylib.common.testing.ResultChecker;
import com.github.squishylib.common.testing.Testing;
import com.github.squishylib.database.Database;
import com.github.squishylib.database.DatabaseBuilder;
import com.github.squishylib.database.Query;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        this.testInsertAndGetFirstQuery();
        this.testGetRecordList();
        this.testGetRecordListQuery();
        this.testGetAmountOfRecords();
        this.testGetAmountOfRecordsQuery();
        this.testRemoveRecord();
        this.testUpdateRecord();
    }

    public void drop() {
        final Database database = this.builder.create().connect();
        database.drop().waitAndGet();
    }

    public void testConnection() {
        this.logger.info("&aRunning test: &ftestConnection");
        final Database database = this.builder.create().connect();

        // Check if the database is connected.
        new ResultChecker("testConnection")
                .expect(database.isConnected(), "database.isConnected()");
    }

    public void testReconnection() {
        this.logger.info("&aRunning test: &ftestReconnection");
        final Database database = this.builder.create().connect();
        database.disconnect(true);

        // Check if the database has reconnected.
        new ResultChecker("testReconnection")
                .expect(database.isConnected(), "database.isConnected()");
    }

    public void testCreateTable() {
        this.logger.info("&aRunning test: &ftestCreateTable");
        final Database database = this.builder.create().connect();

        // Create database table.
        database.createTable(new TestTable());

        // Check if the database table was created.
        new ResultChecker("testCreateTable")
                .expect(database.isConnected(), "database.isConnected()")
                .expect(database.getAmountOfTables() == 1, "database.getTable(ExampleTable.class) != null")
                .expect(database.getTable(TestTable.class).getColumnNames().waitAndGet().size() == 8, "Are there the correct number of columns?");
    }

    public void testInsertAndGetFirst() {
        this.logger.info("&aRunning test: &ftestInsertAndGetFirst");
        final Database database = this.builder.create().connect().createTable(new TestTable());

        // Insert record.
        database.getTable(TestTable.class).insertRecord(
                new TestRecord("testInsertAndGetFirst").setString("testInsertAndGetFirst")
        );

        // Get the first record in the database.
        TestRecord record = database.getTable(TestTable.class).getFirstRecord().waitAndGet();

        // Check if the record is the same.
        new ResultChecker("testInsertAndGetFirst")
                .expect(record != null, "record != null")
                .expect(record.getIdentifier().equals("testInsertAndGetFirst"), "record.getIdentifier().equals(\"testInsertAndGetFirst\")")
                .expect(record.getString().equals("testInsertAndGetFirst"), "record.getString().equals(\"testInsertAndGetFirst\")")
                .expect(record.getI(), 1, "record.i == 1")
                .expect(record.getL(), 2L, "record.i == 2")
                .expect(record.getF(), 3F, "record.i == 3")
                .expect(record.getD(), 4D, "record.i == 4");
    }

    public void testInsertAndGetFirstQuery() {
        this.logger.info("&aRunning test: &ftestInsertAndGetFirstQuery");
        final Database database = this.builder.create().connect().createTable(new TestTable());

        // Insert another record into the database.
        database.getTable(TestTable.class).insertRecord(
                new TestRecord("testInsertAndGetFirstQuery").setString("testInsertAndGetFirstQuery")
        );

        // Get the recently inserted record.
        TestRecord record = database.getTable(TestTable.class).getFirstRecord(new Query()
                .match(TestRecord.IDENTIFIER_KEY, "testInsertAndGetFirstQuery")
        ).waitAndGet();

        // Check if the records are the same.
        new ResultChecker("testInsertAndGetFirstQuery")
                .expect(record != null, "record != null")
                .expect(record.getIdentifier().equals("testInsertAndGetFirstQuery"), "record.getIdentifier().equals(\"testInsertAndGetFirstQuery\")")
                .expect(record.getString().equals("testInsertAndGetFirstQuery"), "record.getString().equals(\"testInsertAndGetFirstQuery\")")
                .expect(record.getBool(), true, "record.getBool() == true")
                .expect(record.getObject().testInt, 123, "record.getObject().testInt == 123");
    }

    public void testGetRecordList() {
        this.logger.info("&aRunning test: &ftestGetRecordList");
        final Database database = this.builder.create().connect().createTable(new TestTable());

        // Get the list of records.
        List<TestRecord> list = database.getTable(TestTable.class).getRecordList().waitAndGet();

        // Check if the records are the same.
        new ResultChecker("testGetRecordList")
                .expect(list != null, "list != null")
                .expect(list.stream().map(TestRecord::getIdentifier).toList().contains("testInsertAndGetFirst"), "contains identifier testInsertAndGetFirst")
                .expect(list.stream().map(TestRecord::getIdentifier).toList().contains("testInsertAndGetFirstQuery"), "contains identifier testInsertAndGetFirstQuery")
                .expect(list.stream().map(TestRecord::getString).toList().contains("testInsertAndGetFirst"), "contains value testInsertAndGetFirst")
                .expect(list.stream().map(TestRecord::getString).toList().contains("testInsertAndGetFirstQuery"), "contains value testInsertAndGetFirstQuery");

    }

    public void testGetRecordListQuery() {
        this.logger.info("&aRunning test: &ftestGetRecordList");
        final Database database = this.builder.create().connect().createTable(new TestTable());

        // Get the list of records.
        List<TestRecord> list = database.getTable(TestTable.class).getRecordList(new Query().match(TestRecord.IDENTIFIER_KEY, "testInsertAndGetFirst")).waitAndGet();

        // Check if the records are the same.
        new ResultChecker("testGetRecordList")
                .expect(list != null, "list != null")
                .expect(list.size(), 1, "list.size() == 1")
                .expect(list.get(0).getIdentifier().equals("testInsertAndGetFirst"), "get0 identifier testInsertAndGetFirst")
                .expect(list.get(0).getString().equals("testInsertAndGetFirst"), "get0 value testInsertAndGetFirst");
    }

    public void testGetAmountOfRecords() {
        this.logger.info("&aRunning test: &ftestGetAmountOfRecords");
        final Database database = this.builder.create().connect().createTable(new TestTable());

        // Get the list of records.
        int amount = database.getTable(TestTable.class).getAmountOfRecords().waitAndGetNotNull();

        // Check if the records are the same.
        new ResultChecker("testGetAmountOfRecords")
                .expect(amount, 2, "amount of records == 2");
    }

    public void testGetAmountOfRecordsQuery() {
        this.logger.info("&aRunning test: &ftestGetAmountOfRecordsQuery");
        final Database database = this.builder.create().connect().createTable(new TestTable());

        // Get the list of records.
        int amount = database.getTable(TestTable.class).getAmountOfRecords(new Query()
                .match(TestRecord.IDENTIFIER_KEY, "testInsertAndGetFirst")
        ).waitAndGetNotNull();

        // Check if the records are the same.
        new ResultChecker("testGetAmountOfRecordsQuery")
                .expect(amount, 1, "amount of records == 1");
    }

    public void testRemoveRecord() {
        this.logger.info("&aRunning test: &ftestRemoveRecord");
        final Database database = this.builder.create().connect().createTable(new TestTable());

        // Get the list of records.
        TestRecord record = database.getTable(TestTable.class).getFirstRecord().waitAndGet();
        assert record != null;
        database.getTable(TestTable.class).removeRecord(record);

        TestRecord recordRemoved = database.getTable(TestTable.class).getFirstRecord(new Query().match(record)).waitAndGet();

        // Check if the records are the same.
        new ResultChecker("testRemoveRecord")
                .expect(recordRemoved == null, "recordRemoved == null");
    }

    public void testUpdateRecord() {
        this.logger.info("&aRunning test: &ftestUpdateRecord");
        final Database database = this.builder.create().connect().createTable(new TestTable());

        // Insert record.
        database.getTable(TestTable.class).insertRecord(new TestRecord("testUpdateRecord").setString("value1"));

        // Update record.
        database.getTable(TestTable.class).insertRecord(new TestRecord("testUpdateRecord").setString("value2"));

        // Get record.
        TestRecord record = database.getTable(TestTable.class)
                .getFirstRecord(new Query().match(TestRecord.IDENTIFIER_KEY, "testUpdateRecord"))
                .waitAndGet();

        // Check if the records are the same.
        new ResultChecker("testRemoveRecord")
                .expect(record.getString().equals("value2"), "record.getString().equals(\"value2\")");
    }
}
