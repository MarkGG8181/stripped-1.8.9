package net.minecraft.world.biome.gen.impl.general;

import java.util.Random;

import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.gen.BiomeGenBase;
import net.minecraft.world.biome.gen.impl.BiomeGenMutated;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;

public class BiomeGenSavanna extends BiomeGenBase {
    private static final WorldGenSavannaTree SAVANNA_TREE = new WorldGenSavannaTree(false);

    public BiomeGenSavanna(int id) {
        super(id);
        this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityHorse.class, 1, 2, 6));
        this.theBiomeDecorator.treesPerChunk = 1;
        this.theBiomeDecorator.flowersPerChunk = 4;
        this.theBiomeDecorator.grassPerChunk = 20;
    }

    public WorldGenAbstractTree genBigTreeChance(Random rand) {
        return rand.nextInt(5) > 0 ? SAVANNA_TREE : this.worldGeneratorTrees;
    }

    protected BiomeGenBase createMutatedBiome(int p_180277_1_) {
        BiomeGenBase biomegenbase = new BiomeGenSavanna.Mutated(p_180277_1_, this);
        biomegenbase.temperature = (this.temperature + 1.0F) * 0.5F;
        biomegenbase.minHeight = this.minHeight * 0.5F + 0.3F;
        biomegenbase.maxHeight = this.maxHeight * 0.5F + 1.2F;
        return biomegenbase;
    }

    public void decorate(World worldIn, Random rand, BlockPos pos) {
        DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.GRASS);

        for (int i = 0; i < 7; ++i) {
            int j = rand.nextInt(16) + 8;
            int k = rand.nextInt(16) + 8;
            int l = rand.nextInt(worldIn.getHeight(pos.add(j, 0, k)).getY() + 32);
            DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(j, l, k));
        }

        super.decorate(worldIn, rand, pos);
    }

    public static class Mutated extends BiomeGenMutated {
        public Mutated(int p_i45382_1_, BiomeGenBase p_i45382_2_) {
            super(p_i45382_1_, p_i45382_2_);
            this.theBiomeDecorator.treesPerChunk = 2;
            this.theBiomeDecorator.flowersPerChunk = 2;
            this.theBiomeDecorator.grassPerChunk = 5;
        }

        public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
            this.topBlock = Blocks.grass.getDefaultState();
            this.fillerBlock = Blocks.dirt.getDefaultState();

            if (noiseVal > 1.75D) {
                this.topBlock = Blocks.stone.getDefaultState();
                this.fillerBlock = Blocks.stone.getDefaultState();
            } else if (noiseVal > -0.5D) {
                this.topBlock = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT);
            }

            this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
        }

        public void decorate(World worldIn, Random rand, BlockPos pos) {
            this.theBiomeDecorator.decorate(worldIn, rand, this, pos);
        }
    }
}
