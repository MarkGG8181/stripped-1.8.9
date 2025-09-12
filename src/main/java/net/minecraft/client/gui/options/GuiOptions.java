package net.minecraft.client.gui.options;

import java.io.IOException;
import java.util.Objects;

import net.minecraft.client.gui.*;
import net.minecraft.client.gui.element.data.GuiYesNoCallback;
import net.minecraft.client.gui.element.impl.GuiButton;
import net.minecraft.client.gui.element.impl.GuiLockIconButton;
import net.minecraft.client.gui.element.impl.GuiOptionButton;
import net.minecraft.client.gui.element.impl.GuiYesNo;
import net.minecraft.client.gui.options.impl.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.EnumDifficulty;

public class GuiOptions extends GuiScreen implements GuiYesNoCallback {
    private final GuiScreen lastScreen;

    /**
     * Reference to the GameSettings object.
     */
    private final GameSettings game_settings_1;
    private GuiButton difficultyButton;
    private GuiLockIconButton lockButton;
    protected String title = "Options";

    public GuiOptions(GuiScreen p_i1046_1_, GameSettings p_i1046_2_) {
        this.lastScreen = p_i1046_1_;
        this.game_settings_1 = p_i1046_2_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        int i = 0;
        this.title = I18n.format("options.title");

        if (this.mc.theWorld != null) {
            EnumDifficulty enumdifficulty = this.mc.theWorld.getDifficulty();
            this.difficultyButton = new GuiButton(108, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, this.func_175355_a(enumdifficulty));
            this.buttonList.add(this.difficultyButton);

            if (this.mc.isSingleplayer() && !this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
                this.difficultyButton.setWidth(this.difficultyButton.getButtonWidth() - 20);
                this.lockButton = new GuiLockIconButton(109, this.difficultyButton.xPosition + this.difficultyButton.getButtonWidth(), this.difficultyButton.yPosition);
                this.buttonList.add(this.lockButton);
                this.lockButton.func_175229_b(this.mc.theWorld.getWorldInfo().isDifficultyLocked());
                this.lockButton.enabled = !this.lockButton.func_175230_c();
                this.difficultyButton.enabled = !this.lockButton.func_175230_c();
            }
            else {
                this.difficultyButton.enabled = false;
            }
        }

        buttonList.add(new GuiButton(110, width / 2 - 155, height / 6 + 48 - 6, 150, 20, I18n.format("options.skinCustomisation")));
        buttonList.add(new GuiButton(106, width / 2 - 155, height / 6 + 72 - 6, 150, 20, I18n.format("options.sounds")));
        buttonList.add(new GuiButton(101, width / 2 - 155, height / 6 + 96 - 6, 150, 20, I18n.format("options.video")));
        buttonList.add(new GuiButton(100, width / 2 + 5, height / 6 + 48 - 6, 150, 20, I18n.format("options.controls")));
        buttonList.add(new GuiButton(103, width / 2 + 5, height / 6 + 72 - 6, 150, 20, I18n.format("options.chat.title")));
        buttonList.add(new GuiButton(105, width / 2 - 155, height / 6 + 120 - 6, 150, 20, I18n.format("options.resourcepack")));
        buttonList.add(new GuiButton(200, width / 2 - 100, height / 6 + 168, I18n.format("gui.done")));
    }

    public String func_175355_a(EnumDifficulty p_175355_1_) {
        IChatComponent ichatcomponent = new ChatComponentText("");
        ichatcomponent.appendSibling(new ChatComponentTranslation("options.difficulty", new Object[0]));
        ichatcomponent.appendText(": ");
        ichatcomponent.appendSibling(new ChatComponentTranslation(p_175355_1_.getDifficultyResourceKey(), new Object[0]));
        return ichatcomponent.getFormattedText();
    }

    public void confirmClicked(boolean result, int id) {
        this.mc.displayGuiScreen(this);

        if (id == 109 && result && this.mc.theWorld != null) {
            this.mc.theWorld.getWorldInfo().setDifficultyLocked(true);
            this.lockButton.func_175229_b(true);
            this.lockButton.enabled = false;
            this.difficultyButton.enabled = false;
        }
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id < 100 && button instanceof GuiOptionButton optionButton) {
                GameSettings.Options gamesettings$options = optionButton.returnEnumOptions();
                this.game_settings_1.setOptionValue(gamesettings$options, 1);
                button.displayString = this.game_settings_1.getKeyBinding(Objects.requireNonNull(GameSettings.Options.getEnumOptions(button.id)));
            }

            if (button.id == 108) {
                this.mc.theWorld.getWorldInfo().setDifficulty(EnumDifficulty.getDifficultyEnum(this.mc.theWorld.getDifficulty().getDifficultyId() + 1));
                this.difficultyButton.displayString = this.func_175355_a(this.mc.theWorld.getDifficulty());
            }

            if (button.id == 109) {
                this.mc.displayGuiScreen(new GuiYesNo(this, (new ChatComponentTranslation("difficulty.lock.title")).getFormattedText(), (new ChatComponentTranslation("difficulty.lock.question", new Object[]{new ChatComponentTranslation(this.mc.theWorld.getWorldInfo().getDifficulty().getDifficultyResourceKey(), new Object[0])})).getFormattedText(), 109));
            }

            if (button.id == 110) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiCustomizeSkin(this));
            }

            if (button.id == 101) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiVideoSettings(this, this.game_settings_1));
            }

            if (button.id == 100) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiControlSettings(this, this.game_settings_1));
            }

            if (button.id == 103) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiChatSettings(this, this.game_settings_1));
            }

            if (button.id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.lastScreen);
            }

            if (button.id == 105) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiResourcePackSettings(this));
            }

            if (button.id == 106) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiSoundSettings(this, this.game_settings_1));
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
