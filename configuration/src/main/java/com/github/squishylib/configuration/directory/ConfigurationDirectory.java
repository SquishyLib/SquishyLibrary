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

package com.github.squishylib.configuration.directory;

import com.github.squishylib.configuration.Configuration;
import com.github.squishylib.configuration.ConfigurationException;
import com.github.squishylib.configuration.ConfigurationFactory;
import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.configuration.implementation.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A directory that contains configuration files viewed as one
 * big configuration section.
 * <p>
 * The configuration files can be different types.
 */
public class ConfigurationDirectory extends MemoryConfigurationSection {

    /**
     * The file extension used to store data about the
     * configuration files in the configuration directory.
     * <p>
     * Files with this extension will not be parsed when the
     * {@link ConfigurationDirectory#load(boolean)} method is called.
     * <pre>{@code
     * DATA_FILE_EXTENSION = ".squishystore.yml"
     * }</pre>
     */
    public static final @NotNull String DATA_FILE_EXTENSION = ".squishystore.yml";

    private final @NotNull File directory;
    private final @NotNull Class<?> clazz;
    private final @NotNull List<String> resourcePathList;

    public ConfigurationDirectory(@NotNull final File directory, @NotNull final Class<?> clazz) {
        this.directory = directory;
        this.resourcePathList = new ArrayList<>();
        this.clazz = clazz;
    }

    public @NotNull File getDirectory() {
        return this.directory;
    }

    /**
     * Used to get a configuration directory above this directory.
     *
     * @param pathFromThisDirectory The path from this directory to the above
     *                              directory using the {@link File#separator}.
     * @return The higher directory.
     */
    public @NotNull ConfigurationDirectory getDirectory(@NotNull String pathFromThisDirectory) {
        if (pathFromThisDirectory.isEmpty()) return this;
        return new ConfigurationDirectory(new File(this.directory, pathFromThisDirectory), clazz);
    }

    public @NotNull String getDirectoryName() {
        return this.directory.getName().split("\\.")[0];
    }

    public @NotNull List<String> getResourcePathList() {
        return this.resourcePathList;
    }

    public @NotNull ConfigurationDirectory addResourcePath(@NotNull final String path) {
        this.resourcePathList.add(path);
        return this;
    }

    public @NotNull ConfigurationDirectory removeResourcePath(@NotNull final String path) {
        this.resourcePathList.remove(path);
        return this;
    }

    public @NotNull ConfigurationDirectory clearResourcePathList() {
        this.resourcePathList.clear();
        return this;
    }

    /**
     * Used to get this directories configuration file.
     * You can use this special configuration file to store
     * infomation about the other files.
     *
     * @return The loaded configuration file instance.
     */
    public @NotNull Configuration getDataStore() {

        // Loop though files in this directory.
        for (final File file : this.getFiles(false, true)) {

            // Is the file not a data store?
            if (!file.getName().endsWith(DATA_FILE_EXTENSION)) continue;

            // Load the configuration file.
            YamlConfiguration configuration = new YamlConfiguration(file, this.clazz);
            configuration.load();
            return configuration;
        }

        // Otherwise, create the data store file.
        YamlConfiguration configuration = new YamlConfiguration(
            this.getDirectory(),
            ConfigurationDirectory.DATA_FILE_EXTENSION,
            this.clazz
        );
        configuration.load();
        return configuration;
    }

    /**
     * Used to get a list of files.
     *
     * @param ignoreDataStore   If it should ignore data store files.
     * @param onlyThisDirectory If it should only look in this directory
     *                          and not higher directories.
     * @return The list of files.
     */
    public @NotNull List<File> getFiles(boolean ignoreDataStore, boolean onlyThisDirectory) {
        return this.getFiles0(this.getDirectory(), ignoreDataStore, onlyThisDirectory);
    }

    private @NotNull List<File> getFiles0(@NotNull File folder, boolean ignoreDataStore, boolean onlyThisDirectory) {

        // Get the files within this folder.
        final File[] fileList = folder.listFiles();

        // Are there any files?
        if (fileList == null) return new ArrayList<>();

        // Set up the list of files.
        final List<File> finalFileList = new ArrayList<>();

        // Loop though files.
        for (final File file : fileList) {

            // Do we ignore the data store file?
            if (ignoreDataStore && file.getName().endsWith(DATA_FILE_EXTENSION)) continue;

            // Is this file a system file?
            if (file.getName().startsWith(".") && !file.getName().endsWith(DATA_FILE_EXTENSION)) continue;

            // Should we get files from this file?
            if (onlyThisDirectory) {

                // Is it a directory?
                if (file.listFiles() != null && Objects.requireNonNull(file.listFiles()).length > 0) continue;

                // Add the file.
                finalFileList.add(file);
                continue;
            }

            // If this is a directory we get the files within the file.
            List<File> filesInFile = this.getFiles0(file, ignoreDataStore, onlyThisDirectory);

            // Were there no files?
            if (filesInFile.isEmpty()) {
                finalFileList.add(file);
                continue;
            }

            // Add all the files within this directory.
            finalFileList.addAll(filesInFile);
        }

        return finalFileList;
    }

