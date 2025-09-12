package net.minecraft.util;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureFix {
    private static final Logger logger = LogManager.getLogger("TextureFix");
    public static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.###");
    public static final LinkedList<UnloadEntry> toUnload = new LinkedList<>();

    public void runFix() {
        Minecraft mc = Minecraft.getMinecraft();
        IReloadableResourceManager manager = (IReloadableResourceManager)mc.getResourceManager();

        manager.registerReloadListener(resourceManager -> {
            TextureMap textureMap = mc.getTextureMapBlocks();
            if (textureMap == null) {
                return;
            }

            Map<String, TextureAtlasSprite> spriteMap = getData(textureMap);
            if (spriteMap == null) {
                return;
            }

            long savedBytes = 0L;
            int fixedCount = 0;

            for (TextureAtlasSprite sprite : spriteMap.values()) {
                if (!sprite.hasAnimationMetadata()) {
                    fixedCount++;
                    savedBytes += (long)sprite.getIconWidth() * sprite.getIconHeight() * 4;
                    sprite.setFramesTextureData(new FixList(sprite));
                }
            }

            int mipmaps = 1 + mc.gameSettings.mipmapLevels;
            long totalSaved = savedBytes * mipmaps;

            logger.info("Fixed {} texture(s), saved: {}MB ({} bytes)", fixedCount, DECIMALFORMAT.format(toMB(totalSaved)), totalSaved);
        });
    }

    public static void markForUnload(TextureAtlasSprite sprite) {
        toUnload.add(new UnloadEntry(sprite));
    }

    private long toMB(long bytes) {
        return bytes / 1024L / 1024L;
    }

    @SuppressWarnings("unchecked")
    private Map<String, TextureAtlasSprite> getData(TextureMap map) {
        try {
            for (Field field : map.getClass().getDeclaredFields()) {
                if (field.getType() == Map.class) {
                    field.setAccessible(true);
                    return (Map<String, TextureAtlasSprite>)field.get(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void reloadTextureData(TextureAtlasSprite sprite) {
        Minecraft mc = Minecraft.getMinecraft();
        reloadTextureData(sprite, mc.getResourceManager(), mc.getTextureMapBlocks());
    }

    private static void reloadTextureData(TextureAtlasSprite sprite, IResourceManager manager, TextureMap map) {
        ResourceLocation location = getResourceLocation(sprite);

        if (sprite.hasCustomLoader(manager, location)) {
            sprite.load(manager, location);
            return;
        }

        try {
            IResource resource = manager.getResource(location);
            int mipLevels = 1 + Minecraft.getMinecraft().gameSettings.mipmapLevels;

            BufferedImage[] images = new BufferedImage[mipLevels];
            images[0] = TextureUtil.readBufferedImage(resource.getInputStream());

            sprite.loadSprite(images, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ResourceLocation getResourceLocation(TextureAtlasSprite sprite) {
        ResourceLocation base = new ResourceLocation(sprite.getIconName());
        return new ResourceLocation(base.getResourceDomain(), "texures/%s.png".formatted(base.getResourcePath()));
    }

    public static class UnloadEntry {
        private int countdown = 2;
        private final TextureAtlasSprite sprite;

        public UnloadEntry(TextureAtlasSprite sprite) {
            this.sprite = sprite;
        }

        public boolean unload() {
            if (--countdown <= 0) {
                sprite.clearFramesTextureData();
                return true;
            }
            return false;
        }
    }
}
