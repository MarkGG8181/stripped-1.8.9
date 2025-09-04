package net.minecraft.phosphor.mod.world.lighting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.*;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.phosphor.api.ILightingEngine;
import net.minecraft.phosphor.mod.collections.PooledLongQueue;
import net.minecraft.phosphor.mod.world.BlockStateHelper;
import net.minecraft.phosphor.mod.world.ChunkHelper;

import java.util.concurrent.locks.ReentrantLock;

public class LightingEngine implements ILightingEngine {
    private static final int MAX_SCHEDULED_COUNT = 1 << 22;
    private static final int MAX_LIGHT = 15;

    private final World world;
    private final Profiler profiler;

    private final PooledLongQueue[] queuedLightUpdates = new PooledLongQueue[EnumSkyBlock.values().length];
    private final PooledLongQueue[] queuedDarkenings = new PooledLongQueue[MAX_LIGHT + 1];
    private final PooledLongQueue[] queuedBrightenings = new PooledLongQueue[MAX_LIGHT + 1];
    private final PooledLongQueue initialBrightenings;
    private final PooledLongQueue initialDarkenings;

    private boolean updating = false;

    private static final int lX = 26, lY = 8, lZ = 26, lL = 4;
    private static final int sZ = 0, sX = sZ + lZ, sY = sX + lX, sL = sY + lY;
    private static final long mX = (1L << lX) - 1, mY = (1L << lY) - 1, mZ = (1L << lZ) - 1, mL = (1L << lL) - 1, mPos = (mY << sY) | (mX << sX) | (mZ << sZ);
    private static final long yCheck = 1L << (sY + lY);
    private static final long[] neighborShifts = new long[6];

    static {
        for (EnumFacing facing : EnumFacing.values()) {
            final Vec3i offset = facing.getDirectionVec();
            neighborShifts[facing.ordinal()] = ((long) offset.getY() << sY) | ((long) offset.getX() << sX) | ((long) offset.getZ() << sZ);
        }
    }

    private static final long mChunk = ((mX >> 4) << (4 + sX)) | ((mZ >> 4) << (4 + sZ));

    private final MutableBlockPos curPos = new MutableBlockPos();
    private Chunk curChunk;
    private long curChunkIdentifier;
    private long curData;

    private boolean isNeighborDataValid = false;
    private final NeighborInfo[] neighborInfos = new NeighborInfo[6];
    private PooledLongQueue.LongQueueIterator queueIt;

    private final ReentrantLock lock = new ReentrantLock();

    public LightingEngine(final World world) {
        this.world = world;
        this.profiler = world.theProfiler;

        PooledLongQueue.Pool pool = new PooledLongQueue.Pool();
        this.initialBrightenings = new PooledLongQueue(pool);
        this.initialDarkenings = new PooledLongQueue(pool);

        for (int i = 0; i < EnumSkyBlock.values().length; ++i) {
            this.queuedLightUpdates[i] = new PooledLongQueue(pool);
        }
        for (int i = 0; i < this.queuedDarkenings.length; ++i) {
            this.queuedDarkenings[i] = new PooledLongQueue(pool);
        }
        for (int i = 0; i < this.queuedBrightenings.length; ++i) {
            this.queuedBrightenings[i] = new PooledLongQueue(pool);
        }
        for (int i = 0; i < this.neighborInfos.length; ++i) {
            this.neighborInfos[i] = new NeighborInfo();
        }
    }

    @Override
    public void scheduleLightUpdate(final EnumSkyBlock lightType, final BlockPos pos) {
        this.acquireLock();
        try {
            this.scheduleLightUpdate(lightType, encodeWorldCoord(pos));
        } finally {
            this.releaseLock();
        }
    }

    private void scheduleLightUpdate(final EnumSkyBlock lightType, final long pos) {
        final PooledLongQueue queue = this.queuedLightUpdates[lightType.ordinal()];
        queue.add(pos);

        if (queue.size() >= MAX_SCHEDULED_COUNT) {
            this.processLightUpdatesForType(lightType);
        }
    }

    @Override
    public void processLightUpdates() {
        this.processLightUpdatesForType(EnumSkyBlock.SKY);
        this.processLightUpdatesForType(EnumSkyBlock.BLOCK);
    }

    @Override
    public void processLightUpdatesForType(final EnumSkyBlock lightType) {
        if (this.world.isRemote && !this.isCallingFromMainThread()) {
            return;
        }

        final PooledLongQueue queue = this.queuedLightUpdates[lightType.ordinal()];
        if (queue.isEmpty()) {
            return;
        }

        this.acquireLock();
        try {
            this.processLightUpdatesForTypeInner(lightType, queue);
        } finally {
            this.releaseLock();
        }
    }

    private boolean isCallingFromMainThread() {
        return Minecraft.getMinecraft().isCallingFromMinecraftThread();
    }

    private void acquireLock() {
        if (!this.lock.tryLock()) {
            this.lock.lock();
        }
    }

    private void releaseLock() {
        this.lock.unlock();
    }

