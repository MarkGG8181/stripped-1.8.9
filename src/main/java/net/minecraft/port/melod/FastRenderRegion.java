package net.minecraft.port.melod;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.gen.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class FastRenderRegion implements IBlockAccess {
    private static final IBlockState AIR_STATE = Blocks.air.getDefaultState();

    private final Chunk[][] chunks;
    private final World world;
    private final int centerX;
    private final int centerZ;

    public FastRenderRegion(World world, BlockPos centerPos) {
        this.world = world;
        this.centerX = centerPos.getX() >> 4;
        this.centerZ = centerPos.getZ() >> 4;

        this.chunks = new Chunk[3][3];
        for (int x = 0; x < 3; ++x) {
            for (int z = 0; z < 3; ++z) {
                this.chunks[x][z] = world.getChunkFromChunkCoords(this.centerX + x - 1, this.centerZ + z - 1);
            }
        }
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        if (pos.getY() < 0 || pos.getY() >= 256) {
            return AIR_STATE;
        }

        int cacheX = (pos.getX() >> 4) - this.centerX + 1;
        int cacheZ = (pos.getZ() >> 4) - this.centerZ + 1;

        if (cacheX >= 0 && cacheX < 3 && cacheZ >= 0 && cacheZ < 3) {
            Chunk chunk = this.chunks[cacheX][cacheZ];
            if (chunk != null) {
                return chunk.getBlockState(pos);
            }
        }

        return AIR_STATE;
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        int cacheX = (pos.getX() >> 4) - this.centerX + 1;
        int cacheZ = (pos.getZ() >> 4) - this.centerZ + 1;
        if (cacheX >= 0 && cacheX < 3 && cacheZ >= 0 && cacheZ < 3) {
            Chunk chunk = this.chunks[cacheX][cacheZ];
            if (chunk != null) {
                return chunk.getTileEntity(pos, Chunk.EnumCreateEntityType.QUEUED);
            }
        }
        return null;
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return this.world.getCombinedLight(pos, lightValue);
    }

    @Override
    public WorldType getWorldType() {
        return this.world.getWorldType();
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return this.getBlockState(pos).getBlock() == Blocks.air;
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(BlockPos pos) {
        return this.world.getBiomeGenForCoords(pos);
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return this.getBlockState(pos).getBlock().getStrongPower(this, pos, this.getBlockState(pos), direction);
    }

    @Override
    public boolean extendedLevelsInChunkCache() {
        return false;
    }
}