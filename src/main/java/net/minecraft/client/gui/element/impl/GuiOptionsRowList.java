package net.minecraft.client.gui.element.impl;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public class GuiOptionsRowList extends GuiListExtended
{
    private final List<GuiOptionsRowList.Row> options = new ArrayList<>();

    public GuiOptionsRowList(Minecraft mcIn, int p_i45015_2_, int p_i45015_3_, int p_i45015_4_, int p_i45015_5_, int p_i45015_6_, GameSettings.Options... p_i45015_7_)
    {
        super(mcIn, p_i45015_2_, p_i45015_3_, p_i45015_4_, p_i45015_5_, p_i45015_6_);
        this.centerListVertically = false;

        for (int i = 0; i < p_i45015_7_.length; i += 2)
        {
            GameSettings.Options gamesettings$options = p_i45015_7_[i];
            GameSettings.Options gamesettings$options1 = i < p_i45015_7_.length - 1 ? p_i45015_7_[i + 1] : null;
            GuiButton guibutton = this.func148182A(mcIn, p_i45015_2_ / 2 - 155, 0, gamesettings$options);
            GuiButton guibutton1 = this.func148182A(mcIn, p_i45015_2_ / 2 - 155 + 160, 0, gamesettings$options1);
            this.options.add(new GuiOptionsRowList.Row(guibutton, guibutton1));
        }
    }

    private GuiButton func148182A(Minecraft mcIn, int p_148182_2_, int p_148182_3_, GameSettings.Options p_148182_4_)
    {
        if (p_148182_4_ == null)
        {
            return null;
        }
        else
        {
            int i = p_148182_4_.returnEnumOrdinal();
            return (GuiButton)(p_148182_4_.getEnumFloat() ? new GuiOptionSlider(i, p_148182_2_, p_148182_3_, p_148182_4_) : new GuiOptionButton(i, p_148182_2_, p_148182_3_, p_148182_4_, mcIn.gameSettings.getKeyBinding(p_148182_4_)));
        }
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public GuiOptionsRowList.Row getListEntry(int index)
    {
        return (GuiOptionsRowList.Row)this.options.get(index);
    }

    protected int getSize()
    {
        return this.options.size();
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth()
    {
        return 400;
    }

    protected int getScrollBarX()
    {
        return super.getScrollBarX() + 32;
    }

    public static class Row implements GuiListExtended.IGuiListEntry
    {
        private final Minecraft client = Minecraft.getMinecraft();
        private final GuiButton buttonA;
        private final GuiButton buttonB;

        public Row(GuiButton p_i45014_1_, GuiButton p_i45014_2_)
        {
            this.buttonA = p_i45014_1_;
            this.buttonB = p_i45014_2_;
        }

        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
        {
            if (this.buttonA != null)
            {
                this.buttonA.yPosition = y;
                this.buttonA.drawButton(this.client, mouseX, mouseY);
            }

            if (this.buttonB != null)
            {
                this.buttonB.yPosition = y;
                this.buttonB.drawButton(this.client, mouseX, mouseY);
            }
        }

        public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
        {
            if (this.buttonA.mousePressed(this.client, p_148278_2_, p_148278_3_))
            {
                if (this.buttonA instanceof GuiOptionButton button)
                {
                    this.client.gameSettings.setOptionValue(button.returnEnumOptions(), 1);
                    this.buttonA.displayString = this.client.gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(this.buttonA.id));
                }

                return true;
            }
            else if (this.buttonB != null && this.buttonB.mousePressed(this.client, p_148278_2_, p_148278_3_))
            {
                if (this.buttonB instanceof GuiOptionButton button)
                {
                    this.client.gameSettings.setOptionValue(button.returnEnumOptions(), 1);
                    this.buttonB.displayString = this.client.gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(this.buttonB.id));
                }

                return true;
            }
            else
            {
                return false;
            }
        }

        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY)
        {
            if (this.buttonA != null)
            {
                this.buttonA.mouseReleased(x, y);
            }

            if (this.buttonB != null)
            {
                this.buttonB.mouseReleased(x, y);
            }
        }

        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_)
        {
        }
    }
}
