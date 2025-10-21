package net.minecraft.client.gui.options.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.studiohartman.jamepad.ControllerButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.element.impl.GuiButton;
import net.minecraft.client.gui.element.impl.GuiListExtended;
import net.minecraft.client.gui.options.impl.GuiControlSettings;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.controller.Controller;
import net.minecraft.controller.bind.ControllerInputBinding;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.ArrayUtils;

public class GuiKeyBindingList extends GuiListExtended {
    private final GuiControlSettings controlsScreen;
    private final Minecraft mc;
    private final List<IGuiListEntry> listEntries = new ArrayList<>();
    private int maxListLabelWidth;

    public GuiKeyBindingList(GuiControlSettings controls, Minecraft mcIn) {
        super(mcIn, controls.width, controls.height, 63, controls.height - 32, 20);
        this.controlsScreen = controls;
        this.mc = mcIn;

        KeyBinding[] akeybinding = ArrayUtils.clone(mcIn.gameSettings.keyBindings);

        Arrays.sort(akeybinding);

        String s = null;

        for (KeyBinding binding : akeybinding) {
            String s1 = binding.getKeyCategory();

            if (!s1.equals(s)) {
                s = s1;
                listEntries.add(new CategoryEntry(s1));
            }

            int j = mcIn.fontRendererObj.getStringWidth(I18n.format(binding.getKeyDescription()));
            if (j > this.maxListLabelWidth) this.maxListLabelWidth = j;

            listEntries.add(new KeyEntry(binding));
        }

        if (Controller.isConnected()) {
            ControllerInputBinding[] acontrolbinding = ArrayUtils.clone(mcIn.gameSettings.controllerBindings);
            for (ControllerInputBinding binding : acontrolbinding) {
                String s1 = binding.getCategory();

                if (!s1.equals(s)) {
                    s = s1;
                    listEntries.add(new CategoryEntry(s1));
                }

                int j = mcIn.fontRendererObj.getStringWidth(I18n.format(binding.getDescription()));
                if (j > this.maxListLabelWidth) this.maxListLabelWidth = j;

                listEntries.add(new KeyEntry(binding));
            }
        }
    }

