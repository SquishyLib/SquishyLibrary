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

package com.github.squishylib.configuration;

import com.github.squishylib.configuration.implementation.TomlConfiguration;
import com.github.squishylib.configuration.implementation.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Optional;

public enum ConfigurationFactory {
    YAML(List.of("yaml", "yml")) {
        @Override
        public @NotNull Configuration create(@NotNull File file) {
            return new YamlConfiguration(file);
        }

        @Override
        public @NotNull Configuration create(@NotNull File folder, @NotNull String pathFromFolder) {
            return new YamlConfiguration(folder, pathFromFolder);
        }
    },
    TOML(List.of("toml")) {
        @Override
        public @NotNull Configuration create(@NotNull File file) {
            return new TomlConfiguration(file);
        }

        @Override
        public @NotNull Configuration create(@NotNull File folder, @NotNull String pathFromFolder) {
            return new TomlConfiguration(folder, pathFromFolder);
        }
    };

    private final @NotNull List<String> extensions;

    ConfigurationFactory(final @NotNull List<String> extensions) {
        this.extensions = extensions;
    }

    public @NotNull List<String> getExtensions() {
        return this.extensions;
    }

    public abstract @NotNull Configuration create(@NotNull File file);

    public abstract @NotNull Configuration create(@NotNull File folder, @NotNull String pathFromFolder);

    public static @NotNull Optional<Configuration> createConfiguration(@NotNull String extension, @NotNull File file) {
        for (ConfigurationFactory factory : ConfigurationFactory.values()) {
            if (factory.extensions.contains(extension)) return Optional.of(factory.create(file));
        }
        return Optional.empty();
    }

    public static @NotNull Optional<Configuration> createConfiguration(@NotNull String extension, @NotNull File folder, @NotNull String pathFromFolder) {
        for (ConfigurationFactory factory : ConfigurationFactory.values()) {
            if (factory.extensions.contains(extension)) return Optional.of(factory.create(folder, pathFromFolder));
        }
        return Optional.empty();
    }

    public static @NotNull Optional<Configuration> createConfiguration(@NotNull File file) {
        return ConfigurationFactory.createConfiguration(file.getName().split("\\.")[file.getName().split("\\.").length - 1], file);
    }
}
