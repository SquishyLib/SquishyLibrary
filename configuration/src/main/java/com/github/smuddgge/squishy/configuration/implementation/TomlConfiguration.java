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

package com.github.smuddgge.squishy.configuration.implementation;

import com.github.smuddgge.squishy.configuration.Configuration;
import com.github.smuddgge.squishy.configuration.ConfigurationException;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.yaml.snakeyaml.DumperOptions.FlowStyle.BLOCK;

public class TomlConfiguration extends MemoryConfigurationSection implements Configuration {

    private final @NotNull File file;
    private @Nullable String resourcePath;

    public TomlConfiguration(@NotNull final File file) {
        this.file = file;
    }

    public TomlConfiguration(@NotNull final File folder, @NotNull final String pathFromFile) {
        this.file = new File(folder.getAbsolutePath() + File.separator + pathFromFile);
    }

    @Override
    public @NotNull String getPath() {
        return this.file.getAbsolutePath();
    }

    @Override
    public @NotNull File getFile() {
        return this.file;
    }

    @Override
    public @NotNull Optional<String> getResourcePath() {
        return Optional.ofNullable(this.resourcePath);
    }

    @Override
    public @NotNull Configuration setResourcePath(@Nullable String path) {
        this.resourcePath = path;
        return this;
    }


    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> load() {

        // Create the completable return value.
        final CompletableFuture<@NotNull Boolean> future = new CompletableFuture<>();

        // Complete the load task asynchronously.
        future.completeAsync(() -> {

            // Are there any missing folders that are needed to create the file?
            if (!this.file.getParentFile().exists()) {

                // Create the missing folders.
                boolean success = this.file.getParentFile().mkdirs();

                // Was it not able to create the parent directories?
                if (!success) {
                    throw new ConfigurationException(this, "load", "Could not create parent directory's.");
                }
            }

            // Has the file not been created yet?
            if (!this.file.exists()) {

                // Attempt to create the file.
                CompletableFuture<Boolean> success = this.createFile();

                try {

                    // Was there a problem while creating the file?
                    if (!success.get()) {
                        throw new ConfigurationException(this, "load", "Could not create the file.");
                    }
                } catch (Exception exception) {
                    throw new ConfigurationException(this, "load", "Could not get the completable future result for creating the file.");
                }
            }

            // Get the file's content.
            try {

                // Replace the data map with the content.
                Toml toml = new Toml().read(this.file);
                this.data.clear();
                this.data.putAll(toml.toMap());
                return true;

            } catch (Exception exception) {
                throw new ConfigurationException(this, "load", "Could not get the content and replace the data map.");
            }
        });

        return future;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> save() {

        final CompletableFuture<@NotNull Boolean> future = new CompletableFuture<>();

        future.completeAsync(() -> {
            try {

                // Write to the file.
                TomlWriter tomlWriter = new TomlWriter();
                tomlWriter.write(this.data, this.file);
                return true;

            } catch (IOException exception) {
                throw new ConfigurationException(this, "save", "Could not write the data into the file.");
            }
        });

        return future;
    }
}
