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

import com.github.smuddgge.squishy.configuration.ConfigurationException;
import com.github.smuddgge.squishy.configuration.ConfigurationSection;
import com.github.smuddgge.squishy.configuration.indicator.ConfigurationConvertible;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A configuration section that used a {@link Map} to
 * store the key value pairs.
 */
public class MemoryConfigurationSection implements ConfigurationSection {

    protected final @NotNull Map<String, Object> data;
    private final @NotNull ConfigurationSection baseSection;
    private final @NotNull String pathFromBase; // Formatted with dots.

    public MemoryConfigurationSection() {
        this.data = new LinkedHashMap<>();
        this.baseSection = this;
        this.pathFromBase = "";
    }

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
    public @NotNull String getPathFromBase(@Nullable String path) {
        if (path == null) return this.pathFromBase;
        if (this.pathFromBase.isEmpty()) return path;
        return this.pathFromBase + "." + path;
    }

    @Override
    public @NotNull String getPathFromBase() {
        return this.getPathFromBase(null);
    }

    @Override
    public @NotNull Map<String, Object> getMap() {
        return this.data;
    }

    @Override
    public @NotNull Optional<ConfigurationSection> getSectionBelow() {

        // Is this the base section?
        if (this.pathFromBase.isEmpty()) return Optional.empty();

        // Is the base section below this section?
        if (!this.pathFromBase.contains(".")) return Optional.of(this.baseSection);

        // Create the path to the section below.
        final String[] pathFromBaseKeys = this.pathFromBase.split("\\.");
        final String sectionKey = pathFromBaseKeys[pathFromBaseKeys.length - 1];
        final String path = this.pathFromBase.substring(0, this.pathFromBase.length() - sectionKey.length());

        // Return the section below.
        return Optional.of(this.baseSection.getSection(path));
    }

    @Override
    public @NotNull ConfigurationSection clear() {
        return this.baseSection.setInSection(this.getPathFromBase(), null);
    }

    @Override
    public @NotNull ConfigurationSection set(@NotNull String path, @Nullable Object value) {
        return this.baseSection.setInSection(this.getPathFromBase(path), value);
    }

