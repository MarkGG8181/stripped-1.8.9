package net.minecraft.client.main;

import com.mojang.authlib.properties.PropertyMap;

import java.io.File;
import java.net.Proxy;

import net.minecraft.util.Session;

public record GameConfiguration(UserInformation userInfo, DisplayInformation displayInfo, FolderInformation folderInfo,
                                GameInformation gameInfo) {
    public record DisplayInformation(int width, int height, boolean fullscreen, boolean checkGlErrors) {
    }

    public record FolderInformation(File mcDataDir, File resourcePacksDir, File assetsDir, String assetIndex) {
    }

    public record GameInformation(String version, boolean stopTextureFix) {
    }

    public record UserInformation(Session session, PropertyMap profileProperties, Proxy proxy) {
    }
}
