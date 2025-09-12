package net.minecraft.port.phosphor.mod.world;

import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;

public class ChunkHelper {

    public static Chunk getLoadedChunk(IChunkProvider chunkProvider, int x, int z) {
        long chunkKey = ChunkCoordIntPair.chunkXZ2Int(x, z);

        if (chunkProvider instanceof ChunkProviderServer serverProvider) {
            return serverProvider.id2ChunkMap.getValueByKey(chunkKey);
        }

        if (chunkProvider instanceof ChunkProviderClient clientProvider) {
            return clientProvider.chunkMapping.getValueByKey(chunkKey);
        }
        return null;
    }

    private ChunkHelper() {
    }
}
