package net.minecraft.client.gui.options.impl;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.element.impl.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class GuiSoundSettings extends GuiScreen
{
    private final GuiScreen parent;

    /** Reference to the GameSettings object. */
    private final GameSettings game_settings_4;
    protected String title = "Options";
    private String offDisplayString;

    public GuiSoundSettings(GuiScreen p_i45025_1_, GameSettings p_i45025_2_)
    {
        this.parent = p_i45025_1_;
        this.game_settings_4 = p_i45025_2_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        int i = 0;
        this.title = I18n.format("options.sounds.title", new Object[0]);
        this.offDisplayString = I18n.format("options.off", new Object[0]);
        this.buttonList.add(new GuiSoundSettings.Button(SoundCategory.MASTER.getCategoryId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.MASTER, true));
        i = i + 2;

        for (SoundCategory soundcategory : SoundCategory.values())
        {
            if (soundcategory != SoundCategory.MASTER)
            {
                this.buttonList.add(new GuiSoundSettings.Button(soundcategory.getCategoryId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundcategory, false));
                ++i;
            }
        }

        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done", new Object[0])));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 200)
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parent);
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected String getSoundVolume(SoundCategory p_146504_1_)
    {
        float f = this.game_settings_4.getSoundLevel(p_146504_1_);
        return f == 0.0F ? this.offDisplayString : (int)(f * 100.0F) + "%";
    }

    class Button extends GuiButton
    {
        private final SoundCategory field_146153_r;
        private final String categoryName;
        public float volume = 1.0F;
        public boolean pressed;

        public Button(int p_i45024_2_, int p_i45024_3_, int p_i45024_4_, SoundCategory p_i45024_5_, boolean p_i45024_6_)
        {
            super(p_i45024_2_, p_i45024_3_, p_i45024_4_, p_i45024_6_ ? 310 : 150, 20, "");
            this.field_146153_r = p_i45024_5_;
            this.categoryName = I18n.format("soundCategory." + p_i45024_5_.getCategoryName(), new Object[0]);
            this.displayString = this.categoryName + ": " + GuiSoundSettings.this.getSoundVolume(p_i45024_5_);
            this.volume = GuiSoundSettings.this.game_settings_4.getSoundLevel(p_i45024_5_);
        }

        protected int getHoverState(boolean mouseOver)
        {
            return 0;
        }

        protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
        {
            if (this.visible)
            {
                if (this.pressed)
                {
                    this.volume = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                    this.volume = MathHelper.clamp_float(this.volume, 0.0F, 1.0F);
                    mc.gameSettings.setSoundLevel(this.field_146153_r, this.volume);
                    mc.gameSettings.saveOptions();
                    this.displayString = this.categoryName + ": " + GuiSoundSettings.this.getSoundVolume(this.field_146153_r);
                }

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.drawTexturedModalRect(this.xPosition + (int)(this.volume * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
                this.drawTexturedModalRect(this.xPosition + (int)(this.volume * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
            }
        }

        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
        {
            if (super.mousePressed(mc, mouseX, mouseY))
            {
                this.volume = (float)(mouseX - (this.xPosition + 4)) / (float)(this.width - 8);
                this.volume = MathHelper.clamp_float(this.volume, 0.0F, 1.0F);
                mc.gameSettings.setSoundLevel(this.field_146153_r, this.volume);
                mc.gameSettings.saveOptions();
                this.displayString = this.categoryName + ": " + GuiSoundSettings.this.getSoundVolume(this.field_146153_r);
                this.pressed = true;
                return true;
            }
            else
            {
                return false;
            }
        }

        public void playPressSound(SoundHandler soundHandlerIn)
        {
        }

        public void mouseReleased(int mouseX, int mouseY)
        {
            if (this.pressed)
            {
                if (this.field_146153_r == SoundCategory.MASTER)
                {
                    float f = 1.0F;
                }
                else
                {
                    GuiSoundSettings.this.game_settings_4.getSoundLevel(this.field_146153_r);
                }

                GuiSoundSettings.this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            }

            this.pressed = false;
        }
    }
}
