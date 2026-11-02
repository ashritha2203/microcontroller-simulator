package integration;
import java.io.File;
import java.nio.file.*;

public class Launcher {

    static final String FIFO = "/tmp/logging_fifo";

    public static void main(String[] args) throws Exception {
        String java = System.getProperty("java.home")
                + File.separator + "bin" + File.separator + "java";

        // 1. Create the named pipe (FIFO) that Core and Logger use
        if (!Files.exists(Paths.get(FIFO))) {
            int rc = new ProcessBuilder("mkfifo", FIFO).inheritIO().start().waitFor();
            if (rc != 0) {
                System.err.println("Could not create " + FIFO + " (FIFOs need Linux)");
                return;
            }
        }

        // 2. Start the Logger first, so it is waiting at the FIFO
        Process logger = new ProcessBuilder(java, "-cp", "bin", "logging.LoggingProcess")
                .inheritIO().start();
        Thread.sleep(1000);

        // 3. Start the UI, which starts Core by itself through a pipe
        Process ui = new ProcessBuilder(java, "-cp", "bin", "UIProcess")
                .inheritIO().start();

        // 4. When the UI window closes, stop the Logger too
        ui.waitFor();
        logger.destroy();
    }
}