# Botania Flower Adjust

A Minecraft Forge mod for 1.12.2 that allows users (modpack authors) to control the generation of mystical flowers from Botania in specified biomes through configuration.

一个 Minecraft Forge 1.12.2 模组，允许用户（整合包作者）通过配置文件控制植物魔法中神秘花在指定群系里的生成。

---

## Features / 功能特性

- Control which biomes each color of mystical flower can generate in / 控制每种颜色的神秘花可以在哪些群系生成
- Whitelist or blacklist mode for each flower color / 每种颜色的花支持白名单或黑名单模式
- Per-flower-color configuration / 每种颜色的花独立配置
- Persistent chunk processing cache to avoid re-checking / 持久化区块处理缓存，避免重复检查

---

## Requirements / 运行要求

- Minecraft 1.12.2
- Forge 14.23.5.2860 or higher / 或更高版本
- Botania (any version for 1.12.2) / 任意 1.12.2 版本

---

## Configuration / 配置说明

After first launch, configuration file will be generated at:  
首次运行后，配置文件将生成在：

```
config/botania_flower_adjust.cfg
```

---

### Example Configuration / 配置示例

```toml
general {
    # Enable the flower generation control feature.
    # 启用神秘花生成控制功能。
    B:enableFlowerGenControl=true
}

flower_red {
    # Whether red mystical flowers can generate.
    # 是否允许红色神秘花生成。
    B:enabled=true

    # Mode for biome list.
    # true = WHITELIST mode: Only biomes in the list will generate flowers.
    # true = 白名单模式：只有列表中的群系会生成花。
    # false = BLACKLIST mode: Biomes in the list will NOT generate flowers.
    # false = 黑名单模式：列表中的群系不会生成花。
    B:useWhitelistMode=false

    # Biome list for red flowers.
    # 红色花的群系列表。
    S:biomes <
        minecraft:desert
        minecraft:mesa
     >
}
```

---

### Whitelist Mode Example / 白名单模式示例

Only generate red flowers in desert and mesa:  
只在沙漠和恶地生成红色花：

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

---

### Blacklist Mode Example / 黑名单模式示例

Do NOT generate red flowers in desert and mesa:  
不在沙漠和恶地生成红色花：

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

---

### Disable a Color Completely / 完全禁用某种颜色

```toml
flower_black {
    # Whether black mystical flowers can generate.
    # 是否允许黑色神秘花生成。
    B:enabled=false
}
```

---

## Building / 构建

```bash
./gradlew build
```

The compiled JAR will be in `build/libs/`.  
编译后的 JAR 文件位于 `build/libs/`。

---

## Author / 作者

nyonio

---

## Credits / 致谢

- Botania by Vazkii
