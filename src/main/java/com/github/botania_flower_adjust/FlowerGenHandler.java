package com.github.botania_flower_adjust;

import com.github.botania_flower_adjust.BotaniaFlowerAdjust;
import com.github.botania_flower_adjust.config.ConfigHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FlowerGenHandler {
    
    private static boolean registered = false;
    private static final Map<Integer, Set<Long>> processedChunksByWorld = new HashMap<>();
    private static final Map<Integer, Boolean> dirtyByWorld = new HashMap<>();
    private static int tickCounter = 0;
    
    public static void register() {
        if (registered) return;
        registered = true;
        MinecraftForge.EVENT_BUS.register(new FlowerGenHandler());
        BotaniaFlowerAdjust.logger.info("FlowerGenHandler registered to EVENT_BUS");
    }
    
    private File getDataFile(World world) {
        File worldDir = world.getSaveHandler().getWorldDirectory();
        return new File(worldDir, "data/botania_flower_adjust_processed_chunks.dat");
    }
    
    private void loadProcessedChunks(World world) {
        int dimensionId = world.provider.getDimension();
        
        if (processedChunksByWorld.containsKey(dimensionId)) {
            return;
        }
        
        Set<Long> chunks = new HashSet<>();
        
        if (dimensionId == 0) {
            File dataFile = getDataFile(world);
            if (dataFile.exists()) {
                try {
                    NBTTagCompound compound = CompressedStreamTools.read(dataFile);
                    NBTTagList list = compound.getTagList("chunks", 4);
                    for (int i = 0; i < list.tagCount(); i++) {
                        chunks.add(((NBTTagLong) list.get(i)).getLong());
                    }
                    BotaniaFlowerAdjust.logger.info("Loaded {} processed chunks from file", chunks.size());
                } catch (IOException e) {
                    BotaniaFlowerAdjust.logger.error("Failed to load processed chunks", e);
                }
            }
        }
        
        processedChunksByWorld.put(dimensionId, chunks);
        dirtyByWorld.put(dimensionId, false);
    }
    
    private void saveProcessedChunks(World world) {
        int dimensionId = world.provider.getDimension();
        
        if (dimensionId != 0) {
            return;
        }
        
        Boolean dirty = dirtyByWorld.get(dimensionId);
        if (dirty == null || !dirty) {
            return;
        }
        
        Set<Long> chunks = processedChunksByWorld.get(dimensionId);
        if (chunks == null || chunks.isEmpty()) {
            return;
        }
        
        File dataFile = getDataFile(world);
        dataFile.getParentFile().mkdirs();
        
        try {
            NBTTagCompound compound = new NBTTagCompound();
            NBTTagList list = new NBTTagList();
            for (Long chunkKey : chunks) {
                list.appendTag(new NBTTagLong(chunkKey));
            }
            compound.setTag("chunks", list);
            CompressedStreamTools.write(compound, dataFile);
            BotaniaFlowerAdjust.logger.info("Saved {} processed chunks to file", chunks.size());
            dirtyByWorld.put(dimensionId, false);
        } catch (IOException e) {
            BotaniaFlowerAdjust.logger.error("Failed to save processed chunks", e);
        }
    }
    
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) {
            loadProcessedChunks(event.getWorld());
            BotaniaFlowerAdjust.logger.info("World loaded (dimension {})", event.getWorld().provider.getDimension());
        }
    }
    
    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) {
        if (!event.getWorld().isRemote) {
            saveProcessedChunks(event.getWorld());
        }
    }
    
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (!event.getWorld().isRemote) {
            saveProcessedChunks(event.getWorld());
            int dimensionId = event.getWorld().provider.getDimension();
            processedChunksByWorld.remove(dimensionId);
            dirtyByWorld.remove(dimensionId);
            BotaniaFlowerAdjust.logger.info("World unloaded (dimension {})", dimensionId);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.world.isRemote || event.phase != TickEvent.Phase.END) {
            return;
        }
        
        if (!ConfigHandler.enableFlowerGenControl) {
            return;
        }
        
        tickCounter++;
        if (tickCounter % 20 != 0) {
            return;
        }
        
        if (!BotaniaFlowerHelper.isInitialized()) {
            BotaniaFlowerHelper.init();
        }
        
        if (!BotaniaFlowerHelper.isInitialized()) {
            return;
        }
        
        World world = event.world;
        
        if (!(world instanceof WorldServer)) {
            return;
        }
        
        int dimensionId = world.provider.getDimension();
        Set<Long> processedChunks = processedChunksByWorld.get(dimensionId);
        
        if (processedChunks == null) {
            loadProcessedChunks(world);
            processedChunks = processedChunksByWorld.get(dimensionId);
            if (processedChunks == null) {
                return;
            }
        }
        
        WorldServer worldServer = (WorldServer) world;
        boolean anyChanges = false;
        
        for (Chunk chunk : worldServer.getChunkProvider().getLoadedChunks()) {
            long chunkKey = getChunkKey(chunk.x, chunk.z);
            
            if (processedChunks.contains(chunkKey)) {
                continue;
            }
            
            int removedCount = 0;
            int checkedCount = 0;
            
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int topY = chunk.getHeightValue(x, z);
                    
                    for (int y = Math.max(0, topY - 15); y <= Math.min(255, topY + 5); y++) {
                        BlockPos pos = new BlockPos(chunk.x * 16 + x, y, chunk.z * 16 + z);
                        IBlockState state = chunk.getBlockState(x, y, z);
                        
                        if (!BotaniaFlowerHelper.isBotaniaFlower(state)) {
                            continue;
                        }
                        
                        checkedCount++;
                        
                        EnumDyeColor color = BotaniaFlowerHelper.getFlowerColor(state);
                        if (color == null) {
                            continue;
                        }
                        
                        Biome biome = world.getBiome(pos);
                        String biomeName = biome.getRegistryName() != null ? biome.getRegistryName().toString() : biome.getBiomeName();
                        
                        boolean allowed = ConfigHandler.isFlowerGenAllowed(color, biomeName);
                        
                        if (!allowed) {
                            world.setBlockToAir(pos);
                            removedCount++;
                        }
                    }
                }
            }
            
            if (checkedCount > 0 || removedCount > 0) {
                BotaniaFlowerAdjust.logger.debug("Chunk [{}, {}]: checked {} flowers, removed {}", chunk.x, chunk.z, checkedCount, removedCount);
            }
            
            processedChunks.add(chunkKey);
            anyChanges = true;
        }
        
        if (anyChanges) {
            dirtyByWorld.put(dimensionId, true);
        }
    }
    
    private long getChunkKey(int x, int z) {
        return (long) x & 0xFFFFFFFFL | ((long) z & 0xFFFFFFFFL) << 32;
    }
}
