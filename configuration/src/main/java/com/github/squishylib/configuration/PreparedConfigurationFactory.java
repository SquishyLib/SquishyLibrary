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

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class PreparedConfigurationFactory {

    private final @NotNull ConfigurationFactory factory;
    private final @NotNull File file;

    public PreparedConfigurationFactory(@NotNull ConfigurationFactory factory, @NotNull File file) {
        this.factory = factory;
        this.file = file;
    }

    public @NotNull Configuration create() {
        return this.factory.create(this.file);
    }
}
