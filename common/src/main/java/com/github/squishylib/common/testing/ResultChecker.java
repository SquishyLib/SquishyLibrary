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

package com.github.squishylib.common.testing;

import com.github.squishylib.common.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;


import java.util.ArrayList;
import java.util.List;

/**
 * Represents a result checker.
 * Create a new instance of this class to
 * compare results.
 */
public class ResultChecker {

    private final @Nullable String name;
    private final @NotNull Logger logger;
    private final List<Runnable> fallBackRunnableList = new ArrayList<>();

    public ResultChecker() {
        this.name = null;
        this.logger = new Logger("com.github.squishylib.common.testing");
    }

    public ResultChecker(@NotNull String name) {
        this.name = name;
        this.logger = new Logger("com.github.squishylib.common.testing");
    }

    public ResultChecker(@NotNull String name, @NotNull Logger logger) {
        this.name = name;
        this.logger = logger;
    }

    /**
     * Used to check if a boolean value is true.
     * If false, the test will fail.
     *
     * @param condition The boolean value.
     * @return This instance.
     */
    public @NotNull ResultChecker expect(boolean condition, @NotNull String subName) {
        if (condition) {
            if (this.name == null) {
                this.logger.info("&aTest passed.");
                return this;
            }
            this.logger.info("&aTest passed: &f" + this.name + " &a" + subName);
            return this;
        }

        this.runFallBack();
        this.logger.info("&eTest failed: &f" + this.name + " &e" + subName);
        Assertions.assertTrue(condition);
        return this;
    }

    /**
     * Used to check if two values are the same.
     * If they are not equal the test will fail.
     *
     * @param value1 The first value.
     * @param value2 The second value.
     * @return This instance.
     */
    public @NotNull ResultChecker expect(Object value1, Object value2, @NotNull String subName) {
        if (value1.equals(value2)) {
            if (this.name == null) {
                this.logger.info("&aTest passed. &7{value1=" + value1 + ", &7value2=" + value2 + "}");
                return this;
            }
            this.logger.info("&aTest passed: &f" + this.name + " &a" + subName + ". &7{value1=" + value1 + ", &7value2=" + value2 + "}");
            return this;
        }

        this.runFallBack();
        this.logger.info("&eTest failed: &f" + this.name + " &e" + subName + ". &7{value1=" + value1 + ", &7value2=" + value2 + "}");
        Assertions.assertEquals(value2, value1);
        return this;
    }

    /**
     * Used to add a runnable that will be
     * executed before an error occurs.
     *
     * @param runnable The instance of the runnable.
     * @return This instance.
     */
    public @NotNull ResultChecker fallBack(@NotNull Runnable runnable) {
        this.fallBackRunnableList.add(runnable);
        return this;
    }

    /**
     * Used to add a warning message that will
     * be sent to console before an error occurs.
     *
     * @param message The instance of the message.
     * @return This instance.
     */
    public @NotNull ResultChecker fallBack(@NotNull String message) {
        return this.fallBack(() -> this.logger.warn(message));
    }

    /**
     * Used to run the fallback lists.
     * This method should be called before an error occurs.
     *
     * @return This instance.
     */
    private @NotNull ResultChecker runFallBack() {
        for (Runnable runnable : this.fallBackRunnableList) {
            runnable.run();
        }
        return this;
    }

    /**
     * Used to run a runnable.
     *
     * @param runnable The instance of a runnable.
     * @return This instance.
     */
    public @NotNull ResultChecker then(@NotNull Runnable runnable) {
        runnable.run();
        return this;
    }

    /**
     * Used to log a message in console.
     *
     * @param message The instance of a message.
     * @return This instance.
     */
    public @NotNull ResultChecker then(@NotNull String message) {
        this.logger.info(message);
        return this;
    }
}
