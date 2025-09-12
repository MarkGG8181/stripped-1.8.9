package net.minecraft.client.gui.ingame;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.element.Gui;
import net.minecraft.client.gui.spectator.GuiSpectator;
import net.minecraft.client.gui.util.FontRenderer;
import net.minecraft.client.gui.util.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.border.WorldBorder;

public class GuiIngame extends Gui {
    private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
    private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
    private final Random rand = new Random();
    private final Minecraft mc;
    private final RenderItem itemRenderer;

    /**
     * ChatGUI instance that retains all previous chat data
     */
    private final GuiNewChat persistantChatGUI;
    private int updateCounter;

    /**
     * The string specifying which record music is playing
     */
    private String recordPlaying = "";

    /**
     * How many ticks the record playing message will be displayed
     */
    private int recordPlayingUpFor;
    private boolean recordIsPlaying;

    /**
     * Previous frame vignette brightness (slowly changes by 1% each frame)
     */
    public float prevVignetteBrightness = 1.0F;

    /**
     * Remaining ticks the item highlight should be visible
     */
    private int remainingHighlightTicks;

    /**
     * The ItemStack that is currently being highlighted
     */
    private ItemStack highlightingItemStack;
    private final GuiOverlayDebug overlayDebug;

    /**
     * The spectator GUI for this in-game GUI instance
     */
    private final GuiSpectator spectatorGui;
    private final GuiPlayerTabOverlay overlayPlayerList;

    /**
     * A timer for the current title and subtitle displayed
     */
    private int titlesTimer;

    /**
     * The current title displayed
     */
    private String displayedTitle = "";

    /**
     * The current subtitle displayed
     */
    private String displayedSubTitle = "";

    /**
     * The time that the title take to fade in
     */
    private int titleFadeIn;

    /**
     * The time that the title is display
     */
    private int titleDisplayTime;

    /**
     * The time that the title take to fade out
     */
    private int titleFadeOut;
    private int playerHealth;
    private int lastPlayerHealth;

    /**
     * The last recorded system time
     */
    private long lastSystemTime;

    /**
     * Used with updateCounter to make the heart bar flash
     */
    private long healthUpdateCounter;

    public GuiIngame(Minecraft mcIn) {
        mc = mcIn;
        itemRenderer = mcIn.getRenderItem();
        overlayDebug = new GuiOverlayDebug(mcIn);
        spectatorGui = new GuiSpectator(mcIn);
        persistantChatGUI = new GuiNewChat(mcIn);
        overlayPlayerList = new GuiPlayerTabOverlay(mcIn, this);
        setDefaultTitlesTimes();
    }

    /**
     * Set the differents times for the titles to their default values
     */
    public void setDefaultTitlesTimes() {
        titleFadeIn = 10;
        titleDisplayTime = 70;
        titleFadeOut = 20;
    }

