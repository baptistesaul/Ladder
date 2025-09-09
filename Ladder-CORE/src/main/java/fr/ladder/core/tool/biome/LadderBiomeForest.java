package fr.ladder.core.tool.biome;

import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.BiomeForest;

public class LadderBiomeForest extends BiomeForest {
    
    public LadderBiomeForest(int id, int type) {
        super(id, type);
        this.ah = BiomeBase.getBiome(id).ah;
        this.am = BiomeBase.getBiome(id).am;
        this.ay = false;
    }
}
