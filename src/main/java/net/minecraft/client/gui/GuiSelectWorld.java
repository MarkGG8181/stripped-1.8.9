package net.minecraft.client.gui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.element.data.GuiYesNoCallback;
import net.minecraft.client.gui.element.impl.GuiButton;
import net.minecraft.client.gui.element.impl.GuiSlot;
import net.minecraft.client.gui.element.impl.GuiYesNo;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiSelectWorld extends GuiScreen implements GuiYesNoCallback {
    private static final Logger logger = LogManager.getLogger();
    private final DateFormat date = new SimpleDateFormat();
    protected GuiScreen parentScreen;
    protected String screenTitle = "Select world";

    /**
     * The list index of the currently-selected world
     */
    private int selectedIndex;
    private java.util.List<SaveFormatComparator> field_146639_s;
    private GuiSelectWorld.List availableWorlds;
    private String selectWorld;
    private String selectWorldConversion;
    private final String[] gamemodes = new String[4];
    private boolean confirmingDelete;
    private GuiButton deleteButton;
    private GuiButton selectButton;
    private GuiButton renameButton;
    private GuiButton recreateButton;

    public GuiSelectWorld(GuiScreen parentScreenIn) {
        this.parentScreen = parentScreenIn;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        this.screenTitle = I18n.format("selectWorld.title", new Object[0]);

        try {
            this.loadLevelList();
        } catch (AnvilConverterException anvilconverterexception) {
            logger.error((String) "Couldn\'t load level list", (Throwable) anvilconverterexception);
            this.mc.displayGuiScreen(new GuiErrorScreen("Unable to load worlds", anvilconverterexception.getMessage()));
            return;
        }

        this.selectWorld = I18n.format("selectWorld.world", new Object[0]);
        this.selectWorldConversion = I18n.format("selectWorld.conversion", new Object[0]);
        this.gamemodes[WorldSettings.GameType.SURVIVAL.getID()] = I18n.format("gameMode.survival", new Object[0]);
        this.gamemodes[WorldSettings.GameType.CREATIVE.getID()] = I18n.format("gameMode.creative", new Object[0]);
        this.gamemodes[WorldSettings.GameType.ADVENTURE.getID()] = I18n.format("gameMode.adventure", new Object[0]);
        this.gamemodes[WorldSettings.GameType.SPECTATOR.getID()] = I18n.format("gameMode.spectator", new Object[0]);
        this.availableWorlds = new GuiSelectWorld.List(this.mc);
        this.availableWorlds.registerScrollButtons(4, 5);
        this.addWorldSelectionButtons();
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.availableWorlds.handleMouseInput();
    }

    /**
     * Load the existing world saves for display
     */
    private void loadLevelList() throws AnvilConverterException {
        ISaveFormat isaveformat = this.mc.getSaveLoader();
        this.field_146639_s = isaveformat.getSaveList();
        Collections.sort(this.field_146639_s);
        this.selectedIndex = -1;
    }

    protected String func_146621_a(int p_146621_1_) {
        return ((SaveFormatComparator) this.field_146639_s.get(p_146621_1_)).getFileName();
    }

    protected String func_146614_d(int p_146614_1_) {
        String s = ((SaveFormatComparator) this.field_146639_s.get(p_146614_1_)).getDisplayName();

        if (StringUtils.isEmpty(s)) {
            s = I18n.format("selectWorld.world", new Object[0]) + " " + (p_146614_1_ + 1);
        }

        return s;
    }

    public void addWorldSelectionButtons() {
        this.buttonList.add(this.selectButton = new GuiButton(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.format("selectWorld.select", new Object[0])));
        this.buttonList.add(new GuiButton(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.format("selectWorld.create", new Object[0])));
        this.buttonList.add(this.renameButton = new GuiButton(6, this.width / 2 - 154, this.height - 28, 72, 20, I18n.format("selectWorld.rename", new Object[0])));
        this.buttonList.add(this.deleteButton = new GuiButton(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.format("selectWorld.delete", new Object[0])));
        this.buttonList.add(this.recreateButton = new GuiButton(7, this.width / 2 + 4, this.height - 28, 72, 20, I18n.format("selectWorld.recreate", new Object[0])));
        this.buttonList.add(new GuiButton(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.format("gui.cancel", new Object[0])));
        this.selectButton.enabled = false;
        this.deleteButton.enabled = false;
        this.renameButton.enabled = false;
        this.recreateButton.enabled = false;
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 2) {
                String s = this.func_146614_d(this.selectedIndex);

                if (s != null) {
                    this.confirmingDelete = true;
                    GuiYesNo guiyesno = makeDeleteWorldYesNo(this, s, this.selectedIndex);
                    this.mc.displayGuiScreen(guiyesno);
                }
            } else if (button.id == 1) {
                this.func_146615_e(this.selectedIndex);
            } else if (button.id == 3) {
                this.mc.displayGuiScreen(new GuiCreateWorld(this));
            } else if (button.id == 6) {
                this.mc.displayGuiScreen(new GuiRenameWorld(this, this.func_146621_a(this.selectedIndex)));
            } else if (button.id == 0) {
                this.mc.displayGuiScreen(this.parentScreen);
            } else if (button.id == 7) {
                GuiCreateWorld guicreateworld = new GuiCreateWorld(this);
                ISaveHandler isavehandler = this.mc.getSaveLoader().getSaveLoader(this.func_146621_a(this.selectedIndex), false);
                WorldInfo worldinfo = isavehandler.loadWorldInfo();
                isavehandler.flush();
                guicreateworld.recreateFromExistingWorld(worldinfo);
                this.mc.displayGuiScreen(guicreateworld);
            } else {
                this.availableWorlds.actionPerformed(button);
            }
        }
    }

    public void func_146615_e(int p_146615_1_) {
        this.mc.displayGuiScreen((GuiScreen) null);

        String s = this.func_146621_a(p_146615_1_);

        if (s == null) {
            s = "World" + p_146615_1_;
        }

        String s1 = this.func_146614_d(p_146615_1_);

        if (s1 == null) {
            s1 = "World" + p_146615_1_;
        }

        if (this.mc.getSaveLoader().canLoadWorld(s)) {
            this.mc.launchIntegratedServer(s, s1, (WorldSettings) null);
        }
    }

    public void confirmClicked(boolean result, int id) {
        if (this.confirmingDelete) {
            this.confirmingDelete = false;

            if (result) {
                ISaveFormat isaveformat = this.mc.getSaveLoader();
                isaveformat.flushCache();
                isaveformat.deleteWorldDirectory(this.func_146621_a(id));

                try {
                    this.loadLevelList();
                } catch (AnvilConverterException anvilconverterexception) {
                    logger.error((String) "Couldn\'t load level list", (Throwable) anvilconverterexception);
                }
            }

            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.availableWorlds.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Generate a GuiYesNo asking for confirmation to delete a world
     * <p>
     * Called when user selects the "Delete" button.
     *
     * @param selectWorld A reference back to the GuiSelectWorld spawning the GuiYesNo
     * @param name        The name of the world selected for deletion
     * @param id          An arbitrary integer passed back to selectWorld's confirmClicked method
     */
    public static GuiYesNo makeDeleteWorldYesNo(GuiYesNoCallback selectWorld, String name, int id) {
        String s = I18n.format("selectWorld.deleteQuestion", new Object[0]);
        String s1 = "\'" + name + "\' " + I18n.format("selectWorld.deleteWarning", new Object[0]);
        String s2 = I18n.format("selectWorld.deleteButton", new Object[0]);
        String s3 = I18n.format("gui.cancel", new Object[0]);
        GuiYesNo guiyesno = new GuiYesNo(selectWorld, s, s1, s2, s3, id);
        return guiyesno;
    }

    class List extends GuiSlot {
        public List(Minecraft mcIn) {
            super(mcIn, GuiSelectWorld.this.width, GuiSelectWorld.this.height, 32, GuiSelectWorld.this.height - 64, 36);
        }

        protected int getSize() {
            return GuiSelectWorld.this.field_146639_s.size();
        }

        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
            GuiSelectWorld.this.selectedIndex = slotIndex;
            boolean flag = GuiSelectWorld.this.selectedIndex >= 0 && GuiSelectWorld.this.selectedIndex < this.getSize();
            GuiSelectWorld.this.selectButton.enabled = flag;
            GuiSelectWorld.this.deleteButton.enabled = flag;
            GuiSelectWorld.this.renameButton.enabled = flag;
            GuiSelectWorld.this.recreateButton.enabled = flag;

            if (isDoubleClick && flag) {
                GuiSelectWorld.this.func_146615_e(slotIndex);
            }
        }

        protected boolean isSelected(int slotIndex) {
            return slotIndex == GuiSelectWorld.this.selectedIndex;
        }

        protected int getContentHeight() {
            return GuiSelectWorld.this.field_146639_s.size() * 36;
        }

        protected void drawBackground() {
            GuiSelectWorld.this.drawDefaultBackground();
        }

        protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn) {
            SaveFormatComparator saveformatcomparator = GuiSelectWorld.this.field_146639_s.get(entryID);
            String s = saveformatcomparator.getDisplayName();

            if (StringUtils.isEmpty(s)) {
                s = GuiSelectWorld.this.selectWorld + " " + (entryID + 1);
            }

            String s1 = saveformatcomparator.getFileName();
            s1 = s1 + " (" + GuiSelectWorld.this.date.format(new Date(saveformatcomparator.getLastTimePlayed()));
            s1 = s1.replace("\u202F", " ");
            s1 = s1 + ")";
            String s2 = "";

            if (saveformatcomparator.requiresConversion()) {
                s2 = GuiSelectWorld.this.selectWorldConversion + " " + s2;
            } else {
                s2 = GuiSelectWorld.this.gamemodes[saveformatcomparator.getEnumGameType().getID()];

                if (saveformatcomparator.isHardcoreModeEnabled()) {
                    s2 = EnumChatFormatting.DARK_RED + I18n.format("gameMode.hardcore", new Object[0]) + EnumChatFormatting.RESET;
                }

                if (saveformatcomparator.getCheatsEnabled()) {
                    s2 = s2 + ", " + I18n.format("selectWorld.cheats");
                }
            }

            GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, s, p_180791_2_ + 2, p_180791_3_ + 1, 16777215);
            GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, s1, p_180791_2_ + 2, p_180791_3_ + 12, 8421504);
            GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, s2, p_180791_2_ + 2, p_180791_3_ + 12 + 10, 8421504);
        }
    }
}
