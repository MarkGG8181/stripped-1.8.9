package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;

import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class Main {
    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");

        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();

        parser.accepts("fullscreen");
        parser.accepts("checkGlErrors");

        OptionSpec<String> server = parser.accepts("server").withRequiredArg();
        OptionSpec<Integer> port = parser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
        OptionSpec<File> gameDir = parser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
        OptionSpec<File> assetsDir = parser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        OptionSpec<File> resourcePackDir = parser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);

        OptionSpec<String> proxyHost = parser.accepts("proxyHost").withRequiredArg();
        OptionSpec<Integer> proxyPort = parser.accepts("proxyPort").withRequiredArg().defaultsTo("8080").ofType(Integer.class);
        OptionSpec<String> proxyUser = parser.accepts("proxyUser").withRequiredArg();
        OptionSpec<String> proxyPass = parser.accepts("proxyPass").withRequiredArg();

        OptionSpec<String> username = parser.accepts("username").withRequiredArg()
                .defaultsTo("Player" + Minecraft.getSystemTime() % 1000L);
        OptionSpec<String> uuid = parser.accepts("uuid").withRequiredArg();
        OptionSpec<String> accessToken = parser.accepts("accessToken").withRequiredArg().required();
        OptionSpec<String> version = parser.accepts("version").withRequiredArg().required();

        OptionSpec<Integer> width = parser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
        OptionSpec<Integer> height = parser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
        OptionSpec<String> profileProperties = parser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> assetIndex = parser.accepts("assetIndex").withRequiredArg();
        OptionSpec<String> userType = parser.accepts("userType").withRequiredArg().defaultsTo("legacy");

        OptionSpec<String> leftoverArgs = parser.nonOptions();
        OptionSet options = parser.parse(args);

        // Warn about ignored arguments
        List<String> ignoredArgs = options.valuesOf(leftoverArgs);
        if (!ignoredArgs.isEmpty()) {
            System.out.println("Ignored arguments: " + ignoredArgs);
        }

        // Proxy setup
        Proxy proxy = Proxy.NO_PROXY;
        String proxyHostVal = options.valueOf(proxyHost);
        if (proxyHostVal != null) {
            try {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHostVal, options.valueOf(proxyPort)));
            } catch (Exception ignored) {
            }
        }

        String proxyUserVal = options.valueOf(proxyUser);
        String proxyPassVal = options.valueOf(proxyPass);

        if (!proxy.equals(Proxy.NO_PROXY) && !isNullOrEmpty(proxyUserVal) && !isNullOrEmpty(proxyPassVal)) {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(proxyUserVal, proxyPassVal.toCharArray());
                }
            });
        }

        int screenWidth = options.valueOf(width);
        int screenHeight = options.valueOf(height);
        boolean fullscreen = options.has("fullscreen");
        boolean checkGlErrors = options.has("checkGlErrors");

        String gameVersion = options.valueOf(version);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(PropertyMap.class, new Serializer())
                .create();

        PropertyMap propertyMap = gson.fromJson(options.valueOf(profileProperties), PropertyMap.class);

        File baseDir = options.valueOf(gameDir);
        File assets = options.has(assetsDir) ? options.valueOf(assetsDir) : new File(baseDir, "assets/");
        File resourcePacks = options.has(resourcePackDir) ? options.valueOf(resourcePackDir) : new File(baseDir, "resourcepacks/");

        String sessionUUID = options.has(uuid) ? options.valueOf(uuid) : options.valueOf(username);
        String assetIndexVal = options.has(assetIndex) ? options.valueOf(assetIndex) : null;

        String serverHost = options.valueOf(server);
        int serverPort = options.valueOf(port);

        Session session = new Session(
                options.valueOf(username),
                sessionUUID,
                options.valueOf(accessToken),
                options.valueOf(userType)
        );

        GameConfiguration config = new GameConfiguration(
                new GameConfiguration.UserInformation(session, propertyMap, proxy),
                new GameConfiguration.DisplayInformation(screenWidth, screenHeight, fullscreen, checkGlErrors),
                new GameConfiguration.FolderInformation(baseDir, resourcePacks, assets, assetIndexVal),
                new GameConfiguration.GameInformation(gameVersion),
                new GameConfiguration.ServerInformation(serverHost, serverPort)
        );

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
            public void run() {
                Minecraft.stopIntegratedServer();
            }
        });

        Thread.currentThread().setName("Client thread");
        new Minecraft(config).run();
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
