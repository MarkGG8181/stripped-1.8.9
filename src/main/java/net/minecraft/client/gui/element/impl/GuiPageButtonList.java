package net.minecraft.client.gui.element.impl;

import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.element.Gui;
import net.minecraft.util.IntHashMap;

public class GuiPageButtonList extends GuiListExtended
{
    private final List<GuiPageButtonList.GuiEntry> entries = Lists.<GuiPageButtonList.GuiEntry>newArrayList();
    private final IntHashMap<Gui> componentMap = new IntHashMap();
    private final List<GuiTextField> editBoxes = Lists.<GuiTextField>newArrayList();
    private final GuiPageButtonList.GuiListEntry[][] pages;
    private int page;
    private GuiPageButtonList.GuiResponder responder;
    private Gui focusedControl;

    public GuiPageButtonList(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn, GuiPageButtonList.GuiResponder p_i45536_7_, GuiPageButtonList.GuiListEntry[]... p_i45536_8_)
    {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.responder = p_i45536_7_;
        this.pages = p_i45536_8_;
        this.centerListVertically = false;
        this.func_178069_s();
        this.func_178055_t();
    }

    private void func_178069_s()
    {
        for (GuiPageButtonList.GuiListEntry[] aguipagebuttonlist$guilistentry : this.pages)
        {
            for (int i = 0; i < aguipagebuttonlist$guilistentry.length; i += 2)
            {
                GuiPageButtonList.GuiListEntry guipagebuttonlist$guilistentry = aguipagebuttonlist$guilistentry[i];
                GuiPageButtonList.GuiListEntry guipagebuttonlist$guilistentry1 = i < aguipagebuttonlist$guilistentry.length - 1 ? aguipagebuttonlist$guilistentry[i + 1] : null;
                Gui gui = this.func_178058_a(guipagebuttonlist$guilistentry, 0, guipagebuttonlist$guilistentry1 == null);
                Gui gui1 = this.func_178058_a(guipagebuttonlist$guilistentry1, 160, guipagebuttonlist$guilistentry == null);
                GuiPageButtonList.GuiEntry guipagebuttonlist$guientry = new GuiPageButtonList.GuiEntry(gui, gui1);
                this.entries.add(guipagebuttonlist$guientry);

                if (guipagebuttonlist$guilistentry != null && gui != null)
                {
                    this.componentMap.addKey(guipagebuttonlist$guilistentry.func_178935_b(), gui);

                    if (gui instanceof GuiTextField)
                    {
                        this.editBoxes.add((GuiTextField)gui);
                    }
                }

                if (guipagebuttonlist$guilistentry1 != null && gui1 != null)
                {
                    this.componentMap.addKey(guipagebuttonlist$guilistentry1.func_178935_b(), gui1);

                    if (gui1 instanceof GuiTextField)
                    {
                        this.editBoxes.add((GuiTextField)gui1);
                    }
                }
            }
        }
    }

    private void func_178055_t()
    {
        this.entries.clear();

        for (int i = 0; i < this.pages[this.page].length; i += 2)
        {
            GuiPageButtonList.GuiListEntry guipagebuttonlist$guilistentry = this.pages[this.page][i];
            GuiPageButtonList.GuiListEntry guipagebuttonlist$guilistentry1 = i < this.pages[this.page].length - 1 ? this.pages[this.page][i + 1] : null;
            Gui gui = (Gui)this.componentMap.lookup(guipagebuttonlist$guilistentry.func_178935_b());
            Gui gui1 = guipagebuttonlist$guilistentry1 != null ? (Gui)this.componentMap.lookup(guipagebuttonlist$guilistentry1.func_178935_b()) : null;
            GuiPageButtonList.GuiEntry guipagebuttonlist$guientry = new GuiPageButtonList.GuiEntry(gui, gui1);
            this.entries.add(guipagebuttonlist$guientry);
        }
    }

    public void func_181156_c(int p_181156_1_)
    {
        if (p_181156_1_ != this.page)
        {
            int i = this.page;
            this.page = p_181156_1_;
            this.func_178055_t();
            this.func_178060_e(i, p_181156_1_);
            this.amountScrolled = 0.0F;
        }
    }

    public int func_178059_e()
    {
        return this.page;
    }

    public int func_178057_f()
    {
        return this.pages.length;
    }

