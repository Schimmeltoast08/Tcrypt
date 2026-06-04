package tcrypt.util;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class Log {

    public static final Logger LOGGER = Logger.getLogger("tcrypt");

    static {
        try {

            String logFilePath = getLogFilePath();
            File logFile = new File(logFilePath);
            
            File parentDir = logFile.getParentFile();
            
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            FileHandler fileHandler = new FileHandler(logFilePath);

            fileHandler.setFormatter(new SimpleFormatter());

            LOGGER.setUseParentHandlers(false);
            LOGGER.addHandler(fileHandler);

            LOGGER.setLevel(Level.INFO);
            fileHandler.setLevel(Level.INFO);

        } catch (IOException e) {
            System.err.println("ERROR AT CREATING LOGGER: " + e);
        }
    }

    private Log() {}

    public static void log(String msg, Level level) {
        LOGGER.log(level, msg);
    }

    private static String getLogFilePath() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);

        if (os.contains("win")) {
            // Windows: C:\Users\<User>\AppData\Roaming\tcrypt\tcrypt.log
            String appData = System.getenv("APPDATA");
            return (appData != null ? appData : userHome) + File.separator + "tcrypt" + File.separator + "tcrypt.log";
        } else if (os.contains("mac")) {
            // macOS: /Users/<User>/Library/Logs/tcrypt/tcrypt.log
            return userHome + File.separator + "Library" + File.separator + "Logs" + File.separator + "tcrypt" + File.separator + "tcrypt.log";
        } else {
            // Linux/Unix /home/<User>/.local/share/tcrypt/tcrypt.log
            return userHome + File.separator + ".local" + File.separator + "share" + File.separator + "tcrypt" + File.separator + "tcrypt.log";
        }
    }
}