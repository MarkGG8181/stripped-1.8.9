package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenRavine extends MapGenBase
{
    private float[] rs = new float[1024];

    protected void func_180707_a(long p_180707_1_, int p_180707_3_, int p_180707_4_, ChunkPrimer p_180707_5_, double p_180707_6_, double p_180707_8_, double p_180707_10_, float p_180707_12_, float p_180707_13_, float p_180707_14_, int p_180707_15_, int p_180707_16_, double p_180707_17_)
    {
        Random random = new Random(p_180707_1_);
        double d0 = (double)(p_180707_3_ * 16 + 8);
        double d1 = (double)(p_180707_4_ * 16 + 8);
        float f = 0.0F;
        float f1 = 0.0F;

        if (p_180707_16_ <= 0)
        {
            int i = this.range * 16 - 16;
            p_180707_16_ = i - random.nextInt(i / 4);
        }

        boolean flag1 = false;

        if (p_180707_15_ == -1)
        {
            p_180707_15_ = p_180707_16_ / 2;
            flag1 = true;
        }

        float f2 = 1.0F;

        for (int j = 0; j < 256; ++j)
        {
            if (j == 0 || random.nextInt(3) == 0)
            {
                f2 = 1.0F + random.nextFloat() * random.nextFloat() * 1.0F;
            }

            this.rs[j] = f2 * f2;
        }

        for (; p_180707_15_ < p_180707_16_; ++p_180707_15_)
        {
            double d9 = 1.5D + (double)(MathHelper.sin((float)p_180707_15_ * (float)Math.PI / (float)p_180707_16_) * p_180707_12_ * 1.0F);
            double d2 = d9 * p_180707_17_;
            d9 = d9 * ((double)random.nextFloat() * 0.25D + 0.75D);
            d2 = d2 * ((double)random.nextFloat() * 0.25D + 0.75D);
            float f3 = MathHelper.cos(p_180707_14_);
            float f4 = MathHelper.sin(p_180707_14_);
            p_180707_6_ += (double)(MathHelper.cos(p_180707_13_) * f3);
            p_180707_8_ += (double)f4;
            p_180707_10_ += (double)(MathHelper.sin(p_180707_13_) * f3);
            p_180707_14_ = p_180707_14_ * 0.7F;
            p_180707_14_ = p_180707_14_ + f1 * 0.05F;
            p_180707_13_ += f * 0.05F;
            f1 = f1 * 0.8F;
            f = f * 0.5F;
            f1 = f1 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            f = f + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

            if (flag1 || random.nextInt(4) != 0)
            {
                double d3 = p_180707_6_ - d0;
                double d4 = p_180707_10_ - d1;
                double d5 = (double)(p_180707_16_ - p_180707_15_);
                double d6 = (double)(p_180707_12_ + 2.0F + 16.0F);

                if (d3 * d3 + d4 * d4 - d5 * d5 > d6 * d6)
                {
                    return;
                }

                if (p_180707_6_ >= d0 - 16.0D - d9 * 2.0D && p_180707_10_ >= d1 - 16.0D - d9 * 2.0D && p_180707_6_ <= d0 + 16.0D + d9 * 2.0D && p_180707_10_ <= d1 + 16.0D + d9 * 2.0D)
                {
                    int k2 = MathHelper.floor_double(p_180707_6_ - d9) - p_180707_3_ * 16 - 1;
                    int k = MathHelper.floor_double(p_180707_6_ + d9) - p_180707_3_ * 16 + 1;
                    int l2 = MathHelper.floor_double(p_180707_8_ - d2) - 1;
                    int l = MathHelper.floor_double(p_180707_8_ + d2) + 1;
                    int i3 = MathHelper.floor_double(p_180707_10_ - d9) - p_180707_4_ * 16 - 1;
                    int i1 = MathHelper.floor_double(p_180707_10_ + d9) - p_180707_4_ * 16 + 1;

                    if (k2 < 0)
                    {
                        k2 = 0;
                    }

                    if (k > 16)
                    {
                        k = 16;
                    }

                    if (l2 < 1)
                    {
                        l2 = 1;
                    }

                    if (l > 248)
                    {
                        l = 248;
                    }

                    if (i3 < 0)
                    {
                        i3 = 0;
                    }

                    if (i1 > 16)
                    {
                        i1 = 16;
                    }

                    boolean flag2 = false;

                    for (int j1 = k2; !flag2 && j1 < k; ++j1)
                    {
                        for (int k1 = i3; !flag2 && k1 < i1; ++k1)
                        {
                            for (int l1 = l + 1; !flag2 && l1 >= l2 - 1; --l1)
                            {
                                if (l1 >= 0 && l1 < 256)
                                {
                                    IBlockState iblockstate = p_180707_5_.getBlockState(j1, l1, k1);

                                    if (iblockstate.getBlock() == Blocks.flowing_water || iblockstate.getBlock() == Blocks.water)
                                    {
                                        flag2 = true;
                                    }

                                    if (l1 != l2 - 1 && j1 != k2 && j1 != k - 1 && k1 != i3 && k1 != i1 - 1)
                                    {
                                        l1 = l2;
                                    }
                                }
                            }
                        }
                    }

                    if (!flag2)
                    {
                        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                        for (int j3 = k2; j3 < k; ++j3)
                        {
                            double d10 = ((double)(j3 + p_180707_3_ * 16) + 0.5D - p_180707_6_) / d9;

                            for (int i2 = i3; i2 < i1; ++i2)
                            {
                                double d7 = ((double)(i2 + p_180707_4_ * 16) + 0.5D - p_180707_10_) / d9;
                                boolean flag = false;

                                if (d10 * d10 + d7 * d7 < 1.0D)
                                {
                                    for (int j2 = l; j2 > l2; --j2)
                                    {
                                        double d8 = ((double)(j2 - 1) + 0.5D - p_180707_8_) / d2;

                                        if ((d10 * d10 + d7 * d7) * (double)this.rs[j2 - 1] + d8 * d8 / 6.0D < 1.0D)
                                        {
                                            IBlockState iblockstate1 = p_180707_5_.getBlockState(j3, j2, i2);

                                            if (iblockstate1.getBlock() == Blocks.grass)
                                            {
                                                flag = true;
                                            }

                                            if (iblockstate1.getBlock() == Blocks.stone || iblockstate1.getBlock() == Blocks.dirt || iblockstate1.getBlock() == Blocks.grass)
                                            {
                                                if (j2 - 1 < 10)
                                                {
                                                    p_180707_5_.setBlockState(j3, j2, i2, Blocks.flowing_lava.getDefaultState());
                                                }
                                                else
                                                {
                                                    p_180707_5_.setBlockState(j3, j2, i2, Blocks.air.getDefaultState());

                                                    if (flag && p_180707_5_.getBlockState(j3, j2 - 1, i2).getBlock() == Blocks.dirt)
                                                    {
                                                        blockpos$mutableblockpos.set(j3 + p_180707_3_ * 16, 0, i2 + p_180707_4_ * 16);
                                                        p_180707_5_.setBlockState(j3, j2 - 1, i2, this.worldObj.getBiomeGenForCoords(blockpos$mutableblockpos).topBlock);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (flag1)
                        {
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Recursively called by generate()
     */
    protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int p_180701_4_, int p_180701_5_, ChunkPrimer chunkPrimerIn)
    {
        if (this.rand.nextInt(50) == 0)
        {
            double d0 = (double)(chunkX * 16 + this.rand.nextInt(16));
            double d1 = (double)(this.rand.nextInt(this.rand.nextInt(40) + 8) + 20);
            double d2 = (double)(chunkZ * 16 + this.rand.nextInt(16));
            int i = 1;

            for (int j = 0; j < i; ++j)
            {
                float f = this.rand.nextFloat() * (float)Math.PI * 2.0F;
                float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float f2 = (this.rand.nextFloat() * 2.0F + this.rand.nextFloat()) * 2.0F;
                this.func_180707_a(this.rand.nextLong(), p_180701_4_, p_180701_5_, chunkPrimerIn, d0, d1, d2, f2, f, f1, 0, 0, 3.0D);
            }
        }
    }
}
