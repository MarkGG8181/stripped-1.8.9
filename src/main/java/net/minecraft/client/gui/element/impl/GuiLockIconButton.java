package net.minecraft.client.gui.element.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class GuiLockIconButton extends GuiButton
{
    private boolean locked = false;

    public GuiLockIconButton(int p_i45538_1_, int p_i45538_2_, int p_i45538_3_)
    {
        super(p_i45538_1_, p_i45538_2_, p_i45538_3_, 20, 20, "");
    }

    public boolean func_175230_c()
    {
        return this.locked;
    }

    public void func_175229_b(boolean p_175229_1_)
    {
        this.locked = p_175229_1_;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            mc.getTextureManager().bindTexture(GuiButton.buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            GuiLockIconButton.Icon guilockiconbutton$icon;

            if (this.locked)
            {
                if (!this.enabled)
                {
                    guilockiconbutton$icon = GuiLockIconButton.Icon.LOCKED_DISABLED;
                }
                else if (flag)
                {
                    guilockiconbutton$icon = GuiLockIconButton.Icon.LOCKED_HOVER;
                }
                else
                {
                    guilockiconbutton$icon = GuiLockIconButton.Icon.LOCKED;
                }
            }
            else if (!this.enabled)
            {
                guilockiconbutton$icon = GuiLockIconButton.Icon.UNLOCKED_DISABLED;
            }
            else if (flag)
            {
                guilockiconbutton$icon = GuiLockIconButton.Icon.UNLOCKED_HOVER;
            }
            else
            {
                guilockiconbutton$icon = GuiLockIconButton.Icon.UNLOCKED;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, guilockiconbutton$icon.func_178910_a(), guilockiconbutton$icon.func_178912_b(), this.width, this.height);
        }
    }

    static enum Icon
    {
        LOCKED(0, 146),
        LOCKED_HOVER(0, 166),
        LOCKED_DISABLED(0, 186),
        UNLOCKED(20, 146),
        UNLOCKED_HOVER(20, 166),
        UNLOCKED_DISABLED(20, 186);

        private final int x;
        private final int y;

        private Icon(int p_i45537_3_, int p_i45537_4_)
        {
            this.x = p_i45537_3_;
            this.y = p_i45537_4_;
        }

        public int func_178910_a()
        {
            return this.x;
        }

        public int func_178912_b()
        {
            return this.y;
        }
    }
}
