package com.github.smuddgge.squishy.configuration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * A section of configuration.
 * <p>
 * Uses a map of string to object.
 */
public interface ConfigurationSection {

    /**
     * Used to get the location of this configuration section
     * from the original configuration file or base section.
     *
     * @return The dot path.
     */
    @NotNull
    String getPathFromBase();

    /**
     * Used to get the location of a configuration section
     * at a higher level then this one from the original
     * configuration file or base section.
     *
     * @param path The location from this configuration section
     *             to another higher section.
     * @return The dot path.
     */
    @NotNull
    String getPathFromBase(String path);

    /**
     * Used to set a value in this section and apply it to the base section.
     * <li>If the value is null, this section will be removed</li>
     *
     * @param value The value to set the configuration section to.
     * @return This instance.
     */
    @NotNull
    ConfigurationSection set(@Nullable Object value);

    /**
     * Used to set or create a value in the section
     * and save it in the base section.
     * <li>If the value is null it will remove the key and value.</li>
     * <li>If the path is null this section will be replaced with the value.</li>
     *
     * @param path  The location to set the value.
     * @param value The value to be set in the config.
     * @return This instance.
     */
    @NotNull
    ConfigurationSection set(@Nullable String path, @Nullable Object value);

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
    ConfigurationSection setInSection(@Nullable String path, @Nullable Object value);

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
     *
     * @param path The location of the section from this section.
     * @return An instance of the section.
     */
    @NotNull
    ConfigurationSection getSection(String path);

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
     * Used to get a string.
     * <li>If the path does not exist it will return the alternative value.</li>
     * <li>If the value is not a string it will return the alternative value.</li>
     * <li>If the path is null it will return the alternative value.
     * This is because this is a section and not a string.</li>
     *
     * @param path        The location of the string in the configuration section.
     * @param alternative The alternative value.
     * @return The requested string.
     */
    String getString(@Nullable String path, @Nullable String alternative);

    /**
     * Used to get a string.
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

    // TODO

    /**
     * Used to get an integer.
     * Will attempt to convert doubles and longs to int.
     * <ul>
     *     <li>If the path does not exist it will return -1.</li>
     *     <li>If the value is not a integer it will return -1.</li>
     * </ul>
     *
     * @param path The location of the integer in the configuration section.
     * @return The requested integer.
     */
    int getInteger(@Nullable String path);

    /**
     * Used to check if a value is an integer.
     * This won't return true if its double or long but
     * this library will try to convert it if the
     * {@link this#getInteger(String)} is called.
     *
     * @param path The instance of the path.
     * @return True if the value is an integer.
     */
    boolean isInteger(String path);

    /**
     * Used to get a long.
     * <ul>
     *      <li>If the path does not exist it will return the alternative value.</li>
     *      <li>If the value is not a integer it will return the alternative value.</li>
     *     <li>If the value is a integer it will be converted into a long</li>
     * </ul>
     *
     * @param path        The location of the long in the configuration section.
     * @param alternative The alternative value.
     * @return The requested long.
     */
    long getLong(String path, long alternative);

    /**
     * Used to get a long.
     * <ul>
     *     <li>If the path does not exist it will return -1.</li>
     *     <li>If the value is not a integer it will return -1.</li>
     *     <li>If the value is a integer it will be converted into a long</li>
     * </ul>
     *
     * @param path The location of the long in the configuration section.
     * @return The requested long.
     */
    long getLong(String path);

    /**
     * Used to check if a value is a long.
     *
     * @param path The instance of the path.
     * @return True if the value is a long.
     */
    boolean isLong(String path);

    /**
     * Used to get a double.
     * <ul>
     *      <li>If the path does not exist it will return the alternative value.</li>
     *      <li>If the value is not a integer it will return the alternative value.</li>
     *      <li>If the value is a integer it will be converted into a double</li>
     * </ul>
     *
     * @param path        The location of the double in the configuration section.
     * @param alternative The alternative value.
     * @return The requested double.
     */
    double getDouble(String path, double alternative);

    /**
     * Used to get a double.
     * <ul>
     *     <li>If the path does not exist it will return -1.</li>
     *     <li>If the value is not a integer it will return -1.</li>
     *     <li>If the value is a integer it will be converted into a double</li>
     * </ul>
     *
     * @param path The location of the double in the configuration section.
     * @return The requested double.
     */
    double getDouble(String path);

