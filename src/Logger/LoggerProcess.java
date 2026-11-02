import shared.IPCMessage;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoggerProcess {

    private static final String LOG_FILE = "simulation.log";

    private static final String CORE_TO_LOGGER = "/tmp/logging_fifo";
    private static final String UI_TO_LOGGER   = "/tmp/ui_to_logger";
    private static final String LOGGER_TO_CORE = "/tmp/logger_to_core";
    private static final String LOGGER_TO_UI   = "/tmp/logger_to_ui";

    private static final int MAX_ERRORS = 50;

    // Outgoing FIFOs: each is opened ONCE (on its own thread) and kept open
    private final OutFifo toCore = new OutFifo(LOGGER_TO_CORE);
    private final OutFifo toUi = new OutFifo(LOGGER_TO_UI);

    private final LinkedList<String> recentErrors = new LinkedList<>();
    private PrintWriter logWriter;

    public LoggerProcess() {
        try {
            logWriter = new PrintWriter(
                    new BufferedWriter(new FileWriter(LOG_FILE, true)), true);
        } catch (IOException e) {
            System.err.println("Unable to open log file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        LoggerProcess logger = new LoggerProcess();

        System.out.println("Logger Process Started");

        // One reader thread per incoming FIFO
        new Thread(() -> logger.readMessages(CORE_TO_LOGGER, "CORE"), "core-reader").start();
        new Thread(() -> logger.readMessages(UI_TO_LOGGER, "UI"), "ui-reader").start();
    }

    // ---------- Reading (Core -> Logger and UI -> Logger) ----------

    private void readMessages(String fifo, String source) {

        boolean warned = false;

        while (!Thread.currentThread().isInterrupted()) {

            // Never read a missing FIFO, or a normal file that is not a FIFO
            Path p = Paths.get(fifo);
            if (!Files.exists(p) || Files.isRegularFile(p)) {
                if (!warned) {
                    System.err.println("Waiting for FIFO " + fifo + " (run the setup script first)");
                    warned = true;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                }
                continue;
            }
            warned = false;

            // Waits here until a writer opens the other end of the FIFO
            try (BufferedReader reader = new BufferedReader(new FileReader(fifo))) {

                String line;

                while ((line = reader.readLine()) != null) {

                    IPCMessage message = IPCMessage.fromString(line);

                    if (message == null) {
                        continue;
                    }

                    processMessage(message, source);
                }
                // null = the writer closed its end; loop and wait for it to come back

            } catch (IOException e) {

                System.err.println("Error reading " + fifo + ": " + e.getMessage());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    // ---------- Handling one message ----------

    private void processMessage(IPCMessage message, String source) {

        // UI asks for the recent errors (optional feature)
        if (IPCMessage.GET_ERRORS.equals(message.getType())) {
            sendRecentErrors();
            return;
        }

        // 1. Everything goes into simulation.log
        processLog(message);

        // 2. Ack Core, but only for command messages (not every instruction line)
        if ("CORE".equals(source)
                && IPCMessage.LOG.equals(message.getType())
                && message.getText().startsWith("[CMD]")) {

            toCore.send(new IPCMessage("LOGGER", IPCMessage.ACK, "Message logged"));
        }

        // 3. Only ERROR messages are forwarded to the UI
        //    (the log file keeps the full text; the UI gets a cleaner version)
        if (IPCMessage.ERROR.equals(message.getType())) {

            String shown = message.getText().replaceFirst("^\\[ERROR\\] (ERROR )?", "");

            rememberError(shown);

            toUi.send(new IPCMessage("LOGGER", IPCMessage.ERROR, shown));
        }
    }

    private synchronized void processLog(IPCMessage message) {

        String logEntry = LocalDateTime.now()
                + " [" + message.getSender() + "] ["
                + message.getType() + "] "
                + message.getText();

        System.out.println(logEntry);

        if (logWriter != null) {
            logWriter.println(logEntry);
            if (logWriter.checkError()) {
                System.err.println("Failed to write log entry.");
            }
        }
    }

    // ---------- Last 50 errors (for GET_ERRORS) ----------

    private void rememberError(String text) {
        synchronized (recentErrors) {
            recentErrors.add(text);
            if (recentErrors.size() > MAX_ERRORS) {
                recentErrors.removeFirst();
            }
        }
    }

    private void sendRecentErrors() {
        List<String> copy;
        synchronized (recentErrors) {
            copy = new ArrayList<>(recentErrors);
        }
        for (String text : copy) {
            toUi.send(new IPCMessage("LOGGER", IPCMessage.ERROR, text));
        }
    }

    // ---------- Writing end of a FIFO (Logger -> Core, Logger -> UI) ----------

    private static class OutFifo {

        private final String path;
        private volatile PrintWriter out;
        private final AtomicBoolean connecting = new AtomicBoolean(false);

        OutFifo(String path) {
            this.path = path;
            connect();
        }

        // Opens the FIFO in its own thread, because opening waits until
        // the other side is reading. The Logger never freezes because of it.
        private void connect() {

            if (!connecting.compareAndSet(false, true)) {
                return;
            }

            Thread t = new Thread(() -> {
                try {
                    // never open a missing FIFO (it would be created as a normal file)
                    while (true) {
                        Path p = Paths.get(path);
                        if (Files.exists(p) && !Files.isRegularFile(p)) {
                            break;
                        }
                        Thread.sleep(1000);
                    }

                    out = new PrintWriter(new FileWriter(path), true);   // waits here

                } catch (Exception e) {
                    System.err.println("Cannot open " + path + ": " + e.getMessage());
                } finally {
                    connecting.set(false);
                }
            }, "connect-" + path);

            t.setDaemon(true);
            t.start();
        }

        synchronized void send(IPCMessage message) {

            PrintWriter w = out;

            if (w == null) {
                connect();          // not connected yet: drop this message and keep trying
                return;
            }

            w.println(message.toString());

            if (w.checkError()) {   // the other side left: close it and wait for it to come back
                w.close();
                out = null;
                connect();
            }
        }
    }
}
