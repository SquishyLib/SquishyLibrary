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
import com.github.squishylib.database.field.ForeignField;
import com.github.squishylib.database.field.PrimaryField;
import com.github.squishylib.database.field.RecordField;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Record<R extends Record<R>> extends ConfigurationConvertible<R> {

    default @NotNull List<RecordField> getFieldList() {
        final List<RecordField> fields = new ArrayList<>();

        for (java.lang.reflect.Field field : getClass().getDeclaredFields()) {

            // Does the field have the field annotation?
            Field annotation = field.getAnnotation(Field.class);
            if (annotation == null) continue;

            fields.add(new RecordField(annotation.value(), FieldType.of(field)));
        }

        return fields;
    }

    default @NotNull List<String> getFieldNameList() {
        final List<String> fieldNames = new ArrayList<>();

        for (java.lang.reflect.Field field : getClass().getDeclaredFields()) {

            Field annotation = field.getAnnotation(Field.class);
            if (annotation == null) continue;

            fieldNames.add(annotation.value());
        }

        return fieldNames;
    }

    default @NotNull Map<RecordField, Object> getFieldMap() {
        final ConfigurationSection section = this.convert();
        final Map<RecordField, Object> fieldMap = new HashMap<>();

        for (RecordField field : this.getFieldList()) {
            final Object value = section.get(field.getName(), null);
            fieldMap.put(field, value);
        }

        return fieldMap;
    }

    default @NotNull List<PrimaryField> getPrimaryFieldList() {
        final List<PrimaryField> primaryFields = new ArrayList<>();

        for (java.lang.reflect.Field field : getClass().getDeclaredFields()) {

            // Does the field have the field annotation?
            Field annotation = field.getAnnotation(Field.class);
            if (annotation == null) continue;

            // Does the field have the primary key annotation?
            Primary primaryAnnotation = field.getAnnotation(Primary.class);
            if (primaryAnnotation == null) continue;

            primaryFields.add(new PrimaryField(annotation.value(), FieldType.of(field)));
        }

        return primaryFields;
    }

    default @NotNull Map<PrimaryField, Object> getPrimaryFieldMap() {
        final ConfigurationSection section = this.convert();
        final Map<PrimaryField, Object> primaryFieldMap = new HashMap<>();

        for (PrimaryField primaryField : this.getPrimaryFieldList()) {
            final Object value = section.get(primaryField.getName(), null);
            primaryFieldMap.put(primaryField, value);
        }

        return primaryFieldMap;
    }

    default @NotNull List<ForeignField> getForeignFieldList() {
        final List<ForeignField> foreignFields = new ArrayList<>();

        for (java.lang.reflect.Field field : getClass().getDeclaredFields()) {

            // Does the field have the field annotation?
            Field annotation = field.getAnnotation(Field.class);
            if (annotation == null) continue;

            // Does the field have the primary key annotation?
            Foreign foreignAnnotation = field.getAnnotation(Foreign.class);
            if (foreignAnnotation == null) continue;

            foreignFields.add(new ForeignField(
                    annotation.value(),
                    FieldType.of(field),
                    foreignAnnotation.tableField(),
                    foreignAnnotation.table()
            ));
        }

        return foreignFields;
    }

    default @NotNull Map<ForeignField, Object> getForeignFieldMap() {
        final ConfigurationSection section = this.convert();
        final Map<ForeignField, Object> foreignFieldMap = new HashMap<>();

        for (ForeignField foreignField : this.getForeignFieldList()) {
            final Object value = section.get(foreignField.getName(), null);
            foreignFieldMap.put(foreignField, value);
        }

        return foreignFieldMap;
    }

    /**
     * Used to convert a result set into this class instance.
     *
     * @param results The results to convert.
     * @return This instance.
     */
    default @NotNull R convert(@NotNull ResultSet results) {
        ConfigurationSection section = new MemoryConfigurationSection();

        for (String fieldName : this.getFieldNameList()) {
            try {
                section.set(fieldName, results.getObject(fieldName));
            } catch (Exception exception) {
                throw new DatabaseException(exception, this, "convert", "Unable to convert " + fieldName + " to an object.");
            }
        }

        return (R) this;
    }
}
