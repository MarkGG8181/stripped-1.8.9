package net.minecraft.port.phosphor.mod.world.lighting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.port.phosphor.api.IChunkLightingData;
import net.minecraft.port.phosphor.api.ILightingEngine;
import net.minecraft.port.phosphor.api.ILightingEngineProvider;
import net.minecraft.port.phosphor.mod.world.ChunkHelper;

@SuppressWarnings("unused")
public class LightingHooks {
    private static final EnumSkyBlock[] ENUM_SKY_BLOCK_VALUES = EnumSkyBlock.values();
    private static final EnumFacing.AxisDirection[] ENUM_AXIS_DIRECTION_VALUES = EnumFacing.AxisDirection.values();

    private static final int FLAG_COUNT = 32;

    public static void relightSkylightColumn(final World world, final Chunk chunk, final int x, final int z, final int height1, final int height2) {
        final int yMin = Math.min(height1, height2);
        final int yMax = Math.max(height1, height2) - 1;

        final ExtendedBlockStorage[] sections = chunk.getBlockStorageArray();
        final int xBase = (chunk.xPosition << 4) + x;
        final int zBase = (chunk.zPosition << 4) + z;

        scheduleRelightChecksForColumn(world, EnumSkyBlock.SKY, xBase, zBase, yMin, yMax);

        if (sections[yMin >> 4] == null && yMin > 0) {
            world.checkLightFor(EnumSkyBlock.SKY, new BlockPos(xBase, yMin - 1, zBase));
        }

        short emptySections = 0;
        for (int sec = yMax >> 4; sec >= yMin >> 4; --sec) {
            if (sections[sec] == null) {
                emptySections |= 1 << sec;
            }
        }

        if (emptySections != 0) {
            for (final EnumFacing dir : EnumFacing.Plane.HORIZONTAL) {
                final int xOffset = dir.getFrontOffsetX();
                final int zOffset = dir.getFrontOffsetZ();

                final boolean neighborColumnExists = (((x + xOffset) | (z + zOffset)) & 16) == 0
                    || ChunkHelper.getLoadedChunk(world.getChunkProvider(), chunk.xPosition + xOffset, chunk.zPosition + zOffset) != null;

                if (neighborColumnExists) {
                    for (int sec = yMax >> 4; sec >= yMin >> 4; --sec) {
                        if ((emptySections & (1 << sec)) != 0) {
                            scheduleRelightChecksForColumn(world, EnumSkyBlock.SKY, xBase + xOffset, zBase + zOffset, sec << 4, (sec << 4) + 15);
                        }
                    }
                }
                else {
                    flagChunkBoundaryForUpdate(chunk, emptySections, EnumSkyBlock.SKY, dir, getAxisDirection(dir, x, z), EnumBoundaryFacing.OUT);
                }
            }
        }
    }

    public static void scheduleRelightChecksForArea(final World world, final EnumSkyBlock lightType, final int xMin, final int yMin, final int zMin, final int xMax, final int yMax, final int zMax) {
        for (int x = xMin; x <= xMax; ++x) {
            for (int z = zMin; z <= zMax; ++z) {
                scheduleRelightChecksForColumn(world, lightType, x, z, yMin, yMax);
            }
        }
    }

    private static void scheduleRelightChecksForColumn(final World world, final EnumSkyBlock lightType, final int x, final int z, final int yMin, final int yMax) {
        for (int y = yMin; y <= yMax; ++y) {
            world.checkLightFor(lightType, new BlockPos(x, y, z));
        }
    }

    public enum EnumBoundaryFacing {
        IN, OUT
    }

    public static void flagChunkBoundaryForUpdate(final Chunk chunk, final short sectionMask, final EnumSkyBlock lightType, final EnumFacing dir, final EnumFacing.AxisDirection axisDirection, final EnumBoundaryFacing boundaryFacing) {
        initNeighborLightChecks(chunk);
        short[] checks = ((IChunkLightingData)chunk).getNeighborLightChecks();
        if (checks != null) {
            checks[getFlagIndex(lightType, dir, axisDirection, boundaryFacing)] |= sectionMask;
        }
        chunk.setChunkModified();
    }

    public static int getFlagIndex(final EnumSkyBlock lightType, final EnumFacing dir, final EnumFacing.AxisDirection axisDirection, final EnumBoundaryFacing boundaryFacing) {
        return (lightType == EnumSkyBlock.BLOCK ? 0 : 16) | ((dir.getFrontOffsetX() + 1) << 2) | ((dir.getFrontOffsetZ() + 1) << 1) | (axisDirection.getOffset() + 1) | boundaryFacing.ordinal();
    }

    private static EnumFacing.AxisDirection getAxisDirection(final EnumFacing dir, final int x, final int z) {
        return ((dir.getAxis() == EnumFacing.Axis.X ? z : x) & 15) < 8 ? EnumFacing.AxisDirection.NEGATIVE : EnumFacing.AxisDirection.POSITIVE;
    }

