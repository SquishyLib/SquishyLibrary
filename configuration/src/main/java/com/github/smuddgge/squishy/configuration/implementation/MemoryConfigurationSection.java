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

package com.github.smuddgge.squishy.configuration.implementation;

import com.github.smuddgge.squishy.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A configuration section that used a {@link Map} to
 * store the key value pairs.
 */
public class MemoryConfigurationSection implements ConfigurationSection {

    private final @NotNull Map<String, Object> data;
    private final @NotNull ConfigurationSection baseSection;
    private final @NotNull String pathFromBase;

    public MemoryConfigurationSection(@NotNull Map<String, Object> data) {
        this.data = data;
        this.baseSection = this;
        this.pathFromBase = "";
    }

    public MemoryConfigurationSection(@NotNull Map<String, Object> data, @NotNull ConfigurationSection baseSection, @NotNull String pathFromBase) {
        this.data = data;
        this.baseSection = baseSection;
        this.pathFromBase = pathFromBase;
    }

    @Override
    public @NotNull ConfigurationSection getBaseSection() {
        return this.baseSection;
    }

    @Override
    public @NotNull String getPathFromBase() {
        return this.getPathFromBase(null);
    }

    @Override
    public @NotNull String getPathFromBase(@Nullable String path) {
        if (path == null) return this.pathFromBase;
        if (this.pathFromBase.isEmpty()) return path;
        return this.pathFromBase + "." + path;
    }

    @Override
    public Map<String, Object> getMap(@Nullable String path) {
        return this.data;
    }

    @Override
    public @NotNull ConfigurationSection set(@Nullable Object value) {
        return this.getBaseSection().setInSection(this.getPathFromBase(), value);
    }

    @Override
    public @NotNull ConfigurationSection set(@Nullable String path, @Nullable Object value) {
        return this.getBaseSection().setInSection(this.getPathFromBase(path), value);
    }

    @Override
    public @NotNull ConfigurationSection setInSection(@Nullable String path, @Nullable Object value) {
        final Object convertedValue = this.convertLists(value);

        // TODO

        return this;
    }

    /**
     * This will convert immutable lists into {@link List} type lists.
     *
     * @param value The value to convert.
     * @return The converted object.
     */
    private @Nullable Object convertLists(@Nullable Object value) {
        if (value == null) return null;

        // Check if the value is a string list.
        if (value instanceof String[] strings) {
            return new ArrayList<>(Arrays.stream(strings).toList());
        }

        // Check if the value is an integer list.
        if (value instanceof int[] integers) {
            return new ArrayList<>(Arrays.stream(integers).boxed().toList());
        }

        // Check if the value is a long list.
        if (value instanceof long[] longs) {
            return new ArrayList<>(Arrays.stream(longs).boxed().toList());
        }

        // Check if the value is a long list.
        if (value instanceof double[] doubles) {
            return new ArrayList<>(Arrays.stream(doubles).boxed().toList());
        }

        // Check if the value is a long list.
        if (value instanceof float[] floats) {
            List<Float> list = new ArrayList<>();
            for (float f : floats) list.add(f);
            return list;
        }

        return value;
    }

    @Override
    public Object get(@Nullable String path, @Nullable Object alternative) {
        return null;
    }

    @Override
    public @Nullable Object get(@Nullable String path) {
        return null;
    }

    @Override
    public <T> T getClass(@Nullable String path, @NotNull Class<T> clazz, @Nullable T alternative) {
        return null;
    }

    @Override
    public <T> @Nullable T getClass(@Nullable String path, @NotNull Class<T> clazz) {
        return null;
    }

    @Override
    public @NotNull ConfigurationSection getSection(String path) {
        return null;
    }

    @Override
    public @NotNull List<@NotNull String> getKeys() {
        return List.of();
    }

    @Override
    public @NotNull List<@NotNull String> getKeys(@Nullable String path) {
        return List.of();
    }

    @Override
    public String getString(@Nullable String path, @Nullable String alternative) {
        return "";
    }

    @Override
    public @Nullable String getString(@Nullable String path) {
        return "";
    }

    @Override
    public boolean isString(@Nullable String path) {
        return false;
    }

    @Override
    public String getAdaptedString(@Nullable String path, @NotNull String join, @Nullable String alternative) {
        return "";
    }

    @Override
    public @Nullable String getAdaptedString(@Nullable String path, @NotNull String join) {
        return "";
    }

    @Override
    public int getInteger(@Nullable String path, int alternative) {
        return 0;
    }

    @Override
    public int getInteger(@Nullable String path) {
        return 0;
    }

    @Override
    public boolean isInteger(@Nullable String path) {
        return false;
    }

    @Override
    public long getLong(@Nullable String path, long alternative) {
        return 0;
    }

    @Override
    public long getLong(@Nullable String path) {
        return 0;
    }

    @Override
    public boolean isLong(@Nullable String path) {
        return false;
    }

    @Override
    public double getDouble(@Nullable String path, double alternative) {
        return 0;
    }

    @Override
    public double getDouble(@Nullable String path) {
        return 0;
    }

    @Override
    public boolean isDouble(@Nullable String path) {
        return false;
    }

    @Override
    public float getFloat(@Nullable String path, double alternative) {
        return 0;
    }

    @Override
    public float getFloat(@Nullable String path) {
        return 0;
    }

    @Override
    public boolean isFloat(@Nullable String path) {
        return false;
    }

    @Override
    public boolean getBoolean(@Nullable String path, boolean alternative) {
        return false;
    }

    @Override
    public boolean getBoolean(@Nullable String path) {
        return false;
    }

    @Override
    public boolean isBoolean(@Nullable String path) {
        return false;
    }

    @Override
    public List<?> getList(@Nullable String path, @Nullable List<?> alternative) {
        return List.of();
    }

    @Override
    public @Nullable List<?> getList(@Nullable String path) {
        return List.of();
    }

    @Override
    public boolean isList(@Nullable String path) {
        return false;
    }

    @Override
    public List<String> getListString(@Nullable String path, @Nullable List<String> alternative) {
        return List.of();
    }

    @Override
    public @Nullable List<String> getListString(@Nullable String path) {
        return List.of();
    }

    @Override
    public List<Integer> getListInteger(@Nullable String path, @Nullable List<Integer> alternative) {
        return List.of();
    }

    @Override
    public @Nullable List<Integer> getListInteger(@Nullable String path) {
        return List.of();
    }

    @Override
    public List<Long> getListLong(@Nullable String path, @Nullable List<Integer> alternative) {
        return List.of();
    }

    @Override
    public @Nullable List<Long> getListLong(@Nullable String path) {
        return List.of();
    }

    @Override
    public List<Double> getListDouble(@Nullable String path, @Nullable List<Integer> alternative) {
        return List.of();
    }

    @Override
    public @Nullable List<Double> getListDouble(@Nullable String path) {
        return List.of();
    }

    @Override
    public List<Float> getListFloat(@Nullable String path, @Nullable List<Integer> alternative) {
        return List.of();
    }

    @Override
    public @Nullable List<Float> getListFloat(@Nullable String path) {
        return List.of();
    }

    @Override
    public @NotNull Map<String, Object> getMap() {
        return Map.of();
    }

    @Override
    public Map<String, Object> getMap(@Nullable String path, @Nullable Map<String, Object> alternative) {
        return Map.of();
    }

    @Override
    public boolean isMap(@Nullable String path) {
        return false;
    }
}
