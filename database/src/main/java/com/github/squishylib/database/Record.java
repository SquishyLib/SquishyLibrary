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

package com.github.squishylib.database;

import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.configuration.indicator.ConfigurationConvertible;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.util.List;

public interface Record<R extends Record<R>> extends ConfigurationConvertible<R> {

    /**
     * The records unique identifier.
     * This will be used as the primary key.
     *
     * @return The unique identifier.
     */
    @NotNull
    String getIdentifier();

    /**
     * Used to get the table columns / the name of the
     * variables / the keys in the configuration map.
     *
     * @return The list of field names.
     */
    @NotNull
    List<String> getFieldNames();

    /**
     * Used to convert a result set into this class instance.
     *
     * @param results The results to convert.
     * @return This instance.
     */
    default @NotNull R convert(@NotNull ResultSet results) {
        ConfigurationSection section = new MemoryConfigurationSection();

        for (String fieldName : this.getFieldNames()) {
            try {
                section.set(fieldName, results.getObject(fieldName));
            } catch (Exception exception) {
                throw new DatabaseException(exception, this, "convert", "Unable to convert " + fieldName + " to an object.");
            }
        }

        return (R) this;
    }
}
