# Using Local Core Library Build

This project supports using either a local JAR file or a Maven dependency for the DiscordIntegration-Core library.

## Configuration

Edit `gradle.properties` to control which source to use:

```properties
# Set to true to use a local JAR file instead of Maven dependency
use_local_core = false

# Path to local core JAR (relative to project root or absolute)
# Default assumes DiscordIntegration-Core is a sibling directory
local_core_path = ../DiscordIntegration-Core/build/libs/dcintegration-core-3.1.0.jar
```

## Usage

### Using Local Core (Development)

1. Build the DiscordIntegration-Core library:
   ```bash
   cd ../DiscordIntegration-Core
   ./gradlew shadowJar
   ```

2. Set `use_local_core = true` in `gradle.properties`

3. Optionally adjust `local_core_path` if your core JAR is in a different location

4. Build the mod:
   ```bash
   ./gradlew build
   ```

### Using Maven Core (Production/Release)

1. Set `use_local_core = false` in `gradle.properties` (or remove the property)

2. Build the mod:
   ```bash
   ./gradlew build
   ```

## Notes

- When using local core, the build will fail with a clear error message if the JAR file is not found
- The build will print which source it's using (local JAR path or Maven coordinates)
- All platform modules (neoforge, forge, fabric, fabric-like, common) support this feature



