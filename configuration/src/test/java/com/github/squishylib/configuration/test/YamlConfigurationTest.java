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

package com.github.squishylib.configuration.test;

import com.github.squishylib.configuration.Configuration;
import com.github.squishylib.configuration.ConfigurationFactory;
import com.github.squishylib.configuration.PreparedConfigurationFactory;
import com.github.squishylib.configuration.test.tester.ConfigurationTester;
import org.junit.jupiter.api.Test;

import java.io.File;

public class YamlConfigurationTest {

    @Test
    public void test() {
        PreparedConfigurationFactory factory = new PreparedConfigurationFactory(
                ConfigurationFactory.YAML,
                new File("src/test/resources/test.yaml")
        );

        ConfigurationTester tester = new ConfigurationTester(factory);
        tester.testAll();
    }
}
