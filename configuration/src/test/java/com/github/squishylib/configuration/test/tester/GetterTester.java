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

package com.github.squishylib.configuration.test.tester;

import com.github.squishylib.common.logger.Logger;
import com.github.squishylib.common.testing.ResultChecker;
import com.github.squishylib.configuration.Configuration;
import com.github.squishylib.configuration.ConfigurationSection;
import com.github.squishylib.configuration.PreparedConfigurationFactory;
import com.github.squishylib.configuration.implementation.MemoryConfigurationSection;
import com.github.squishylib.configuration.indicator.ConfigurationConvertible;
import org.jetbrains.annotations.NotNull;

public class GetterTester {

    private final @NotNull PreparedConfigurationFactory factory;
    private final @NotNull Logger logger;

    public GetterTester(@NotNull final PreparedConfigurationFactory factory, @NotNull Logger logger) {
        this.factory = factory;
        this.logger = logger;
    }

    public void testAll() {
        this.testGet();
        this.testGetString();
        this.testGetStringDefault();
        this.testGetInteger();
        this.testGetIntegerDefault();
        this.testGetIntegerThatIsAFloat();
        this.testGetLong();
        this.testGetLongDefault();
        this.testGetFloat();
        this.testGetFloatDefault();
        this.testGetDouble();
        this.testGetDoubleDefault();
        this.testGetClass();
        this.testGetConvertable();
    }

    public void testGet() {
        logger.info("&aRunning test: &ftestGet");
        final Configuration configuration = this.factory.create().load();
        final Object object = configuration.get("get");

        new ResultChecker("testGet")
                .fallBack("&etestGet Failed")
                .expect(object instanceof String, "object instanceof String")
                .expect(object.equals("test"), "object.equals(\"test\")");
    }

    public void testGetString() {
        logger.info("&aRunning test: &ftestGetString");
        final Configuration configuration = this.factory.create().load();
        final String string = configuration.getString("getString");

        new ResultChecker("testGetString")
                .fallBack("&etestGetString Failed")
                .expect(string.equals("testString"), "object.equals(\"testString\")");
    }

    public void testGetStringDefault() {
        logger.info("&aRunning test: &ftestGetStringDefault");
        final Configuration configuration = this.factory.create().load();
        final String string = configuration.getString("getStringDefault", "default");

        new ResultChecker("testGetStringDefault")
                .expect(string.equals("default"), "object.equals(\"testString\")");
    }

    public void testGetInteger() {
        logger.info("&aRunning test: &ftestGetInteger");
        final Configuration configuration = this.factory.create().load();
        final int i = configuration.getInteger("getInteger");

        new ResultChecker("testGetInteger")
                .fallBack("&etestGetInteger Failed")
                .expect(i == 7, "i == 7");
    }

    public void testGetIntegerDefault() {
        logger.info("&aRunning test: &ftestGetIntegerDefault");
        final Configuration configuration = this.factory.create().load();
        final int i = configuration.getInteger("getIntegerDefault", 8);

        new ResultChecker("testGetIntegerDefault")
                .fallBack("&etestGetIntegerDefault Failed")
                .expect(i == 8, "i == 8");
    }

    public void testGetIntegerThatIsAFloat() {
        logger.info("&aRunning test: &ftestGetIntegerThatIsAFloat");
        final Configuration configuration = this.factory.create().load();
        final int i = configuration.getInteger("getIntegerThatIsAFloat");

        new ResultChecker("testGetIntegerThatIsAFloat")
                .fallBack("&etestGetIntegerThatIsAFloat Failed")
                .expect(i == 7, "i == 7");

    }

    public void testGetLong() {
        logger.info("&aRunning test: &ftestGetLong");
        final Configuration configuration = this.factory.create().load();
        final long i = configuration.getLong("getLong");

        new ResultChecker("testGetLong")
                .fallBack("&etestGetLong Failed")
                .expect(i == 1231231231231231231L, "i == 1231231231231231231L");
    }

    public void testGetLongDefault() {
        logger.info("&aRunning test: &ftestGetLongDefault");
        final Configuration configuration = this.factory.create().load();
        final long i = configuration.getLong("getLongDefault", 2231231231231231231L);

        new ResultChecker("testGetLongDefault")
                .fallBack("&etestGetLongDefault Failed")
                .expect(i == 2231231231231231231L, "i == 2231231231231231231L");
    }

    public void testGetFloat() {
        logger.info("&aRunning test: &ftestGetFloat");
        final Configuration configuration = this.factory.create().load();
        System.out.println(configuration.getMap());
        final float i = configuration.getFloat("getFloat");

        new ResultChecker("testGetFloat")
                .fallBack("&etestGetFloat Failed")
                .expect(i, 0.5F, "i == 0.5F");
    }

    public void testGetFloatDefault() {
        logger.info("&aRunning test: &ftestGetFloatDefault");
        final Configuration configuration = this.factory.create().load();
        final float i = configuration.getFloat("getFloatDefault", 0.25F);

        new ResultChecker("testGetFloatDefault")
                .fallBack("&etestGetFloatDefault Failed")
                .expect(i == 0.25F, "i == 0.0.25F");
    }

    public void testGetDouble() {
        logger.info("&aRunning test: &ftestGetDouble");
        final Configuration configuration = this.factory.create().load();
        final double i = configuration.getDouble("getDouble");

        new ResultChecker("testGetDouble")
                .fallBack("&etestGetDouble Failed")
                .expect(i == 0.5D, "i == 0.5D");
    }

    public void testGetDoubleDefault() {
        logger.info("&aRunning test: &ftestGetDoubleDefault");
        final Configuration configuration = this.factory.create().load();
        final double i = configuration.getDouble("getDoubleDefault", 0.25D);

        new ResultChecker("testGetDoubleDefault")
                .fallBack("&etestGetDoubleDefault Failed")
                .expect(i == 0.25D, "i == 0.25D");
    }

    private static class Test {
        private String key;
    }

    public void testGetClass() {
        logger.info("&aRunning test: &ftestGetClass");
        final Configuration configuration = this.factory.create().load();
        final Test instance = configuration.getClass("getClass", Test.class);

        new ResultChecker("testGetClass")
                .fallBack("&etestGetClass Failed")
                .expect(instance != null, "instance != null")
                .expect(instance.key.equals("value"), "instance.key.equals(\"value\")");
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
        logger.info("&aRunning test: &ftestGetConvertable");
        final Configuration configuration = this.factory.create().load();
        final Convertable instance = configuration.getConvertable("getConvertable", new Convertable("getConvertable"));

        new ResultChecker("testGetConvertable")
                .fallBack("&etestGetConvertable Failed")
                .expect(instance != null, "instance != null")
                .expect(instance.key.equals("value"), "instance.key.equals(\"value\")");
    }
}