    @Override
    public @NotNull ConfigurationSection setInSection(@NotNull String path, @Nullable Object value) {

        // Convert immutable lists into List<> type.
        final Object convertedValue = this.convertLists(value);

        // Is the value be in a section above this one?
        if (path.contains(".")) {

            // Get the key to the section above.
            final String key = path.split("\\.")[0];

            // Get the remaining path to the value.
            final String remainingPath = path.substring(key.length() + 1);

            try {

                // Create the section above.
                MemoryConfigurationSection sectionAbove = new MemoryConfigurationSection(
                        this.getMap(key) == null ? new LinkedHashMap<>() : this.getMap(key),
                        this.baseSection,
                        this.getPathFromBase(key)
                );

                // Populate the above sections with the keys and finally the value.
                sectionAbove.setInSection(remainingPath, convertedValue);

                // Update this section's data.
                this.data.put(key, sectionAbove.getMap());
                return this;

            } catch (Exception exception) {
                throw new ConfigurationException(this, "setInSection", "Unable to create section above and update data. remaining_path=" + remainingPath + ".");
            }
        }

        // Is the key being set to null?
        if (convertedValue == null) {
            this.data.remove(path);
            return this;
        }

        // Is the value type supported?
        if (this.isValueSupported(value)) {
            this.data.put(path, convertedValue);
            return this;
        }

        // Convert the value into a map.
        final Gson gson = new Gson();
        final String json = gson.toJson(convertedValue);
        final Map<?, ?> map = gson.fromJson(json, Map.class);

        // Put the map into the data map.
        this.data.put(path, map);
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
    public boolean isValueSupported(@NotNull Object value) {
        return value instanceof String
                || value instanceof Integer
                || value instanceof Long
                || value instanceof Double
                || value instanceof Float
                || value instanceof Boolean
                || value instanceof List
                || value instanceof Map;
    }

    @Override
    public Object get(@Nullable String path, @Nullable Object alternative) {
        try {

            // Do they want this sections map?
            if (path == null) {
                return this.data;
            }

            // Is the value be in a section above this one?
            if (path.contains(".")) {

                // Get the key to the section above.
                final String key = path.split("\\.")[0];

                // Get the remaining path to the value.
                final String remainingPath = path.substring(key.length() + 1);

                // Get the section above.
                final ConfigurationSection sectionAbove = this.getSection(key);
                return sectionAbove.get(remainingPath, alternative);
            }

            // Return the value in this sections map.
            return this.data.getOrDefault(path, alternative);

        } catch (Exception exception) {
            throw new ConfigurationException(this, "get", "Unable to get value from " + this.getPathFromBase(path) + ". The alternative value was " + alternative + ".");
        }
    }

    @Override
    public @Nullable Object get(@Nullable String path) {
        return this.get(path, null);
    }

    @Override
    public <T> T getClass(@Nullable String path, @NotNull Class<T> clazz, @Nullable T alternative) {

        // Set up the json converter.
        final Gson gson = new Gson();

        // Get the instance of the map from the path.
        final Map<String, Object> map = this.getMap(path);

        // Does the map exist?
        if (map == null) return alternative;

        try {

            // Convert the map into the object.
            final String json = gson.toJson(map);
            final T type = gson.fromJson(json, clazz);

            // Was the json empty?
            if (type == null) return alternative;
            return type;

        } catch (Exception exception) {
            throw new ConfigurationException(this, "getClass", "Unable to convert map " + map + " located " + this.getPathFromBase(path) + " into the class type " + clazz + ".");
        }
    }

    @Override
    public <T> @Nullable T getClass(@Nullable String path, @NotNull Class<T> clazz) {
        return this.getClass(path, clazz, null);
    }

    @Override
    public <T extends ConfigurationConvertible<T>> T getConvertable(@Nullable String path, @NotNull T convertable, @Nullable T alternative) {

        // Get the section from the path.
        final ConfigurationSection section = this.getSection(path);

        // Is the section empty?
        if (section.getKeys().isEmpty()) return alternative;

        // Convert the class.
        return convertable.convert(section);
    }

    @Override
    public <T extends ConfigurationConvertible<T>> @Nullable T getConvertable(@Nullable String path, @NotNull T convertable) {
        return this.getConvertable(path, convertable, null);
    }

    @Override
    public @NotNull ConfigurationSection getSection(@Nullable String path) {

        // Do they want this section?
        if (path == null) return this;

        // Return the section that is higher.
        return new MemoryConfigurationSection(
                this.getMap(path, new LinkedHashMap<>()),
                this.baseSection,
                this.getPathFromBase(path)
        );
    }

    @Override
    public @NotNull List<@NotNull String> getKeys() {
        if (this.data.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(this.data.keySet().stream().toList());
    }

    @Override
    public @NotNull List<@NotNull String> getKeys(@Nullable String path) {
        return this.getSection(path).getKeys();
    }

    @Override
    public String getAdaptedString(@Nullable String path, @NotNull String join, @Nullable String alternative) {

        // Get the value from the section.
        final Object value = this.get(path);

        // Attempt to convert the object into a string.
        final String converted = this.convertObject(value);

        // Has the value now been converted?
        if (converted != null) return converted;

        // Check if the value is a list.
        if (value instanceof List<?> list) {

            // Set up a string builder.
            StringBuilder builder = new StringBuilder();

            // Add all elements with the
            int index = 0;
            for (final Object item : list) {

                // Add the list item as a converted string.
                builder.append(this.convertObject(item));

                // Should there be a joiner here?
                if (index + 1 != list.size()) builder.append(join);
                index++;
            }

            return builder.toString();
        }

        return alternative;
    }

    private @Nullable String convertObject(@Nullable Object value) {

        // Does the value not exist?
        if (value == null) return null;

        // Check if the value is a string or type of supported number.
        if (value instanceof String string) return string;
        if (value instanceof Integer integer) return String.valueOf(integer);
        if (value instanceof Long number) return String.valueOf(number);
        if (value instanceof Double number) return String.valueOf(number);
        if (value instanceof Float number) return String.valueOf(number);

        return null;
    }

    @Override
    public @Nullable String getAdaptedString(@Nullable String path, @NotNull String join) {
        return this.getAdaptedString(path, join, null);
    }

    @Override
    public String getString(@Nullable String path, @Nullable String alternative) {
        return this.getAdaptedString(path, ",", alternative);
    }

    @Override
    public @Nullable String getString(@Nullable String path) {
        return this.getAdaptedString(path, ",", null);
    }

    @Override
    public boolean isString(@Nullable String path) {
        return this.get(path, false) instanceof String;
    }

    private interface StringParser<T> {
        @Nullable
        T parse(@NotNull String value);
    }

    private @Nullable <T> T parseValue(@NotNull Object value, @NotNull StringParser<T> parser) {
        if (value instanceof Integer number) return parser.parse(Integer.toString(number));
        if (value instanceof Long number) return parser.parse(Long.toString(number));
        if (value instanceof Double number) return parser.parse(Double.toString(number));
        if (value instanceof Float number) return parser.parse(Float.toString(number));
        if (value instanceof String string) return parser.parse(string);
        return null;
    }

    private @Nullable <T> T parse(@Nullable String path, @NotNull StringParser<T> parser) {

        // Get the value from the path.
        final Object value = this.get(path);

        // Does the value exist?
        if (value == null) return null;

        // Parse the value into the object.
        return this.parseValue(value, parser);
    }

    @Override
    public int getInteger(@Nullable String path, int alternative) {
        final Integer number = this.parse(path, Integer::parseInt);
        return number == null ? alternative : number;
    }

    @Override
    public int getInteger(@Nullable String path) {
        return this.getInteger(path, -1);
    }

    @Override
    public boolean isInteger(@Nullable String path) {
        return this.get(path) instanceof Integer;
    }

    @Override
    public long getLong(@Nullable String path, long alternative) {
        final Long number = this.parse(path, Long::parseLong);
        return number == null ? alternative : number;
    }

    @Override
    public long getLong(@Nullable String path) {
        return this.getLong(path, -1L);
    }

    @Override
    public boolean isLong(@Nullable String path) {
        return this.get(path) instanceof Long;
    }

    @Override
    public double getDouble(@Nullable String path, double alternative) {
        final Double number = this.parse(path, Double::parseDouble);
        return number == null ? alternative : number;
    }

    @Override
    public double getDouble(@Nullable String path) {
        return this.getDouble(path, -1D);
    }

    @Override
    public boolean isDouble(@Nullable String path) {
        return this.get(path) instanceof Double;
    }

    @Override
    public float getFloat(@Nullable String path, float alternative) {
        final Float number = this.parse(path, Float::parseFloat);
        return number == null ? alternative : number;
    }

    @Override
    public float getFloat(@Nullable String path) {
        return this.getFloat(path, -1F);
    }

    @Override
    public boolean isFloat(@Nullable String path) {
        return this.get(path) instanceof Float;
    }

    @Override
    public boolean getBoolean(@Nullable String path, boolean alternative) {

        // Get the value from the path.
        final Object value = this.get(path);

        // Return the value if it is a boolean.
        return value instanceof Boolean ? (Boolean) value : alternative;
    }

    @Override
    public boolean getBoolean(@Nullable String path) {
        return this.getBoolean(path, false);
    }

    @Override
    public boolean isBoolean(@Nullable String path) {
        return this.get(path) instanceof Boolean;
    }

    @Override
    public List<?> getList(@Nullable String path, @Nullable List<?> alternative) {

        // Get the value from the path.
        final Object object = this.get(path);

        // Return the value if it is a list.
        return object instanceof List<?> ? (List<?>) object : alternative;
    }

    @Override
    public @Nullable List<?> getList(@Nullable String path) {
        return this.getList(path, null);
    }

    @Override
    public boolean isList(@Nullable String path) {
        return this.get(path) instanceof List<?>;
    }

    private <T> List<T> getList(@Nullable String path, @Nullable List<T> alternative, @NotNull StringParser<T> parser) {

        // Is the location not a list?
        if (!this.isList(path)) return alternative;

        // Create a new list.
        final List<T> list = new ArrayList<>();

        // Loop though items in the list.
        for (final Object item : this.getList(path, new ArrayList<>())) {

            final T parsed = this.parseValue(item, parser);

            // Is the value not the correct type.
            if (parsed == null) {
                return alternative;
            }

            // Add the value.
            list.add(parsed);
        }

        return list;
    }

    @Override
    public List<String> getListString(@Nullable String path, @Nullable List<String> alternative) {
        return this.getList(path, alternative, (string) -> string);
    }

    @Override
    public @Nullable List<String> getListString(@Nullable String path) {
        return this.getListString(path, null);
    }

    @Override
    public List<Integer> getListInteger(@Nullable String path, @Nullable List<Integer> alternative) {
        return this.getList(path, alternative, Integer::parseInt);
    }

    @Override
    public @Nullable List<Integer> getListInteger(@Nullable String path) {
        return this.getListInteger(path, null);
    }

    @Override
    public List<Long> getListLong(@Nullable String path, @Nullable List<Long> alternative) {
        return this.getList(path, alternative, Long::parseLong);
    }

    @Override
    public @Nullable List<Long> getListLong(@Nullable String path) {
        return this.getListLong(path, null);
    }

    @Override
    public List<Double> getListDouble(@Nullable String path, @Nullable List<Double> alternative) {
        return this.getList(path, alternative, Double::parseDouble);
    }

    @Override
    public @Nullable List<Double> getListDouble(@Nullable String path) {
        return this.getListDouble(path, null);
    }

    @Override
    public List<Float> getListFloat(@Nullable String path, @Nullable List<Float> alternative) {
        return this.getList(path, alternative, Float::parseFloat);
    }

    @Override
    public @Nullable List<Float> getListFloat(@Nullable String path) {
        return this.getListFloat(path, null);
    }

    @Override
    public Map<String, Object> getMap(@Nullable String path, @Nullable Map<String, Object> alternative) {

        // Get the value from the path.
        final Object object = this.get(path);

        // Is the object not a map?
        if (!(object instanceof Map<?, ?> map)) return alternative;

        // Create the new map instance.
        final Map<String, Object> result = new LinkedHashMap<>();

        // Loop though entry's.
        for (Map.Entry<?, ?> entry : map.entrySet()) {

            // Is the key a string?
            if (entry.getKey() instanceof String key) {
                result.put(key, entry.getValue());
                continue;
            }

            // Otherwise, it is not a string to object map.
            return alternative;
        }

        return result;
    }

    @Override
    public @Nullable Map<String, Object> getMap(@Nullable String path) {
        return this.getMap(path, null);
    }

    @Override
    public boolean isMap(@Nullable String path) {
        return this.getMap(path, null) != null;
    }
}
