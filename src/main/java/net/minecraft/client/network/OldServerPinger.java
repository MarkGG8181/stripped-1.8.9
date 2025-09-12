package net.minecraft.client.network;

import com.google.common.base.Splitter;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OldServerPinger {
    private static final Splitter PING_RESPONSE_SPLITTER = Splitter.on('\u0000').limit(6);
    private static final Logger logger = LogManager.getLogger();
    private static final String ICON_PREFIX = "data:image/png;base64,";
    private final List<NetworkManager> pingDestinations = new CopyOnWriteArrayList<>();

    public void ping(final ServerData server) throws UnknownHostException {
        ServerAddress serveraddress = ServerAddress.fromString(server.serverIP);
        final NetworkManager networkmanager = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(serveraddress.getIP()), serveraddress.getPort(), false);
        this.pingDestinations.add(networkmanager);
        server.serverMOTD = "Pinging...";
        server.pingToServer = -1L;
        server.playerList = null;

        networkmanager.setNetHandler(new INetHandlerStatusClient() {
            private boolean successful;
            private boolean receivedStatus;
            private long pingSentAt;

            @Override
            public void handleServerInfo(S00PacketServerInfo packetIn) {
                if (this.receivedStatus) {
                    networkmanager.closeChannel(new ChatComponentText("Received unrequested status"));
                    return;
                }

                this.receivedStatus = true;
                ServerStatusResponse response = packetIn.getResponse();

                if (response.getServerDescription() != null) {
                    server.serverMOTD = response.getServerDescription().getFormattedText();
                }
                else {
                    server.serverMOTD = "";
                }

                if (response.getProtocolVersionInfo() != null) {
                    server.gameVersion = response.getProtocolVersionInfo().getName();
                    server.version = response.getProtocolVersionInfo().getProtocol();
                }
                else {
                    server.gameVersion = "Old";
                    server.version = 0;
                }

                if (response.getPlayerCountData() != null) {
                    server.populationInfo = EnumChatFormatting.GRAY + "" + response.getPlayerCountData().getOnlinePlayerCount() + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + response.getPlayerCountData().getMaxPlayers();
                    if (ArrayUtils.isNotEmpty(response.getPlayerCountData().getPlayers())) {
                        StringBuilder playerListBuilder = new StringBuilder();
                        for (GameProfile profile : response.getPlayerCountData().getPlayers()) {
                            if (!playerListBuilder.isEmpty()) {
                                playerListBuilder.append("\n");
                            }
                            playerListBuilder.append(profile.getName());
                        }

                        if (response.getPlayerCountData().getPlayers().length < response.getPlayerCountData().getOnlinePlayerCount()) {
                            if (!playerListBuilder.isEmpty()) {
                                playerListBuilder.append("\n");
                            }
                            playerListBuilder.append("... and ").append(response.getPlayerCountData().getOnlinePlayerCount() - response.getPlayerCountData().getPlayers().length).append(" more ...");
                        }
                        server.playerList = playerListBuilder.toString();
                    }
                }
                else {
                    server.populationInfo = EnumChatFormatting.DARK_GRAY + "???";
                }

                if (response.getFavicon() != null) {
                    String favicon = response.getFavicon();
                    if (favicon.startsWith(ICON_PREFIX)) {
                        server.setBase64EncodedIconData(favicon.substring(ICON_PREFIX.length()));
                    }
                    else {
                        logger.error("Invalid server icon (unknown format)");
                    }
                }
                else {
                    server.setBase64EncodedIconData(null);
                }

                this.pingSentAt = Minecraft.getSystemTime();
                networkmanager.sendPacket(new C01PacketPing(this.pingSentAt));
                this.successful = true;
            }

            @Override
            public void handlePong(S01PacketPong packetIn) {
                long currentTime = Minecraft.getSystemTime();
                server.pingToServer = currentTime - this.pingSentAt;
                networkmanager.closeChannel(new ChatComponentText("Finished"));
            }

            @Override
            public void onDisconnect(IChatComponent reason) {
                if (!this.successful) {
                    logger.error("Can't ping {}: {}", server.serverIP, reason.getUnformattedText());
                    server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
                    server.populationInfo = "";
                    OldServerPinger.this.tryCompatibilityPing(server);
                }
            }
        });

        try {
            networkmanager.sendPacket(new C00Handshake(47, serveraddress.getIP(), serveraddress.getPort(), EnumConnectionState.STATUS));
            networkmanager.sendPacket(new C00PacketServerQuery());
        } catch (Throwable throwable) {
            logger.error("Exception while pinging server", throwable);
        }
    }

    private void tryCompatibilityPing(final ServerData server) {
        final ServerAddress serveraddress = ServerAddress.fromString(server.serverIP);
        new Bootstrap()
            .group(NetworkManager.CLIENT_NIO_EVENTLOOP.getValue())
            .channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true)
            .handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) {
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            super.channelActive(ctx);
                            ByteBuf bytebuf = Unpooled.buffer();
                            try {
                                bytebuf.writeByte(0xFE);
                                bytebuf.writeByte(0x01);
                                bytebuf.writeByte(0xFA);
                                char[] mcPingHost = "MC|PingHost".toCharArray();
                                bytebuf.writeShort(mcPingHost.length);
                                for (char c : mcPingHost) {
                                    bytebuf.writeChar(c);
                                }
                                bytebuf.writeShort(7 + 2 * serveraddress.getIP().length());
                                bytebuf.writeByte(127);
                                char[] ipChars = serveraddress.getIP().toCharArray();
                                bytebuf.writeShort(ipChars.length);
                                for (char c : ipChars) {
                                    bytebuf.writeChar(c);
                                }
                                bytebuf.writeInt(serveraddress.getPort());
                                ctx.channel().writeAndFlush(bytebuf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                            if (msg.readUnsignedByte() == 0xFF) {
                                int length = msg.readShort() * 2;
                                byte[] bytes = new byte[length];
                                msg.readBytes(bytes);
                                String response = new String(bytes, StandardCharsets.UTF_16BE);
                                String[] parts = PING_RESPONSE_SPLITTER.splitToList(response).toArray(new String[0]);

                                if ("\u00a71".equals(parts[0])) {
                                    server.version = -1;
                                    server.gameVersion = parts[2];
                                    server.serverMOTD = parts[3];
                                    int onlinePlayers = MathHelper.parseIntWithDefault(parts[4], -1);
                                    int maxPlayers = MathHelper.parseIntWithDefault(parts[5], -1);
                                    server.populationInfo = EnumChatFormatting.GRAY + "" + onlinePlayers + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + maxPlayers;
                                }
                            }
                            ctx.close();
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                            ctx.close();
                        }
                    });
                }
            })
            .connect(serveraddress.getIP(), serveraddress.getPort());
    }

    public void pingPendingNetworks() {
        for (NetworkManager networkmanager : this.pingDestinations) {
            if (networkmanager.isChannelOpen()) {
                networkmanager.processReceivedPackets();
            }
            else {
                this.pingDestinations.remove(networkmanager);
                networkmanager.checkDisconnected();
            }
        }
    }

    public void clearPendingNetworks() {
        for (NetworkManager networkmanager : this.pingDestinations) {
            if (networkmanager.isChannelOpen()) {
                this.pingDestinations.remove(networkmanager);
                networkmanager.closeChannel(new ChatComponentText("Cancelled"));
            }
        }
    }
}
