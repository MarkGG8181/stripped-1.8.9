package net.minecraft.client.gui.ingame;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.util.FontRenderer;
import net.minecraft.client.gui.util.ScaledResolution;
import net.minecraft.client.gui.element.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class GuiOverlayDebug extends Gui {
    private final Minecraft mc;
    private final FontRenderer fontRenderer;

    public GuiOverlayDebug(Minecraft mc) {
        this.mc = mc;
        this.fontRenderer = mc.fontRendererObj;
    }

    public void renderDebugInfo(ScaledResolution scaledResolutionIn) {
        this.mc.mcProfiler.startSection("debug");
        GlStateManager.pushMatrix();
        this.renderDebugInfoLeft();
        this.renderDebugInfoRight(scaledResolutionIn);
        GlStateManager.popMatrix();

        if (this.mc.gameSettings.showLagometer) {
            this.renderLagometer();
        }

        this.mc.mcProfiler.endSection();
    }

    protected void renderDebugInfoLeft() {
        List<String> list = this.call();

        for (int i = 0; i < list.size(); ++i) {
            String s = list.get(i);

            if (!Strings.isNullOrEmpty(s)) {
                int j = this.fontRenderer.FONT_HEIGHT;
                int k = this.fontRenderer.getStringWidth(s);
                int i1 = 2 + j * i;
                drawRect(1, i1 - 1, 2 + k + 1, i1 + j - 1, -1873784752);
                this.fontRenderer.drawString(s, 2, i1, 14737632);
            }
        }
    }

    protected void renderDebugInfoRight(ScaledResolution scaledRes) {
        List<String> list = this.getDebugInfoRight();

        for (int i = 0; i < list.size(); ++i) {
            String text = list.get(i);

            if (!Strings.isNullOrEmpty(text)) {
                int height = this.fontRenderer.FONT_HEIGHT;
                int width = this.fontRenderer.getStringWidth(text);
                int l = scaledRes.getScaledWidth() - 2 - width;
                int i1 = 2 + height * i;
                drawRect(l - 1, i1 - 1, l + width + 1, i1 + height - 1, -1873784752);
                this.fontRenderer.drawString(text, l, i1, 14737632);
            }
        }
    }

    @SuppressWarnings("incomplete-switch")
    protected List<String> call() {
        BlockPos blockpos = new BlockPos(this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ);

        Entity entity = this.mc.getRenderViewEntity();
        EnumFacing enumfacing = entity.getHorizontalFacing();
        String s = switch (enumfacing) {
            case NORTH -> "Towards negative Z";
            case SOUTH -> "Towards positive Z";
            case WEST -> "Towards negative X";
            case EAST -> "Towards positive X";
            default -> "Invalid";
        };

        List<String> list = Lists.newArrayList("Minecraft 1.8.9 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.mc.debug, this.mc.renderGlobal.getDebugInfoRenders(), this.mc.renderGlobal.getDebugInfoEntities(), "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.theWorld.getDebugLoadedEntities(), this.mc.theWorld.getProviderName(), "", "XYZ: %.3f / %.5f / %.3f".formatted(this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ), "Block: %d %d %d".formatted(blockpos.getX(), blockpos.getY(), blockpos.getZ()), "Chunk: %d %d %d in %d %d %d".formatted(blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15, blockpos.getX() >> 4, blockpos.getY() >> 4, blockpos.getZ() >> 4), "Facing: %s (%s) (%.1f / %.1f)".formatted(enumfacing, s, MathHelper.wrapAngleTo180_float(entity.rotationYaw), MathHelper.wrapAngleTo180_float(entity.rotationPitch)));

        if (this.mc.theWorld != null && this.mc.theWorld.isBlockLoaded(blockpos)) {
            Chunk chunk = this.mc.theWorld.getChunkFromBlockCoords(blockpos);
            list.add("Biome: " + chunk.getBiome(blockpos, this.mc.theWorld.getWorldChunkManager()).biomeName);
            list.add("Light: " + chunk.getLightSubtracted(blockpos, 0) + " (" + chunk.getLightFor(EnumSkyBlock.SKY, blockpos) + " sky, " + chunk.getLightFor(EnumSkyBlock.BLOCK, blockpos) + " block)");
            DifficultyInstance difficultyinstance = this.mc.theWorld.getDifficultyForLocation(blockpos);

            if (this.mc.isIntegratedServerRunning() && this.mc.getIntegratedServer() != null) {
                EntityPlayerMP entityplayermp = this.mc.getIntegratedServer().getConfigurationManager().getPlayerByUUID(this.mc.thePlayer.getUniqueID());

                if (entityplayermp != null) {
                    difficultyinstance = entityplayermp.worldObj.getDifficultyForLocation(new BlockPos(entityplayermp));
                }
            }

            list.add("Local Difficulty: %.2f (Day %d)".formatted(difficultyinstance.getAdditionalDifficulty(), this.mc.theWorld.getWorldTime() / 24000L));
        }

        if (this.mc.entityRenderer != null && this.mc.entityRenderer.isShaderActive()) {
            list.add("Shader: " + this.mc.entityRenderer.getShaderGroup().getShaderGroupName());
        }

        if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.objectMouseOver.getBlockPos() != null) {
            BlockPos blockpos1 = this.mc.objectMouseOver.getBlockPos();
            list.add("Looking at: %d %d %d".formatted(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ()));
        }

        return list;
    }

    protected List<String> getDebugInfoRight() {
        long i = Runtime.getRuntime().maxMemory();
        long j = Runtime.getRuntime().totalMemory();
        long k = Runtime.getRuntime().freeMemory();
        long l = j - k;
        List<String> list = Lists.newArrayList("Java: %s %dbit".formatted(System.getProperty("java.version"), this.mc.isJava64bit() ? 64 : 32), "Mem: % 2d%% %03d/%03dMB".formatted(l * 100L / i, bytesToMb(l), bytesToMb(i)), "Allocated: % 2d%% %03dMB".formatted(j * 100L / i, bytesToMb(j)), "", "CPU: %s".formatted(OpenGlHelper.getCpu()), "", "Display: %dx%d (%s)".formatted(Display.getWidth(), Display.getHeight(), GL11.glGetString(GL11.GL_VENDOR)), GL11.glGetString(GL11.GL_RENDERER), GL11.glGetString(GL11.GL_VERSION));

        if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.objectMouseOver.getBlockPos() != null) {
            BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
            IBlockState iblockstate = this.mc.theWorld.getBlockState(blockpos);

            if (this.mc.theWorld.getWorldType() != WorldType.DEBUG_WORLD) {
                iblockstate = iblockstate.getBlock().getActualState(iblockstate, this.mc.theWorld, blockpos);
            }

            list.add("");
            list.add(String.valueOf(Block.blockRegistry.getNameForObject(iblockstate.getBlock())));

            for (Entry<IProperty, Comparable> entry : iblockstate.getProperties().entrySet()) {
                String s = ((Comparable<?>)entry.getValue()).toString();

                if (entry.getValue() == Boolean.TRUE) {
                    s = EnumChatFormatting.GREEN + s;
                }
                else if (entry.getValue() == Boolean.FALSE) {
                    s = EnumChatFormatting.RED + s;
                }

                list.add(((IProperty<?>)entry.getKey()).getName() + ": " + s);
            }
        }

        return list;
    }

    private void renderLagometer() {
        GlStateManager.disableDepth();
        FrameTimer frametimer = this.mc.getFrameTimer();
        int i = frametimer.getLastIndex();
        int j = frametimer.getIndex();
        long[] along = frametimer.getFrames();
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int k = i;
        int l = 0;
        drawRect(0, scaledresolution.getScaledHeight() - 60, 240, scaledresolution.getScaledHeight(), -1873784752);

        while (k != j) {
            int i1 = frametimer.getLagometerValue(along[k], 30);
            int j1 = this.getFrameColor(MathHelper.clamp_int(i1, 0, 60), 30, 60);
            this.drawVerticalLine(l, scaledresolution.getScaledHeight(), scaledresolution.getScaledHeight() - i1, j1);
            ++l;
            k = frametimer.parseIndex(k + 1);
        }

        drawRect(1, scaledresolution.getScaledHeight() - 30 + 1, 14, scaledresolution.getScaledHeight() - 30 + 10, -1873784752);
        this.fontRenderer.drawString("60", 2, scaledresolution.getScaledHeight() - 30 + 2, 14737632);
        this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 30, -1);
        drawRect(1, scaledresolution.getScaledHeight() - 60 + 1, 14, scaledresolution.getScaledHeight() - 60 + 10, -1873784752);
        this.fontRenderer.drawString("30", 2, scaledresolution.getScaledHeight() - 60 + 2, 14737632);
        this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 60, -1);
        this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 1, -1);
        this.drawVerticalLine(0, scaledresolution.getScaledHeight() - 60, scaledresolution.getScaledHeight(), -1);
        this.drawVerticalLine(239, scaledresolution.getScaledHeight() - 60, scaledresolution.getScaledHeight(), -1);

        if (this.mc.gameSettings.limitFramerate <= 120) {
            this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 60 + this.mc.gameSettings.limitFramerate / 2, -16711681);
        }

        GlStateManager.enableDepth();
    }

    private int getFrameColor(int p_181552_1_, int p_181552_3_, int p_181552_4_) {
        return p_181552_1_ < p_181552_3_ ? this.blendColors(-16711936, -256, (float)p_181552_1_ / (float)p_181552_3_) : this.blendColors(-256, -65536, (float)(p_181552_1_ - p_181552_3_) / (float)(p_181552_4_ - p_181552_3_));
    }

    private int blendColors(int p_181553_1_, int p_181553_2_, float p_181553_3_) {
        int i = p_181553_1_ >> 24 & 255;
        int j = p_181553_1_ >> 16 & 255;
        int k = p_181553_1_ >> 8 & 255;
        int l = p_181553_1_ & 255;
        int i1 = p_181553_2_ >> 24 & 255;
        int j1 = p_181553_2_ >> 16 & 255;
        int k1 = p_181553_2_ >> 8 & 255;
        int l1 = p_181553_2_ & 255;
        int i2 = MathHelper.clamp_int((int)((float)i + (float)(i1 - i) * p_181553_3_), 0, 255);
        int j2 = MathHelper.clamp_int((int)((float)j + (float)(j1 - j) * p_181553_3_), 0, 255);
        int k2 = MathHelper.clamp_int((int)((float)k + (float)(k1 - k) * p_181553_3_), 0, 255);
        int l2 = MathHelper.clamp_int((int)((float)l + (float)(l1 - l) * p_181553_3_), 0, 255);
        return i2 << 24 | j2 << 16 | k2 << 8 | l2;
    }

    private static long bytesToMb(long bytes) {
        return bytes / 1024L / 1024L;
    }
}
