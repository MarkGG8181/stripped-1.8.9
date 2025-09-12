package net.minecraft.client.resources;

import net.minecraft.client.gui.options.impl.GuiResourcePackSettings;

public class ResourcePackListEntryFound extends ResourcePackListEntry
{
    private final ResourcePackRepository.Entry resourcePackEntry;

    public ResourcePackListEntryFound(GuiResourcePackSettings resourcePacksGUIIn, ResourcePackRepository.Entry p_i45053_2_)
    {
        super(resourcePacksGUIIn);
        this.resourcePackEntry = p_i45053_2_;
    }

    protected void func148313C()
    {
        this.resourcePackEntry.bindTexturePackIcon(this.mc.getTextureManager());
    }

    protected int func183019A()
    {
        return this.resourcePackEntry.func_183027_f();
    }

    protected String func148311A()
    {
        return this.resourcePackEntry.getTexturePackDescription();
    }

    protected String func148312B()
    {
        return this.resourcePackEntry.getResourcePackName();
    }

    public ResourcePackRepository.Entry func_148318_i()
    {
        return this.resourcePackEntry;
    }
}
