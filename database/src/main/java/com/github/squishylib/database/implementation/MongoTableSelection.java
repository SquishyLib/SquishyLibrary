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

package com.github.squishylib.database.implementation;

import com.github.squishylib.common.CompletableFuture;
import com.github.squishylib.common.logger.Logger;
import com.github.squishylib.database.*;
import com.github.squishylib.database.Record;
import com.github.squishylib.database.field.RecordField;
import com.github.squishylib.database.field.RecordFieldPool;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MongoTableSelection<R extends Record<R>> implements TableSelection<R, MongoDatabase> {

    private final @NotNull MongoDatabase database;
    private final @NotNull Table<R> table;

    public MongoTableSelection(final @NotNull MongoDatabase database, @NotNull Table<R> table) {
        this.database = database;
        this.table = table;
    }

    @Override
    public @NotNull String getName() {
        return this.table.getName();
    }

    @Override
    public @NotNull MongoDatabase getDatabase() {
        return this.database;
    }

    public @NotNull MongoCollection<Document> getCollection() {
        return this.database.getDatabase().getCollection(this.getName());
    }

    @Override
    public @NotNull TableSelection<R, MongoDatabase> setDatabase(@NotNull MongoDatabase database) {
        return this;
    }

    @Override
    public @NotNull R createEmptyRecord(@NotNull RecordFieldPool pool) {
        return this.table.createEmptyRecord(pool);
    }

    /**
     * This will return the fields defined in your records.
     * This is because a mongo database doesn't define what
     * the fields should be.
     *
     * @return The completed future.
     */
    @Override
    public @NotNull CompletableFuture<@NotNull List<String>> getColumnNames() {
        // Create this requests logger.
        final Logger tempLogger = this.database.getLogger().extend(" &b.getColumnNames &7MongoTableSelection.java:80");
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        future.complete(this.getFieldNameList());
        tempLogger.debug("Column names: " + future.waitAndGet());
        return future;
    }

    /**
     * There is no need for this in mongo.
     * @param field The column info.
     * @return The completed future.
     */
    @Deprecated
    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> addColumn(@NotNull RecordField field) {
        final Logger tempLogger = this.database.getLogger().extend(" &b.addColumn(field) &7MongoTableSelection.java:96");
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        future.complete(true);
        tempLogger.debug("No column was added because this is automatically done in a mongo database.");
        return future;
    }

    @Override
    public @NotNull CompletableFuture<@Nullable R> getFirstRecord(@Nullable Query query) {
        return this.database.addRequest(new Request<>(() -> {

            final Logger tempLogger = this.database.getLogger().extend(" &b.getFirstRecord(query) &7MongoTableSelection.java:105");

            if (query == null) {
                tempLogger.debug("No query, getting first record.");
                Document document = this.getCollection().find().first();
                if (document == null) return null;
                final R record = this.createEmptyRecord(this.getFieldPool(document)).convert(document);
                tempLogger.debug("Found record: &b" + record.convertToMap());
                return record;
            }

            // Create the document filter.
            List<Bson> filter = query.buildMongoFilter();
            tempLogger.debug("Created filter: &b" + filter);

            // Get the document.
            Document document = this.getCollection().find(Filters.and(filter)).first();
            if (document == null) return null;
            R record = this.createEmptyRecord(this.getFieldPool(document)).convert(document);
            tempLogger.debug("Found record: &b" + record.convertToMap());
            return record;
        }));
    }

    @Override
    public @NotNull CompletableFuture<@NotNull List<R>> getRecordList(@Nullable Query query) {
        return this.database.addRequest(new Request<>(() -> {

            final Logger tempLogger = this.database.getLogger().extend(" &b.getRecordList(query) &7MongoTableSelection.java:133");

            if (query == null) {
                tempLogger.debug("No query, getting all records in table: " + this.getName());
                FindIterable<Document> documentList = this.getCollection().find();
                List<R> records = new ArrayList<>();
                for (Document document : documentList) {
                    R record = this.createEmptyRecord(this.getFieldPool(document)).convert(document);
                    records.add(record);
                    tempLogger.debug("Added record: &b" + record.convertToMap());
                }
                return records;
            }

            // Create the document filter.
            List<Bson> filter = query.buildMongoFilter();
            tempLogger.debug("Created filter: &b" + filter);

            // Get and return found documents.
            FindIterable<Document> documentList = this.getCollection().find(Filters.and(filter));
            List<R> records = new ArrayList<>();
            for (Document document : documentList) {
                R record = this.createEmptyRecord(this.getFieldPool(document)).convert(document);
                records.add(record);
                tempLogger.debug("Added record: &b" + record.convertToMap());
            }
            return records;
        }));
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Integer> getAmountOfRecords(@Nullable Query query) {
        return this.database.addRequest(new Request<>(() -> {

            final Logger tempLogger = this.database.getLogger().extend(" &b.getAmountOfRecords(query) &7MongoTableSelection.java:167");

            if (query == null) {
                tempLogger.debug("No query, getting amount of records in table &b" + this.getName());
                int result = Integer.parseInt(String.valueOf(this.getCollection().countDocuments()));
                tempLogger.debug("Amount of records: &b" + result);
                return result;
            }

            List<Bson> filter = query.buildMongoFilter();
            tempLogger.debug("Created filter: &b" + filter);
            int result = Integer.parseInt(String.valueOf(this.getCollection().countDocuments(Filters.and(filter))));
            tempLogger.debug("Amount of records: &b" + result);
            return result;
        }));
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> insertRecord(@NotNull R record) {
        return this.database.addRequest(new Request<>(() -> {

            final Logger tempLogger = this.database.getLogger().extend(" &b.insertRecord(query) &7MongoTableSelection.java:188");

            List<Bson> filter = new Query().match(record.getFieldPool().onlyPrimaryKeys()).buildMongoFilter();
            tempLogger.debug("Removing record &b" + record.convertToMap() + ": &7with filter &b" + filter);
            DeleteResult result = this.getCollection().deleteMany(Filters.and(filter));
            tempLogger.debug("Amount of records deleted: &b" + result.getDeletedCount());

            this.getCollection().insertOne(record.convertToDocument());
            tempLogger.debug("Inserted updated record: " + record.convertToMap());
            return true;

        }));
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> removeAllRecords(@NotNull Query query) {
        return this.database.addRequest(new Request<>(() -> {
            final Logger tempLogger = this.database.getLogger().extend(" &b.removeAllRecords(query) &7MongoTableSelection.java:203");
            List<Bson> filter = query.buildMongoFilter();
            tempLogger.debug("Created filter: &b" + filter);

            DeleteResult result = this.getCollection().deleteMany(Filters.and(filter));
            tempLogger.debug("Amount deleted: &b" + result.getDeletedCount());
            return result.wasAcknowledged();
        }));
    }
}
