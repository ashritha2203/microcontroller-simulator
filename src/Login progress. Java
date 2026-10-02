package logging;

import shared.IPCMessage;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class LoggingProcess {

    // POSIX FIFO path
    private static final String FIFO_PATH = "/tmp/logging_fifo";

    // FCFS queue for log messages
    private final BlockingQueue<String> logFCFSQueue =
            new ArrayBlockingQueue<>(200);

    public static void main(String[] args) {
        LoggingProcess process = new LoggingProcess();
        process.start();
    }

    public void start() {

        System.out.println(
                "[LOGGING PROCESS] Starting Async Log Daemon..."
        );

        // Start the file-writer thread
        Thread writerThread =
                new Thread(this::fileWriterWorker);

        writerThread.start();

        // Listen to the POSIX FIFO
        fifoListener();
    }

    private void fifoListener() {

        System.out.println(
                "[LOGGING PROCESS] Waiting for log streams on FIFO: "
                        + FIFO_PATH
        );

        try (
                FileInputStream fis =
                        new FileInputStream(FIFO_PATH);

                ObjectInputStream in =
                        new ObjectInputStream(fis)
        ) {

            while (true) {

                // Receive IPC message
                IPCMessage msg =
                        (IPCMessage) in.readObject();

                // Check message
                if (msg != null && msg.getLogText() != null) {

                    // Add message to FCFS queue
                    logFCFSQueue.put(msg.getLogText());
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "[LOGGER ERROR] FIFO connection lost: "
                            + e.getMessage()
            );
        }
    }

    private void fileWriterWorker() {

        // false = DEBUG messages are not displayed
        boolean showDebugInConsole = false;

        try (
                PrintWriter writer =
                        new PrintWriter(
                                new FileWriter(
                                        "simulation.log",
                                        true
                                )
                        )
        ) {

            while (true) {

                // Get the first message from FCFS queue
                String logText =
                        logFCFSQueue.take();

                // Add date and time
                String formattedLog =
                        String.format(
                                "[%tF %<tT] %s",
                                System.currentTimeMillis(),
                                logText
                        );

                // Write log to file
                writer.println(formattedLog);
                writer.flush();

                // Display logs in terminal
                if (showDebugInConsole
                        || !logText.contains("[DEBUG]")) {

                    System.out.println(
                            "[LOG] " + formattedLog
                    );
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "[LOGGER WRITE ERROR] "
                            + e.getMessage()
            );
        }
    }
}
