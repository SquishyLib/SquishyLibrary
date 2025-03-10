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

import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.database.Record;
import com.github.squishylib.database.annotation.Field;
import com.github.squishylib.database.annotation.Primary;
import com.github.squishylib.database.annotation.Size;
import org.jetbrains.annotations.NotNull;

public class TestRecord implements Record<TestRecord> {

    public static class ObjectTest {
        public final @NotNull String testString = "test";
        public int testInt = 123;
    }

    public static final @NotNull String IDENTIFIER_KEY = "identifier";
    public static final @NotNull String STRING_KEY = "value";
    public static final @NotNull String BOOL_KEY = "bool";
    public static final @NotNull String OBJECT_KEY = "object";
    public static final @NotNull String INTEGER_KEY = "i";
    public static final @NotNull String LONG_KEY = "l";
    public static final @NotNull String FLOAT_KEY = "f";
    public static final @NotNull String DOUBLE_KEY = "d";

    private final @Field(IDENTIFIER_KEY) @Primary @Size(255) @NotNull String identifier;
    private @Field(STRING_KEY) String string;
    private @Field(BOOL_KEY) boolean bool;
    private @Field(OBJECT_KEY) ObjectTest object;
    private @Field(INTEGER_KEY) int i;
    private @Field(LONG_KEY) long l;
    private @Field(FLOAT_KEY) float f;
    private @Field(DOUBLE_KEY) double d;


    public TestRecord(@NotNull String identifier) {
        this.identifier = identifier;
        this.string = "The default value.";
        this.bool = true;
        this.object = new ObjectTest();
        this.i = 1;
        this.l = 2L;
        this.f = 3F;
        this.d = 4D;
    }

    public @NotNull String getIdentifier() {
        return this.identifier;
    }

    public @NotNull String getString() {
        return this.string;
    }

    public boolean getBool() {
        return this.bool;
    }

    public @NotNull ObjectTest getObject() {
        return this.object;
    }

    public int getI() {
        return this.i;
    }

    public long getL() {
        return this.l;
    }

    public float getF() {
        return this.f;
    }

    public double getD() {
        return this.d;
    }

    public @NotNull TestRecord setString(@NotNull String value) {
        this.string = value;
        return this;
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        ConfigurationSection section = new MemoryConfigurationSection();

        section.set(IDENTIFIER_KEY, this.identifier);
        section.set(TestRecord.STRING_KEY, this.string);
        section.set(TestRecord.BOOL_KEY, this.bool);
        section.set(TestRecord.OBJECT_KEY, this.object);
        section.set(TestRecord.INTEGER_KEY, this.i);
        section.set(TestRecord.LONG_KEY, this.l);
        section.set(TestRecord.FLOAT_KEY, this.f);
        section.set(TestRecord.DOUBLE_KEY, this.d);

        return section;
    }

    @Override
    public @NotNull TestRecord convert(@NotNull ConfigurationSection section) {
        this.string = section.getString(TestRecord.STRING_KEY);
        this.bool = section.getBoolean(TestRecord.BOOL_KEY);
        this.object = section.getClass(OBJECT_KEY, ObjectTest.class);

        this.i = section.getInteger(TestRecord.INTEGER_KEY);
        this.l = section.getLong(TestRecord.LONG_KEY);
        this.f = section.getFloat(TestRecord.FLOAT_KEY);
        this.d = section.getDouble(TestRecord.DOUBLE_KEY);
        return this;
    }
}
