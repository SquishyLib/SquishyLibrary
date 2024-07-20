package com.github.smuddgge.squishy.configuration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A section of configuration.
 * <p>
 * Uses a map of string to object.
 */
public interface ConfigurationSection {

    /**
     * The original configuration section that
     * this section was found in.
     *
     * @return The base section.
     */
    @NotNull
    ConfigurationSection getBaseSection();

    /**
     * Used to get the location of a configuration section
     * at a higher level then this one from the original
     * configuration file or base section.
     * <li>If you are getting the path from base to base, it will return a empty string.</li>
     *
     * @param path The location from this configuration section
     *             to another higher section.
     * @return The dot path.
     */
    @NotNull
    String getPathFromBase(@Nullable String path);

    /**
     * Used to get the location of this configuration section
     * from the original configuration file or base section.
     * <li>If this is the base section it will return a empty string.</li>
     *
     * @return The dot path.
     */
    @NotNull
    String getPathFromBase();

    /**
     * Used to get the configuration section as a map.
     *
     * @return A map representing the configuration section.
     */
    @NotNull
    Map<String, Object> getMap();

    /**
     * Used to get the section that contains this section.
     * <p>
     * If this is the base section it will return empty.
     *
     * @return The optional section below.
     */
    @NotNull
    Optional<ConfigurationSection> getSectionBelow();

    /**
     * Used to remove all keys and values from this section.
     *
     * @return This instance.
     */
    @NotNull
    ConfigurationSection clear();

    /**
     * Used to set or create a value in the section
     * and save it in the base section.
     * <li>If the value is null it will remove the key and value from the section.</li>
     *
     * @param path  The location to set the value.
     * @param value The value to be set in the config.
     * @return This instance.
     */
    @NotNull
    ConfigurationSection set(@NotNull String path, @Nullable Object value);

    /**
     * Used to set or create a value in this section
     * and not save it to the base section.
     * <li>If the value is null it will remove the key and value.</li>
     * <li>If the path is null this section will be replaced with the value.</li>
     *
     * @param path  The location to set the value.
     * @param value The value to be set in the section.
     * @return This instance.
     */
    @NotNull
    ConfigurationSection setInSection(@NotNull String path, @Nullable Object value);

    /**
     * Used to remove a key and value from the section.
     *
     * @param path the path to the key.
     * @return This instance.
     */
    default @NotNull ConfigurationSection remove(@NotNull String path) {
        return this.set(path, null);
    }

    /**
     * Used to check if a value is supported.
     * If a value is not supported, this library will still attempt to
     * convert it into a map object using {@link com.google.gson.Gson}.
     *
     * @param value The value to check.
     * @return If the value is supported.
     */
    boolean isValueSupported(@NotNull Object value);

    /**
     * Used to get any value from this section or
     * any higher sections.
     * <li>If the path does not exist it will return the alternative value.</li>
     * <li>If the path is null it will return this section as a map.</li>
     *
     * @param path        The location of the value.
     * @param alternative The alternative value.
     * @return The requested value.
     */
    Object get(@Nullable String path, @Nullable Object alternative);

    /**
     * Used to get any value from this section or
     * any higher sections.
     * <li>If the path does not exist it will return null.</li>
     * <li>If the path is null it will return this section as a map.</li>
     *
     * @param path The location of the value.
     * @return The requested value.
     */
    @Nullable
    Object get(@Nullable String path);

    /**
     * Used to fill a class with the configuration values.
     * The section will be mapped on to the class.
     * <li>If the path does not exist the alternative value will be returned.</li>
     *
     * @param <T>         The class type.
     * @param path        The location of the value.
     * @param clazz       The class to map the values onto.
     * @param alternative The alternative value.
     * @return An instance of the class.
     */
    <T> T getClass(@Nullable String path, @NotNull Class<T> clazz, @Nullable T alternative);

    /**
     * Used to fill a class with the configuration values.
     * The section will be mapped on to the class.
     * <li>If the path does not exist null will be returned.</li>
     *
     * @param <T>   The class type.
     * @param path  The location of the value.
     * @param clazz The class to map the values onto.
     * @return An instance of the class.
     */
    @Nullable
    <T> T getClass(@Nullable String path, @NotNull Class<T> clazz);

