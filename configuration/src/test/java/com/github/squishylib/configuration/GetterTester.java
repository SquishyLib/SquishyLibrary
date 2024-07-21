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

import com.github.squishylib.common.testing.ResultChecker;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.configuration.indicator.ConfigurationConvertible;
import org.jetbrains.annotations.NotNull;

public class GetterTester {

    private @NotNull final PreparedConfigurationFactory factory;

    public GetterTester(@NotNull final PreparedConfigurationFactory factory) {
        this.factory = factory;
    }

    public void testAll() {
        this.testGet();
        this.testGetClass();
        this.testGetConvertable();
    }

    public void testGet() {
        final Configuration configuration = this.factory.create().load();
        final Object object = configuration.get("get");

        new ResultChecker()
                .fallBack("&etestGet Failed")
                .expect(object instanceof String)
                .expect(object.equals("test"))
                .then("&atestGet Passed");
    }

    private static class Test {
        private String key;
    }

    public void testGetClass() {
        final Configuration configuration = this.factory.create().load();
        final Test instance = configuration.getClass("getClass", Test.class);

        new ResultChecker()
                .fallBack("&etestGetClass Failed")
                .expect(instance != null)
                .expect(instance.key.equals("value"))
                .then("&atestGetClass Passed");
    }

    private static class Convertable implements ConfigurationConvertible<Convertable> {

        private final @NotNull String identifier;
        private String key;

        public Convertable(@NotNull final String identifier) {
            this.identifier = identifier;
        }

        @Override
        public @NotNull ConfigurationSection convert() {
            ConfigurationSection section = new MemoryConfigurationSection();
            section.set("key", key);
            return section;
        }

        @Override
        public @NotNull Convertable convert(@NotNull ConfigurationSection section) {
            this.key = section.getString("key");
            return this;
        }
    }

    public void testGetConvertable() {
        final Configuration configuration = this.factory.create().load();
        final Convertable instance = configuration.getConvertable("getConvertable", new Convertable("getConvertable"));

        new ResultChecker()
                .fallBack("&etestGetConvertable Failed")
                .expect(instance != null)
                .expect(instance.key.equals("value"))
                .then("&atestGetConvertable Passed");
    }
}
