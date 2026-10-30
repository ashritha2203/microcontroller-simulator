import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import shared.IPCMessage;

public class UIProcess {

    private static final String LOGGER_TO_UI = "/tmp/logger_to_ui";
    private static final String UI_TO_LOGGER = "/tmp/ui_to_logger";

    private Process coreProcess;
    private BufferedReader coreReader;
    private PrintWriter coreWriter;

    private SimulatorUI ui;

    private final BlockingQueue<IPCMessage> outgoingMessages =
            new LinkedBlockingQueue<>();

    private final AtomicBoolean running = new AtomicBoolean(false);

    private Thread fifoReaderThread;
    private Thread fifoWriterThread;

    public UIProcess() {
        startCoreProcess();
    }

    private void startCoreProcess() {
        try {
            String java = System.getProperty("java.home")
                    + File.separator + "bin"
                    + File.separator + "java";

            String corePath = new File("bin", "Core").getAbsolutePath();
            String mainPath = new File("bin").getAbsolutePath();

            String classPath = corePath
                    + File.pathSeparator + mainPath;

            ProcessBuilder builder = new ProcessBuilder(
                    java, "-cp", classPath, "CoreMain");

            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            coreProcess = builder.start();

            coreReader = new BufferedReader(
                    new InputStreamReader(
                            coreProcess.getInputStream(),
                            StandardCharsets.UTF_8));

            coreWriter = new PrintWriter(
                    new OutputStreamWriter(
                            coreProcess.getOutputStream(),
                            StandardCharsets.UTF_8),
                    true);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not start Core Process: " + e.getMessage(), e);
        }
    }

    public synchronized String sendCommand(String command) {
        if (coreProcess == null || !coreProcess.isAlive()) {
            showError("Core Process is not running.");
            return "ERROR Core Process is not running";
        }

        coreWriter.println(command);

        if (coreWriter.checkError()) {
            showError("Could not send command to Core.");
            return "ERROR Could not send command to Core";
        }

        try {
            String response = coreReader.readLine();

            if (response == null) {
                showError("Core Process closed the connection.");
                return "ERROR Core Process closed";
            }

            if (response.startsWith("ERROR")) {
                showError(response);
                sendUIEvent("Core returned an error: " + response);
            }

            return response;

        } catch (IOException e) {
            String error = "ERROR " + e.getMessage();
            showError(error);
            return error;
        }
    }

    public void startLoggerCommunication(SimulatorUI simulatorUI) {
        if (!running.compareAndSet(false, true)) {
            return;
        }

        ui = simulatorUI;

        fifoReaderThread = new Thread(
                this::readLoggerMessages, "logger-reader");
        fifoReaderThread.setDaemon(true);
        fifoReaderThread.start();

        fifoWriterThread = new Thread(
                this::writeLoggerMessages, "logger-writer");
        fifoWriterThread.setDaemon(true);
        fifoWriterThread.start();
    }

    public void sendUIEvent(String message) {
        if (!running.get()) {
            return;
        }

        outgoingMessages.offer(
                new IPCMessage("UI", "LOG", message));
    }

    private void readLoggerMessages() {
        while (running.get()) {
            try (BufferedReader reader = new BufferedReader(
                    new FileReader(LOGGER_TO_UI))) {

                String line;

                while (running.get() && (line = reader.readLine()) != null) {
                    IPCMessage message = IPCMessage.fromString(line);

                    if (message == null) {
                        continue;
                    }

                    if ("ERROR".equals(message.getType())) {
                        String errorText = message.getText();

                        SwingUtilities.invokeLater(() -> {
                            if (ui != null) {
                                ui.appendError(errorText);
                            }
                        });
                    }
                }

            } catch (IOException e) {
                if (running.get()) {
                    sleepBriefly();
                }
            }
        }
    }

    private void writeLoggerMessages() {
        while (running.get()) {
            try {
                IPCMessage message =
                        outgoingMessages.poll(500, TimeUnit.MILLISECONDS);

                if (message == null) {
                    continue;
                }

                try (PrintWriter writer = new PrintWriter(
                        new FileWriter(UI_TO_LOGGER))) {

                    writer.println(message.toString());
                    writer.flush();

                    if (writer.checkError()) {
                        System.err.println(
                                "Could not write message to Logger FIFO.");
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;

            } catch (IOException e) {
                if (running.get()) {
                    System.err.println(
                            "Logger FIFO write failed: " + e.getMessage());
                    sleepBriefly();
                }
            }
        }
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(
                        ui,
                        message,
                        "Simulator Error",
                        JOptionPane.ERROR_MESSAGE));
    }

    public void stopCore() {
        running.set(false);

        if (fifoReaderThread != null) {
            fifoReaderThread.interrupt();
        }

        if (fifoWriterThread != null) {
            fifoWriterThread.interrupt();
        }

        if (coreWriter != null) {
            coreWriter.println("QUIT");
            coreWriter.close();
        }

        if (coreReader != null) {
            try {
                coreReader.close();
            } catch (IOException ignored) {
                // Stream is already closing.
            }
        }

        if (coreProcess != null) {
            coreProcess.destroy();

            try {
                if (!coreProcess.waitFor(2, TimeUnit.SECONDS)) {
                    coreProcess.destroyForcibly();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                coreProcess.destroyForcibly();
            }
        }
    }

    public static void main(String[] args) {
        UIProcess uiProcess = new UIProcess();

        SimulatorUI simulatorUI = new SimulatorUI(uiProcess);
        uiProcess.startLoggerCommunication(simulatorUI);

        Runtime.getRuntime().addShutdownHook(
                new Thread(uiProcess::stopCore));

        simulatorUI.setVisible(true);
    }
}