package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.util.FontRenderer;
import net.minecraft.client.gui.element.Gui;
import net.minecraft.client.gui.spectator.GuiSpectator;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TeleportToTeam implements ISpectatorMenuView, ISpectatorMenuObject
{
    private final List<ISpectatorMenuObject> items = Lists.<ISpectatorMenuObject>newArrayList();

    public TeleportToTeam()
    {
        Minecraft minecraft = Minecraft.getMinecraft();

        for (ScorePlayerTeam scoreplayerteam : minecraft.theWorld.getScoreboard().getTeams())
        {
            this.items.add(new TeleportToTeam.TeamSelectionObject(scoreplayerteam));
        }
    }

    public List<ISpectatorMenuObject> func_178669_a()
    {
        return this.items;
    }

    public IChatComponent func_178670_b()
    {
        return new ChatComponentText("Select a team to teleport to");
    }

    public void func_178661_a(SpectatorMenu menu)
    {
        menu.func_178647_a(this);
    }

    public IChatComponent getSpectatorName()
    {
        return new ChatComponentText("Teleport to team member");
    }

    public void func_178663_a(float p_178663_1_, int alpha)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.SPECTATOR_WIDGETS);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 16.0F, 0.0F, 16, 16, 256.0F, 256.0F);
    }

    public boolean func_178662_A_()
    {
        for (ISpectatorMenuObject ispectatormenuobject : this.items)
        {
            if (ispectatormenuobject.func_178662_A_())
            {
                return true;
            }
        }

        return false;
    }

    class TeamSelectionObject implements ISpectatorMenuObject
    {
        private final ScorePlayerTeam team;
        private final ResourceLocation location;
        private final List<NetworkPlayerInfo> players;

        public TeamSelectionObject(ScorePlayerTeam p_i45492_2_)
        {
            this.team = p_i45492_2_;
            this.players = Lists.<NetworkPlayerInfo>newArrayList();

            for (String s : p_i45492_2_.getMembershipCollection())
            {
                NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(s);

                if (networkplayerinfo != null)
                {
                    this.players.add(networkplayerinfo);
                }
            }

            if (!this.players.isEmpty())
            {
                String s1 = ((NetworkPlayerInfo)this.players.get((new Random()).nextInt(this.players.size()))).getGameProfile().getName();
                this.location = AbstractClientPlayer.getLocationSkin(s1);
                AbstractClientPlayer.getDownloadImageSkin(this.location, s1);
            }
            else
            {
                this.location = DefaultPlayerSkin.getDefaultSkinLegacy();
            }
        }

        public void func_178661_a(SpectatorMenu menu)
        {
            menu.func_178647_a(new TeleportToPlayer(this.players));
        }

        public IChatComponent getSpectatorName()
        {
            return new ChatComponentText(this.team.getTeamName());
        }

        public void func_178663_a(float p_178663_1_, int alpha)
        {
            int i = -1;
            String s = FontRenderer.getFormatFromString(this.team.getColorPrefix());

            if (s.length() >= 2)
            {
                i = Minecraft.getMinecraft().fontRendererObj.getColorCode(s.charAt(1));
            }

            if (i >= 0)
            {
                float f = (float)(i >> 16 & 255) / 255.0F;
                float f1 = (float)(i >> 8 & 255) / 255.0F;
                float f2 = (float)(i & 255) / 255.0F;
                Gui.drawRect(1, 1, 15, 15, MathHelper.func_180183_b(f * p_178663_1_, f1 * p_178663_1_, f2 * p_178663_1_) | alpha << 24);
            }

            Minecraft.getMinecraft().getTextureManager().bindTexture(this.location);
            GlStateManager.color(p_178663_1_, p_178663_1_, p_178663_1_, (float)alpha / 255.0F);
            Gui.drawScaledCustomSizeModalRect(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
            Gui.drawScaledCustomSizeModalRect(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
        }

        public boolean func_178662_A_()
        {
            return !this.players.isEmpty();
        }
    }
}
