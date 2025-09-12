package net.minecraft.client.multiplayer;

import java.net.IDN;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public final class ServerAddress {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int DEFAULT_PORT = 25565;

    private final String ipAddress;
    private final int serverPort;

    private ServerAddress(String address, int port) {
        this.ipAddress = address;
        this.serverPort = port;
    }

    public String getIP() {
        return IDN.toASCII(this.ipAddress);
    }

    public int getPort() {
        return this.serverPort;
    }

    public static ServerAddress fromString(String addressString) {
        if (addressString == null) {
            return null;
        }

        String[] parts;
        String host;

        if (addressString.startsWith("[")) {
            int closingBracketIndex = addressString.indexOf(']');
            if (closingBracketIndex > 0) {
                host = addressString.substring(1, closingBracketIndex);
                String portPart = addressString.substring(closingBracketIndex + 1).trim();
                if (portPart.startsWith(":")) {
                    parts = new String[]{host, portPart.substring(1)};
                }
                else {
                    parts = new String[]{host};
                }
            }
            else {
                parts = new String[]{addressString};
            }
        }
        else {
            parts = addressString.split(":");
        }

        if (parts.length > 2) {
            parts = new String[]{addressString};
        }

        host = parts[0];
        int port = parts.length > 1 ? parseIntWithDefault(parts[1], DEFAULT_PORT) : DEFAULT_PORT;

        if (port == DEFAULT_PORT) {
            String[] srvResult = getServerAddress(host);
            host = srvResult[0];
            port = parseIntWithDefault(srvResult[1], DEFAULT_PORT);
        }

        return new ServerAddress(host, port);
    }

    private static String[] getServerAddress(String hostname) {
        try {
            String srvQuery = "_minecraft._tcp." + hostname;
            Lookup lookup = new Lookup(srvQuery, Type.SRV);
            Record[] records = lookup.run();

            if (records != null && records.length > 0) {
                SRVRecord srvRecord = (SRVRecord)records[0];
                String targetHost = srvRecord.getTarget().toString().replaceAll("\\.$", "");
                int targetPort = srvRecord.getPort();
                return new String[]{targetHost, Integer.toString(targetPort)};
            }
        } catch (TextParseException e) {
            LOGGER.debug("Invalid SRV query for {}: {}", hostname, e.getMessage());
        }
        return new String[]{hostname, Integer.toString(DEFAULT_PORT)};
    }

    private static int parseIntWithDefault(String portString, int defaultValue) {
        try {
            return Integer.parseInt(portString.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}