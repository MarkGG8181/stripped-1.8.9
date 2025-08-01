package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.gen.BiomeGenBase;

public class GenLayerRiver extends GenLayer
{
    public GenLayerRiver(long p_i2128_1_, GenLayer p_i2128_3_)
    {
        super(p_i2128_1_);
        super.parent = p_i2128_3_;
    }

    /**
     * Returns a list of integer values generated by this layer. These may be interpreted as temperatures, rainfall
     * amounts, or biomeList[] indices based on the particular GenLayer subclass.
     */
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight)
    {
        int i = areaX - 1;
        int j = areaY - 1;
        int k = areaWidth + 2;
        int l = areaHeight + 2;
        int[] aint = this.parent.getInts(i, j, k, l);
        int[] aint1 = IntCache.getIntCache(areaWidth * areaHeight);

        for (int i1 = 0; i1 < areaHeight; ++i1)
        {
            for (int j1 = 0; j1 < areaWidth; ++j1)
            {
                int k1 = this.func_151630_c(aint[j1 + 0 + (i1 + 1) * k]);
                int l1 = this.func_151630_c(aint[j1 + 2 + (i1 + 1) * k]);
                int i2 = this.func_151630_c(aint[j1 + 1 + (i1 + 0) * k]);
                int j2 = this.func_151630_c(aint[j1 + 1 + (i1 + 2) * k]);
                int k2 = this.func_151630_c(aint[j1 + 1 + (i1 + 1) * k]);

                if (k2 == k1 && k2 == i2 && k2 == l1 && k2 == j2)
                {
                    aint1[j1 + i1 * areaWidth] = -1;
                }
                else
                {
                    aint1[j1 + i1 * areaWidth] = BiomeGenBase.river.biomeID;
                }
            }
        }

        return aint1;
    }

    private int func_151630_c(int p_151630_1_)
    {
        return p_151630_1_ >= 2 ? 2 + (p_151630_1_ & 1) : p_151630_1_;
    }
}
