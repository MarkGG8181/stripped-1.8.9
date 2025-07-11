package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class StructureOceanMonument extends MapGenStructure
{
    private int spacing;
    private int separation;
    public static final List<BiomeGenBase> WATER_BIOMES = Arrays.<BiomeGenBase>asList(new BiomeGenBase[] {BiomeGenBase.ocean, BiomeGenBase.deepOcean, BiomeGenBase.river, BiomeGenBase.frozenOcean, BiomeGenBase.frozenRiver});
    private static final List<BiomeGenBase.SpawnListEntry> MONUMENT_ENEMIES = Lists.<BiomeGenBase.SpawnListEntry>newArrayList();

    public StructureOceanMonument()
    {
        this.spacing = 32;
        this.separation = 5;
    }

    public StructureOceanMonument(Map<String, String> p_i45608_1_)
    {
        this();

        for (Entry<String, String> entry : p_i45608_1_.entrySet())
        {
            if (((String)entry.getKey()).equals("spacing"))
            {
                this.spacing = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.spacing, 1);
            }
            else if (((String)entry.getKey()).equals("separation"))
            {
                this.separation = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.separation, 1);
            }
        }
    }

    public String getStructureName()
    {
        return "Monument";
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= this.spacing - 1;
        }

        if (chunkZ < 0)
        {
            chunkZ -= this.spacing - 1;
        }

        int k = chunkX / this.spacing;
        int l = chunkZ / this.spacing;
        Random random = this.worldObj.setRandomSeed(k, l, 10387313);
        k = k * this.spacing;
        l = l * this.spacing;
        k = k + (random.nextInt(this.spacing - this.separation) + random.nextInt(this.spacing - this.separation)) / 2;
        l = l + (random.nextInt(this.spacing - this.separation) + random.nextInt(this.spacing - this.separation)) / 2;

        if (i == k && j == l)
        {
            if (this.worldObj.getWorldChunkManager().getBiomeGenerator(new BlockPos(i * 16 + 8, 64, j * 16 + 8), (BiomeGenBase)null) != BiomeGenBase.deepOcean)
            {
                return false;
            }

            boolean flag = this.worldObj.getWorldChunkManager().areBiomesViable(i * 16 + 8, j * 16 + 8, 29, WATER_BIOMES);

            if (flag)
            {
                return true;
            }
        }

        return false;
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new StructureOceanMonument.StartMonument(this.worldObj, this.rand, chunkX, chunkZ);
    }

    public List<BiomeGenBase.SpawnListEntry> getScatteredFeatureSpawnList()
    {
        return MONUMENT_ENEMIES;
    }

    static
    {
        MONUMENT_ENEMIES.add(new BiomeGenBase.SpawnListEntry(EntityGuardian.class, 1, 2, 4));
    }

    public static class StartMonument extends StructureStart
    {
        private Set<ChunkCoordIntPair> processed = Sets.<ChunkCoordIntPair>newHashSet();
        private boolean wasCreated;

        public StartMonument()
        {
        }

        public StartMonument(World worldIn, Random p_i45607_2_, int p_i45607_3_, int p_i45607_4_)
        {
            super(p_i45607_3_, p_i45607_4_);
            this.func_175789_b(worldIn, p_i45607_2_, p_i45607_3_, p_i45607_4_);
        }

        private void func_175789_b(World worldIn, Random p_175789_2_, int p_175789_3_, int p_175789_4_)
        {
            p_175789_2_.setSeed(worldIn.getSeed());
            long i = p_175789_2_.nextLong();
            long j = p_175789_2_.nextLong();
            long k = (long)p_175789_3_ * i;
            long l = (long)p_175789_4_ * j;
            p_175789_2_.setSeed(k ^ l ^ worldIn.getSeed());
            int i1 = p_175789_3_ * 16 + 8 - 29;
            int j1 = p_175789_4_ * 16 + 8 - 29;
            EnumFacing enumfacing = EnumFacing.Plane.HORIZONTAL.random(p_175789_2_);
            this.components.add(new StructureOceanMonumentPieces.MonumentBuilding(p_175789_2_, i1, j1, enumfacing));
            this.updateBoundingBox();
            this.wasCreated = true;
        }

        public void generateStructure(World worldIn, Random rand, StructureBoundingBox structurebb)
        {
            if (!this.wasCreated)
            {
                this.components.clear();
                this.func_175789_b(worldIn, rand, this.getChunkPosX(), this.getChunkPosZ());
            }

            super.generateStructure(worldIn, rand, structurebb);
        }

        public boolean func_175788_a(ChunkCoordIntPair pair)
        {
            return this.processed.contains(pair) ? false : super.func_175788_a(pair);
        }

        public void func_175787_b(ChunkCoordIntPair pair)
        {
            super.func_175787_b(pair);
            this.processed.add(pair);
        }

        public void writeToNBT(NBTTagCompound tagCompound)
        {
            super.writeToNBT(tagCompound);
            NBTTagList nbttaglist = new NBTTagList();

            for (ChunkCoordIntPair chunkcoordintpair : this.processed)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setInteger("X", chunkcoordintpair.chunkXPos);
                nbttagcompound.setInteger("Z", chunkcoordintpair.chunkZPos);
                nbttaglist.appendTag(nbttagcompound);
            }

            tagCompound.setTag("Processed", nbttaglist);
        }

        public void readFromNBT(NBTTagCompound tagCompound)
        {
            super.readFromNBT(tagCompound);

            if (tagCompound.hasKey("Processed", 9))
            {
                NBTTagList nbttaglist = tagCompound.getTagList("Processed", 10);

                for (int i = 0; i < nbttaglist.tagCount(); ++i)
                {
                    NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                    this.processed.add(new ChunkCoordIntPair(nbttagcompound.getInteger("X"), nbttagcompound.getInteger("Z")));
                }
            }
        }
    }
}
