```yaml
name: SquishyLibrary
description: Java configuration and database library.
author: Smuddgge and Contributors
status: Beta
```

# Using the Library

[![](https://jitpack.io/v/Smuddgge/SquishyLibrary.svg)](https://jitpack.io/#Smuddgge/SquishyLibrary)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
```xml
<dependency>
    <groupId>com.github.squishylib.SquishyLibrary</groupId>
    <artifactId>common</artifactId>
    <version>Tag</version>
</dependency>

<dependency>
    <groupId>com.github.squishylib.SquishyLibrary</groupId>
    <artifactId>configuration</artifactId>
    <version>Tag</version>
</dependency>

<dependency>
    <groupId>com.github.squishylib.SquishyLibrary</groupId>
    <artifactId>database</artifactId>
    <version>Tag</version>
</dependency>
```

# Configuration File
An easy way of getting and setting values in a config file.
```java
// An example of a configuration file.
Configuration config = new YamlConfiguration(new File("file.yml"));
config.setResourcePath("file.yml"); // The name of the file in the resource folder.
config.load();

// An example of getting a string from the file.
String hello = config.getString("hello", "hello world");

// An example of putting a string into the file.
config.set("hello", "hello again");

// Save the changes to the file.
config.save();
```

# Configuration Factory
A way of creating a config instance.
```java
Configuration config = ConfigurationFactory.createConfiguration(new File("config.yml")).orElse(null);
config.load();
```
```java 
PreparedConfigurationFactory factory = new PreparedConfigurationFactory(
        ConfigurationFactory.YAML, 
        new File("config.yml")
);

Configuration config = factory.create();
config.load();
```

# Configuration Directory
Combines multiple config files into one big one.
```java
// An example of a configuration directory.
ConfigurationDirectory directory = new ConfigurationDirectory(new File("directory"));
directory.addResourcePath("example1.yml"); // Add default resource files.
directory.addResourcePath("example2.yml");
directory.load(true); // Loads all files in directory.

// You can get values from any file as if it was 1 big file.
String string1 = directory.getString("from_file_2", "test");
String string2 = directory.getString("from_file_1", "test");

// Set and save intelligently.
directory.set("from_file_3", "test");
directory.save(true);

// Get the list of files being combined.
List<Configuration> files = directory.getConfigurationFiles(true);
```

# Single Type Configuration Directory
Combines multiple config files into one big one.\
Each key represents a class.
```java
// A type example.
public class Egg implements ConfigurationConvertible<Egg> {

    private final @NotNull String identifier;
    private boolean hasCracked;

    public Egg(@NotNull String identifier) {
        this.identifier = identifier;
    }

    public void setHasCracked(boolean hasCracked) {
        this.hasCracked = hasCracked;
    }

    @Override
    public @NotNull ConfigurationSection convert() {
        ConfigurationSection section = new MemoryConfigurationSection();
        section.set("has_cracked", hasCracked);
        return section;
    }

    @Override
    public @NotNull Egg convert(@NotNull ConfigurationSection section) {
        this.hasCracked = section.getBoolean("has_cracked");
        return this;
    }
}
```
```java
// Directory examples.
SingleTypeConfigurationDirectory<Egg> directory = new SingleTypeConfigurationDirectory<>(new File("directory"), Egg::new, false);
directory.load();

Egg egg = directory.get("first_egg").orElse(null);

List<Egg> eggs = directory.getAll();
        
directory.set("second_egg", new Egg("second_egg"));

directory.remove("third_egg");

boolean contains = directory.contains("fourth_egg");
```
