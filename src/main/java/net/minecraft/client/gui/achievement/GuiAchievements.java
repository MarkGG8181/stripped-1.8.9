package net.minecraft.client.gui.achievement;

import java.io.IOException;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.element.impl.GuiButton;
import net.minecraft.client.gui.element.impl.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.element.data.IProgressMeter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class GuiAchievements extends GuiScreen implements IProgressMeter
{
    private static final int X_MIN = AchievementList.minDisplayColumn * 24 - 112;
    private static final int Y_MIN = AchievementList.minDisplayRow * 24 - 112;
    private static final int X_MAX = AchievementList.maxDisplayColumn * 24 - 77;
    private static final int Y_MAX = AchievementList.maxDisplayRow * 24 - 77;
    private static final ResourceLocation ACHIEVEMENT_BACKGROUND = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    protected GuiScreen parentScreen;
    protected int imageWidth = 256;
    protected int imageHeight = 202;
    protected int xLastScroll;
    protected int yLastScroll;
    protected float zoom = 1.0F;
    protected double xScrollO;
    protected double yScrollO;
    protected double xScrollP;
    protected double yScrollP;
    protected double xScrollTarget;
    protected double yScrollTarget;
    private int scrolling;
    private StatFileWriter statFileWriter;
    private boolean loadingAchievements = true;

    public GuiAchievements(GuiScreen parentScreenIn, StatFileWriter statFileWriterIn)
    {
        this.parentScreen = parentScreenIn;
        this.statFileWriter = statFileWriterIn;
        int i = 141;
        int j = 141;
        this.xScrollO = this.xScrollP = this.xScrollTarget = (double)(AchievementList.openInventory.displayColumn * 24 - i / 2 - 12);
        this.yScrollO = this.yScrollP = this.yScrollTarget = (double)(AchievementList.openInventory.displayRow * 24 - j / 2);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS));
        this.buttonList.clear();
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 24, this.height / 2 + 74, 80, 20, I18n.format("gui.done", new Object[0])));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (!this.loadingAchievements)
        {
            if (button.id == 1)
            {
                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode())
        {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
        }
        else
        {
            super.keyTyped(typedChar, keyCode);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.loadingAchievements)
        {
            this.drawDefaultBackground();
            this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingStats", new Object[0]), this.width / 2, this.height / 2, 16777215);
            this.drawCenteredString(this.fontRendererObj, lanSearchStates[(int)(Minecraft.getSystemTime() / 150L % (long)lanSearchStates.length)], this.width / 2, this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2, 16777215);
        }
        else
        {
            if (Mouse.isButtonDown(0))
            {
                int i = (this.width - this.imageWidth) / 2;
                int j = (this.height - this.imageHeight) / 2;
                int k = i + 8;
                int l = j + 17;

                if ((this.scrolling == 0 || this.scrolling == 1) && mouseX >= k && mouseX < k + 224 && mouseY >= l && mouseY < l + 155)
                {
                    if (this.scrolling == 0)
                    {
                        this.scrolling = 1;
                    }
                    else
                    {
                        this.xScrollP -= (double)((float)(mouseX - this.xLastScroll) * this.zoom);
                        this.yScrollP -= (double)((float)(mouseY - this.yLastScroll) * this.zoom);
                        this.xScrollTarget = this.xScrollO = this.xScrollP;
                        this.yScrollTarget = this.yScrollO = this.yScrollP;
                    }

                    this.xLastScroll = mouseX;
                    this.yLastScroll = mouseY;
                }
            }
            else
            {
                this.scrolling = 0;
            }

            int i1 = Mouse.getDWheel();
            float f3 = this.zoom;

            if (i1 < 0)
            {
                this.zoom += 0.25F;
            }
            else if (i1 > 0)
            {
                this.zoom -= 0.25F;
            }

            this.zoom = MathHelper.clamp_float(this.zoom, 1.0F, 2.0F);

            if (this.zoom != f3)
            {
                float f5 = f3 - this.zoom;
                float f4 = f3 * (float)this.imageWidth;
                float f = f3 * (float)this.imageHeight;
                float f1 = this.zoom * (float)this.imageWidth;
                float f2 = this.zoom * (float)this.imageHeight;
                this.xScrollP -= (double)((f1 - f4) * 0.5F);
                this.yScrollP -= (double)((f2 - f) * 0.5F);
                this.xScrollTarget = this.xScrollO = this.xScrollP;
                this.yScrollTarget = this.yScrollO = this.yScrollP;
            }

            if (this.xScrollTarget < (double)X_MIN)
            {
                this.xScrollTarget = (double)X_MIN;
            }

            if (this.yScrollTarget < (double)Y_MIN)
            {
                this.yScrollTarget = (double)Y_MIN;
            }

            if (this.xScrollTarget >= (double)X_MAX)
            {
                this.xScrollTarget = (double)(X_MAX - 1);
            }

            if (this.yScrollTarget >= (double)Y_MAX)
            {
                this.yScrollTarget = (double)(Y_MAX - 1);
            }

            this.drawDefaultBackground();
            this.drawAchievementScreen(mouseX, mouseY, partialTicks);
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.drawTitle();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
        }
    }

    public void doneLoading()
    {
        if (this.loadingAchievements)
        {
            this.loadingAchievements = false;
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        if (!this.loadingAchievements)
        {
            this.xScrollO = this.xScrollP;
            this.yScrollO = this.yScrollP;
            double d0 = this.xScrollTarget - this.xScrollP;
            double d1 = this.yScrollTarget - this.yScrollP;

            if (d0 * d0 + d1 * d1 < 4.0D)
            {
                this.xScrollP += d0;
                this.yScrollP += d1;
            }
            else
            {
                this.xScrollP += d0 * 0.85D;
                this.yScrollP += d1 * 0.85D;
            }
        }
    }

    protected void drawTitle()
    {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.fontRendererObj.drawString(I18n.format("gui.achievements", new Object[0]), i + 15, j + 5, 4210752);
    }

    protected void drawAchievementScreen(int p_146552_1_, int p_146552_2_, float p_146552_3_)
    {
        int i = MathHelper.floor_double(this.xScrollO + (this.xScrollP - this.xScrollO) * (double)p_146552_3_);
        int j = MathHelper.floor_double(this.yScrollO + (this.yScrollP - this.yScrollO) * (double)p_146552_3_);

        if (i < X_MIN)
        {
            i = X_MIN;
        }

        if (j < Y_MIN)
        {
            j = Y_MIN;
        }

        if (i >= X_MAX)
        {
            i = X_MAX - 1;
        }

        if (j >= Y_MAX)
        {
            j = Y_MAX - 1;
        }

        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        int i1 = k + 16;
        int j1 = l + 17;
        this.zLevel = 0.0F;
        GlStateManager.depthFunc(518);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)i1, (float)j1, -200.0F);
        GlStateManager.scale(1.0F / this.zoom, 1.0F / this.zoom, 0.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        int k1 = i + 288 >> 4;
        int l1 = j + 288 >> 4;
        int i2 = (i + 288) % 16;
        int j2 = (j + 288) % 16;
        int k2 = 4;
        int l2 = 8;
        int i3 = 10;
        int j3 = 22;
        int k3 = 37;
        Random random = new Random();
        float f = 16.0F / this.zoom;
        float f1 = 16.0F / this.zoom;

        for (int l3 = 0; (float)l3 * f - (float)j2 < 155.0F; ++l3)
        {
            float f2 = 0.6F - (float)(l1 + l3) / 25.0F * 0.3F;
            GlStateManager.color(f2, f2, f2, 1.0F);

            for (int i4 = 0; (float)i4 * f1 - (float)i2 < 224.0F; ++i4)
            {
                random.setSeed((long)(this.mc.getSession().getPlayerID().hashCode() + k1 + i4 + (l1 + l3) * 16));
                int j4 = random.nextInt(1 + l1 + l3) + (l1 + l3) / 2;
                TextureAtlasSprite textureatlassprite = this.func_175371_a(Blocks.sand);

                if (j4 <= 37 && l1 + l3 != 35)
                {
                    if (j4 == 22)
                    {
                        if (random.nextInt(2) == 0)
                        {
                            textureatlassprite = this.func_175371_a(Blocks.diamond_ore);
                        }
                        else
                        {
                            textureatlassprite = this.func_175371_a(Blocks.redstone_ore);
                        }
                    }
                    else if (j4 == 10)
                    {
                        textureatlassprite = this.func_175371_a(Blocks.iron_ore);
                    }
                    else if (j4 == 8)
                    {
                        textureatlassprite = this.func_175371_a(Blocks.coal_ore);
                    }
                    else if (j4 > 4)
                    {
                        textureatlassprite = this.func_175371_a(Blocks.stone);
                    }
                    else if (j4 > 0)
                    {
                        textureatlassprite = this.func_175371_a(Blocks.dirt);
                    }
                }
                else
                {
                    Block block = Blocks.bedrock;
                    textureatlassprite = this.func_175371_a(block);
                }

                this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                this.drawTexturedModalRect(i4 * 16 - i2, l3 * 16 - j2, textureatlassprite, 16, 16);
            }
        }

        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);

        for (int j5 = 0; j5 < AchievementList.achievementList.size(); ++j5)
        {
            Achievement achievement1 = (Achievement)AchievementList.achievementList.get(j5);

            if (achievement1.parentAchievement != null)
            {
                int k5 = achievement1.displayColumn * 24 - i + 11;
                int l5 = achievement1.displayRow * 24 - j + 11;
                int j6 = achievement1.parentAchievement.displayColumn * 24 - i + 11;
                int k6 = achievement1.parentAchievement.displayRow * 24 - j + 11;
                boolean flag = this.statFileWriter.hasAchievementUnlocked(achievement1);
                boolean flag1 = this.statFileWriter.canUnlockAchievement(achievement1);
                int k4 = this.statFileWriter.func_150874_c(achievement1);

                if (k4 <= 4)
                {
                    int l4 = -16777216;

                    if (flag)
                    {
                        l4 = -6250336;
                    }
                    else if (flag1)
                    {
                        l4 = -16711936;
                    }

                    this.drawHorizontalLine(k5, j6, l5, l4);
                    this.drawVerticalLine(j6, l5, k6, l4);

                    if (k5 > j6)
                    {
                        this.drawTexturedModalRect(k5 - 11 - 7, l5 - 5, 114, 234, 7, 11);
                    }
                    else if (k5 < j6)
                    {
                        this.drawTexturedModalRect(k5 + 11, l5 - 5, 107, 234, 7, 11);
                    }
                    else if (l5 > k6)
                    {
                        this.drawTexturedModalRect(k5 - 5, l5 - 11 - 7, 96, 234, 11, 7);
                    }
                    else if (l5 < k6)
                    {
                        this.drawTexturedModalRect(k5 - 5, l5 + 11, 96, 241, 11, 7);
                    }
                }
            }
        }

        Achievement achievement = null;
        float f3 = (float)(p_146552_1_ - i1) * this.zoom;
        float f4 = (float)(p_146552_2_ - j1) * this.zoom;
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();

        for (int i6 = 0; i6 < AchievementList.achievementList.size(); ++i6)
        {
            Achievement achievement2 = (Achievement)AchievementList.achievementList.get(i6);
            int l6 = achievement2.displayColumn * 24 - i;
            int j7 = achievement2.displayRow * 24 - j;

            if (l6 >= -24 && j7 >= -24 && (float)l6 <= 224.0F * this.zoom && (float)j7 <= 155.0F * this.zoom)
            {
                int l7 = this.statFileWriter.func_150874_c(achievement2);

                if (this.statFileWriter.hasAchievementUnlocked(achievement2))
                {
                    float f5 = 0.75F;
                    GlStateManager.color(f5, f5, f5, 1.0F);
                }
                else if (this.statFileWriter.canUnlockAchievement(achievement2))
                {
                    float f6 = 1.0F;
                    GlStateManager.color(f6, f6, f6, 1.0F);
                }
                else if (l7 < 3)
                {
                    float f7 = 0.3F;
                    GlStateManager.color(f7, f7, f7, 1.0F);
                }
                else if (l7 == 3)
                {
                    float f8 = 0.2F;
                    GlStateManager.color(f8, f8, f8, 1.0F);
                }
                else
                {
                    if (l7 != 4)
                    {
                        continue;
                    }

                    float f9 = 0.1F;
                    GlStateManager.color(f9, f9, f9, 1.0F);
                }

                this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);

                if (achievement2.getSpecial())
                {
                    this.drawTexturedModalRect(l6 - 2, j7 - 2, 26, 202, 26, 26);
                }
                else
                {
                    this.drawTexturedModalRect(l6 - 2, j7 - 2, 0, 202, 26, 26);
                }

                if (!this.statFileWriter.canUnlockAchievement(achievement2))
                {
                    float f10 = 0.1F;
                    GlStateManager.color(f10, f10, f10, 1.0F);
                    this.itemRender.isNotRenderingEffectsInGUI(false);
                }

                GlStateManager.enableLighting();
                GlStateManager.enableCull();
                this.itemRender.renderItemAndEffectIntoGUI(achievement2.theItemStack, l6 + 3, j7 + 3);
                GlStateManager.blendFunc(770, 771);
                GlStateManager.disableLighting();

                if (!this.statFileWriter.canUnlockAchievement(achievement2))
                {
                    this.itemRender.isNotRenderingEffectsInGUI(true);
                }

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                if (f3 >= (float)l6 && f3 <= (float)(l6 + 22) && f4 >= (float)j7 && f4 <= (float)(j7 + 22))
                {
                    achievement = achievement2;
                }
            }
        }

        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);
        this.drawTexturedModalRect(k, l, 0, 0, this.imageWidth, this.imageHeight);
        this.zLevel = 0.0F;
        GlStateManager.depthFunc(515);
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        super.drawScreen(p_146552_1_, p_146552_2_, p_146552_3_);

        if (achievement != null)
        {
            String s = achievement.getStatName().getUnformattedText();
            String s1 = achievement.getDescription();
            int i7 = p_146552_1_ + 12;
            int k7 = p_146552_2_ - 4;
            int i8 = this.statFileWriter.func_150874_c(achievement);

            if (this.statFileWriter.canUnlockAchievement(achievement))
            {
                int j8 = Math.max(this.fontRendererObj.getStringWidth(s), 120);
                int i9 = this.fontRendererObj.splitStringWidth(s1, j8);

                if (this.statFileWriter.hasAchievementUnlocked(achievement))
                {
                    i9 += 12;
                }

                this.drawGradientRect(i7 - 3, k7 - 3, i7 + j8 + 3, k7 + i9 + 3 + 12, -1073741824, -1073741824);
                this.fontRendererObj.drawSplitString(s1, i7, k7 + 12, j8, -6250336);

                if (this.statFileWriter.hasAchievementUnlocked(achievement))
                {
                    this.fontRendererObj.drawStringWithShadow(I18n.format("achievement.taken", new Object[0]), (float)i7, (float)(k7 + i9 + 4), -7302913);
                }
            }
            else if (i8 == 3)
            {
                s = I18n.format("achievement.unknown", new Object[0]);
                int k8 = Math.max(this.fontRendererObj.getStringWidth(s), 120);
                String s2 = (new ChatComponentTranslation("achievement.requires", new Object[] {achievement.parentAchievement.getStatName()})).getUnformattedText();
                int i5 = this.fontRendererObj.splitStringWidth(s2, k8);
                this.drawGradientRect(i7 - 3, k7 - 3, i7 + k8 + 3, k7 + i5 + 12 + 3, -1073741824, -1073741824);
                this.fontRendererObj.drawSplitString(s2, i7, k7 + 12, k8, -9416624);
            }
            else if (i8 < 3)
            {
                int l8 = Math.max(this.fontRendererObj.getStringWidth(s), 120);
                String s3 = (new ChatComponentTranslation("achievement.requires", new Object[] {achievement.parentAchievement.getStatName()})).getUnformattedText();
                int j9 = this.fontRendererObj.splitStringWidth(s3, l8);
                this.drawGradientRect(i7 - 3, k7 - 3, i7 + l8 + 3, k7 + j9 + 12 + 3, -1073741824, -1073741824);
                this.fontRendererObj.drawSplitString(s3, i7, k7 + 12, l8, -9416624);
            }
            else
            {
                s = null;
            }

            if (s != null)
            {
                this.fontRendererObj.drawStringWithShadow(s, (float)i7, (float)k7, this.statFileWriter.canUnlockAchievement(achievement) ? (achievement.getSpecial() ? -128 : -1) : (achievement.getSpecial() ? -8355776 : -8355712));
            }
        }

        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();
    }

    private TextureAtlasSprite func_175371_a(Block p_175371_1_)
    {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(p_175371_1_.getDefaultState());
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return !this.loadingAchievements;
    }
}
