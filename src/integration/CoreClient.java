package integration;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class CoreClient {
    private final Process p;
    private final BufferedReader in;
    private final PrintWriter out;

    public CoreClient() throws IOException {
        String java = System.getProperty("java.home")
                + File.separator + "bin" + File.separator + "java";
        p = new ProcessBuilder(java, "-cp", "bin", "CoreMain")
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start();
        in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(p.getOutputStream()), true);
    }

    // Send one command line, wait for exactly one reply line
    public String send(String cmd) throws IOException {
        out.println(cmd);
        String reply = in.readLine();
        return reply == null ? "ERROR core closed" : reply;
    }

    public ProcessHandle handle() { return p.toHandle(); }

    public void close() {
        try {
            out.println("QUIT");
            if (!p.waitFor(3, TimeUnit.SECONDS)) p.destroy();
        } catch (Exception e) {
            p.destroy();
        }
    }
}