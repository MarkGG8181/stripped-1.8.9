package net.minecraft.client.resources.data;

import java.util.Collections;
import java.util.List;

public record TextureMetadataSection(boolean textureBlur, boolean textureClamp,
                                     List<Integer> listMipmaps) implements IMetadataSection {

    @Override
    public List<Integer> listMipmaps() {
        return Collections.<Integer>unmodifiableList(this.listMipmaps);
    }
}
