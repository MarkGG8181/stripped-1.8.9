package net.minecraft.client.gui.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.element.Gui;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class SpectatorMenu {
    private static final ISpectatorMenuObject CLOSE_ITEM = new SpectatorMenu.EndSpectatorObject();
    private static final ISpectatorMenuObject SCROLL_LEFT = new SpectatorMenu.MoveMenuObject(-1, true);
    private static final ISpectatorMenuObject SCROLL_RIGHT_ENABLED = new SpectatorMenu.MoveMenuObject(1, true);
    private static final ISpectatorMenuObject SCROLL_RIGHT_DISABLED = new SpectatorMenu.MoveMenuObject(1, false);
    public static final ISpectatorMenuObject EMPTY_SLOT = new ISpectatorMenuObject() {
        public void func_178661_a(SpectatorMenu menu) {
        }

        public IChatComponent getSpectatorName() {
            return new ChatComponentText("");
        }

        public void func_178663_a(float p_178663_1_, int alpha) {
        }

        public boolean func_178662_A_() {
            return false;
        }
    };
    private final ISpectatorMenuRecipient listener;
    private final List<SpectatorDetails> previousCategories = Lists.<SpectatorDetails>newArrayList();
    private ISpectatorMenuView category = new BaseSpectatorGroup();
    private int selectedSlot = -1;
    private int page;

    public SpectatorMenu(ISpectatorMenuRecipient p_i45497_1_) {
        this.listener = p_i45497_1_;
    }

    public ISpectatorMenuObject func_178643_a(int p_178643_1_) {
        int i = p_178643_1_ + this.page * 6;
        return this.page > 0 && p_178643_1_ == 0 ? SCROLL_LEFT : (p_178643_1_ == 7 ? (i < this.category.func_178669_a().size() ? SCROLL_RIGHT_ENABLED : SCROLL_RIGHT_DISABLED) : (p_178643_1_ == 8 ? CLOSE_ITEM : (i >= 0 && i < this.category.func_178669_a().size() ? MoreObjects.firstNonNull(this.category.func_178669_a().get(i), EMPTY_SLOT) : EMPTY_SLOT)));
    }

    public List<ISpectatorMenuObject> func_178642_a() {
        List<ISpectatorMenuObject> list = Lists.<ISpectatorMenuObject>newArrayList();

        for (int i = 0; i <= 8; ++i) {
            list.add(this.func_178643_a(i));
        }

        return list;
    }

    public ISpectatorMenuObject func_178645_b() {
        return this.func_178643_a(this.selectedSlot);
    }

    public ISpectatorMenuView func_178650_c() {
        return this.category;
    }

    public void func_178644_b(int p_178644_1_) {
        ISpectatorMenuObject ispectatormenuobject = this.func_178643_a(p_178644_1_);

        if (ispectatormenuobject != EMPTY_SLOT) {
            if (this.selectedSlot == p_178644_1_ && ispectatormenuobject.func_178662_A_()) {
                ispectatormenuobject.func_178661_a(this);
            } else {
                this.selectedSlot = p_178644_1_;
            }
        }
    }

    public void func_178641_d() {
        this.listener.func_175257_a(this);
    }

    public int func_178648_e() {
        return this.selectedSlot;
    }

    public void func_178647_a(ISpectatorMenuView p_178647_1_) {
        this.previousCategories.add(this.func_178646_f());
        this.category = p_178647_1_;
        this.selectedSlot = -1;
        this.page = 0;
    }

    public SpectatorDetails func_178646_f() {
        return new SpectatorDetails(this.func_178642_a(), this.selectedSlot);
    }

    static class EndSpectatorObject implements ISpectatorMenuObject {
        private EndSpectatorObject() {
        }

        public void func_178661_a(SpectatorMenu menu) {
            menu.func_178641_d();
        }

        public IChatComponent getSpectatorName() {
            return new ChatComponentText("Close menu");
        }

        public void func_178663_a(float p_178663_1_, int alpha) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.SPECTATOR_WIDGETS);
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 128.0F, 0.0F, 16, 16, 256.0F, 256.0F);
        }

        public boolean func_178662_A_() {
            return true;
        }
    }

    static class MoveMenuObject implements ISpectatorMenuObject {
        private final int direction;
        private final boolean enabled;

        public MoveMenuObject(int p_i45495_1_, boolean p_i45495_2_) {
            this.direction = p_i45495_1_;
            this.enabled = p_i45495_2_;
        }

        public void func_178661_a(SpectatorMenu menu) {
            menu.page = this.direction;
        }

        public IChatComponent getSpectatorName() {
            return this.direction < 0 ? new ChatComponentText("Previous Page") : new ChatComponentText("Next Page");
        }

        public void func_178663_a(float p_178663_1_, int alpha) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.SPECTATOR_WIDGETS);

            if (this.direction < 0) {
                Gui.drawModalRectWithCustomSizedTexture(0, 0, 144.0F, 0.0F, 16, 16, 256.0F, 256.0F);
            } else {
                Gui.drawModalRectWithCustomSizedTexture(0, 0, 160.0F, 0.0F, 16, 16, 256.0F, 256.0F);
            }
        }

        public boolean func_178662_A_() {
            return this.enabled;
        }
    }
}