    public Gui func_178056_g()
    {
        return this.focusedControl;
    }

    public void func_178071_h()
    {
        if (this.page > 0)
        {
            this.func_181156_c(this.page - 1);
        }
    }

    public void func_178064_i()
    {
        if (this.page < this.pages.length - 1)
        {
            this.func_181156_c(this.page + 1);
        }
    }

    public Gui func_178061_c(int p_178061_1_)
    {
        return (Gui)this.componentMap.lookup(p_178061_1_);
    }

    private void func_178060_e(int p_178060_1_, int p_178060_2_)
    {
        for (GuiPageButtonList.GuiListEntry guipagebuttonlist$guilistentry : this.pages[p_178060_1_])
        {
            if (guipagebuttonlist$guilistentry != null)
            {
                this.func_178066_a((Gui)this.componentMap.lookup(guipagebuttonlist$guilistentry.func_178935_b()), false);
            }
        }

        for (GuiPageButtonList.GuiListEntry guipagebuttonlist$guilistentry1 : this.pages[p_178060_2_])
        {
            if (guipagebuttonlist$guilistentry1 != null)
            {
                this.func_178066_a((Gui)this.componentMap.lookup(guipagebuttonlist$guilistentry1.func_178935_b()), true);
            }
        }
    }

    private void func_178066_a(Gui p_178066_1_, boolean p_178066_2_)
    {
        if (p_178066_1_ instanceof GuiButton)
        {
            ((GuiButton)p_178066_1_).visible = p_178066_2_;
        }
        else if (p_178066_1_ instanceof GuiTextField)
        {
            ((GuiTextField)p_178066_1_).setVisible(p_178066_2_);
        }
        else if (p_178066_1_ instanceof GuiLabel)
        {
            ((GuiLabel)p_178066_1_).visible = p_178066_2_;
        }
    }

    private Gui func_178058_a(GuiPageButtonList.GuiListEntry p_178058_1_, int p_178058_2_, boolean p_178058_3_)
    {
        return (Gui)(p_178058_1_ instanceof GuiPageButtonList.GuiSlideEntry ? this.func_178067_a(this.width / 2 - 155 + p_178058_2_, 0, (GuiPageButtonList.GuiSlideEntry)p_178058_1_) : (p_178058_1_ instanceof GuiPageButtonList.GuiButtonEntry ? this.func_178065_a(this.width / 2 - 155 + p_178058_2_, 0, (GuiPageButtonList.GuiButtonEntry)p_178058_1_) : (p_178058_1_ instanceof GuiPageButtonList.EditBoxEntry ? this.func_178068_a(this.width / 2 - 155 + p_178058_2_, 0, (GuiPageButtonList.EditBoxEntry)p_178058_1_) : (p_178058_1_ instanceof GuiPageButtonList.GuiLabelEntry ? this.func_178063_a(this.width / 2 - 155 + p_178058_2_, 0, (GuiPageButtonList.GuiLabelEntry)p_178058_1_, p_178058_3_) : null))));
    }

