package net.minecraft.client.resources;

import com.google.gson.JsonParseException;
import java.io.IOException;
import net.minecraft.client.gui.options.impl.GuiResourcePackSettings;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackListEntryDefault extends ResourcePackListEntry
{
    private static final Logger logger = LogManager.getLogger();
    private final IResourcePack resourcePack;
    private final ResourceLocation resourcePackIcon;

    public ResourcePackListEntryDefault(GuiResourcePackSettings resourcePacksGUIIn)
    {
        super(resourcePacksGUIIn);
        this.resourcePack = this.mc.getResourcePackRepository().rprDefaultResourcePack;
        DynamicTexture dynamictexture;

        try
        {
            dynamictexture = new DynamicTexture(this.resourcePack.getPackImage());
        }
        catch (IOException var4)
        {
            dynamictexture = TextureUtil.missingTexture;
        }

        this.resourcePackIcon = this.mc.getTextureManager().getDynamicTextureLocation("texturepackicon", dynamictexture);
    }

    protected int func183019A()
    {
        return 1;
    }

    protected String func148311A()
    {
        try
        {
            PackMetadataSection packmetadatasection = this.resourcePack.getPackMetadata(this.mc.getResourcePackRepository().rprMetadataSerializer, "pack");

            if (packmetadatasection != null)
            {
                return packmetadatasection.packDescription().getFormattedText();
            }
        }
        catch (JsonParseException | IOException jsonparseexception)
        {
            logger.error("Couldn't load metadata info", jsonparseexception);
        }

        return EnumChatFormatting.RED + "Missing " + "pack.mcmeta" + " :(";
    }

    protected boolean func148309E()
    {
        return false;
    }

    protected boolean func148308F()
    {
        return false;
    }

    protected boolean func148314G()
    {
        return false;
    }

    protected boolean func148307H()
    {
        return false;
    }

    protected String func148312B()
    {
        return "Default";
    }

    protected void func148313C()
    {
        this.mc.getTextureManager().bindTexture(this.resourcePackIcon);
    }

    protected boolean func148310D()
    {
        return false;
    }
}
