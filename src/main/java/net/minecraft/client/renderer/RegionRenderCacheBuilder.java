package net.minecraft.client.renderer;

import net.minecraft.port.melod.buffer.BufferPool;
import net.minecraft.util.EnumWorldBlockLayer;
import java.nio.ByteBuffer;

public class RegionRenderCacheBuilder {
    private final WorldRenderer[] worldRenderers = new WorldRenderer[EnumWorldBlockLayer.values().length];

    private final ByteBuffer[] acquiredBuffers = new ByteBuffer[EnumWorldBlockLayer.values().length];

    public RegionRenderCacheBuilder() {
        this.acquiredBuffers[EnumWorldBlockLayer.SOLID.ordinal()] = BufferPool.acquire_2MB();
        this.worldRenderers[EnumWorldBlockLayer.SOLID.ordinal()] = new WorldRenderer(this.acquiredBuffers[EnumWorldBlockLayer.SOLID.ordinal()]);

        this.acquiredBuffers[EnumWorldBlockLayer.CUTOUT.ordinal()] = BufferPool.acquire_128KB();
        this.worldRenderers[EnumWorldBlockLayer.CUTOUT.ordinal()] = new WorldRenderer(this.acquiredBuffers[EnumWorldBlockLayer.CUTOUT.ordinal()]);

        this.acquiredBuffers[EnumWorldBlockLayer.CUTOUT_MIPPED.ordinal()] = BufferPool.acquire_128KB();
        this.worldRenderers[EnumWorldBlockLayer.CUTOUT_MIPPED.ordinal()] = new WorldRenderer(this.acquiredBuffers[EnumWorldBlockLayer.CUTOUT_MIPPED.ordinal()]);

        this.acquiredBuffers[EnumWorldBlockLayer.TRANSLUCENT.ordinal()] = BufferPool.acquire_256KB();
        this.worldRenderers[EnumWorldBlockLayer.TRANSLUCENT.ordinal()] = new WorldRenderer(this.acquiredBuffers[EnumWorldBlockLayer.TRANSLUCENT.ordinal()]);
    }

    public WorldRenderer getWorldRendererByLayer(EnumWorldBlockLayer layer) {
        return this.worldRenderers[layer.ordinal()];
    }

    public WorldRenderer getWorldRendererByLayerId(int id) {
        return this.worldRenderers[id];
    }

    public ByteBuffer[] getAcquiredBuffers() {
        return this.acquiredBuffers;
    }
}