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

package com.github.squishylib.common;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * An array list but with some usefully methods.
 *
 * @param <T> The class inheriting this class.
 * @param <E> The type of elements in this list.
 */
public class Pool<T extends Pool<T, E>, E> extends ArrayList<E> {

    public T addAll(E[] list) {
        this.addAll(new ArrayList<>(Arrays.stream(list).toList()));
        return (T) this;
    }
}
