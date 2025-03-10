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

package com.github.squishylib.configuration.implementation;

import com.github.squishylib.common.CompletableFuture;
import com.github.squishylib.configuration.Configuration;
import com.github.squishylib.configuration.ConfigurationException;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TomlConfiguration extends MemoryConfigurationSection implements Configuration {

    private final @NotNull File file;
    private final @NotNull Class<?> clazz;
    private @Nullable String resourcePath;
    private final @NotNull List<Listener> listenerList;

    public TomlConfiguration(@NotNull final File file, @NotNull Class<?> clazz) {
        this.file = file;
        this.listenerList = new ArrayList<>();
        this.clazz = clazz;
    }

    public TomlConfiguration(@NotNull final File folder, @NotNull final String pathFromFile, @NotNull Class<?> clazz) {
        this.file = new File(folder.getAbsolutePath() + File.separator + pathFromFile);
        this.listenerList = new ArrayList<>();
        this.clazz = clazz;
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
    public @NotNull Class<?> getProjectClass() {
        return this.clazz;
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
    public @NotNull Configuration addListener(@NotNull Listener listener) {
        this.listenerList.add(listener);
        return this;
    }

    @Override
    public @NotNull Configuration load() {
        this.loadAsync().waitAndGet();
        return this;
    }

    @Override
    public boolean loadWithResult() {
        return this.loadAsync().waitAndGet();
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> loadAsync() {

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
                    throw new ConfigurationException(
                        new RuntimeException(),
                        "TomlConfiguration.loadAsync()",
                        "Could not create parent directory's."
                    );
                }
            }

            // Has the file not been created yet?
            if (!this.file.exists()) {

                // Attempt to create the file.
                CompletableFuture<Boolean> success = this.createFile();

                try {

                    // Was there a problem while creating the file?
                    if (!success.get()) {
                        throw new ConfigurationException(
                            new RuntimeException(),
                            "TomlConfiguration.loadAsync()",
                            "Could not create the file."
                        );
                    }
                } catch (Exception exception) {
                    throw new ConfigurationException(
                        exception,
                        "TomlConfiguration.loadAsync()",
                        "Could not get the completable future result for creating the file."
                    );
                }
            }

            // Get the file's content.
            try {

                // Replace the data map with the content.
                Toml toml = new Toml().read(this.file);
                this.data.clear();
                this.data.putAll(toml.toMap());
                this.listenerList.forEach(listener -> listener.onLoadFinished(this));
                return true;

            } catch (Exception exception) {
                throw new ConfigurationException(exception,
                    "TomlConfiguration.loadAsync()",
                    "Could not get the content and replace the data map."
                );
            }
        });

        return future;
    }

    @Override
    public @NotNull Configuration save() {
        this.saveAsync().waitAndGet();
        return this;
    }

    @Override
    public boolean saveWithResult() {
        return this.saveAsync().waitAndGet();
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> saveAsync() {
        final CompletableFuture<@NotNull Boolean> future = new CompletableFuture<>();

        future.completeAsync(() -> {
            try {

                // Write to the file.
                TomlWriter tomlWriter = new TomlWriter();
                tomlWriter.write(this.data, this.file);
                return true;

            } catch (IOException exception) {
                throw new ConfigurationException(
                    exception,
                    "TomlConfiguration.loadAsync()",
                    "Could not write the data into the file."
                );
            }
        });

        return future;
    }
}
