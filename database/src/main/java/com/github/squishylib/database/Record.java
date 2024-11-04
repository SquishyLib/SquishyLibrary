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

import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.configuration.indicator.ConfigurationConvertible;
import com.github.squishylib.database.annotation.Field;
import com.github.squishylib.database.annotation.Foreign;
import com.github.squishylib.database.annotation.Primary;
import com.github.squishylib.database.annotation.Size;
import com.github.squishylib.database.datatype.DataType;
import com.github.squishylib.database.field.ForeignField;
import com.github.squishylib.database.field.PrimaryField;
import com.github.squishylib.database.field.PrimaryFieldMap;
import com.github.squishylib.database.field.RecordField;
import com.google.gson.Gson;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A database record.
 *
 * @param <R> The record that inherits this class.
 */
public interface Record<R extends Record<R>> extends ConfigurationConvertible<R> {

    default @NotNull List<RecordField> getFieldList() {
        final List<RecordField> fields = new ArrayList<>();

        // Loop though java fields.
        for (java.lang.reflect.Field field : getClass().getDeclaredFields()) {

            // Does the field have the field annotation?
            Field annotation = field.getAnnotation(Field.class);
            if (annotation == null) continue;

            // Does the field have a size annotation?
            long maxSize = Long.MAX_VALUE;
            Size size = field.getAnnotation(Size.class);
            if (size != null) maxSize = size.value();

            // Add the field to the list.
            fields.add(new RecordField(
                    annotation.value(),
                    DataType.of(field.getType()),
                    maxSize
            ));
        }

        return fields;
    }

    default @NotNull List<String> getFieldNameList() {
        final List<String> fieldNames = new ArrayList<>();

        // Loop though java fields.
        for (java.lang.reflect.Field field : getClass().getDeclaredFields()) {

            // Does the field have the field annotation?
            Field annotation = field.getAnnotation(Field.class);
            if (annotation == null) continue;

            // Add the field name.
            fieldNames.add(annotation.value());
        }

        return fieldNames;
    }

    default @NotNull Map<RecordField, Object> getFieldValues() {
        final ConfigurationSection section = this.convert();
        final Map<RecordField, Object> fieldMap = new HashMap<>();

        // Loop though the field list.
        for (RecordField field : this.getFieldList()) {

            // Get the value from this class.
            final Object value = section.get(field.getName(), null);
            fieldMap.put(field, value);
        }

        return fieldMap;
    }

    default @NotNull List<PrimaryField> getPrimaryFieldList() {
        final List<PrimaryField> primaryFields = new ArrayList<>();

        // Loop though java fields.
        for (java.lang.reflect.Field field : getClass().getDeclaredFields()) {

            // Does the field have the field annotation?
            Field annotation = field.getAnnotation(Field.class);
            if (annotation == null) continue;

            // Does the field have the primary key annotation?
            Primary primaryAnnotation = field.getAnnotation(Primary.class);
            if (primaryAnnotation == null) continue;

            // Does the field have a size annotation?
            long maxSize = Long.MAX_VALUE;
            Size size = field.getAnnotation(Size.class);
            if (size != null) maxSize = size.value();

            // Add the primary field.
            primaryFields.add(new PrimaryField(
                    annotation.value(),
                    DataType.of(field.getType()),
                    maxSize
            ));
        }

        return primaryFields;
    }

    default @NotNull PrimaryFieldMap getPrimaryFieldMap() {
        final ConfigurationSection section = this.convert();
        final PrimaryFieldMap map = new PrimaryFieldMap(null);

        for (final PrimaryField field : this.getPrimaryFieldList()) {
            map.set(field, section.get(field.getName(), null));
        }

        return map;
    }

    default @NotNull List<ForeignField> getForeignFieldList() {
        final List<ForeignField> foreignFields = new ArrayList<>();

        // Loop though java fields.
        for (java.lang.reflect.Field field : getClass().getDeclaredFields()) {

            // Does the field have the field annotation?
            Field annotation = field.getAnnotation(Field.class);
            if (annotation == null) continue;

            // Does the field have the primary key annotation?
            Foreign foreignAnnotation = field.getAnnotation(Foreign.class);
            if (foreignAnnotation == null) continue;

            // Does the field have a size annotation?
            long maxSize = Long.MAX_VALUE;
            Size size = field.getAnnotation(Size.class);
            if (size != null) maxSize = size.value();

            // Add the foreign field.
            foreignFields.add(new ForeignField(
                    annotation.value(),
                    DataType.of(field.getType()),
                    maxSize,
                    foreignAnnotation.tableField(),
                    foreignAnnotation.table()
            ));
        }

        return foreignFields;
    }

    interface ResultSetDataTypeConverter {
        @Nullable Object convert(
                @NotNull ResultSet results,
                @NotNull String fieldName,
                @NotNull DataType<?> dataType
        );
    }

    /**
     * Used to convert a result set into this class instance.
     *
     * @param results The results to convert.
     * @return This instance.
     */
    default @NotNull R convert(@NotNull ResultSet results, @NotNull ResultSetDataTypeConverter converter) {
        ConfigurationSection section = new MemoryConfigurationSection();

        for (RecordField field : this.getFieldList()) {
            try {

                section.set(
                        field.getName(),
                        converter.convert(results, field.getName(), field.getType())
                );

            } catch (Exception exception) {
                throw new DatabaseException(exception, this, "convert", "Unable to convert " + field.getName() + " to an object.");
            }
        }

        // Convert this object using the configuration section.
        this.convert(section);
        return (R) this;
    }

    default @NotNull R convert(@NotNull Document document) {
        ConfigurationSection section = new MemoryConfigurationSection();

        for (RecordField field : this.getFieldList()) {
            try {
                section.set(field.getName(), document.get(field.getName()));
            } catch (Exception exception) {
                throw new DatabaseException(exception, this, "convert", "Unable to convert " + field.getName() + " to an object.");
            }
        }

        // Convert this object using the configuration section.
        this.convert(section);
        return (R) this;
    }

    default @NotNull Document convertToDocument() {
        Gson gson = new Gson();
        String json = gson.toJson(this.convert().getMap());
        return Document.parse(json);
    }
}
