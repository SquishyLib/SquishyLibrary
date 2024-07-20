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

package com.github.smuddgge.squishy.configuration.indicator;

import com.github.smuddgge.squishy.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Indicates if a class can be converted to
 * and from a {@link ConfigurationSection}.
 * <p>
 * This makes it easier to save or load data from
 * configuration files.
 * <p>
 * It is advised that the constructor only has 1 argument
 * being the identifier that represents this object in the
 * configuration section.
 *
 * @param <T> The class being converted.
 *            Normally the class where this is implemented.
 */
public interface ConfigurationConvertible<T> {

    /**
     * Used to convert this class into a configuration section.
     * <p>
     * The configuration section can then be converted into a map
     * to add to other configuration files.
     *
     * @return The configuration section instance.
     */
    @NotNull
    ConfigurationSection convert();

    /**
     * Used to apply a configuration section to this class
     * and return this instance.
     *
     * @param section The instance of the configuration section.
     * @return This instance.
     */
    @NotNull
    T convert(@NotNull ConfigurationSection section);

    /**
     * Used to get this class as a map.
     * <p>
     * Uses {@link ConfigurationConvertible#convert()} to convert into
     * a configuration section.
     *
     * @return The map of this class.
     */
    default @NotNull Map<String, Object> convertToMap() {
        return this.convert().getMap();
    }
}