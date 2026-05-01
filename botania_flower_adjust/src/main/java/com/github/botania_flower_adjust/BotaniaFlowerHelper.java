package com.github.botania_flower_adjust;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.Field;

public class BotaniaFlowerHelper {
    
    private static boolean initialized = false;
    private static Block flowerBlock = null;
    private static IProperty<EnumDyeColor> colorProperty = null;
    
    public static boolean isInitialized() {
        return initialized;
    }
    
    public static Block getFlowerBlock() {
        return flowerBlock;
    }
    
    @SuppressWarnings("unchecked")
    public static void init() {
        if (initialized) return;
        initialized = true;
        
        BotaniaFlowerAdjust.logger.info("Initializing BotaniaFlowerHelper...");
        
        try {
            Class<?> modBlocksClass = Class.forName("vazkii.botania.common.block.ModBlocks");
            BotaniaFlowerAdjust.logger.info("Found ModBlocks class");
            
            Field flowerField = modBlocksClass.getField("flower");
            flowerBlock = (Block) flowerField.get(null);
            
            if (flowerBlock == null) {
                BotaniaFlowerAdjust.logger.error("Botania flower block is null!");
                return;
            }
            BotaniaFlowerAdjust.logger.info("Got flower block: {}", flowerBlock);
            
            IBlockState defaultState = flowerBlock.getDefaultState();
            
            for (IProperty<?> prop : defaultState.getPropertyKeys()) {
                if (prop.getName().equals("color")) {
                    colorProperty = (IProperty<EnumDyeColor>) prop;
                    BotaniaFlowerAdjust.logger.info("Found color property!");
                    break;
                }
            }
            
            if (colorProperty == null) {
                BotaniaFlowerAdjust.logger.error("Color property not found!");
                return;
            }
            
            BotaniaFlowerAdjust.logger.info("BotaniaFlowerHelper initialized SUCCESSFULLY!");
        } catch (Exception e) {
            BotaniaFlowerAdjust.logger.error("Failed to initialize BotaniaFlowerHelper", e);
        }
    }
    
    public static boolean isBotaniaFlower(IBlockState state) {
        return flowerBlock != null && state.getBlock() == flowerBlock;
    }
    
    public static EnumDyeColor getFlowerColor(IBlockState state) {
        if (colorProperty == null) {
            return null;
        }
        
        try {
            return state.getValue(colorProperty);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static boolean placeFlower(World world, BlockPos pos, EnumDyeColor color) {
        if (!initialized || flowerBlock == null || colorProperty == null) {
            return false;
        }
        
        try {
            IBlockState defaultState = flowerBlock.getDefaultState();
            IBlockState coloredState = defaultState.withProperty(colorProperty, color);
            
            if (!flowerBlock.canPlaceBlockAt(world, pos)) {
                return false;
            }
            
            world.setBlockState(pos, coloredState, 2);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
