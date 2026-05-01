package com.github.botania_flower_adjust;

import com.github.botania_flower_adjust.config.ConfigHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = BotaniaFlowerAdjust.MODID, name = BotaniaFlowerAdjust.NAME, version = BotaniaFlowerAdjust.VERSION, dependencies = "after:botania", acceptableRemoteVersions = "*")
public class BotaniaFlowerAdjust {
    public static final String MODID = "botania_flower_adjust";
    public static final String NAME = "Botania Flower Adjust";
    public static final String VERSION = "1.0.0";

    @Mod.Instance(MODID)
    public static BotaniaFlowerAdjust instance;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info("Botania Flower Adjust starting...");
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        FlowerGenHandler.register();
        logger.info("Botania Flower Adjust loaded!");
    }
}
