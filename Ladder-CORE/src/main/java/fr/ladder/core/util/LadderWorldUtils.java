package fr.ladder.core.util;

import fr.ladder.api.injector.annotation.Inject;
import fr.ladder.api.tool.Task;
import fr.ladder.api.util.WorldUtils;
import fr.ladder.core.LadderEngine;
import fr.ladder.core.tool.biome.LadderBiomeForest;
import fr.ladder.core.tool.biome.LadderBiomePlains;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

/**
 * @author Snowtyy
 **/
public class LadderWorldUtils implements WorldUtils.Implementation {

    @Inject
    private static LadderEngine engine;

    public void replaceBiomes() {
        try {
            BiomeBase[] biomes = BiomeBase.getBiomes();
            biomes[BiomeBase.OCEAN.id] = new LadderBiomeForest(BiomeBase.OCEAN.id, 0);
            biomes[BiomeBase.DEEP_OCEAN.id] = new LadderBiomeForest(BiomeBase.DEEP_OCEAN.id, 0);
            biomes[BiomeBase.JUNGLE.id] = new LadderBiomePlains(BiomeBase.JUNGLE.id);
            biomes[BiomeBase.BEACH.id] = new LadderBiomePlains(BiomeBase.BEACH.id);
            biomes[BiomeBase.RIVER.id] = new LadderBiomePlains(BiomeBase.RIVER.id);
        }
        catch(Exception e) {
            engine.catchException("An error occurred while replacing biomes", e);
        }
    }
    
    @Override
    public void setTypeIdAndDataSync(WorldServer world, int x, int y, int z, int blockId, byte data) {
        BlockPosition bp = new BlockPosition(x, y, z);
        IBlockData bd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(blockId + (data << 12));
        
        if(world.setTypeAndData(bp, bd, 2))
            world.notify(bp);
    }
    
    @Override
    public void setTypeIdAndDataAsync(WorldServer world, int x, int y, int z, int blockId, byte data) {
        if(!this.isValid(x, y, z))
            return;
        Chunk nmsChunk = world.getChunkAt(x >> 4, z >> 4);
        
        BlockPosition bp = new BlockPosition(x, y, z);
        IBlockData bd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(blockId + (data << 12));
        
        boolean loaded = world.isLoaded(bp);
        while(!loaded) {
            Task.run(() -> world.getWorld().loadChunk(x >> 4, z >> 4));
            try {
                Thread.sleep(25);
            } catch(Exception ignored) {}
            loaded = world.isLoaded(bp);
        }
        
        ChunkSection cs = nmsChunk.getSections()[y >> 4];
        if (cs == null) {
            cs = new ChunkSection(y >> 4 << 4, !world.worldProvider.o());
            nmsChunk.getSections()[y >> 4] = cs;
        }
        
        if(bd == cs.getType(x & 15, y & 15, z & 15))
            return;
        
        cs.setType(x & 15, y & 15, z & 15, bd);
        if(bd.getBlock() instanceof IContainer)
            world.t(new BlockPosition(x, y, z));
        
        world.notify(new BlockPosition(x, y, z));
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public void setBlockTypeSync(Block block, Material material, byte data) {
        this.setTypeIdAndDataSync(((CraftWorld) block.getWorld()).getHandle(), block.getX(), block.getY(), block.getZ(), material.getId(), data);
    }

    @Override
    public void setBlockTypeSync(Block block, Material material) {
        this.setBlockTypeSync(block, material, (byte) 0);
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public void setBlockTypeAsync(Block block, Material material, byte data) {
        this.setTypeIdAndDataAsync(((CraftWorld) block.getWorld()).getHandle(), block.getX(), block.getY(), block.getZ(), material.getId(), data);
    }
    
    @Override
    public void setBlockTypeAsync(Block block, Material material) {
        this.setBlockTypeAsync(block, material, (byte) 0);
    }
    
    private boolean isValid(int x, int y, int z) {
        return x >= -30000000
                && z >= -30000000
                && x < 30000000
                && z < 30000000
                && y >= 0
                && y < 256;
        
    }
    
}