    /**
     * Used to get a configuration section.
     * <li>If the path does not exist it will create a temporary empty section.</li>
     * <li>If the path is null it will return this instance.</li>
     *
     * @param path The location of the section from this section.
     * @return An instance of the section.
     */
    @NotNull
    ConfigurationSection getSection(@Nullable String path);

    /**
     * This will return the map keys.
     * <li>If there are no keys it will return a empty list.</li>
     *
     * @return The section's keys.
     */
    @NotNull
    List<@NotNull String> getKeys();

    /**
     * This will return the map keys of the section indicated by the path.
     * <li>If there are no keys it will return a empty list.</li>
     * <li>If the path is null it will return this sections keys.</li>
     *
     * @param path The location of the section.
     * @return The section's keys.
     */
    @NotNull
    List<@NotNull String> getKeys(@Nullable String path);

    /**
     * Used to get the value as a string.
     * <li>If the path does not exist it will return the alternative.</li>
     * <li>If the path is null it will return the alternative.</li>
     * <li>If the value is a list it will join the items with the join string.</li>
     * <li>If the value is a integer it will use the {@link Integer#parseInt(String)}.</li>
     * <li>If the value is a long it will use the {@link Long#parseLong(String)}.</li>
     * <li>If the value is a double it will use the {@link Double#parseDouble(String)}.</li>
     * <li>If the value is a float it will use the {@link Float#parseFloat(String)}.</li>
     * <li>If the value is any other object it will use {@link Object#toString()}.</li>
     *
     * @param path        The location of the value.
     * @param join        The string to join lists with.
     * @param alternative The alternative value.
     * @return The adapted string.
     */
    String getAdaptedString(@Nullable String path, @NotNull String join, @Nullable String alternative);

    /**
     * Used to get the value as a string.
     * <li>If the path does not exist it will return null.</li>
     * <li>If the path is null it will return null.</li>
     * <li>If the value is a list it will join the items with the join string.</li>
     * <li>If the value is a integer it will use the {@link Integer#parseInt(String)}.</li>
     * <li>If the value is a long it will use the {@link Long#parseLong(String)}.</li>
     * <li>If the value is a double it will use the {@link Double#parseDouble(String)}.</li>
     * <li>If the value is a float it will use the {@link Float#parseFloat(String)}.</li>
     * <li>If the value is any other object it will use {@link Object#toString()}.</li>
     *
     * @param path The location of the value.
     * @param join The string to join lists with.
     * @return The adapted string.
     */
    @Nullable
    String getAdaptedString(@Nullable String path, @NotNull String join);

    /**
     * Used to get a string.
     * <li>This method will also attempt to adapt non strings into strings using
     * {@link ConfigurationSection#getAdaptedString(String, String, String)}</li>
     * <li>If the path does not exist it will return the alternative value.</li>
     * <li>If the value is not a string it will return the alternative value.</li>
     * <li>If the path is null it will return the alternative value.
     * This is because this is a section and not a string.</li>
     *
     * @param path        The location of the string in the section.
     * @param alternative The alternative value.
     * @return The requested string.
     */
    String getString(@Nullable String path, @Nullable String alternative);

    /**
     * Used to get a string.
     * <li>This method will also attempt to adapt non strings into strings using
     * {@link ConfigurationSection#getAdaptedString(String, String, String)}</li>
     * <li>If the path does not exist it will return null.</li>
     * <li>If the value is not a string it will return null.</li>
     * <li>If the path is null it will return null.
     * This is because this is a section and not a string.</li>
     *
     * @param path The location of the string in the section.
     * @return The requested string.
     */
    @Nullable
    String getString(@Nullable String path);

    /**
     * Used to check if a value is a string.
     * <li>If the path is null it will return false.
     * This is because this is a section not a string.</li>
     *
     * @param path The instance of the path.
     * @return True if the value is a string.
     */
    boolean isString(@Nullable String path);

