package net.minecraft.client.resources;

import net.minecraft.client.gui.GuiScreenResourcePacks;

public class ResourcePackListEntryFound extends ResourcePackListEntry
{
    private final ResourcePackRepository.Entry resourcePackEntry;

    public ResourcePackListEntryFound(GuiScreenResourcePacks resourcePacksGUIIn, ResourcePackRepository.Entry p_i45053_2_)
    {
        super(resourcePacksGUIIn);
        this.resourcePackEntry = p_i45053_2_;
    }

    protected void func_148313_c()
    {
        this.resourcePackEntry.bindTexturePackIcon(this.mc.getTextureManager());
    }

    protected int func_183019_a()
    {
        return this.resourcePackEntry.func_183027_f();
    }

    protected String func_148311_a()
    {
        return this.resourcePackEntry.getTexturePackDescription();
    }

    protected String func_148312_b()
    {
        return this.resourcePackEntry.getResourcePackName();
    }

    public ResourcePackRepository.Entry func_148318_i()
    {
        return this.resourcePackEntry;
    }
}
