package net.minecraft.server.management;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreYggdrasilConverter
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static void lookupNames(MinecraftServer server, Collection<String> names, ProfileLookupCallback callback)
    {
        String[] astring = (String[])Iterators.toArray(Iterators.filter(names.iterator(), new Predicate<String>()
        {
            public boolean apply(String p_apply_1_)
            {
                return !StringUtils.isNullOrEmpty(p_apply_1_);
            }
        }), String.class);

        if (server.isServerInOnlineMode())
        {
            server.getGameProfileRepository().findProfilesByNames(astring, Agent.MINECRAFT, callback);
        }
        else
        {
            for (String s : astring)
            {
                UUID uuid = EntityPlayer.getUUID(new GameProfile((UUID)null, s));
                GameProfile gameprofile = new GameProfile(uuid, s);
                callback.onProfileLookupSucceeded(gameprofile);
            }
        }
    }

    public static String getStringUUIDFromName(String p_152719_0_)
    {
        if (!StringUtils.isNullOrEmpty(p_152719_0_) && p_152719_0_.length() <= 16)
        {
            final MinecraftServer minecraftserver = MinecraftServer.getServer();
            GameProfile gameprofile = minecraftserver.getPlayerProfileCache().getGameProfileForUsername(p_152719_0_);

            if (gameprofile != null && gameprofile.getId() != null)
            {
                return gameprofile.getId().toString();
            }
            else if (!minecraftserver.isSinglePlayer() && minecraftserver.isServerInOnlineMode())
            {
                final List<GameProfile> list = new ArrayList<>();
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback()
                {
                    public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_)
                    {
                        minecraftserver.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
                        list.add(p_onProfileLookupSucceeded_1_);
                    }

                    public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_)
                    {
                        PreYggdrasilConverter.LOGGER.warn((String)("Could not lookup user whitelist entry for " + p_onProfileLookupFailed_1_.getName()), (Throwable)p_onProfileLookupFailed_2_);
                    }
                };
                lookupNames(minecraftserver, Lists.newArrayList(new String[]{p_152719_0_}), profilelookupcallback);
                return list.size() > 0 && ((GameProfile)list.getFirst()).getId() != null ? ((GameProfile)list.getFirst()).getId().toString() : "";
            }
            else
            {
                return EntityPlayer.getUUID(new GameProfile((UUID)null, p_152719_0_)).toString();
            }
        }
        else
        {
            return p_152719_0_;
        }
    }
}