    public void func_181155_a(boolean p_181155_1_)
    {
        for (GuiPageButtonList.GuiEntry guipagebuttonlist$guientry : this.entries)
        {
            if (guipagebuttonlist$guientry.component1 instanceof GuiButton)
            {
                ((GuiButton)guipagebuttonlist$guientry.component1).enabled = p_181155_1_;
            }

            if (guipagebuttonlist$guientry.component2 instanceof GuiButton)
            {
                ((GuiButton)guipagebuttonlist$guientry.component2).enabled = p_181155_1_;
            }
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent)
    {
        boolean flag = super.mouseClicked(mouseX, mouseY, mouseEvent);
        int i = this.getSlotIndexFromScreenCoords(mouseX, mouseY);

        if (i >= 0)
        {
            GuiPageButtonList.GuiEntry guipagebuttonlist$guientry = this.getListEntry(i);

            if (this.focusedControl != guipagebuttonlist$guientry.focusedControl && this.focusedControl != null && this.focusedControl instanceof GuiTextField)
            {
                ((GuiTextField)this.focusedControl).setFocused(false);
            }

            this.focusedControl = guipagebuttonlist$guientry.focusedControl;
        }

        return flag;
    }

    private GuiSlider func_178067_a(int p_178067_1_, int p_178067_2_, GuiPageButtonList.GuiSlideEntry p_178067_3_)
    {
        GuiSlider guislider = new GuiSlider(this.responder, p_178067_3_.func_178935_b(), p_178067_1_, p_178067_2_, p_178067_3_.func_178936_c(), p_178067_3_.func_178943_e(), p_178067_3_.func_178944_f(), p_178067_3_.func_178942_g(), p_178067_3_.func_178945_a());
        guislider.visible = p_178067_3_.func_178934_d();
        return guislider;
    }

    private GuiListButton func_178065_a(int p_178065_1_, int p_178065_2_, GuiPageButtonList.GuiButtonEntry p_178065_3_)
    {
        GuiListButton guilistbutton = new GuiListButton(this.responder, p_178065_3_.func_178935_b(), p_178065_1_, p_178065_2_, p_178065_3_.func_178936_c(), p_178065_3_.func_178940_a());
        guilistbutton.visible = p_178065_3_.func_178934_d();
        return guilistbutton;
    }

    private GuiTextField func_178068_a(int p_178068_1_, int p_178068_2_, GuiPageButtonList.EditBoxEntry p_178068_3_)
    {
        GuiTextField guitextfield = new GuiTextField(p_178068_3_.func_178935_b(), this.mc.fontRendererObj, p_178068_1_, p_178068_2_, 150, 20);
        guitextfield.setText(p_178068_3_.func_178936_c());
        guitextfield.func_175207_a(this.responder);
        guitextfield.setVisible(p_178068_3_.func_178934_d());
        guitextfield.setValidator(p_178068_3_.func_178950_a());
        return guitextfield;
    }

    private GuiLabel func_178063_a(int p_178063_1_, int p_178063_2_, GuiPageButtonList.GuiLabelEntry p_178063_3_, boolean p_178063_4_)
    {
        GuiLabel guilabel;

        if (p_178063_4_)
        {
            guilabel = new GuiLabel(this.mc.fontRendererObj, p_178063_3_.func_178935_b(), p_178063_1_, p_178063_2_, this.width - p_178063_1_ * 2, 20, -1);
        }
        else
        {
            guilabel = new GuiLabel(this.mc.fontRendererObj, p_178063_3_.func_178935_b(), p_178063_1_, p_178063_2_, 150, 20, -1);
        }

        guilabel.visible = p_178063_3_.func_178934_d();
        guilabel.func_175202_a(p_178063_3_.func_178936_c());
        guilabel.setCentered();
        return guilabel;
    }

    public void func_178062_a(char p_178062_1_, int p_178062_2_)
    {
        if (this.focusedControl instanceof GuiTextField)
        {
            GuiTextField guitextfield = (GuiTextField)this.focusedControl;

            if (!GuiScreen.isKeyComboCtrlV(p_178062_2_))
            {
                if (p_178062_2_ == 15)
                {
                    guitextfield.setFocused(false);
                    int k = this.editBoxes.indexOf(this.focusedControl);

                    if (GuiScreen.isShiftKeyDown())
                    {
                        if (k == 0)
                        {
                            k = this.editBoxes.size() - 1;
                        }
                        else
                        {
                            --k;
                        }
                    }
                    else if (k == this.editBoxes.size() - 1)
                    {
                        k = 0;
                    }
                    else
                    {
                        ++k;
                    }

                    this.focusedControl = (Gui)this.editBoxes.get(k);
                    guitextfield = (GuiTextField)this.focusedControl;
                    guitextfield.setFocused(true);
                    int l = guitextfield.yPosition + this.slotHeight;
                    int i1 = guitextfield.yPosition;

                    if (l > this.bottom)
                    {
                        this.amountScrolled += (float)(l - this.bottom);
                    }
                    else if (i1 < this.top)
                    {
                        this.amountScrolled = (float)i1;
                    }
                }
                else
                {
                    guitextfield.textboxKeyTyped(p_178062_1_, p_178062_2_);
                }
            }
            else
            {
                String s = GuiScreen.getClipboardString();
                String[] astring = s.split(";");
                int i = this.editBoxes.indexOf(this.focusedControl);
                int j = i;

                for (String s1 : astring)
                {
                    ((GuiTextField)this.editBoxes.get(j)).setText(s1);

                    if (j == this.editBoxes.size() - 1)
                    {
                        j = 0;
                    }
                    else
                    {
                        ++j;
                    }

                    if (j == i)
                    {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public GuiPageButtonList.GuiEntry getListEntry(int index)
    {
        return (GuiPageButtonList.GuiEntry)this.entries.get(index);
    }

    public int getSize()
    {
        return this.entries.size();
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

    public static class EditBoxEntry extends GuiPageButtonList.GuiListEntry
    {
        private final Predicate<String> filter;

        public EditBoxEntry(int p_i45534_1_, String p_i45534_2_, boolean p_i45534_3_, Predicate<String> p_i45534_4_)
        {
            super(p_i45534_1_, p_i45534_2_, p_i45534_3_);
            this.filter = MoreObjects.firstNonNull(p_i45534_4_, Predicates.alwaysTrue());
        }

        public Predicate<String> func_178950_a()
        {
            return this.filter;
        }
    }

    public static class GuiButtonEntry extends GuiPageButtonList.GuiListEntry
    {
        private final boolean initialValue;

        public GuiButtonEntry(int p_i45535_1_, String p_i45535_2_, boolean p_i45535_3_, boolean p_i45535_4_)
        {
            super(p_i45535_1_, p_i45535_2_, p_i45535_3_);
            this.initialValue = p_i45535_4_;
        }

        public boolean func_178940_a()
        {
            return this.initialValue;
        }
    }

    public static class GuiEntry implements GuiListExtended.IGuiListEntry
    {
        private final Minecraft client = Minecraft.getMinecraft();
        private final Gui component1;
        private final Gui component2;
        private Gui focusedControl;

        public GuiEntry(Gui p_i45533_1_, Gui p_i45533_2_)
        {
            this.component1 = p_i45533_1_;
            this.component2 = p_i45533_2_;
        }

        public Gui func_178022_a()
        {
            return this.component1;
        }

        public Gui func_178021_b()
        {
            return this.component2;
        }

        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
        {
            this.func_178017_a(this.component1, y, mouseX, mouseY, false);
            this.func_178017_a(this.component2, y, mouseX, mouseY, false);
        }

        private void func_178017_a(Gui p_178017_1_, int p_178017_2_, int p_178017_3_, int p_178017_4_, boolean p_178017_5_)
        {
            if (p_178017_1_ != null)
            {
                if (p_178017_1_ instanceof GuiButton)
                {
                    this.func_178024_a((GuiButton)p_178017_1_, p_178017_2_, p_178017_3_, p_178017_4_, p_178017_5_);
                }
                else if (p_178017_1_ instanceof GuiTextField)
                {
                    this.func_178027_a((GuiTextField)p_178017_1_, p_178017_2_, p_178017_5_);
                }
                else if (p_178017_1_ instanceof GuiLabel)
                {
                    this.func_178025_a((GuiLabel)p_178017_1_, p_178017_2_, p_178017_3_, p_178017_4_, p_178017_5_);
                }
            }
        }

        private void func_178024_a(GuiButton p_178024_1_, int p_178024_2_, int p_178024_3_, int p_178024_4_, boolean p_178024_5_)
        {
            p_178024_1_.yPosition = p_178024_2_;

            if (!p_178024_5_)
            {
                p_178024_1_.drawButton(this.client, p_178024_3_, p_178024_4_);
            }
        }

        private void func_178027_a(GuiTextField p_178027_1_, int p_178027_2_, boolean p_178027_3_)
        {
            p_178027_1_.yPosition = p_178027_2_;

            if (!p_178027_3_)
            {
                p_178027_1_.drawTextBox();
            }
        }

        private void func_178025_a(GuiLabel p_178025_1_, int p_178025_2_, int p_178025_3_, int p_178025_4_, boolean p_178025_5_)
        {
            p_178025_1_.y = p_178025_2_;

            if (!p_178025_5_)
            {
                p_178025_1_.drawLabel(this.client, p_178025_3_, p_178025_4_);
            }
        }

        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_)
        {
            this.func_178017_a(this.component1, p_178011_3_, 0, 0, true);
            this.func_178017_a(this.component2, p_178011_3_, 0, 0, true);
        }

        public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
        {
            boolean flag = this.func_178026_a(this.component1, p_148278_2_, p_148278_3_, p_148278_4_);
            boolean flag1 = this.func_178026_a(this.component2, p_148278_2_, p_148278_3_, p_148278_4_);
            return flag || flag1;
        }

        private boolean func_178026_a(Gui p_178026_1_, int p_178026_2_, int p_178026_3_, int p_178026_4_)
        {
            if (p_178026_1_ == null)
            {
                return false;
            }
            else if (p_178026_1_ instanceof GuiButton)
            {
                return this.func_178023_a((GuiButton)p_178026_1_, p_178026_2_, p_178026_3_, p_178026_4_);
            }
            else
            {
                if (p_178026_1_ instanceof GuiTextField)
                {
                    this.func_178018_a((GuiTextField)p_178026_1_, p_178026_2_, p_178026_3_, p_178026_4_);
                }

                return false;
            }
        }

        private boolean func_178023_a(GuiButton p_178023_1_, int p_178023_2_, int p_178023_3_, int p_178023_4_)
        {
            boolean flag = p_178023_1_.mousePressed(this.client, p_178023_2_, p_178023_3_);

            if (flag)
            {
                this.focusedControl = p_178023_1_;
            }

            return flag;
        }

        private void func_178018_a(GuiTextField p_178018_1_, int p_178018_2_, int p_178018_3_, int p_178018_4_)
        {
            p_178018_1_.mouseClicked(p_178018_2_, p_178018_3_, p_178018_4_);

            if (p_178018_1_.isFocused())
            {
                this.focusedControl = p_178018_1_;
            }
        }

        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY)
        {
            this.func_178016_b(this.component1, x, y, mouseEvent);
            this.func_178016_b(this.component2, x, y, mouseEvent);
        }

        private void func_178016_b(Gui p_178016_1_, int p_178016_2_, int p_178016_3_, int p_178016_4_)
        {
            if (p_178016_1_ != null)
            {
                if (p_178016_1_ instanceof GuiButton)
                {
                    this.func_178019_b((GuiButton)p_178016_1_, p_178016_2_, p_178016_3_, p_178016_4_);
                }
            }
        }

        private void func_178019_b(GuiButton p_178019_1_, int p_178019_2_, int p_178019_3_, int p_178019_4_)
        {
            p_178019_1_.mouseReleased(p_178019_2_, p_178019_3_);
        }
    }

    public static class GuiLabelEntry extends GuiPageButtonList.GuiListEntry
    {
        public GuiLabelEntry(int p_i45532_1_, String p_i45532_2_, boolean p_i45532_3_)
        {
            super(p_i45532_1_, p_i45532_2_, p_i45532_3_);
        }
    }

    public static class GuiListEntry
    {
        private final int id;
        private final String caption;
        private final boolean startVisible;

        public GuiListEntry(int p_i45531_1_, String p_i45531_2_, boolean p_i45531_3_)
        {
            this.id = p_i45531_1_;
            this.caption = p_i45531_2_;
            this.startVisible = p_i45531_3_;
        }

        public int func_178935_b()
        {
            return this.id;
        }

        public String func_178936_c()
        {
            return this.caption;
        }

        public boolean func_178934_d()
        {
            return this.startVisible;
        }
    }

    public interface GuiResponder
    {
        void func_175321_a(int p_175321_1_, boolean p_175321_2_);

        void onTick(int id, float value);

        void func_175319_a(int p_175319_1_, String p_175319_2_);
    }

    public static class GuiSlideEntry extends GuiPageButtonList.GuiListEntry
    {
        private final GuiSlider.FormatHelper formatter;
        private final float minValue;
        private final float maxValue;
        private final float initialValue;

        public GuiSlideEntry(int p_i45530_1_, String p_i45530_2_, boolean p_i45530_3_, GuiSlider.FormatHelper p_i45530_4_, float p_i45530_5_, float p_i45530_6_, float p_i45530_7_)
        {
            super(p_i45530_1_, p_i45530_2_, p_i45530_3_);
            this.formatter = p_i45530_4_;
            this.minValue = p_i45530_5_;
            this.maxValue = p_i45530_6_;
            this.initialValue = p_i45530_7_;
        }

        public GuiSlider.FormatHelper func_178945_a()
        {
            return this.formatter;
        }

        public float func_178943_e()
        {
            return this.minValue;
        }

        public float func_178944_f()
        {
            return this.maxValue;
        }

        public float func_178942_g()
        {
            return this.initialValue;
        }
    }
}
