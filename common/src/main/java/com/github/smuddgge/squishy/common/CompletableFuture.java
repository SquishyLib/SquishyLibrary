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

package com.github.smuddgge.squishy.common;

import org.jetbrains.annotations.NotNull;

/**
 * Adds option to wait for the future to complete without
 * needing to handel exceptions.
 *
 * @param <T> The type that will be completed in the future.
 */
public class CompletableFuture<T> extends java.util.concurrent.CompletableFuture<T> {

    public @NotNull T waitForComplete() {
        try {
            return this.get();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
