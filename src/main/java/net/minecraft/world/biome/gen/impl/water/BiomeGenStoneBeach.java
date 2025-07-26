package net.minecraft.world.biome.gen.impl.water;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.gen.BiomeGenBase;

public class BiomeGenStoneBeach extends BiomeGenBase {
    public BiomeGenStoneBeach(int id) {
        super(id);
        this.spawnableCreatureList.clear();
        this.topBlock = Blocks.stone.getDefaultState();
        this.fillerBlock = Blocks.stone.getDefaultState();
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.deadBushPerChunk = 0;
        this.theBiomeDecorator.sugarcanePerChunk = 0;
        this.theBiomeDecorator.cactiPerChunk = 0;
    }
}
