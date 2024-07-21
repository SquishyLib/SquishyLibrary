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

package com.github.smuddgge.squishy.configuration.directory;

import com.github.smuddgge.squishy.configuration.Configuration;
import com.github.smuddgge.squishy.configuration.implementation.YamlConfiguration;
import com.github.smuddgge.squishy.configuration.indicator.ConfigurationConvertible;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * An easier way to configure a configuration directory that only
 * contains one type of convertable class.
 *
 * @param <T> The class type that this directory will contain.
 */
public class SingleTypeConfigurationDirectory<T extends ConfigurationConvertible<T>> {

    public interface Factory<T> {

        /**
         * Used to create the empty configuration convertable class.
         *
         * @param identifier The object's identifier.
         * @return The empty class.
         */
        @NotNull
        T createEmpty(@NotNull String identifier);
    }

    private final @NotNull ConfigurationDirectory directory;
    private final @NotNull Factory<T> factory;
    private final boolean onlyThisDirectory;

    public SingleTypeConfigurationDirectory(@NotNull File directory, @NotNull Factory<T> factory, boolean onlyThisDirectory) {
        this.directory = new ConfigurationDirectory(directory);
        this.factory = factory;
        this.onlyThisDirectory = onlyThisDirectory;
    }

    public @NotNull ConfigurationDirectory getDirectory() {
        return this.directory;
    }

    public @NotNull Factory<T> getFactory() {
        return this.factory;
    }

    public boolean isOnlyThisDirectory() {
        return this.onlyThisDirectory;
    }

    /**
     * Used to get a type object from the configuration.
     *
     * @param identifier The object's identifier.
     * @return The instance of the object.
     * Empty if it isn't in the configuration directory.
     */
    public @NotNull Optional<T> get(@NotNull String identifier) {

        // Check if the type exists.
        if (this.directory.getKeys().contains(identifier)) {
            return Optional.of(this.factory.createEmpty(identifier).convert(this.directory.getSection(identifier)));
        }

        // Otherwise the object does not exist.
        return Optional.empty();
    }

    /**
     * Used to get the list of all the types in
     * the configuration directory.
     *
     * @return The list of types.
     */
    public @NotNull List<T> getAll() {
        List<T> typeList = new ArrayList<>();

        for (String identifier : this.directory.getKeys()) {
            typeList.add(this.get(identifier).orElseThrow());
        }

        return typeList;
    }

    /**
     * Used to insert a type into the directory.
     * <p>
     * If the identifier already exists, it will update that converted object.
     * Otherwise, it will add the object to the default configuration file.
     * If the default configuration file doesn't exist, it will be created.
     *
     * @param identifier The instance of the identifier.
     * @param type       The type to insert.
     * @return This instance.
     */
    public @NotNull SingleTypeConfigurationDirectory<T> set(@NotNull String identifier, @NotNull T type) {

        // Get the instance of the configuration to use.
        Configuration configuration = this.directory.getConfiguration(identifier, this.onlyThisDirectory).orElseGet(() -> {
            Configuration temp = new YamlConfiguration(this.directory.getDirectory(), "default.yml");
            temp.load();
            return temp;
        });

        configuration.set(identifier, type.convert().getMap());
        configuration.save();

        // Reload the directory.
        this.load();
        return this;
    }

    /**
     * Used to remove a type from the configuration directory.
     *
     * @param identifier The instance of the identifier.
     * @return This instance.
     */
    public @NotNull SingleTypeConfigurationDirectory<T> remove(@NotNull String identifier) {

        // Get the local configuration file.
        Optional<Configuration> optionalConfiguration = this.directory.getConfiguration(identifier, this.onlyThisDirectory);

        // Check if the configuration exists.
        if (optionalConfiguration.isEmpty()) return this;
        Configuration configuration = optionalConfiguration.get();

        // Remove the identifier and data.
        configuration.set(identifier, null);
        configuration.save();

        // Reload the directory.
        this.load();
        return this;
    }

    /**
     * Used to check if the configuration directory
     * contains a certain identifier.
     *
     * @param identifier The identifier to check for.
     * @return True if it exists in the configuration directory.
     */
    public boolean contains(@NotNull String identifier) {
        return this.directory.getKeys().contains(identifier);
    }

    public @NotNull SingleTypeConfigurationDirectory<T> load() {
        this.directory.load(this.onlyThisDirectory);
        return this;
    }
}
