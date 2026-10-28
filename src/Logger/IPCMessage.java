public class IPCMessage {

    private String sender;
    private String type;
    private String text;

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
        return sender + "|" + type + "|" + text;
    }

    public static IPCMessage fromString(String line) {

        if (line == null) {
            return null;
        }

        String[] parts = line.split("\\|", 3);

        if (parts.length != 3) {
            return null;
        }

        return new IPCMessage(
                parts[0],
                parts[1],
                parts[2]
        );
    }
}
