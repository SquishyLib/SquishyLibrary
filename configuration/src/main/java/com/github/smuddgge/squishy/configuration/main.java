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

package com.github.smuddgge.squishy.configuration;

import com.github.smuddgge.squishy.configuration.directory.SingleTypeConfigurationDirectory;
import com.github.smuddgge.squishy.configuration.implementation.MemoryConfigurationSection;
import com.github.smuddgge.squishy.configuration.indicator.ConfigurationConvertible;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class main {

    public static class Egg implements ConfigurationConvertible<Egg> {

        private final @NotNull String identifier;

        private boolean hasCracked;

        public Egg(@NotNull String identifier) {
            this.identifier = identifier;
        }

        public void setHasCracked(boolean hasCracked) {
            this.hasCracked = hasCracked;
        }

        @Override
        public @NotNull ConfigurationSection convert() {
            ConfigurationSection section = new MemoryConfigurationSection();
            section.set("has_cracked", hasCracked);
            return section;
        }

        @Override
        public @NotNull Egg convert(@NotNull ConfigurationSection section) {
            this.hasCracked = section.getBoolean("has_cracked");
            return this;
        }
    }

    public static void main(final String[] args) {

        SingleTypeConfigurationDirectory<Egg> directory = new SingleTypeConfigurationDirectory<>(new File("directory"), Egg::new, false);
        directory.load();

        Egg egg = directory.get("first_egg").orElse(null);

        List<Egg> eggs = directory.getAll();

        directory.set("second_egg", new Egg("second_egg"));

        directory.remove("third_egg");

        boolean contains = directory.contains("fourth_egg");
    }
}
