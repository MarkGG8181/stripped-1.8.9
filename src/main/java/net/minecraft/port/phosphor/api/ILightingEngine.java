package net.minecraft.port.phosphor.api;

import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;

public interface ILightingEngine {
    void scheduleLightUpdate(EnumSkyBlock lightType, BlockPos pos);

    void processLightUpdates();

    void processLightUpdatesForType(EnumSkyBlock lightType);
}