    /**
     * Used to check if a value is a double.
     *
     * @param path The instance of the path.
     * @return True if the value is a double.
     */
    boolean isDouble(String path);

    /**
     * Used to get a boolean.
     * <ul>
     *     <li>If the path does not exist it will return the alternative value.</li>
     *     <li>If the value is not a boolean it will return the alternative value.</li>
     * </ul>
     *
     * @param path        The location of the boolean in the configuration section.
     * @param alternative The alternative value.
     * @return The requested boolean.
     */
    boolean getBoolean(String path, boolean alternative);

    /**
     * Used to get a boolean.
     * <ul>
     *     <li>If the path does not exist it will return false.</li>
     *     <li>If the value is not a boolean it will return false.</li>
     * </ul>
     *
     * @param path The location of the boolean in the configuration section.
     * @return The requested boolean.
     */
    boolean getBoolean(String path);

    /**
     * Used to check if a value is a boolean.
     *
     * @param path The instance of the path.
     * @return True if the value is a boolean.
     */
    boolean isBoolean(String path);

    /**
     * Used to get a list.
     * <ul>
     *     <li>If the path does not exist it will return the alternative value.</li>
     *     <li>If the value is not a list it will return the alternative value.</li>
     * </ul>
     *
     * @param path        The location of the list in this configuration section.
     * @param alternative The alternative list.
     * @return The requested list.
     */
    List<?> getList(String path, List<?> alternative);

    /**
     * Used to get a list.
     * <ul>
     *     <li>If the path does not exist it will return null.</li>
     *     <li>If the value is not a list it will return null.</li>
     * </ul>
     *
     * @param path The location of the list in this configuration section.
     * @return The requested list.
     */
    List<?> getList(String path);

    /**
     * Used to check if a value is a list.
     *
     * @param path The instance of the path.
     * @return True if the value is a list.
     */
    boolean isList(String path);

    /**
     * Used to get a list of strings.
     * <ul>
     *     <li>If the path does not exist it will return the alternative value.</li>
     *     <li>If the value is not a string list it will return the alternative value.</li>
     * </ul>
     *
     * @param path        The location of the string list in this configuration section.
     * @param alternative The alternative string list.
     * @return The requested list of strings.
     */
    List<String> getListString(String path, List<String> alternative);

    /**
     * Used to get a list of strings.
     * <ul>
     *     <li>If the path does not exist it will return null.</li>
     *     <li>If the value is not a string list it will return null.</li>
     * </ul>
     *
     * @param path The location of the string list in this configuration section.
     * @return The requested list of strings.
     */
    List<String> getListString(String path);

    /**
     * Used to get a list of integers.
     * <ul>
     *     <li>If the path does not exist it will return the alternative value.</li>
     *     <li>If the value is not a integer list it will return the alternative value.</li>
     * </ul>
     *
     * @param path        The location of the integer list in this configuration section.
     * @param alternative The alternative value.
     * @return The requested list of integers.
     */
    List<Integer> getListInteger(String path, List<Integer> alternative);

    /**
     * Used to get a list of integers.
     * <ul>
     *     <li>If the path does not exist it will return null.</li>
     *     <li>If the value is not a integer list it will return null.</li>
     * </ul>
     *
     * @param path The location of the integer list in this configuration section.
     * @return The requested list of integers.
     */
    List<Integer> getListInteger(String path);

    /**
     * Used to get the configuration section as a map.
     *
     * @return A map representing the configuration section.
     */
    Map<String, Object> getMap();

    /**
     * Used to get the configuration section as a map.
     * <ul>
     *     <li>If the path does not exist it will return the alternative value.</li>
     *     <li>If the value is not a map it will return the alternative value.</li>
     * </ul>
     *
     * @param alternative The alternative value.
     * @return A map representing the configuration section.
     */
    Map<String, Object> getMap(String path, Map<String, Object> alternative);

    /**
     * Used to get the configuration section as a map.
     * <ul>
     *     <li>If the path does not exist it will return null.</li>
     *     <li>If the value is not a map it will return null.</li>
     * </ul>
     *
     * @return A map representing the configuration section.
     */
    Map<String, Object> getMap(String path);

    /**
     * Used to check if a value is a map.
     *
     * @param path The instance of the path.
     * @return True if the value is a map.
     */
    boolean isMap(String path);
}
