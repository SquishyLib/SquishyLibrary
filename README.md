```yaml
name: SquishyLibrary
description: Java configuration and database library.
author: Smuddgge and Contributors
status: In Development
```

# Configuration File

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