    protected int getSize() {
        return this.listEntries.size();
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public GuiListExtended.IGuiListEntry getListEntry(int index) {
        return this.listEntries.get(index);
    }

    protected int getScrollBarX() {
        return super.getScrollBarX() + 15;
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth() {
        return super.getListWidth() + 32;
    }

    public class CategoryEntry implements GuiListExtended.IGuiListEntry {
        private final String labelText;
        private final int labelWidth;

        public CategoryEntry(String p_i45028_2_) {
            this.labelText = I18n.format(p_i45028_2_);
            this.labelWidth = GuiKeyBindingList.this.mc.fontRendererObj.getStringWidth(this.labelText);
        }

        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
            GuiKeyBindingList.this.mc.fontRendererObj.drawString(this.labelText, GuiKeyBindingList.this.mc.currentScreen.width / 2 - this.labelWidth / 2, y + slotHeight - GuiKeyBindingList.this.mc.fontRendererObj.FONT_HEIGHT - 1, 16777215);
        }

        public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
            return false;
        }

        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
        }

        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
        }
    }

    public final class KeyEntry implements GuiListExtended.IGuiListEntry {
        private KeyBinding keybinding;
        private ControllerInputBinding controllerBinding;

        private final String keyDesc;
        private final GuiButton btnChangeKeyBinding;
        private final GuiButton btnReset;

        private KeyEntry(ControllerInputBinding p_i45029_2_) {
            this.controllerBinding = p_i45029_2_;
            this.keyDesc = I18n.format(p_i45029_2_.getDescription());
            this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 75, 20, I18n.format(p_i45029_2_.getDescription()));
            this.btnReset = new GuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset"));
        }

        private KeyEntry(KeyBinding p_i45029_2_) {
            this.keybinding = p_i45029_2_;
            this.keyDesc = I18n.format(p_i45029_2_.getKeyDescription());
            this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 75, 20, I18n.format(p_i45029_2_.getKeyDescription()));
            this.btnReset = new GuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset"));
        }

        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
            if (this.keybinding != null) {
                boolean flag = GuiKeyBindingList.this.controlsScreen.buttonId == this.keybinding;
                GuiKeyBindingList.this.mc.fontRendererObj.drawString(this.keyDesc, x + 90 - GuiKeyBindingList.this.maxListLabelWidth, y + slotHeight / 2 - GuiKeyBindingList.this.mc.fontRendererObj.FONT_HEIGHT / 2, 16777215);
                this.btnReset.xPosition = x + 190;
                this.btnReset.yPosition = y;
                this.btnReset.enabled = this.keybinding.getKeyCode() != this.keybinding.getKeyCodeDefault();
                this.btnReset.drawButton(GuiKeyBindingList.this.mc, mouseX, mouseY);
                this.btnChangeKeyBinding.xPosition = x + 105;
                this.btnChangeKeyBinding.yPosition = y;
                this.btnChangeKeyBinding.displayString = GameSettings.getKeyDisplayString(this.keybinding.getKeyCode());
                boolean flag1 = false;

                if (this.keybinding.getKeyCode() != 0) {
                    for (KeyBinding keybinding : GuiKeyBindingList.this.mc.gameSettings.keyBindings) {
                        if (keybinding != this.keybinding && keybinding.getKeyCode() == this.keybinding.getKeyCode()) {
                            flag1 = true;
                            break;
                        }
                    }
                }

                if (flag) {
                    this.btnChangeKeyBinding.displayString = EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + this.btnChangeKeyBinding.displayString + EnumChatFormatting.WHITE + " <";
                } else if (flag1) {
                    this.btnChangeKeyBinding.displayString = EnumChatFormatting.RED + this.btnChangeKeyBinding.displayString;
                }

                this.btnChangeKeyBinding.drawButton(GuiKeyBindingList.this.mc, mouseX, mouseY);
            } else {
                boolean flag = (GuiKeyBindingList.this.controlsScreen.controllerBindingId == this.controllerBinding);
                GuiKeyBindingList.this.mc.fontRendererObj.drawString(this.keyDesc, x + 90 - GuiKeyBindingList.this.maxListLabelWidth, y + slotHeight / 2 - GuiKeyBindingList.this.mc.fontRendererObj.FONT_HEIGHT / 2, 16777215);
                this.btnReset.xPosition = x + 190;
                this.btnReset.yPosition = y;
                this.btnReset.enabled = this.controllerBinding.getButton() != this.controllerBinding.getDefaultButton();
                this.btnReset.drawButton(GuiKeyBindingList.this.mc, mouseX, mouseY);
                this.btnChangeKeyBinding.xPosition = x + 105;
                this.btnChangeKeyBinding.yPosition = y;
                this.btnChangeKeyBinding.displayString = this.controllerBinding.getName();
                boolean flag1 = false;

                if (this.controllerBinding.getButton() != ControllerButton.GUIDE) {
                    for (ControllerInputBinding controllerBinding1 : GuiKeyBindingList.this.mc.gameSettings.controllerBindings) {
                        if (controllerBinding1 != this.controllerBinding && controllerBinding1.getButton() == this.controllerBinding.getButton()) {
                            flag1 = true;
                            break;
                        }
                    }
                }

                if (flag) {
                    this.btnChangeKeyBinding.displayString = EnumChatFormatting.WHITE + "> " + EnumChatFormatting.YELLOW + this.btnChangeKeyBinding.displayString + EnumChatFormatting.WHITE + " <";
                } else if (flag1) {
                    this.btnChangeKeyBinding.displayString = EnumChatFormatting.RED + this.btnChangeKeyBinding.displayString;
                }

                this.btnChangeKeyBinding.drawButton(GuiKeyBindingList.this.mc, mouseX, mouseY);
            }
        }

        public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
            if (this.keybinding != null) {
                if (this.btnChangeKeyBinding.mousePressed(GuiKeyBindingList.this.mc, p_148278_2_, p_148278_3_)) {
                    GuiKeyBindingList.this.controlsScreen.buttonId = this.keybinding;
                    return true;
                } else if (this.btnReset.mousePressed(GuiKeyBindingList.this.mc, p_148278_2_, p_148278_3_)) {
                    GuiKeyBindingList.this.mc.gameSettings.setOptionKeyBinding(this.keybinding, this.keybinding.getKeyCodeDefault());
                    KeyBinding.resetKeyBindingArrayAndHash();
                    return true;
                } else {
                    return false;
                }
            } else {
                if (this.btnChangeKeyBinding.mousePressed(GuiKeyBindingList.this.mc, p_148278_2_, p_148278_3_)) {
                    GuiKeyBindingList.this.controlsScreen.controllerBindingId = this.controllerBinding;
                    return true;
                } else if (this.btnReset.mousePressed(GuiKeyBindingList.this.mc, p_148278_2_, p_148278_3_)) {
                    if (this.controllerBinding.isAxis()) {
                        this.controllerBinding.setAxis(this.controllerBinding.getDefaultAxis());
                        return true;
                    } else {
                        this.controllerBinding.setButton(this.controllerBinding.getDefaultButton());
                        return true;
                    }
                }
            }

            return false;
        }

        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            this.btnChangeKeyBinding.mouseReleased(x, y);
            this.btnReset.mouseReleased(x, y);
        }

        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
        }
    }
}
