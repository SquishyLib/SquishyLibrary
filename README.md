```yaml
name: SquishyLibrary
description: Java tools, configuration and database library.
author: Smuddgge and Contributors
```

# Java Dependency

[![](https://jitpack.io/v/squishylib/SquishyLibrary.svg)](https://jitpack.io/#squishylib/SquishyLibrary)

Below is examples for **maven**, there is a **gradle** example if you click the button above.
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
```xml
<!-- Just Tools -->
<dependency>
    <groupId>com.github.squishylib.SquishyLibrary</groupId>
    <artifactId>common</artifactId>
    <version>Tag</version>
</dependency>

<!-- Just Configuration and tools -->
<dependency>
    <groupId>com.github.squishylib.SquishyLibrary</groupId>
    <artifactId>configuration</artifactId>
    <version>Tag</version>
</dependency>

<!-- Tools, Configuration and Database Library -->
<dependency>
    <groupId>com.github.squishylib.SquishyLibrary</groupId>
    <artifactId>database</artifactId>
    <version>Tag</version>
</dependency>
```

```xml
<!-- Tools, Configuration and Database Library -->
<dependency>
    <groupId>com.github.squishylib</groupId>
    <artifactId>SquishyLibrary</artifactId>
    <version>Tag</version>
</dependency>
```

# Configuration Library
An easy way of getting and setting values in a config file.
```java
Configuration config = new YamlConfiguration(new File("file.yml"));
config.setResourcePath("file.yml"); // A file in the resource folder to copy.
config.load();

String value = config.getString("hello", "hello world");

config.set("hello", "A diffrent value");

config.save();
```
For more infomation visit the [wiki by clicking here](https://smuddgge.gitbook.io/squishy-library/configuration/configuration-file).

# Database Library

First [click here to add the database config](https://github.com/SquishyLib/SquishyLibrary/blob/main/database/src/main/resources/database.yml) to your project.\
Next load the configuration file and connect to the database.

```java
Configuration databaseConfig = new YamlConfiguration(
    this.getPlugin().getDataFolder(),
    "database.yml"
);
databaseConfig.setResourcePath("database.yml");
databaseConfig.load();

Database database = new DatabaseBuilder(
    this.databaseConfig
).create().connect();
```

Here is an example of creating a record.

```java
public class ExampleRecord implements Record<Example2Record> {

    public static final @NotNull String IDENTIFIER_KEY = "identifier";
    public static final @NotNull String STRING_KEY = "value";

    private final @Field(IDENTIFIER_KEY) @Primary @NotNull String identifier;
    private @Field(STRING_KEY) String string;
    
    public ExampleRecord(@NotNull String identifier) {
        this.identifier = identifier;
    }
    
    @Override
    public @NotNull ConfigurationSection convert() {
        MemoryConfigurationSection section = new MemoryConfigurationSection();
        section.set(STRING_KEY, string);
        return section;
    }

    @Override
    public @NotNull Example2Record convert(
        @NotNull ConfigurationSection section) {
        
        this.string = section.getString(STRING_KEY);
        return this;
    }
}
```

Here is the corresponding table for the example record.
```java
public class ExampleTable extends Table<ExampleRecord> {

    public static final @NotNull String TABLE_NAME = "example";

    @Override
    public @NotNull String getName() {
        return ExampleTable.TABLE_NAME;
    }

    @Override
    public @NotNull ExampleRecord createEmpty(@NotNull PrimaryFieldMap identifiers) {
        return new ExampleRecord(
            identifiers.getString(ExampleRecord.IDENTIFIER_KEY)
        );
    }
}
```

Once you have created a table and record, add it to the database.

```java
database.createTable(new ExampleTable());
```

You can now query the database table.

```java
ExampleRecord record = database.getTable(ExampleTable.class)
    .getFirstRecord();
```