import java.io.*;

public class UIProcess {

    private Process coreProcess;
    private BufferedReader coreReader;
    private PrintWriter coreWriter;

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
                    + File.pathSeparator
                    + mainPath;

            ProcessBuilder builder = new ProcessBuilder(
                    java,
                    "-cp",
                    classPath,
                    "CoreMain"
            );

            builder.redirectError(ProcessBuilder.Redirect.INHERIT);

            coreProcess = builder.start();

            coreReader = new BufferedReader(
                    new InputStreamReader(
                            coreProcess.getInputStream()
                    )
            );

            coreWriter = new PrintWriter(
                    new OutputStreamWriter(
                            coreProcess.getOutputStream()
                    ),
                    true
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not start Core Process: "
                    + e.getMessage()
            );
        }
    }

    public synchronized String sendCommand(String command) {

        if (coreProcess == null || !coreProcess.isAlive()) {
            return "ERROR Core Process is not running";
        }

        coreWriter.println(command);

        try {
            String response = coreReader.readLine();

            if (response == null) {
                return "ERROR Core Process closed";
            }

            return response;

        } catch (IOException e) {
            return "ERROR " + e.getMessage();
        }
    }

    public void stopCore() {

        try {
            if (coreWriter != null) {
                coreWriter.println("QUIT");
            }

            if (coreProcess != null) {
                coreProcess.destroy();
            }

        } catch (Exception e) {
            System.err.println(
                    "Error stopping Core Process"
            );
        }
    }

    public static void main(String[] args) {

        UIProcess uiProcess = new UIProcess();

        Runtime.getRuntime().addShutdownHook(
                new Thread(uiProcess::stopCore)
        );

        SimulatorUI ui =
                new SimulatorUI(uiProcess);

        ui.setVisible(true);
    }
}
