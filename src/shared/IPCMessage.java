package shared;

public class IPCMessage {

    // message types the team agreed on
    public static final String LOG = "LOG";
    public static final String ERROR = "ERROR";
    public static final String ACK = "ACK";
    public static final String GET_ERRORS = "GET_ERRORS";

    private final String sender;
    private final String type;
    private final String text;

    public IPCMessage(String sender, String type, String text) {
        this.sender = sender;
        this.type = type;
        this.text = text;
    }

    public String getSender() { return sender; }
    public String getType() { return type; }
    public String getText() { return text; }

    @Override
    public String toString() {
        return sender + "|" + type + "|" + text;
    }

    public static IPCMessage fromString(String line) {
        if (line == null) return null;
        String[] parts = line.split("\\|", 3);
        if (parts.length != 3) return null;
        return new IPCMessage(parts[0], parts[1], parts[2]);
    }
}