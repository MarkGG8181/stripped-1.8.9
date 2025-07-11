package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.PlayerMenuObject;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

public class TeleportToPlayer implements ISpectatorMenuView, ISpectatorMenuObject
{
    private static final Ordering<NetworkPlayerInfo> PROFILE_ORDER = Ordering.from(new Comparator<NetworkPlayerInfo>()
    {
        public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_)
        {
            return ComparisonChain.start().compare(p_compare_1_.getGameProfile().getId(), p_compare_2_.getGameProfile().getId()).result();
        }
    });
    private final List<ISpectatorMenuObject> items;

    public TeleportToPlayer()
    {
        this(PROFILE_ORDER.<NetworkPlayerInfo>sortedCopy(Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()));
    }

    public TeleportToPlayer(Collection<NetworkPlayerInfo> p_i45493_1_)
    {
        this.items = Lists.<ISpectatorMenuObject>newArrayList();

        for (NetworkPlayerInfo networkplayerinfo : PROFILE_ORDER.sortedCopy(p_i45493_1_))
        {
            if (networkplayerinfo.getGameType() != WorldSettings.GameType.SPECTATOR)
            {
                this.items.add(new PlayerMenuObject(networkplayerinfo.getGameProfile()));
            }
        }
    }

    public List<ISpectatorMenuObject> func_178669_a()
    {
        return this.items;
    }

    public IChatComponent func_178670_b()
    {
        return new ChatComponentText("Select a player to teleport to");
    }

    public void func_178661_a(SpectatorMenu menu)
    {
        menu.func_178647_a(this);
    }

    public IChatComponent getSpectatorName()
    {
        return new ChatComponentText("Teleport to player");
    }

    public void func_178663_a(float p_178663_1_, int alpha)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.SPECTATOR_WIDGETS);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, 16, 16, 256.0F, 256.0F);
    }

    public boolean func_178662_A_()
    {
        return !this.items.isEmpty();
    }
}