    /**
     * Used to get the list of file names.
     *
     * @param ignoreDataStore   If it should ignore data store files.
     * @param onlyThisDirectory If it should only look in this directory
     *                          and not higher directories.
     * @param includeExtensions If the file names should include the dot extensions.
     * @return The list of file names.
     */
    public @NotNull List<String> getFileNames(boolean ignoreDataStore, boolean onlyThisDirectory, boolean includeExtensions) {
        final List<String> fileNames = new ArrayList<>();

        for (final File file : this.getFiles(ignoreDataStore, onlyThisDirectory)) {

            // Should the extensions be included?
            if (includeExtensions) {
                fileNames.add(file.getName());
                continue;
            }

            fileNames.add(file.getName().split("\\.")[0]);
        }

        return fileNames;
    }

    public @NotNull List<Configuration> getConfigurationFiles(boolean onlyThisDirectory) {
        List<Configuration> configurationList = new ArrayList<>();

        for (final File file : this.getFiles(true, onlyThisDirectory)) {

            final Configuration configuration = ConfigurationFactory.createConfiguration(file, this.clazz).orElseThrow(
                () -> new ConfigurationException(
                    new RuntimeException(),
                    "ConfigurationDirectory.getConfigurationFiles(onlyThisDirectory)",
                    "File may not be supported. Unable to create configuration instance from file with path.",
                    "File absolute path: " + file.getAbsolutePath()
                )
            );

            configuration.load();
            configurationList.add(configuration);
        }

        return configurationList;
    }

    /**
     * Used to get the configuration file instance that
     * contains a specific key.
     *
     * @param key               The key to look for.
     * @param factory           The type of configuration to look in.
     *                          If this is null it will look into all types of supported configuration.
     * @param onlyThisDirectory If it should only look in this directory and not directories above.
     * @return The optional configuration.
     */
    public @NotNull Optional<Configuration> getConfiguration(@NotNull String key, @Nullable ConfigurationFactory factory, boolean onlyThisDirectory) {
        for (File file : this.getFiles(true, onlyThisDirectory)) {

            // Get the file's last extension.
            final String extension = file.getName().split("\\.")[file.getName().split("\\.").length - 1];

            // Is the factory not specified?
            if (factory == null) {

                // Attempt to create the configuration.
                final Configuration configuration = ConfigurationFactory.createConfiguration(file, this.clazz).orElseThrow();
                configuration.load();

                if (configuration.getKeys().contains(key)) return Optional.of(configuration);
                continue;
            }

            // Does the file have the wrong extension?
            if (!factory.getExtensions().contains(extension)) continue;

            // Create the configuration.
            Configuration configuration = factory.create(file, this.clazz);
            configuration.load();

            if (configuration.getKeys().contains(key)) return Optional.of(configuration);
        }

        return Optional.empty();
    }

    public @NotNull Optional<Configuration> getConfiguration(@NotNull String key, boolean onlyThisDirectory) {
        return this.getConfiguration(key, null, onlyThisDirectory);
    }

    /**
     * Used to add all the keys and values from a configuration
     * section into this section.
     *
     * @param section The section to add.
     * @return This instance.
     */
    public @NotNull ConfigurationDirectory appendSection(@NotNull final ConfigurationSection section) {
        this.data.putAll(section.getMap());
        return this;
    }

    /**
     * Used to add all the keys and values from a supported
     * configuration file into this section.
     *
     * @param file The file to add.
     * @return This instance.
     */
    public @NotNull ConfigurationDirectory appendFile(@NotNull final File file) {

        // Create the configuration file.
        final Configuration configuration = ConfigurationFactory.createConfiguration(file, this.clazz).orElseThrow(
            () -> new ConfigurationException(
                new RuntimeException(),
                "ConfigurationDirectory.getConfigurationFiles(onlyThisDirectory)",
                "File may not be supported. Unable to create configuration instance from file with path.",
                "File absolute path: " + file.getAbsolutePath()
            )
        );

        // Load the configuration file.
        configuration.load();

        // Append the configuration's section.
        this.appendSection(configuration);
        return this;
    }

