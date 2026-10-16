import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class LoggerProcess {

    private static final String LOG_FILE = "simulator.log";

    public static void main(String[] args) {

        LoggerProcess logger = new LoggerProcess();

        System.out.println("Logger Process Started");

        LogMessage executionLog =
                new LogMessage("EXECUTION", "ADD instruction executed");

        logger.processLog(executionLog);

        LogMessage errorLog =
                new LogMessage("ERROR", "Invalid instruction");

        logger.processLog(errorLog);

        System.out.println("Logger Process Finished");
    }

    public void processLog(LogMessage log) {

        String time = LocalDateTime.now().toString();

        String logEntry =
                time + " " + log.toString();

        System.out.println(logEntry);

        writeToFile(logEntry);
    }

    private void writeToFile(String logEntry) {

        try {
            FileWriter fileWriter =
                    new FileWriter(LOG_FILE, true);

            PrintWriter writer =
                    new PrintWriter(fileWriter);

            writer.println(logEntry);

            writer.close();

        } catch (IOException e) {

            System.out.println(
                    "ERROR: Unable to write log file"
            );
        }
    }
}
        
