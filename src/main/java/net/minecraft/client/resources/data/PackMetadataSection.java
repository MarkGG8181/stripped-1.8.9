package net.minecraft.client.resources.data;

import net.minecraft.util.IChatComponent;

public record PackMetadataSection(IChatComponent packDescription, int packFormat) implements IMetadataSection {
}
