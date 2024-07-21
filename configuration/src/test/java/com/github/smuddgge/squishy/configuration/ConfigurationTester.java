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

import org.jetbrains.annotations.NotNull;

public class ConfigurationTester {

    private @NotNull final PreparedConfigurationFactory factory;

    public ConfigurationTester(@NotNull final PreparedConfigurationFactory factory) {
        this.factory = factory;
    }

    public void testAll() {
        this.testGetters();
    }

    public void testGetters() {
        GetterTester tester = new GetterTester(factory);
        tester.testAll();
    }
}