    /**
     * Used to check if this directory has no files.
     * This will not count directories or files in higher directories.
     *
     * @return True if empty.
     */
    public boolean isEmpty() {
        return this.getFiles(false, true).isEmpty();
    }

    public @NotNull ConfigurationDirectory createDirectory() {
        this.directory.mkdirs();
        return this;
    }

    public @NotNull ConfigurationDirectory createResource(@NotNull String resourcePath) {

        // Attempt to get the resource as an input stream.
        try (InputStream input = this.getClass().getResourceAsStream("/" + resourcePath)) {

            // Get the name of the file.
            final String name = resourcePath.substring(
                resourcePath.lastIndexOf('/') + 1
            );

            // Create the file within the directory.
            File file = new File(this.getDirectory(), name);

            if (input != null) {
                Files.copy(input, file.toPath());
                return this;
            }

            throw new ConfigurationException(
                new RuntimeException(),
                "ConfigurationDirectory.createResource(resourcePath)",
                "Unable to create resource as the input stream was null.",
                "The resource path is " + resourcePath + "."
            );

        } catch (IOException exception) {
            throw new ConfigurationException(
                exception,
                "ConfigurationDirectory.createResource(resourcePath)",
                "Unable to copy and paste the resource file.",
                "The resource path was" + resourcePath + "."
            );
        }
    }

    public @NotNull ConfigurationDirectory createDefaultResourceFiles() {

        // Loop though resource paths.
        for (String resourcePath : this.resourcePathList) {
            this.createResource(resourcePath);
        }

        return this;
    }

    /**
     * Used to load the configuration file sections into this
     * directory instance.
     *
     * @param onlyThisDirectory If it should only load files from this directory
     *                          and not directories higher.
     * @return This instance.
     */
    public @NotNull ConfigurationDirectory load(boolean onlyThisDirectory) {

        // Reset the configuration section data.
        this.data.clear();

        // Attempt to create the directory.
        this.createDirectory();

        // Get the list of files to load.
        final List<File> files = this.getFiles(true, onlyThisDirectory);

        // Are there no files?
        if (files.isEmpty()) {

            // Create the default files.
            this.createDefaultResourceFiles();

            // Load resource files.
            for (final File file : this.getFiles(true, onlyThisDirectory)) {
                this.appendFile(file);
            }

            return this;
        }

        // Append each file.
        files.forEach(this::appendFile);
        return this;
    }

    /**
     * This will first attempt to update all the values though the keys.
     * Then it will remove any keys that were removed from this directory.
     *
     * @param onlyThisDirectory If it should only use files from this directory
     *                          and not directories higher.
     * @return This instance.
     */
    public @NotNull ConfigurationDirectory save(boolean onlyThisDirectory) {

        // Get the current keys from the files.
        List<String> currentKeys = this.getCurrentKeys(onlyThisDirectory);

        // Loop though each key and then update it within
        // its configuration file.
        for (String key : this.getKeys()) {

            // Remove the key as will be edited.
            currentKeys.remove(key);

            try {

                // Get the configuration file from the key.
                Configuration configuration = this.getConfiguration(key, onlyThisDirectory).orElse(null);

                // Does the configuration not exist?
                if (configuration == null) {
                    Configuration temp = new YamlConfiguration(this.getDirectory(), "lost_keys.yml", this.clazz).load();
                    temp.set(key, this.get(key));
                    continue;
                }

                configuration.load();
                configuration.set(key, this.get(key));

            } catch (Exception exception) {
                throw new ConfigurationException(
                    exception,
                    "ConfigurationDirectory.save(onlyThisDirectory)",
                    "Unable to set a key to a value.",
                    "The key is " + key + "."
                );
            }
        }

        // Is the current keys empty?
        if (currentKeys.isEmpty()) return this;

        // Loop though each key and remove it from
        // its configuration file.
        for (String key : currentKeys) {
            try {

                // Get the configuration file from the key.
                Configuration configuration = this.getConfiguration(key, onlyThisDirectory).orElse(null);

                // Does the configuration not exist?
                if (configuration == null) continue;

                configuration.load();
                configuration.remove(key);

            } catch (Exception exception) {
                throw new ConfigurationException(
                    exception,
                    "ConfigurationDirectory.save(onlyThisDirectory)",
                    "Unable to remove the key " + key + "."
                );
            }
        }

        return this;
    }

    private @NotNull List<String> getCurrentKeys(boolean onlyThisDirectory) {
        List<String> currentKeys = new ArrayList<>();

        for (final Configuration configuration : this.getConfigurationFiles(onlyThisDirectory)) {
            currentKeys.addAll(configuration.getKeys());
        }

        return currentKeys;
    }
}