    private void processLightUpdatesForTypeInner(final EnumSkyBlock lightType, final PooledLongQueue queue) {
        if (this.updating) {
            throw new IllegalStateException("Already processing updates!");
        }

        this.updating = true;
        this.curChunkIdentifier = -1;

        this.profiler.startSection("lighting");
        this.profiler.startSection("checking");

        this.queueIt = queue.iterator();
        while (this.nextItem()) {
            if (this.curChunk == null) continue;

            final int oldLight = this.getCursorCachedLight(lightType);
            final int newLight = this.calculateNewLightFromCursor(lightType);

            if (oldLight < newLight) {
                this.initialBrightenings.add(((long) newLight << sL) | this.curData);
            } else if (oldLight > newLight) {
                this.initialDarkenings.add(this.curData);
            }
        }

        this.queueIt = this.initialBrightenings.iterator();
        while (this.nextItem()) {
            final int newLight = (int) (this.curData >> sL & mL);
            if (newLight > this.getCursorCachedLight(lightType)) {
                this.enqueueBrightening(this.curPos, this.curData & mPos, newLight, this.curChunk, lightType);
            }
        }

        this.queueIt = this.initialDarkenings.iterator();
        while (this.nextItem()) {
            final int oldLight = this.getCursorCachedLight(lightType);
            if (oldLight != 0) {
                this.enqueueDarkening(this.curPos, this.curData, oldLight, this.curChunk, lightType);
            }
        }

        this.profiler.endSection();

        for (int curLight = MAX_LIGHT; curLight >= 0; --curLight) {
            this.profiler.startSection("darkening");

            this.queueIt = this.queuedDarkenings[curLight].iterator();
            while (this.nextItem()) {
                if (this.getCursorCachedLight(lightType) >= curLight) continue;

                final IBlockState state = BlockStateHelper.getBlockState(this.curChunk, this.curPos);
                final int luminosity = this.getCursorLuminosity(state, lightType);
                final int opacity = luminosity >= MAX_LIGHT - 1 ? 1 : this.getPosOpacity(this.curPos, state);

                if (this.calculateNewLightFromCursor(luminosity, opacity, lightType) < curLight) {
                    int newLight = luminosity;
                    this.fetchNeighborDataFromCursor(lightType);
                    for (NeighborInfo info : this.neighborInfos) {
                        if (info.chunk == null || info.light == 0) continue;
                        
                        if (curLight - this.getPosOpacity(info.pos, BlockStateHelper.getBlockState(info.chunk, info.pos)) >= info.light) {
                            this.enqueueDarkening(info.pos, info.key, info.light, info.chunk, lightType);
                        } else {
                            newLight = Math.max(newLight, info.light - opacity);
                        }
                    }
                    this.enqueueBrighteningFromCursor(newLight, lightType);
                } else {
                    this.enqueueBrighteningFromCursor(curLight, lightType);
                }
            }

            this.profiler.endStartSection("brightening");

            this.queueIt = this.queuedBrightenings[curLight].iterator();
            while (this.nextItem()) {
                if (this.getCursorCachedLight(lightType) == curLight) {
                    this.world.checkLight(this.curPos);
                    if (curLight > 1) {
                        this.spreadLightFromCursor(curLight, lightType);
                    }
                }
            }
            this.profiler.endSection();
        }

        this.profiler.endSection();
        this.updating = false;
    }

    private void fetchNeighborDataFromCursor(final EnumSkyBlock lightType) {
        if (this.isNeighborDataValid) return;
        this.isNeighborDataValid = true;

        for (int i = 0; i < this.neighborInfos.length; ++i) {
            NeighborInfo info = this.neighborInfos[i];
            final long nLongPos = info.key = this.curData + neighborShifts[i];

            if ((nLongPos & yCheck) != 0) {
                info.chunk = null;
                info.section = null;
                continue;
            }

            final MutableBlockPos nPos = decodeWorldCoord(info.pos, nLongPos);
            final Chunk nChunk = ((nLongPos & mChunk) == this.curChunkIdentifier) ? this.curChunk : this.getChunk(nPos);
            info.chunk = nChunk;

            if (nChunk != null) {
                ExtendedBlockStorage nSection = nChunk.getBlockStorageArray()[nPos.getY() >> 4];
                info.section = nSection;
                info.light = getCachedLightFor(nChunk, nSection, nPos, lightType);
            }
        }
    }

    private static int getCachedLightFor(Chunk chunk, ExtendedBlockStorage section, BlockPos pos, EnumSkyBlock lightType) {
        int i = pos.getX() & 15;
        int j = pos.getY();
        int k = pos.getZ() & 15;

        if (j < 0 || j >= 256) return lightType.defaultLightValue;

        if (section == null) {
            return lightType == EnumSkyBlock.SKY && chunk.canSeeSky(pos) ? lightType.defaultLightValue : 0;
        } else if (lightType == EnumSkyBlock.SKY) {
            return chunk.getWorld().provider.getHasNoSky() ? 0 : section.getExtSkylightValue(i, j & 15, k);
        } else {
            return section.getExtBlocklightValue(i, j & 15, k);
        }
    }

