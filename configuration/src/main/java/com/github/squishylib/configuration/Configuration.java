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

import com.github.squishylib.common.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;
import java.util.logging.Logger;

public interface Configuration extends ConfigurationSection {

    interface Listener {
        void onLoadFinished(@NotNull ConfigurationSection section);
    }

    /**
     * Used to get the absolute file path.
     *
     * @return The absolute path.
     */
    @NotNull
    String getPath();

    /**
     * Used to get the instance of the file.
     *
     * @return The instance of the file.
     */
    @NotNull
    File getFile();

    /**
     * Used to get the file's full name with extensions.
     *
     * @return The file's name full name.
     */
    default @NotNull String getFileName() {
        return this.getFile().getName();
    }

    /**
     * Used to get the file's name without extensions.
     *
     * @return The file's name full name.
     */
    default @NotNull String getFileNameWithoutExtensions() {
        return this.getFile().getName().split("\\.")[0];
    }

    /**
     * Used to get one of the file's extensions.
     * <p>
     * To get the first extension you can use getFileExtension(0).
     *
     * @param index Starting from 0, the extension to get.
     * @return The optional file extension.
     */
    default @NotNull Optional<String> getFileExtension(int index) {
        String[] extensions = this.getFile().getName().split("\\.");
        if (index > extensions.length) return Optional.empty();
        return Optional.of(extensions[index + 1]);
    }

    /**
     * A class within the module where the
     * resource file exists.
     *
     * @return The project class.
     */
    @NotNull Class<?> getProjectClass();

    /**
     * Used to get the default resource file's path
     * from the resource folder.
     * <li>Example: test.yml</li>
     *
     * @return The default resource file's path.
     */
    @NotNull
    Optional<String> getResourcePath();

    /**
     * Used to set where the default resource file is located.
     * This file will then be loaded if the file doesn't already exist.
     * <li>Example: test.yml</li>
     *
     * @param path The path from the resource folder.
     * @return This instance.
     */
    @NotNull
    Configuration setResourcePath(@Nullable String path);

    /**
     * Adds a listener to the configuration instance.
     * When the configuration instance is next loaded it
     * will also run all the listeners.
     *
     * @param listener The instance of a listener.
     * @return Tis instance.
     */
    @NotNull
    Configuration addListener(@NotNull Listener listener);

    /**
     * Used to copy the configuration file into this class instance,
     * clearing and then adding the keys and values.
     *
     * @return This instance.
     */
    @NotNull
    Configuration load();

    /**
     * Used to copy the configuration file into this class instance,
     * clearing and then adding the keys and values.
     *
     * @return True if successful.
     */
    boolean loadWithResult();

    /**
     * Used to copy the configuration file into this class instance,
     * clearing and then adding the keys and values.
     * <p>
     * You can use the {@link CompletableFuture#get()} to wait for the result.
     *
     * @return True if successful.
     */
    @NotNull
    CompletableFuture<@NotNull Boolean> loadAsync();

    /**
     * Used to update the configuration file with this class
     * instance's values.
     *
     * @return This instance.
     */
    @NotNull
    Configuration save();

    /**
     * Used to update the configuration file with this class
     * instance's values.
     *
     * @return True if successful.
     */
    boolean saveWithResult();

    /**
     * Used to update the configuration file with this class
     * instance's values.
     * <p>
     * You can use the {@link CompletableFuture#get()} to wait for the result.
     *
     * @return True if successful.
     */
    @NotNull
    CompletableFuture<@NotNull Boolean> saveAsync();

    /**
     * Attempts to create the file.
     * It will use the default resource file if provided.
     * <p>
     * You can use the {@link CompletableFuture#get()} to wait for the result.
     *
     * @return True if a file was created.
     */
    @SuppressWarnings("all")
    default @NotNull CompletableFuture<@NotNull Boolean> createFile() {
        try {
            // Set up the future value provider.
            CompletableFuture<Boolean> future = new CompletableFuture<>();

            // Get the resource file path.
            String resourcePath = this.getResourcePath().orElse(null);

            // Check if the path doesnt exist.
            if (resourcePath == null) {
                future.completeAsync(() -> {
                    try {
                        boolean success = this.getFile().createNewFile();
                        if (!success) {
                            throw new ConfigurationException(
                                new RuntimeException(),
                                "Configuration.createFile()",
                                "Failed to create the file.",
                                "Unable to create a empty file with path " + this.getPath()
                            );
                        }
                        return success;
                    } catch (IOException exception) {
                        throw new ConfigurationException(
                            exception,
                            "Configuration.createFile() create empty file",
                            "Unable to create a empty file with path " + this.getPath()
                        );
                    }
                });
                return future;
            }

            future.completeAsync(() -> {

                // Attempt to copy the default resource file to the configuration location.
                try (InputStream input = this.getProjectClass().getResourceAsStream("/" + resourcePath)) {

                    if (input == null) {
                        Logger logger = ConfigurationException.getLogger();
                        logger.info("[Configuration] Could not load resource " + resourcePath);
                        return false;
                    }

                    Files.copy(input, this.getFile().toPath());
                    return true;

                } catch (IOException exception) {
                    throw new ConfigurationException(
                        exception,
                        "Configuration.createFile() create resource",
                        "Unable to copy the resource file with path " + this.getResourcePath()
                    );
                }
            });
            return future;
        } catch (Exception exception) {
            throw new ConfigurationException(
                exception,
                "Configuration.createFile()",
                "Somthing occured while creating the config file. "
            );
        }
    }
}
