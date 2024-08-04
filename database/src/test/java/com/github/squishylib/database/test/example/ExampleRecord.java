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

package com.github.squishylib.database.test.example;

import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.database.Record;
import com.github.squishylib.database.annotation.Field;
import com.github.squishylib.database.annotation.Primary;
import org.jetbrains.annotations.NotNull;

public class ExampleRecord implements Record<ExampleRecord> {

    public class ObjectTest {
        private final @NotNull String testString = "test";
        private int testInt = 123;
    }

    public static final @NotNull String IDENTIFIER_KEY = "identifier";
    public static final @NotNull String STRING_KEY = "value";
    public static final @NotNull String BOOL_KEY = "bool";
    public static final @NotNull String OBJECT_KEY = "object";

    private final @Field(IDENTIFIER_KEY) @Primary @NotNull String identifier;
    private @Field(STRING_KEY) String string;
    private @Field(BOOL_KEY) boolean bool;
    private @Field(OBJECT_KEY) ObjectTest object;

    public ExampleRecord(@NotNull String identifier) {
        this.identifier = identifier;
        this.string = "The default value.";
        this.bool = true;
        this.object = new ObjectTest();
    }

    public @NotNull String getIdentifier() {
        return this.identifier;
    }

    public @NotNull String getString() {
        return this.string;
    }

    public @NotNull ExampleRecord setString(@NotNull String value) {
        this.string = value;
        return this;
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        ConfigurationSection section = new MemoryConfigurationSection();

        section.set(IDENTIFIER_KEY, this.identifier);
        section.set(ExampleRecord.STRING_KEY, this.string);
        section.set(ExampleRecord.BOOL_KEY, this.bool);
        section.set(ExampleRecord.OBJECT_KEY, this.object);

        return section;
    }

    @Override
    public @NotNull ExampleRecord convert(@NotNull ConfigurationSection section) {
        this.string = section.getString(ExampleRecord.STRING_KEY);
        this.bool = section.getBoolean(ExampleRecord.BOOL_KEY);
        this.object = section.getClass(OBJECT_KEY, ObjectTest.class);
        return this;
    }
}
