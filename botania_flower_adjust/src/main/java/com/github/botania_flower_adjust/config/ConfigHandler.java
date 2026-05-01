package com.github.botania_flower_adjust.config;

import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigHandler {
    private static Configuration config;
    
    public static boolean enableFlowerGenControl = true;
    public static boolean enableMushroomGenControl = true;
    
    private static final Map<EnumDyeColor, FlowerConfig> flowerConfigs = new EnumMap<>(EnumDyeColor.class);
    
    public static final String[] DEFAULT_BIOMES = new String[0];

    public static void init(File configFile) {
        config = new Configuration(configFile);
        syncConfig();
    }

    private static void syncConfig() {
        enableFlowerGenControl = config.getBoolean(
            "enableFlowerGenControl",
            Configuration.CATEGORY_GENERAL,
            true,
            "Enable the flower generation control feature."
        );
        
        enableMushroomGenControl = config.getBoolean(
            "enableMushroomGenControl",
            Configuration.CATEGORY_GENERAL,
            true,
            "Enable the mystical mushroom generation control feature."
        );
        
        for (EnumDyeColor color : EnumDyeColor.values()) {
            String colorName = getColorName(color);
            String categoryName = "flower_" + colorName;
            
            boolean enabled = config.getBoolean(
                "enabled",
                categoryName,
                true,
                "Whether " + colorName + " mystical flowers can generate."
            );
            
            boolean useWhitelist = config.getBoolean(
                "useWhitelistMode",
                categoryName,
                false,
                "Mode for biome list.\n" +
                "true = WHITELIST mode: Only biomes in the list will generate " + colorName + " flowers.\n" +
                "false = BLACKLIST mode: Biomes in the list will NOT generate " + colorName + " flowers."
            );
            
            String[] biomes = config.getStringList(
                "biomes",
                categoryName,
                DEFAULT_BIOMES,
                "Biome list for " + colorName + " flowers.\n" +
                "Use biome registry names (e.g., minecraft:plains, minecraft:desert).\n" +
                "In WHITELIST mode: Only these biomes will generate flowers.\n" +
                "In BLACKLIST mode: These biomes will NOT generate flowers.\n" +
                "Leave empty to allow all biomes (in BLACKLIST mode) or none (in WHITELIST mode)."
            );
            
            flowerConfigs.put(color, new FlowerConfig(enabled, useWhitelist, biomes));
        }
        
        if (config.hasChanged()) {
            config.save();
        }
    }
    
    public static boolean isFlowerGenAllowed(EnumDyeColor color, String biomeRegistryName) {
        if (!enableFlowerGenControl) {
            return true;
        }
        
        FlowerConfig cfg = flowerConfigs.get(color);
        if (cfg == null || !cfg.enabled) {
            return false;
        }
        
        if (cfg.useWhitelistMode) {
            return cfg.biomesSet.contains(biomeRegistryName);
        } else {
            return !cfg.biomesSet.contains(biomeRegistryName);
        }
    }
    
    public static boolean isMushroomGenAllowed() {
        return enableMushroomGenControl;
    }
    
    private static String getColorName(EnumDyeColor color) {
        switch (color) {
            case WHITE: return "white";
            case ORANGE: return "orange";
            case MAGENTA: return "magenta";
            case LIGHT_BLUE: return "light_blue";
            case YELLOW: return "yellow";
            case LIME: return "lime";
            case PINK: return "pink";
            case GRAY: return "gray";
            case SILVER: return "light_gray";
            case CYAN: return "cyan";
            case PURPLE: return "purple";
            case BLUE: return "blue";
            case BROWN: return "brown";
            case GREEN: return "green";
            case RED: return "red";
            case BLACK: return "black";
            default: return color.getName();
        }
    }
    
    public static void save() {
        config.save();
    }
    
    private static class FlowerConfig {
        final boolean enabled;
        final boolean useWhitelistMode;
        final Set<String> biomesSet;
        
        FlowerConfig(boolean enabled, boolean useWhitelistMode, String[] biomes) {
            this.enabled = enabled;
            this.useWhitelistMode = useWhitelistMode;
            this.biomesSet = new HashSet<>(Arrays.asList(biomes));
        }
    }
}
