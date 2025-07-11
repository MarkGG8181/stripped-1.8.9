package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenHellLava extends WorldGenerator
{
    private final Block block;
    private final boolean insideRock;

    public WorldGenHellLava(Block p_i45453_1_, boolean p_i45453_2_)
    {
        this.block = p_i45453_1_;
        this.insideRock = p_i45453_2_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        if (worldIn.getBlockState(position.up()).getBlock() != Blocks.netherrack)
        {
            return false;
        }
        else if (worldIn.getBlockState(position).getBlock().getMaterial() != Material.air && worldIn.getBlockState(position).getBlock() != Blocks.netherrack)
        {
            return false;
        }
        else
        {
            int i = 0;

            if (worldIn.getBlockState(position.west()).getBlock() == Blocks.netherrack)
            {
                ++i;
            }

            if (worldIn.getBlockState(position.east()).getBlock() == Blocks.netherrack)
            {
                ++i;
            }

            if (worldIn.getBlockState(position.north()).getBlock() == Blocks.netherrack)
            {
                ++i;
            }

            if (worldIn.getBlockState(position.south()).getBlock() == Blocks.netherrack)
            {
                ++i;
            }

            if (worldIn.getBlockState(position.down()).getBlock() == Blocks.netherrack)
            {
                ++i;
            }

            int j = 0;

            if (worldIn.isAirBlock(position.west()))
            {
                ++j;
            }

            if (worldIn.isAirBlock(position.east()))
            {
                ++j;
            }

            if (worldIn.isAirBlock(position.north()))
            {
                ++j;
            }

            if (worldIn.isAirBlock(position.south()))
            {
                ++j;
            }

            if (worldIn.isAirBlock(position.down()))
            {
                ++j;
            }

            if (!this.insideRock && i == 4 && j == 1 || i == 5)
            {
                worldIn.setBlockState(position, this.block.getDefaultState(), 2);
                worldIn.forceBlockUpdateTick(this.block, position, rand);
            }

            return true;
        }
    }
}
