public class LogMessage {

    private String category;
    private String message;

    public LogMessage(String category, String message) {
        this.category = category;
        this.message = message;
    }

    public String getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "[" + category + "] " + message;
    }
}