    public void renderGameOverlay(float partialTicks) {
        ScaledResolution scaledresolution = new ScaledResolution(mc);
        int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
        mc.entityRenderer.setupOverlayRendering();
        GlStateManager.enableBlend();

        if (mc.gameSettings.renderVignette) {
            renderVignette(mc.thePlayer.getBrightness(partialTicks), scaledresolution);
        }
        else {
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        }

        ItemStack itemstack = mc.thePlayer.inventory.armorItemInSlot(3);

        if (mc.gameSettings.thirdPersonView == 0 && itemstack != null && itemstack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
            renderPumpkinOverlay(scaledresolution);
        }

        if (!mc.thePlayer.isPotionActive(Potion.confusion)) {
            float f = mc.thePlayer.prevTimeInPortal + (mc.thePlayer.timeInPortal - mc.thePlayer.prevTimeInPortal) * partialTicks;

            if (f > 0.0F) {
                renderPortal(f, scaledresolution);
            }
        }

        if (mc.playerController.isSpectator()) {
            spectatorGui.renderTooltip(scaledresolution, partialTicks);
        }
        else {
            renderTooltip(scaledresolution, partialTicks);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(icons);
        GlStateManager.enableBlend();

        if (showCrosshair()) {
            GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
            GlStateManager.enableAlpha();
            drawTexturedModalRect(i / 2 - 7, j / 2 - 7, 0, 0, 16, 16);
        }

        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        mc.mcProfiler.startSection("bossHealth");
        renderBossHealth();
        mc.mcProfiler.endSection();

        if (mc.playerController.shouldDrawHUD()) {
            renderPlayerStats(scaledresolution);
        }

        GlStateManager.disableBlend();

        if (mc.thePlayer.getSleepTimer() > 0) {
            mc.mcProfiler.startSection("sleep");
            GlStateManager.disableDepth();
            GlStateManager.disableAlpha();
            int j1 = mc.thePlayer.getSleepTimer();
            float f1 = (float)j1 / 100.0F;

            if (f1 > 1.0F) {
                f1 = 1.0F - (float)(j1 - 100) / 10.0F;
            }

            int k = (int)(220.0F * f1) << 24 | 1052704;
            drawRect(0, 0, i, j, k);
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            mc.mcProfiler.endSection();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int k1 = i / 2 - 91;

        if (mc.thePlayer.isRidingHorse()) {
            renderHorseJumpBar(scaledresolution, k1);
        }
        else if (mc.playerController.gameIsSurvivalOrAdventure()) {
            renderExpBar(scaledresolution, k1);
        }

        if (mc.gameSettings.heldItemTooltips && !mc.playerController.isSpectator()) {
            renderSelectedItem(scaledresolution);
        }
        else if (mc.thePlayer.isSpectator()) {
            spectatorGui.renderSelectedItem(scaledresolution);
        }

        if (mc.gameSettings.showDebugInfo) {
            overlayDebug.renderDebugInfo(scaledresolution);
        }
        else {
            if (mc.gameSettings.showFramerate) {
                String text = Minecraft.getDebugFPS() + " fps";
                int height = getFontRenderer().FONT_HEIGHT;
                int width = getFontRenderer().getStringWidth(text);
                int padding = 2;
                int y = 2;
                drawRect(1, y - 1, padding + width + 1, y + height - 1, -1873784752);
                getFontRenderer().drawString(text, 2, y, 14737632);
            }
        }

        if (recordPlayingUpFor > 0) {
            mc.mcProfiler.startSection("overlayMessage");
            float f2 = (float)recordPlayingUpFor - partialTicks;
            int l1 = (int)(f2 * 255.0F / 20.0F);

            if (l1 > 255) {
                l1 = 255;
            }

            if (l1 > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(i / 2), (float)(j - 68), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                int l = 16777215;

                if (recordIsPlaying) {
                    l = MathHelper.hsvToRGB(f2 / 50.0F, 0.7F, 0.6F) & 16777215;
                }

                getFontRenderer().drawString(recordPlaying, -getFontRenderer().getStringWidth(recordPlaying) / 2, -4, l + (l1 << 24 & -16777216));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            mc.mcProfiler.endSection();
        }

        if (titlesTimer > 0) {
            mc.mcProfiler.startSection("titleAndSubtitle");
            float f3 = (float)titlesTimer - partialTicks;
            int i2 = 255;

            if (titlesTimer > titleFadeOut + titleDisplayTime) {
                float f4 = (float)(titleFadeIn + titleDisplayTime + titleFadeOut) - f3;
                i2 = (int)(f4 * 255.0F / (float)titleFadeIn);
            }

            if (titlesTimer <= titleFadeOut) {
                i2 = (int)(f3 * 255.0F / (float)titleFadeOut);
            }

            i2 = MathHelper.clamp_int(i2, 0, 255);

            if (i2 > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(i / 2), (float)(j / 2), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.pushMatrix();
                GlStateManager.scale(4.0F, 4.0F, 4.0F);
                int j2 = i2 << 24 & -16777216;
                getFontRenderer().drawString(displayedTitle, (float)(-getFontRenderer().getStringWidth(displayedTitle) / 2), -10.0F, 16777215 | j2, true);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
                getFontRenderer().drawString(displayedSubTitle, (float)(-getFontRenderer().getStringWidth(displayedSubTitle) / 2), 5.0F, 16777215 | j2, true);
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            mc.mcProfiler.endSection();
        }

        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScoreObjective scoreobjective = null;
        ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(mc.thePlayer.getName());

        if (scoreplayerteam != null) {
            int i1 = scoreplayerteam.getChatFormat().getColorIndex();

            if (i1 >= 0) {
                scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + i1);
            }
        }

        ScoreObjective scoreobjective1 = scoreobjective != null ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);

        if (scoreobjective1 != null) {
            renderScoreboard(scoreobjective1, scaledresolution);
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, (float)(j - 48), 0.0F);
        mc.mcProfiler.startSection("chat");
        persistantChatGUI.drawChat(updateCounter);
        mc.mcProfiler.endSection();
        GlStateManager.popMatrix();
        scoreobjective1 = scoreboard.getObjectiveInDisplaySlot(0);

        if (!mc.gameSettings.keyBindPlayerList.isKeyDown() || mc.isIntegratedServerRunning() && mc.thePlayer.sendQueue.getPlayerInfoMap().size() <= 1 && scoreobjective1 == null) {
            overlayPlayerList.updatePlayerList(false);
        }
        else {
            overlayPlayerList.updatePlayerList(true);
            overlayPlayerList.renderPlayerlist(i, scoreboard, scoreobjective1);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
    }

    protected void renderTooltip(ScaledResolution sr, float partialTicks) {
        if (mc.getRenderViewEntity() instanceof EntityPlayer entityplayer) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(widgetsTexPath);
            int i = sr.getScaledWidth() / 2;
            float f = zLevel;
            zLevel = -90.0F;
            drawTexturedModalRect(i - 91, sr.getScaledHeight() - 22, 0, 0, 182, 22);
            drawTexturedModalRect(i - 91 - 1 + entityplayer.inventory.currentItem * 20, sr.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
            zLevel = f;
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();

            for (int j = 0; j < 9; j++) {
                int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
                int l = sr.getScaledHeight() - 16 - 3;
                renderHotbarItem(j, k, l, partialTicks, entityplayer);
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
    }

    public void renderHorseJumpBar(ScaledResolution scaledRes, int x) {
        mc.mcProfiler.startSection("jumpBar");
        mc.getTextureManager().bindTexture(Gui.icons);
        float f = mc.thePlayer.getHorseJumpPower();
        int i = 182;
        int j = (int)(f * (float)(i + 1));
        int k = scaledRes.getScaledHeight() - 32 + 3;
        drawTexturedModalRect(x, k, 0, 84, i, 5);

        if (j > 0) {
            drawTexturedModalRect(x, k, 0, 89, j, 5);
        }

        mc.mcProfiler.endSection();
    }

    public void renderExpBar(ScaledResolution scaledRes, int x) {
        mc.mcProfiler.startSection("expBar");
        mc.getTextureManager().bindTexture(Gui.icons);
        int i = mc.thePlayer.xpBarCap();

        if (i > 0) {
            int j = 182;
            int k = (int)(mc.thePlayer.experience * (float)(j + 1));
            int l = scaledRes.getScaledHeight() - 32 + 3;
            drawTexturedModalRect(x, l, 0, 64, j, 5);

            if (k > 0) {
                drawTexturedModalRect(x, l, 0, 69, k, 5);
            }
        }

        mc.mcProfiler.endSection();

        if (mc.thePlayer.experienceLevel > 0) {
            mc.mcProfiler.startSection("expLevel");
            int k1 = 8453920;
            String s = "" + mc.thePlayer.experienceLevel;
            int l1 = (scaledRes.getScaledWidth() - getFontRenderer().getStringWidth(s)) / 2;
            int i1 = scaledRes.getScaledHeight() - 31 - 4;
            getFontRenderer().drawString(s, l1 + 1, i1, 0);
            getFontRenderer().drawString(s, l1 - 1, i1, 0);
            getFontRenderer().drawString(s, l1, i1 + 1, 0);
            getFontRenderer().drawString(s, l1, i1 - 1, 0);
            getFontRenderer().drawString(s, l1, i1, k1);
            mc.mcProfiler.endSection();
        }
    }

    public void renderSelectedItem(ScaledResolution scaledRes) {
        mc.mcProfiler.startSection("selectedItemName");

        if (remainingHighlightTicks > 0 && highlightingItemStack != null) {
            String s = highlightingItemStack.getDisplayName();

            if (highlightingItemStack.hasDisplayName()) {
                s = EnumChatFormatting.ITALIC + s;
            }

            int i = (scaledRes.getScaledWidth() - getFontRenderer().getStringWidth(s)) / 2;
            int j = scaledRes.getScaledHeight() - 59;

            if (!mc.playerController.shouldDrawHUD()) {
                j += 14;
            }

            int k = (int)((float)remainingHighlightTicks * 256.0F / 10.0F);

            if (k > 255) {
                k = 255;
            }

            if (k > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                getFontRenderer().drawStringWithShadow(s, (float)i, (float)j, 16777215 + (k << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }

        mc.mcProfiler.endSection();
    }

    protected boolean showCrosshair() {
        if (mc.gameSettings.showDebugInfo) {
            return false;
        }
        else if (mc.playerController.isSpectator()) {
            if (mc.pointedEntity != null) {
                return true;
            }
            else {
                if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    BlockPos blockpos = mc.objectMouseOver.getBlockPos();
                    return mc.theWorld.getTileEntity(blockpos) instanceof IInventory;
                }

                return false;
            }
        }
        else {
            return true;
        }
    }

    private void renderScoreboard(ScoreObjective objective, ScaledResolution scaledRes) {
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<Score> collection = scoreboard.getSortedScores(objective);
        List<Score> list = Lists.newArrayList(Iterables.filter(collection, p_apply_1_ -> {
            assert p_apply_1_ != null;
            return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
        }));

        if (list.size() > 15) {
            collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        }
        else {
            collection = list;
        }

        int i = getFontRenderer().getStringWidth(objective.getDisplayName());

        for (Score score : collection) {
            ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
            String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + ": " + EnumChatFormatting.RED + score.getScorePoints();
            i = Math.max(i, getFontRenderer().getStringWidth(s));
        }

        int i1 = collection.size() * getFontRenderer().FONT_HEIGHT;
        int j1 = scaledRes.getScaledHeight() / 2 + i1 / 3;
        int k1 = 3;
        int l1 = scaledRes.getScaledWidth() - i - k1;
        int j = 0;

        for (Score score1 : collection) {
            ++j;
            ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
            String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
            String s2 = EnumChatFormatting.RED + "" + score1.getScorePoints();
            int k = j1 - j * getFontRenderer().FONT_HEIGHT;
            int l = scaledRes.getScaledWidth() - k1 + 2;
            drawRect(l1 - 2, k, l, k + getFontRenderer().FONT_HEIGHT, 1342177280);
            getFontRenderer().drawString(s1, l1, k, 553648127);
            getFontRenderer().drawString(s2, l - getFontRenderer().getStringWidth(s2), k, 553648127);

            if (j == collection.size()) {
                String s3 = objective.getDisplayName();
                drawRect(l1 - 2, k - getFontRenderer().FONT_HEIGHT - 1, l, k - 1, 1610612736);
                drawRect(l1 - 2, k - 1, l, k, 1342177280);
                getFontRenderer().drawString(s3, l1 + i / 2 - getFontRenderer().getStringWidth(s3) / 2, k - getFontRenderer().FONT_HEIGHT, 553648127);
            }
        }
    }

    private void renderPlayerStats(ScaledResolution scaledRes) {
        if (mc.getRenderViewEntity() instanceof EntityPlayer entityplayer) {
            int i = MathHelper.ceiling_float_int(entityplayer.getHealth());
            boolean flag = healthUpdateCounter > (long)updateCounter && (healthUpdateCounter - (long)updateCounter) / 3L % 2L == 1L;

            if (i < playerHealth && entityplayer.hurtResistantTime > 0) {
                lastSystemTime = Minecraft.getSystemTime();
                healthUpdateCounter = updateCounter + 20;
            }
            else if (i > playerHealth && entityplayer.hurtResistantTime > 0) {
                lastSystemTime = Minecraft.getSystemTime();
                healthUpdateCounter = updateCounter + 10;
            }

            if (Minecraft.getSystemTime() - lastSystemTime > 1000L) {
                playerHealth = i;
                lastPlayerHealth = i;
                lastSystemTime = Minecraft.getSystemTime();
            }

            playerHealth = i;
            int j = lastPlayerHealth;
            rand.setSeed(updateCounter * 312871L);
            FoodStats foodstats = entityplayer.getFoodStats();
            int k = foodstats.getFoodLevel();
            IAttributeInstance iattributeinstance = entityplayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
            int i1 = scaledRes.getScaledWidth() / 2 - 91;
            int j1 = scaledRes.getScaledWidth() / 2 + 91;
            int k1 = scaledRes.getScaledHeight() - 39;
            float f = (float)iattributeinstance.getAttributeValue();
            float f1 = entityplayer.getAbsorptionAmount();
            int l1 = MathHelper.ceiling_float_int((f + f1) / 2.0F / 10.0F);
            int i2 = Math.max(10 - (l1 - 2), 3);
            int j2 = k1 - (l1 - 1) * i2 - 10;
            float f2 = f1;
            int k2 = entityplayer.getTotalArmorValue();
            int l2 = -1;

            if (entityplayer.isPotionActive(Potion.regeneration)) {
                l2 = updateCounter % MathHelper.ceiling_float_int(f + 5.0F);
            }

            mc.mcProfiler.startSection("armor");

            for (int i3 = 0; i3 < 10; i3++) {
                if (k2 > 0) {
                    int j3 = i1 + i3 * 8;

                    if (i3 * 2 + 1 < k2) {
                        drawTexturedModalRect(j3, j2, 34, 9, 9, 9);
                    }

                    if (i3 * 2 + 1 == k2) {
                        drawTexturedModalRect(j3, j2, 25, 9, 9, 9);
                    }

                    if (i3 * 2 + 1 > k2) {
                        drawTexturedModalRect(j3, j2, 16, 9, 9, 9);
                    }
                }
            }

            mc.mcProfiler.endStartSection("health");

            for (int i6 = MathHelper.ceiling_float_int((f + f1) / 2.0F) - 1; i6 >= 0; i6--) {
                int j6 = 16;

                if (entityplayer.isPotionActive(Potion.poison)) {
                    j6 += 36;
                }
                else if (entityplayer.isPotionActive(Potion.wither)) {
                    j6 += 72;
                }

                int k3 = 0;

                if (flag) {
                    k3 = 1;
                }

                int l3 = MathHelper.ceiling_float_int((float)(i6 + 1) / 10.0F) - 1;
                int i4 = i1 + i6 % 10 * 8;
                int j4 = k1 - l3 * i2;

                if (i <= 4) {
                    j4 += rand.nextInt(2);
                }

                if (i6 == l2) {
                    j4 -= 2;
                }

                int k4 = 0;

                if (entityplayer.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
                    k4 = 5;
                }

                drawTexturedModalRect(i4, j4, 16 + k3 * 9, 9 * k4, 9, 9);

                if (flag) {
                    if (i6 * 2 + 1 < j) {
                        drawTexturedModalRect(i4, j4, j6 + 54, 9 * k4, 9, 9);
                    }

                    if (i6 * 2 + 1 == j) {
                        drawTexturedModalRect(i4, j4, j6 + 63, 9 * k4, 9, 9);
                    }
                }

                if (f2 > 0.0F) {
                    if (f2 == f1 && f1 % 2.0F == 1.0F) {
                        drawTexturedModalRect(i4, j4, j6 + 153, 9 * k4, 9, 9);
                    }
                    else {
                        drawTexturedModalRect(i4, j4, j6 + 144, 9 * k4, 9, 9);
                    }

                    f2 -= 2.0F;
                }
                else {
                    if (i6 * 2 + 1 < i) {
                        drawTexturedModalRect(i4, j4, j6 + 36, 9 * k4, 9, 9);
                    }

                    if (i6 * 2 + 1 == i) {
                        drawTexturedModalRect(i4, j4, j6 + 45, 9 * k4, 9, 9);
                    }
                }
            }

            Entity entity = entityplayer.ridingEntity;

            if (entity == null) {
                mc.mcProfiler.endStartSection("food");

                for (int k6 = 0; k6 < 10; k6++) {
                    int i7 = k1;
                    int l7 = 16;
                    int j8 = 0;

                    if (entityplayer.isPotionActive(Potion.hunger)) {
                        l7 += 36;
                        j8 = 13;
                    }

                    if (entityplayer.getFoodStats().getSaturationLevel() <= 0.0F && updateCounter % (k * 3 + 1) == 0) {
                        i7 = k1 + (rand.nextInt(3) - 1);
                    }

                    int i9 = j1 - k6 * 8 - 9;
                    drawTexturedModalRect(i9, i7, 16 + j8 * 9, 27, 9, 9);

                    if (k6 * 2 + 1 < k) {
                        drawTexturedModalRect(i9, i7, l7 + 36, 27, 9, 9);
                    }

                    if (k6 * 2 + 1 == k) {
                        drawTexturedModalRect(i9, i7, l7 + 45, 27, 9, 9);
                    }
                }
            }
            else if (entity instanceof EntityLivingBase entitylivingbase) {
                mc.mcProfiler.endStartSection("mountHealth");
                int j7 = (int)Math.ceil((double)entitylivingbase.getHealth());
                float f3 = entitylivingbase.getMaxHealth();
                int k8 = (int)(f3 + 0.5F) / 2;

                if (k8 > 30) {
                    k8 = 30;
                }

                int j9 = k1;

                for (int k9 = 0; k8 > 0; k9 += 20) {
                    int l4 = Math.min(k8, 10);
                    k8 -= l4;

                    for (int i5 = 0; i5 < l4; i5++) {
                        int j5 = 52;

                        int l5 = j1 - i5 * 8 - 9;
                        drawTexturedModalRect(l5, j9, j5, 9, 9, 9);

                        if (i5 * 2 + 1 + k9 < j7) {
                            drawTexturedModalRect(l5, j9, j5 + 36, 9, 9, 9);
                        }

                        if (i5 * 2 + 1 + k9 == j7) {
                            drawTexturedModalRect(l5, j9, j5 + 45, 9, 9, 9);
                        }
                    }

                    j9 -= 10;
                }
            }

            mc.mcProfiler.endStartSection("air");

            if (entityplayer.isInsideOfMaterial(Material.water)) {
                int l6 = mc.thePlayer.getAir();
                int k7 = MathHelper.ceiling_double_int((double)(l6 - 2) * 10.0D / 300.0D);
                int i8 = MathHelper.ceiling_double_int((double)l6 * 10.0D / 300.0D) - k7;

                for (int l8 = 0; l8 < k7 + i8; l8++) {
                    if (l8 < k7) {
                        drawTexturedModalRect(j1 - l8 * 8 - 9, j2, 16, 18, 9, 9);
                    }
                    else {
                        drawTexturedModalRect(j1 - l8 * 8 - 9, j2, 25, 18, 9, 9);
                    }
                }
            }

            mc.mcProfiler.endSection();
        }
    }

    /**
     * Renders dragon's (boss) health on the HUD
     */
    private void renderBossHealth() {
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
            --BossStatus.statusBarTime;
            ScaledResolution scaledresolution = new ScaledResolution(mc);
            int i = scaledresolution.getScaledWidth();
            int j = 182;
            int k = i / 2 - j / 2;
            int l = (int)(BossStatus.healthScale * (float)(j + 1));
            int i1 = 12;
            drawTexturedModalRect(k, i1, 0, 74, j, 5);
            drawTexturedModalRect(k, i1, 0, 74, j, 5);

            if (l > 0) {
                drawTexturedModalRect(k, i1, 0, 79, l, 5);
            }

            String s = BossStatus.bossName;
            getFontRenderer().drawStringWithShadow(s, (float)(i / 2 - getFontRenderer().getStringWidth(s) / 2), (float)(i1 - 10), 16777215);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(icons);
        }
    }

    private void renderPumpkinOverlay(ScaledResolution scaledRes) {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();
        mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0.0D, (double)scaledRes.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        worldrenderer.pos((double)scaledRes.getScaledWidth(), (double)scaledRes.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
        worldrenderer.pos((double)scaledRes.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders a Vignette arount the entire screen that changes with light level.
     *
     * @param lightLevel The current brightness
     * @param scaledRes  The current resolution of the game
     */
    private void renderVignette(float lightLevel, ScaledResolution scaledRes) {
        lightLevel = 1.0F - lightLevel;
        lightLevel = MathHelper.clamp_float(lightLevel, 0.0F, 1.0F);
        WorldBorder worldborder = mc.theWorld.getWorldBorder();
        float f = (float)worldborder.getClosestDistance(mc.thePlayer);
        double d0 = Math.min(worldborder.getResizeSpeed() * (double)worldborder.getWarningTime() * 1000.0D, Math.abs(worldborder.getTargetSize() - worldborder.getDiameter()));
        double d1 = Math.max((double)worldborder.getWarningDistance(), d0);

        if ((double)f < d1) {
            f = 1.0F - (float)((double)f / d1);
        }
        else {
            f = 0.0F;
        }

        prevVignetteBrightness = (float)((double)prevVignetteBrightness + (double)(lightLevel - prevVignetteBrightness) * 0.01D);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0);

        if (f > 0.0F) {
            GlStateManager.color(0.0F, f, f, 1.0F);
        }
        else {
            GlStateManager.color(prevVignetteBrightness, prevVignetteBrightness, prevVignetteBrightness, 1.0F);
        }

        mc.getTextureManager().bindTexture(vignetteTexPath);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0.0D, (double)scaledRes.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        worldrenderer.pos((double)scaledRes.getScaledWidth(), (double)scaledRes.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
        worldrenderer.pos((double)scaledRes.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    }

    private void renderPortal(float timeInPortal, ScaledResolution scaledRes) {
        if (timeInPortal < 1.0F) {
            timeInPortal = timeInPortal * timeInPortal;
            timeInPortal = timeInPortal * timeInPortal;
            timeInPortal = timeInPortal * 0.8F + 0.2F;
        }

        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, timeInPortal);
        mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        TextureAtlasSprite textureatlassprite = mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.portal.getDefaultState());
        float f = textureatlassprite.getMinU();
        float f1 = textureatlassprite.getMinV();
        float f2 = textureatlassprite.getMaxU();
        float f3 = textureatlassprite.getMaxV();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(0.0D, (double)scaledRes.getScaledHeight(), -90.0D).tex((double)f, (double)f3).endVertex();
        worldrenderer.pos((double)scaledRes.getScaledWidth(), (double)scaledRes.getScaledHeight(), -90.0D).tex((double)f2, (double)f3).endVertex();
        worldrenderer.pos((double)scaledRes.getScaledWidth(), 0.0D, -90.0D).tex((double)f2, (double)f1).endVertex();
        worldrenderer.pos(0.0D, 0.0D, -90.0D).tex((double)f, (double)f1).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player) {
        ItemStack itemstack = player.inventory.mainInventory[index];

        if (itemstack != null) {
            float f = (float)itemstack.animationsToGo - partialTicks;

            if (f > 0.0F) {
                GlStateManager.pushMatrix();
                float f1 = 1.0F + f / 5.0F;
                GlStateManager.translate((float)(xPos + 8), (float)(yPos + 12), 0.0F);
                GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float)(-(xPos + 8)), (float)(-(yPos + 12)), 0.0F);
            }

            itemRenderer.renderItemAndEffectIntoGUI(itemstack, xPos, yPos);

            if (f > 0.0F) {
                GlStateManager.popMatrix();
            }

            itemRenderer.renderItemOverlays(mc.fontRendererObj, itemstack, xPos, yPos);
        }
    }

    /**
     * The update tick for the ingame UI
     */
    public void updateTick() {
        if (recordPlayingUpFor > 0) {
            --recordPlayingUpFor;
        }

        if (titlesTimer > 0) {
            --titlesTimer;

            if (titlesTimer <= 0) {
                displayedTitle = "";
                displayedSubTitle = "";
            }
        }

        ++updateCounter;

        if (mc.thePlayer != null) {
            ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();

            if (itemstack == null) {
                remainingHighlightTicks = 0;
            }
            else if (highlightingItemStack != null && itemstack.getItem() == highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(itemstack, highlightingItemStack) && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == highlightingItemStack.getMetadata())) {
                if (remainingHighlightTicks > 0) {
                    --remainingHighlightTicks;
                }
            }
            else {
                remainingHighlightTicks = 40;
            }

            highlightingItemStack = itemstack;
        }
    }

    public void setRecordPlayingMessage(String recordName) {
        setRecordPlaying(I18n.format("record.nowPlaying", recordName), true);
    }

    public void setRecordPlaying(String message, boolean isPlaying) {
        recordPlaying = message;
        recordPlayingUpFor = 60;
        recordIsPlaying = isPlaying;
    }

    public void displayTitle(String title, String subTitle, int timeFadeIn, int displayTime, int timeFadeOut) {
        if (title == null && subTitle == null && timeFadeIn < 0 && displayTime < 0 && timeFadeOut < 0) {
            displayedTitle = "";
            displayedSubTitle = "";
            titlesTimer = 0;
        }
        else if (title != null) {
            displayedTitle = title;
            titlesTimer = titleFadeIn + titleDisplayTime + titleFadeOut;
        }
        else if (subTitle != null) {
            displayedSubTitle = subTitle;
        }
        else {
            if (timeFadeIn >= 0) {
                titleFadeIn = timeFadeIn;
            }

            if (displayTime >= 0) {
                titleDisplayTime = displayTime;
            }

            if (timeFadeOut >= 0) {
                titleFadeOut = timeFadeOut;
            }

            if (titlesTimer > 0) {
                titlesTimer = titleFadeIn + titleDisplayTime + titleFadeOut;
            }
        }
    }

    public void setRecordPlaying(IChatComponent component, boolean isPlaying) {
        setRecordPlaying(component.getUnformattedText(), isPlaying);
    }

    /**
     * returns a pointer to the persistant Chat GUI, containing all previous chat messages and such
     */
    public GuiNewChat getChatGUI() {
        return persistantChatGUI;
    }

    public int getUpdateCounter() {
        return updateCounter;
    }

    public FontRenderer getFontRenderer() {
        return mc.fontRendererObj;
    }

    public GuiSpectator getSpectatorGui() {
        return spectatorGui;
    }

    public GuiPlayerTabOverlay getTabList() {
        return overlayPlayerList;
    }

    /**
     * Reset the GuiPlayerTabOverlay's message header and footer
     */
    public void resetPlayersOverlayFooterHeader() {
        overlayPlayerList.resetFooterHeader();
    }
}
