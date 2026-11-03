

import java.io.*;
import java.util.concurrent.TimeUnit;

public class CoreClient {
    private final Process p;
    private final BufferedReader in;
    private final PrintWriter out;

    public CoreClient() throws IOException {
        String java = System.getProperty("java.home")
                + File.separator + "bin" + File.separator + "java";

       p = new ProcessBuilder(java, "-cp", "bin", "Core.CoreMain")
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start();

        in = new BufferedReader(
                new InputStreamReader(p.getInputStream())
        );

        out = new PrintWriter(
                new OutputStreamWriter(p.getOutputStream()), true
        );
    }

    // Send one command and wait for exactly one reply
    public synchronized String send(String cmd) throws IOException {
        out.println(cmd);

        String reply = in.readLine();

        return reply == null ? "ERROR core closed" : reply;
    }

    // Return the Core process handle
    public ProcessHandle handle() {
        return p.toHandle();
    }

    // Close the Core process safely
    public void close() {
        try {
            out.println("QUIT");
            out.flush();

            // Wait for graceful shutdown
            if (!p.waitFor(3, TimeUnit.SECONDS)) {
                p.destroy();

                // If still running, force shutdown
                if (!p.waitFor(2, TimeUnit.SECONDS)) {
                    p.destroyForcibly();
                }
            }

        } catch (Exception e) {
            p.destroyForcibly();
        }
    }
}