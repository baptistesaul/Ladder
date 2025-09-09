package fr.ladder.api.util;

import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author Snowtyy
 **/
public abstract class WorldUtils {
    
    private static Implementation implementation;
    
    public static void setTypeIdAndDataSync(WorldServer world, int x, int y, int z, int blockId, byte date) {
        implementation.setTypeIdAndDataSync(world, x, y, z, blockId, date);
    }
    
    public static void setTypeIdAndDataAsync(WorldServer world, int x, int y, int z, int blockId, byte date) {
        implementation.setTypeIdAndDataAsync(world, x, y, z, blockId, date);
    }
    
    public static void setBlockTypeSync(Block block, Material material, byte data) {
        implementation.setBlockTypeSync(block, material, data);
    }
    
    public static void setBlockTypeAsync(Block block, Material material, byte data) {
        implementation.setBlockTypeAsync(block, material, data);
    }
    
    public static void setBlockTypeSync(Block block, Material material) {
        implementation.setBlockTypeSync(block, material);
    }
    
    public static void setBlockTypeAsync(Block block, Material material) {
        implementation.setBlockTypeAsync(block, material);
    }
    
    public interface Implementation {
        
        void setTypeIdAndDataSync(WorldServer world, int x, int y, int z, int blockId, byte data);
        
        void setTypeIdAndDataAsync(WorldServer world, int x, int y, int z, int blockId, byte data);
        
        void setBlockTypeSync(Block block, Material material, byte data);
        
        void setBlockTypeAsync(Block block, Material material, byte data);
        
        void setBlockTypeSync(Block block, Material material);
        
        void setBlockTypeAsync(Block block, Material material);
        
    }
    
}
