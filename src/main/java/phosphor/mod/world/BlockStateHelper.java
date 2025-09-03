package phosphor.mod.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class BlockStateHelper {

    private static final IBlockState DEFAULT_AIR_STATE = Blocks.air.getDefaultState();

    /**
     * A faster method to get a block state from a chunk, bypassing some vanilla checks.
     */
    public static IBlockState getBlockState(final Chunk chunk, final BlockPos pos) {
        int y = pos.getY();
        
        // Return air for requests outside the valid world height for this chunk
        if (y < 0 || y > 255) {
            return DEFAULT_AIR_STATE;
        }

        ExtendedBlockStorage[] storageArrays = chunk.getBlockStorageArray();
        ExtendedBlockStorage section = storageArrays[y >> 4];

        if (section == null) {
            return DEFAULT_AIR_STATE;
        }

        // Use the direct getter from the chunk section
        return section.get(pos.getX() & 15, y & 15, pos.getZ() & 15);
    }
}