    /**
     * Used to get an integer.
     * <li>If the path does not exist it will return the alternative value.</li>
     * <li>If the value is a long it will convert the long into a integer.</li>
     * <li>If the value is a double it will convert the double into a integer.</li>
     * <li>If the value is a float it will convert the float into a integer.</li>
     * <li>If the value is not a integer and not listed above it will return the alternative value..</li>
     *
     * @param path        The location of the integer in the section.
     * @param alternative The alternative value.
     * @return The requested integer.
     */
    int getInteger(@Nullable String path, int alternative);

    /**
     * Used to get an integer.
     * Will attempt to convert doubles and longs to int.
     * <li>If the path does not exist it will return the alternative value.</li>
     * <li>If the value is a long it will convert the long into a integer.</li>
     * <li>If the value is a double it will convert the double into a integer.</li>
     * <li>If the value is a float it will convert the float into a integer.</li>
     * <li>If the value is not a integer and not listed above it will return the alternative value..</li>
     *
     * @param path The location of the integer in the section.
     * @return The requested integer.
     */
    int getInteger(@Nullable String path);

    /**
     * Used to check if a value is an integer.
     *
     * @param path The location from this section.
     * @return True if the value is an integer.
     */
    boolean isInteger(@Nullable String path);

    /**
     * Used to get a long.
     * <li>If the value is a integer it will be converted into a long</li>
     * <li>If the value is a double it will be converted into a long</li>
     * <li>If the value is a float it will be converted into a long</li>
     * <li>If the path does not exist it will return the alternative value.</li>
     * <li>If the value is not a long it will return the alternative value.</li>
     *
     * @param path        The location of the long in the section.
     * @param alternative The alternative value.
     * @return The requested long.
     */
    long getLong(@Nullable String path, long alternative);

    /**
     * Used to get a long.
     * <li>If the value is a integer it will be converted into a long</li>
     * <li>If the value is a double it will be converted into a long</li>
     * <li>If the value is a float it will be converted into a long</li>
     * <li>If the path does not exist it will return -1.</li>
     * <li>If the value is not a long it will return -1.</li>
     *
     * @param path The location of the long in the section.
     * @return The requested long.
     */
    long getLong(@Nullable String path);

    /**
     * Used to check if a value is a long.
     *
     * @param path The location from this section.
     * @return True if the value is a long.
     */
    boolean isLong(@Nullable String path);

    /**
     * Used to get a double.
     * <li>If the value is a integer it will be converted into a double</li>
     * <li>If the value is a long it will be converted into a double</li>
     * <li>If the value is a float it will be converted into a double</li>
     * <li>If the path does not exist it will return the alternative value.</li>
     * <li>If the value is not a double it will return the alternative value.</li>
     *
     * @param path        The location of the double in the section.
     * @param alternative The alternative value.
     * @return The requested double.
     */
    double getDouble(@Nullable String path, double alternative);

    /**
     * Used to get a double.
     * <li>If the value is a integer it will be converted into a double</li>
     * <li>If the value is a long it will be converted into a double</li>
     * <li>If the value is a float it will be converted into a double</li>
     * <li>If the path does not exist it will return -1.</li>
     * <li>If the value is not a double it will return -1.</li>
     *
     * @param path The location of the double in the section.
     * @return The requested double.
     */
    double getDouble(@Nullable String path);

    /**
     * Used to check if a value is a double.
     *
     * @param path The location from this section.
     * @return True if the value is a double.
     */
    boolean isDouble(@Nullable String path);

    /**
     * Used to get a float.
     * <li>If the value is a integer it will be converted into a float</li>
     * <li>If the value is a long it will be converted into a float</li>
     * <li>If the value is a double it will be converted into a float</li>
     * <li>If the path does not exist it will return the alternative value.</li>
     * <li>If the value is not a float it will return the alternative value.</li>
     *
     * @param path        The location of the float in the section.
     * @param alternative The alternative value.
     * @return The requested float.
     */
    float getFloat(@Nullable String path, double alternative);

