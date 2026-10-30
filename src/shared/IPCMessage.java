package shared;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class IPCMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String sender;
    private final String type;
    private final String text;

    public IPCMessage(String sender, String type, String text) {
        this.sender = sender;
        this.type = type;
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return encode(sender) + "|" + encode(type) + "|" + encode(text);
    }

    public static IPCMessage fromString(String line) {
        if (line == null) {
            return null;
        }

        String[] parts = line.split("\\|", 3);

        if (parts.length != 3) {
            return null;
        }

        try {
            return new IPCMessage(
                    decode(parts[0]),
                    decode(parts[1]),
                    decode(parts[2])
            );
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String encode(String value) {
        String safe = value == null ? "" : value;
        return Base64.getEncoder().encodeToString(
                safe.getBytes(StandardCharsets.UTF_8)
        );
    }

    private static String decode(String value) {
        return new String(
                Base64.getDecoder().decode(value),
                StandardCharsets.UTF_8
        );
    }
}