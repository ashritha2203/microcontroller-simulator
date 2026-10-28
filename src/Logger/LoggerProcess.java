import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class LoggerProcess {

    private static final String LOG_FILE = "simulation.log";

    private static final String CORE_TO_LOGGER =
            "/tmp/logging_fifo";

    private static final String UI_TO_LOGGER =
            "/tmp/ui_to_logger";

    private static final String LOGGER_TO_CORE =
            "/tmp/logger_to_core";

    private static final String LOGGER_TO_UI =
            "/tmp/logger_to_ui";

    public static void main(String[] args) {

        LoggerProcess logger = new LoggerProcess();

        System.out.println("Logger Process Started");

        Thread coreReader = new Thread(() ->
                logger.readMessages(CORE_TO_LOGGER, "CORE"));

        Thread uiReader = new Thread(() ->
                logger.readMessages(UI_TO_LOGGER, "UI"));

        coreReader.start();
        uiReader.start();
    }

    private void readMessages(String fifo, String source) {

        while (!Thread.currentThread().isInterrupted()) {

            try (BufferedReader reader =
                         new BufferedReader(new FileReader(fifo))) {

                String line;

                while ((line = reader.readLine()) != null) {

                    IPCMessage message = IPCMessage.fromString(line);

                    if (message == null) {
                        continue;
                    }

                    processMessage(message, source);
                }

            } catch (IOException e) {

                System.err.println(
                        "Error reading " + fifo + ": "
                                + e.getMessage());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private void processMessage(IPCMessage message, String source) {

        if ("GET_ERRORS".equals(message.getType())) {
            return;
        }

        processLog(message);

        if ("CORE".equals(source)
                && "LOG".equals(message.getType())) {

            sendMessage(
                    LOGGER_TO_CORE,
                    new IPCMessage(
                            "LOGGER",
                            "ACK",
                            "Message logged"));
        }

        if ("ERROR".equals(message.getType())) {

            sendMessage(
                    LOGGER_TO_UI,
                    new IPCMessage(
                            "LOGGER",
                            "ERROR",
                            message.getText()));
        }
    }

    private void processLog(IPCMessage message) {

        String time = LocalDateTime.now().toString();

        String logEntry =
                time + " [" + message.getSender() + "] ["
                        + message.getType() + "] "
                        + message.getText();

        System.out.println(logEntry);

        writeToFile(logEntry);
    }

    private synchronized void writeToFile(String logEntry) {

        try (PrintWriter writer =
                     new PrintWriter(
                             new BufferedWriter(
                                     new FileWriter(LOG_FILE, true)))) {

            writer.println(logEntry);
            writer.flush();

            if (writer.checkError()) {
                System.err.println("Failed to write log entry.");
            }

        } catch (IOException e) {

            System.err.println(
                    "Unable to write log file: " + e.getMessage());
        }
    }

    private void sendMessage(String fifo, IPCMessage message) {

        try (PrintWriter writer =
                     new PrintWriter(
                             new BufferedWriter(
                                     new FileWriter(fifo)))) {

            writer.println(message.toString());
            writer.flush();

            if (writer.checkError()) {
                System.err.println(
                        "Failed to send message to " + fifo);
            }

        } catch (IOException e) {

            System.err.println(
                    "Unable to send message to " + fifo
                            + ": " + e.getMessage());
        }
    }
}