    /**
     * Used to get a float.
     * <li>If the value is a integer it will be converted into a float</li>
     * <li>If the value is a long it will be converted into a float</li>
     * <li>If the value is a double it will be converted into a float</li>
     * <li>If the path does not exist it will return -1.</li>
     * <li>If the value is not a integer it will return -1.</li>
     *
     * @param path The location of the float in the section.
     * @return The requested float.
     */
    float getFloat(@Nullable String path);

    /**
     * Used to check if a value is a float.
     *
     * @param path The location from this section.
     * @return True if the value is a float.
     */
    boolean isFloat(@Nullable String path);

    /**
     * Used to get a boolean.
     * <li>If the path does not exist it will return the alternative value.</li>
     * <li>If the value is not a boolean it will return the alternative value.</li>
     *
     * @param path        The location of the boolean in the section.
     * @param alternative The alternative value.
     * @return The requested boolean.
     */
    boolean getBoolean(@Nullable String path, boolean alternative);

    /**
     * Used to get a boolean.
     * <li>If the path does not exist it will return false.</li>
     * <li>If the value is not a boolean it will return false.</li>
     *
     * @param path The location of the boolean in the section.
     * @return The requested boolean.
     */
    boolean getBoolean(@Nullable String path);

    /**
     * Used to check if a value is a boolean.
     *
     * @param path The location from the section.
     * @return True if the value is a boolean.
     */
    boolean isBoolean(@Nullable String path);

    /**
     * Used to get a list.
     * <li>If the path does not exist it will return the alternative value.</li>
     * <li>If the value is not a list it will return the alternative value.</li>
     *
     * @param path        The location of the list in this section.
     * @param alternative The alternative list.
     * @return The requested list.
     */
    List<?> getList(@Nullable String path, @Nullable List<?> alternative);

    /**
     * Used to get a list.
     * <li>If the path does not exist it will return null.</li>
     * <li>If the value is not a list it will return null.</li>
     *
     * @param path The location of the list in this section.
     * @return The requested list.
     */
    @Nullable
    List<?> getList(@Nullable String path);

    /**
     * Used to check if a value is a list.
     *
     * @param path The location from this section.
     * @return True if the value is a list.
     */
    boolean isList(@Nullable String path);

    /**
     * Used to get a list of strings.
     * <li>If the path does not exist it will return the alternative value.</li>
     * <li>If the value is not a string list it will return the alternative value.</li>x
     *
     * @param path        The location of the string list in this section.
     * @param alternative The alternative string list.
     * @return The requested list of strings.
     */
    List<String> getListString(@Nullable String path, @Nullable List<String> alternative);

    /**
     * Used to get a list of strings.
     * <li>If the path does not exist it will return null.</li>
     * <li>If the value is not a string list it will return null.</li>
     *
     * @param path The location of the string list in this section.
     * @return The requested list of strings.
     */
    @Nullable
    List<String> getListString(@Nullable String path);

    /**
     * Used to get a list of integers.
     * <li>If the value is a long list it will be converted into a integer list.</li>
     * <li>If the value is a double list it will be converted into a integer list.</li>
     * <li>If the value is a float list it will be converted into a integer list.</li>
     * <li>If the path does not exist it will return the alternative value.</li>
     * <li>If the value is not a integer list it will return the alternative value.</li>
     *
     * @param path        The location of the integer list in this section.
     * @param alternative The alternative value.
     * @return The requested list of integers.
     */
    List<Integer> getListInteger(@Nullable String path, @Nullable List<Integer> alternative);

    /**
     * Used to get a list of integers.
     * <li>If the value is a long list it will be converted into a integer list.</li>
     * <li>If the value is a double list it will be converted into a integer list.</li>
     * <li>If the value is a float list it will be converted into a integer list.</li>
     * <li>If the path does not exist it will return null.</li>
     * <li>If the value is not a integer list it will return null.</li>
     *
     * @param path The location of the integer list in this configuration section.
     * @return The requested list of integers.
     */
    @Nullable
    List<Integer> getListInteger(@Nullable String path);

