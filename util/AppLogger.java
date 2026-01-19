package projekPBO.projek.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Logger Utility untuk semua aplikasi
 * Centralized logging untuk database operations dan errors
 */
public class AppLogger {
    private static final Logger logger = Logger.getLogger("BioskopApp");
    private static boolean initialized = false;

    static {
        try {
            if (!initialized) {
                // Set up console handler
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setLevel(Level.ALL);
                
                // Set format
                Formatter formatter = new SimpleFormatter() {
                    @Override
                    public synchronized String format(LogRecord record) {
                        return String.format(
                            "[%1$tH:%1$tM:%1$tS] [%2$-7s] %3$s - %4$s%n",
                            System.currentTimeMillis(),
                            record.getLevel().getLocalizedName(),
                            record.getSourceClassName(),
                            record.getMessage()
                        );
                    }
                };
                consoleHandler.setFormatter(formatter);
                
                // Add handler to logger
                logger.addHandler(consoleHandler);
                logger.setLevel(Level.ALL);
                logger.setUseParentHandlers(false);
                
                initialized = true;
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    /**
     * Get logger instance
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Log INFO message
     */
    public static void info(String message) {
        logger.info(message);
    }

    /**
     * Log WARNING message
     */
    public static void warning(String message) {
        logger.warning(message);
    }

    /**
     * Log SEVERE (error) message dengan exception
     */
    public static void severe(String message, Exception e) {
        logger.severe(message);
        if (e != null) {
            logger.severe("Exception: ".concat(e.getMessage()));
            logger.severe("Stack trace: ".concat(getStackTrace(e)));
        }
    }

    /**
     * Log error message tanpa exception
     */
    public static void error(String message) {
        logger.severe("ERROR: ".concat(message));
    }

    /**
     * Log database operation
     */
    public static void database(String operation, String message) {
        logger.info("[DB] ".concat(operation).concat(": ").concat(message));
    }

    /**
     * Log database error
     */
    public static void databaseError(String operation, Exception e) {
        logger.severe("[DB] ".concat(operation).concat(" FAILED: ").concat(e.getMessage()));
        if (e.getCause() != null) {
            logger.severe("Caused by: ".concat(e.getCause().getMessage()));
        }
    }

    /**
     * Log UI event
     */
    public static void ui(String event, String message) {
        logger.info("[UI] ".concat(event).concat(": ").concat(message));
    }

    /**
     * Convert exception stack trace to string
     */
    private static String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] elements = e.getStackTrace();
        for (int i = 0; i < Math.min(3, elements.length); i++) {
            sb.append("\n\t").append(elements[i]);
        }
        if (elements.length > 3) {
            sb.append("\n\t... ").append(elements.length - 3).append(" more");
        }
        return sb.toString();
    }

    /**
     * Test logger
     */
    public static void testLogger() {
        info("Logger initialized successfully");
    }
}
