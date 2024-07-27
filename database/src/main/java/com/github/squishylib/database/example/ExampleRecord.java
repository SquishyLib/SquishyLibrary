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

package com.github.squishylib.database.example;

import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.database.Record;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExampleRecord implements Record<ExampleRecord> {

    public static final @NotNull String IDENTIFIER_KEY = "identifier";
    public static final @NotNull String VALUE_KEY = "value";

    private final @NotNull String identifier;
    private String value;

    public ExampleRecord(@NotNull String identifier) {
        this.identifier = identifier;
        this.value = "The default value.";
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.identifier;
    }

    @Override
    public @NotNull List<String> getFieldNames() {
        return List.of(ExampleRecord.VALUE_KEY);
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        ConfigurationSection section = new MemoryConfigurationSection();

        section.set(ExampleRecord.VALUE_KEY, this.value);

        return new MemoryConfigurationSection();
    }

    @Override
    public @NotNull ExampleRecord convert(@NotNull ConfigurationSection section) {

        this.value = section.getString(ExampleRecord.VALUE_KEY);

        return this;
    }
}