    /**
     * Used to get a list of longs.
     * <li>If the value is a integer list it will be converted into a long list.</li>
     * <li>If the value is a double list it will be converted into a long list.</li>
     * <li>If the value is a float list it will be converted into a long list.</li>
     * <li>If the path does not exist it will return the alternative value.</li>
     * <li>If the value is not a integer list it will return the alternative value.</li>
     *
     * @param path        The location of the long list in this section.
     * @param alternative The alternative value.
     * @return The requested list of longs.
     */
    List<Long> getListLong(@Nullable String path, @Nullable List<Integer> alternative);

    /**
     * Used to get a list of longs.
     * <li>If the value is a integer list it will be converted into a long list.</li>
     * <li>If the value is a double list it will be converted into a long list.</li>
     * <li>If the value is a float list it will be converted into a long list.</li>
     * <li>If the path does not exist it will return null.</li>
     * <li>If the value is not a integer list it will return null.</li>
     *
     * @param path The location of the long list in this configuration section.
     * @return The requested list of longs.
     */
    @Nullable
    List<Long> getListLong(@Nullable String path);

    /**
     * Used to get a list of doubles.
     * <li>If the value is a integer list it will be converted into a double list.</li>
     * <li>If the value is a long list it will be converted into a double list.</li>
     * <li>If the value is a float list it will be converted into a double list.</li>
     * <li>If the path does not exist it will return the alternative value.</li>
     * <li>If the value is not a integer list it will return the alternative value.</li>
     *
     * @param path        The location of the double list in this section.
     * @param alternative The alternative value.
     * @return The requested list of doubles.
     */
    List<Double> getListDouble(@Nullable String path, @Nullable List<Integer> alternative);

    /**
     * Used to get a list of doubles.
     * <li>If the value is a integer list it will be converted into a double list.</li>
     * <li>If the value is a long list it will be converted into a double list.</li>
     * <li>If the value is a float list it will be converted into a double list.</li>
     * <li>If the path does not exist it will return null.</li>
     * <li>If the value is not a integer list it will return null.</li>
     *
     * @param path The location of the double list in this configuration section.
     * @return The requested list of doubles.
     */
    @Nullable
    List<Double> getListDouble(@Nullable String path);

    /**
     * Used to get a list of floats.
     * <li>If the value is a integer list it will be converted into a float list.</li>
     * <li>If the value is a long list it will be converted into a float list.</li>
     * <li>If the value is a double list it will be converted into a float list.</li>
     * <li>If the path does not exist it will return the alternative value.</li>
     * <li>If the value is not a integer list it will return the alternative value.</li>
     *
     * @param path        The location of the float list in this section.
     * @param alternative The alternative value.
     * @return The requested list of floats.
     */
    List<Float> getListFloat(@Nullable String path, @Nullable List<Integer> alternative);

    /**
     * Used to get a list of floats.
     * <li>If the value is a integer list it will be converted into a float list.</li>
     * <li>If the value is a long list it will be converted into a float list.</li>
     * <li>If the value is a double list it will be converted into a float list.</li>
     * <li>If the path does not exist it will return null.</li>
     * <li>If the value is not a integer list it will return null.</li>
     *
     * @param path The location of the float list in this configuration section.
     * @return The requested list of floats.
     */
    @Nullable
    List<Float> getListFloat(@Nullable String path);

    /**
     * Used to get the configuration section as a map.
     * <li>If the path does not exist it will return the alternative value.</li>
     * <li>If the value is not a map it will return the alternative value.</li>
     *
     * @param alternative The alternative value.
     * @return A map representing the configuration section.
     */
    Map<String, Object> getMap(@Nullable String path, @Nullable Map<String, Object> alternative);

    /**
     * Used to get the configuration section as a map.
     * <li>If the path does not exist it will return null.</li>
     * <li>If the value is not a map it will return null.</li>
     *
     * @return A map representing the configuration section.
     */
    Map<String, Object> getMap(@Nullable String path);

    /**
     * Used to check if a value is a map.
     *
     * @param path The instance of the path.
     * @return True if the value is a map.
     */
    boolean isMap(@Nullable String path);
}
