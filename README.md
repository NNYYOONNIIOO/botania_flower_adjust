# Botania Flower Adjust

A Minecraft Forge mod for 1.12.2 that allows users (modpack authors) to control the generation of mystical flowers from Botania in specified biomes through configuration.

## Features

- Control which biomes each color of mystical flower can generate in
- Whitelist or blacklist mode for each flower color
- Per-flower-color configuration
- Persistent chunk processing cache to avoid re-checking

## Requirements

- Minecraft 1.12.2
- Forge 14.23.5.2860 or higher
- Botania (any version for 1.12.2)

## Configuration

After first launch, configuration file will be generated at:
```
config/botania_flower_adjust.cfg
```

### Example Configuration

```toml
general {
    # Enable the flower generation control feature.
    B:enableFlowerGenControl=true
}

flower_red {
    # Whether red mystical flowers can generate.
    B:enabled=true

    # Mode for biome list.
    # true = WHITELIST mode: Only biomes in the list will generate flowers.
    # false = BLACKLIST mode: Biomes in the list will NOT generate flowers.
    B:useWhitelistMode=false

    # Biome list for red flowers.
    S:biomes <
        minecraft:desert
        minecraft:mesa
     >
}
```

### Whitelist Mode Example

Only generate red flowers in desert and mesa:
```toml
flower_red {
    B:enabled=true
    B:useWhitelistMode=true
    S:biomes <
        minecraft:desert
        minecraft:mesa
     >
}
```

### Blacklist Mode Example

Do NOT generate red flowers in desert and mesa:
```toml
flower_red {
    B:enabled=true
    B:useWhitelistMode=false
    S:biomes <
        minecraft:desert
        minecraft:mesa
     >
}
```

### Disable a Color Completely

```toml
flower_black {
    B:enabled=false
}
```

## Building

```bash
./gradlew build
```

The compiled JAR will be in `build/libs/`.

## Author

nyonio

## Credits

- Botania by Vazkii
