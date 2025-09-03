package net.minecraft.client.resources.data;

public record FontMetadataSection(float[] charWidths, float[] charLefts,
                                  float[] charSpacings) implements IMetadataSection {
}