    private int calculateNewLightFromCursor(final EnumSkyBlock lightType) {
        final IBlockState state = BlockStateHelper.getBlockState(this.curChunk, this.curPos);
        final int luminosity = this.getCursorLuminosity(state, lightType);
        final int opacity = luminosity >= MAX_LIGHT - 1 ? 1 : this.getPosOpacity(this.curPos, state);
        return this.calculateNewLightFromCursor(luminosity, opacity, lightType);
    }

    private int calculateNewLightFromCursor(final int luminosity, final int opacity, final EnumSkyBlock lightType) {
        if (luminosity >= MAX_LIGHT - opacity) return luminosity;

        int newLight = luminosity;
        this.fetchNeighborDataFromCursor(lightType);

        for (NeighborInfo info : this.neighborInfos) {
            if (info.chunk == null) continue;
            newLight = Math.max(info.light - opacity, newLight);
        }
        return newLight;
    }

    private void spreadLightFromCursor(final int curLight, final EnumSkyBlock lightType) {
        this.fetchNeighborDataFromCursor(lightType);

        for (NeighborInfo info : this.neighborInfos) {
            if (info.chunk == null) continue;
            final int newLight = curLight - this.getPosOpacity(info.pos, BlockStateHelper.getBlockState(info.chunk, info.pos));
            if (newLight > info.light) {
                this.enqueueBrightening(info.pos, info.key, newLight, info.chunk, lightType);
            }
        }
    }

    private void enqueueBrighteningFromCursor(final int newLight, final EnumSkyBlock lightType) {
        this.enqueueBrightening(this.curPos, this.curData, newLight, this.curChunk, lightType);
    }

    private void enqueueBrightening(final BlockPos pos, final long longPos, final int newLight, final Chunk chunk, final EnumSkyBlock lightType) {
        this.queuedBrightenings[newLight].add(longPos);
        chunk.setLightFor(lightType, pos, newLight);
    }

    private void enqueueDarkening(final BlockPos pos, final long longPos, final int oldLight, final Chunk chunk, final EnumSkyBlock lightType) {
        this.queuedDarkenings[oldLight].add(longPos);
        chunk.setLightFor(lightType, pos, 0);
    }

    private static MutableBlockPos decodeWorldCoord(final MutableBlockPos pos, final long longPos) {
        final int posX = (int) (longPos >> sX & mX) - (1 << lX - 1);
        final int posY = (int) (longPos >> sY & mY);
        final int posZ = (int) (longPos >> sZ & mZ) - (1 << lZ - 1);
        return pos.setPos(posX, posY, posZ);
    }

    private static long encodeWorldCoord(final BlockPos pos) {
        return encodeWorldCoord(pos.getX(), pos.getY(), pos.getZ());
    }

    private static long encodeWorldCoord(final long x, final long y, final long z) {
        return (y << sY) | (x + (1 << lX - 1) << sX) | (z + (1 << lZ - 1) << sZ);
    }

    private boolean nextItem() {
        if (!this.queueIt.hasNext()) {
            this.queueIt.finish();
            this.queueIt = null;
            return false;
        }

        this.curData = this.queueIt.next();
        this.isNeighborDataValid = false;
        decodeWorldCoord(this.curPos, this.curData);

        final long chunkIdentifier = this.curData & mChunk;
        if (this.curChunkIdentifier != chunkIdentifier) {
            this.curChunk = this.getChunk(this.curPos);
            this.curChunkIdentifier = chunkIdentifier;
        }
        return true;
    }

    private int getCursorCachedLight(final EnumSkyBlock lightType) {
        return this.curChunk.getCachedLightFor(lightType, this.curPos);
    }

    private int getCursorLuminosity(final IBlockState state, final EnumSkyBlock lightType) {
        if (lightType == EnumSkyBlock.SKY) {
            return this.curPos.getY() >= this.curChunk.getHeightValue(this.curPos.getX() & 15, this.curPos.getZ() & 15) ? 15 : 0;
        }
        return MathHelper.clamp_int(state.getBlock().getLightValue(), 0, MAX_LIGHT);
    }

    private int getPosOpacity(final BlockPos pos, final IBlockState state) {
        return MathHelper.clamp_int(state.getBlock().getLightOpacity(), 1, MAX_LIGHT);
    }

    private Chunk getChunk(final BlockPos pos) {
        return ChunkHelper.getLoadedChunk(this.world.getChunkProvider(), pos.getX() >> 4, pos.getZ() >> 4);
    }

    private static class NeighborInfo {
        Chunk chunk;
        ExtendedBlockStorage section;
        int light;
        long key;
        final MutableBlockPos pos = new MutableBlockPos();
    }

    private static class MutableBlockPos extends BlockPos {
        private int x;
        private int y;
        private int z;

        public MutableBlockPos() {
            super(0, 0, 0);
        }
        
        @Override
        public int getX() { return this.x; }
        @Override
        public int getY() { return this.y; }
        @Override
        public int getZ() { return this.z; }

        public LightingEngine.MutableBlockPos setPos(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }
    }
}