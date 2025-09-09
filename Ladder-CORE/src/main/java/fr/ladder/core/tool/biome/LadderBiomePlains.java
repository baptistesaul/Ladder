package fr.ladder.core.tool.biome;

import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.BiomePlains;

public class LadderBiomePlains extends BiomePlains {
    
    public LadderBiomePlains(int i) {
        super(i);
        this.ah = BiomeBase.PLAINS.ah;
        this.ay = false;
    }
}
