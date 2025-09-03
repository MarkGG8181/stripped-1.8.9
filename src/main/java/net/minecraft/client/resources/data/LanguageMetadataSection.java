package net.minecraft.client.resources.data;

import java.util.Collection;

import net.minecraft.client.resources.Language;

public record LanguageMetadataSection(Collection<Language> languages) implements IMetadataSection {
}
