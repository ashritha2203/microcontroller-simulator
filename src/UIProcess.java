import java.io.*;

public class UIProcess {

    private Process coreProcess;
    private BufferedReader coreOutput;
    private PrintWriter coreInput;

    public void startCore() throws Exception {
        String javaHome = System.getProperty("java.home");
        String java = javaHome + File.separator + "bin"
                + File.separator + "java";

        String classPath = System.getProperty("java.class.path");

        ProcessBuilder builder = new ProcessBuilder(
                java,
                "-cp",
                classPath,
                "CoreMain"
        );

        builder.redirectError(ProcessBuilder.Redirect.INHERIT);

        coreProcess = builder.start();

        coreOutput = new BufferedReader(
                new InputStreamReader(coreProcess.getInputStream())
        );

        coreInput = new PrintWriter(
                new OutputStreamWriter(coreProcess.getOutputStream()),
                true
        );
    }

    public String sendCommand(String command) throws IOException {
        if (coreProcess == null || !coreProcess.isAlive()) {
            throw new IOException("Core process is not running");
        }

        coreInput.println(command);
        return coreOutput.readLine();
    }

    public void stopCore() {
        try {
            if (coreInput != null) {
                coreInput.println("QUIT");
            }

            if (coreProcess != null) {
                coreProcess.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        UIProcess uiProcess = new UIProcess();

        try {
            uiProcess.startCore();

            System.out.println("UI Process started");
            System.out.println("Core Process connected");

            Runtime.getRuntime().addShutdownHook(
                new Thread(uiProcess::stopCore)
            );

            javax.swing.SwingUtilities.invokeLater(() -> {
                SimulatorUI ui = new SimulatorUI(uiProcess);
                ui.setVisible(true);
            });

        } catch (Exception e) {
            System.err.println("Failed to start Core Process");
            e.printStackTrace();
        }
    }
}