    public static void scheduleRelightChecksForChunkBoundaries(final World world, final Chunk chunk) {
        for (final EnumFacing dir : EnumFacing.Plane.HORIZONTAL) {
            final Chunk nChunk = ChunkHelper.getLoadedChunk(world.getChunkProvider(), chunk.xPosition + dir.getFrontOffsetX(), chunk.zPosition + dir.getFrontOffsetZ());
            if (nChunk == null) continue;

            for (final EnumSkyBlock lightType : ENUM_SKY_BLOCK_VALUES) {
                for (final EnumFacing.AxisDirection axisDir : ENUM_AXIS_DIRECTION_VALUES) {
                    mergeFlags(lightType, chunk, nChunk, dir, axisDir);
                    mergeFlags(lightType, nChunk, chunk, dir.getOpposite(), axisDir);
                }
            }
        }
    }

    private static void mergeFlags(final EnumSkyBlock lightType, final Chunk inChunk, final Chunk outChunk, final EnumFacing dir, final EnumFacing.AxisDirection axisDir) {
        if (((IChunkLightingData)outChunk).getNeighborLightChecks() == null) return;

        initNeighborLightChecks(inChunk);

        final int inIndex = getFlagIndex(lightType, dir, axisDir, EnumBoundaryFacing.IN);
        final int outIndex = getFlagIndex(lightType, dir.getOpposite(), axisDir, EnumBoundaryFacing.OUT);

        ((IChunkLightingData)inChunk).getNeighborLightChecks()[inIndex] |= ((IChunkLightingData)outChunk).getNeighborLightChecks()[outIndex];
    }

    public static void initNeighborLightChecks(final Chunk chunk) {
        IChunkLightingData lightingData = (IChunkLightingData)chunk;
        if (lightingData.getNeighborLightChecks() == null) {
            lightingData.setNeighborLightChecks(new short[FLAG_COUNT]);
        }
    }

    public static final String neighborLightChecksKey = "NeighborLightChecks";

    public static void writeNeighborLightChecksToNBT(final Chunk chunk, final NBTTagCompound nbt) {
        short[] neighborLightChecks = ((IChunkLightingData)chunk).getNeighborLightChecks();
        if (neighborLightChecks == null) return;

        boolean empty = true;
        final NBTTagList list = new NBTTagList();
        for (final short flags : neighborLightChecks) {
            list.appendTag(new NBTTagShort(flags));
            if (flags != 0) empty = false;
        }

        if (!empty) {
            nbt.setTag(neighborLightChecksKey, list);
        }
    }

    public static void readNeighborLightChecksFromNBT(final Chunk chunk, final NBTTagCompound nbt) {
        if (nbt.hasKey(neighborLightChecksKey, 9)) {
            final NBTTagList list = nbt.getTagList(neighborLightChecksKey, 2);
            if (list.tagCount() == FLAG_COUNT) {
                initNeighborLightChecks(chunk);
                short[] neighborLightChecks = ((IChunkLightingData)chunk).getNeighborLightChecks();
                for (int i = 0; i < FLAG_COUNT; ++i) {
                    neighborLightChecks[i] = ((NBTTagShort)list.get(i)).getShort();
                }
            }
        }
    }

    public static void initChunkLighting(final Chunk chunk, final World world) {
        final int xBase = chunk.xPosition << 4;
        final int zBase = chunk.zPosition << 4;

        BlockPos corner1 = new BlockPos(xBase - 16, 0, zBase - 16);
        BlockPos corner2 = new BlockPos(xBase + 31, 255, zBase + 31);

        if (world.isAreaLoaded(corner1, corner2, false)) {
            final ExtendedBlockStorage[] sections = chunk.getBlockStorageArray();
            for (int j = 0; j < sections.length; ++j) {
                final ExtendedBlockStorage section = sections[j];
                if (section == null) continue;

                int yBase = j << 4;
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        for (int x = 0; x < 16; x++) {
                            IBlockState state = section.get(x, y, z);
                            int light = state.getBlock().getLightValue();

                            if (light > 0) {
                                world.checkLightFor(EnumSkyBlock.BLOCK, new BlockPos(xBase + x, yBase + y, zBase + z));
                            }
                        }
                    }
                }
            }

            if (!world.provider.getHasNoSky()) {
                ((IChunkLightingData)chunk).setSkylightUpdatedPublic();
            }

            ((IChunkLightingData)chunk).setLightInitialized(true);
        }
    }

    public static void checkChunkLighting(final Chunk chunk, final World world) {
        if (!((IChunkLightingData)chunk).isLightInitialized()) {
            initChunkLighting(chunk, world);
        }

        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                if (x == 0 && z == 0) continue;
                Chunk nChunk = ChunkHelper.getLoadedChunk(world.getChunkProvider(), chunk.xPosition + x, chunk.zPosition + z);
                if (nChunk == null || !((IChunkLightingData)nChunk).isLightInitialized()) {
                    return;
                }
            }
        }

        chunk.setLightPopulated(true);
    }

    public static void initSkylightForSection(final World world, final Chunk chunk, final ExtendedBlockStorage section) {
        if (!world.provider.getHasNoSky()) {
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    if (chunk.getHeightValue(x, z) <= section.getYLocation()) {
                        for (int y = 0; y < 16; ++y) {
                            section.setExtSkylightValue(x, y, z, EnumSkyBlock.SKY.defaultLightValue);
                        }
                    }
                }
            }
        }
    }

    public static ILightingEngine getLightingEngine(World world) {
        return ((ILightingEngineProvider)world).getLightingEngine();
    }
}