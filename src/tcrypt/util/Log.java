package tcrypt.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class Log {

    public static final Logger LOGGER = Logger.getLogger("tcrypt");

    static {
        try {
            FileHandler fileHandler = new FileHandler("tcrypt.log");

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
}