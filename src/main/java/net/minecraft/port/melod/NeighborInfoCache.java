package net.minecraft.port.melod;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.gen.BiomeGenBase;

public class NeighborInfoCache implements IBlockAccess {
    private final IBlockAccess world;
    private final BlockPos centerPos;
    private final IBlockState[] neighborStates = new IBlockState[6];

    public NeighborInfoCache(IBlockAccess world, BlockPos pos) {
        this.world = world;
        this.centerPos = pos;

        for (EnumFacing facing : EnumFacing.values()) {
            this.neighborStates[facing.getIndex()] = world.getBlockState(pos.offset(facing));
        }
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        int dx = pos.getX() - this.centerPos.getX();
        int dy = pos.getY() - this.centerPos.getY();
        int dz = pos.getZ() - this.centerPos.getZ();
        
        if (dx * dx + dy * dy + dz * dz == 1) {
            if (dx == 1) return this.neighborStates[EnumFacing.EAST.getIndex()];
            if (dx == -1) return this.neighborStates[EnumFacing.WEST.getIndex()];
            if (dy == 1) return this.neighborStates[EnumFacing.UP.getIndex()];
            if (dy == -1) return this.neighborStates[EnumFacing.DOWN.getIndex()];
            if (dz == 1) return this.neighborStates[EnumFacing.SOUTH.getIndex()];
            if (dz == -1) return this.neighborStates[EnumFacing.NORTH.getIndex()];
        }
        
        return this.world.getBlockState(pos);
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) { return this.world.getTileEntity(pos); }
    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) { return this.world.getCombinedLight(pos, lightValue); }
    @Override
    public boolean isAirBlock(BlockPos pos) { return this.world.isAirBlock(pos); }
    @Override
    public BiomeGenBase getBiomeGenForCoords(BlockPos pos) { return this.world.getBiomeGenForCoords(pos); }
    @Override
    public boolean extendedLevelsInChunkCache() { return this.world.extendedLevelsInChunkCache(); }
    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) { return this.world.getStrongPower(pos, direction); }
    @Override
    public WorldType getWorldType() { return this.world.getWorldType(); }
}