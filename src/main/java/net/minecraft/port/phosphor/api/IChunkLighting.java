package net.minecraft.port.phosphor.api;

import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;

public interface IChunkLighting {
    int getCachedLightFor(EnumSkyBlock lightType, BlockPos pos);
}