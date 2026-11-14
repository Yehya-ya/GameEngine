package GameEngine.Engine.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YH_Log {
    private static final Logger error_logger = LoggerFactory.getLogger("error_logger");
    private static final Logger logger = LoggerFactory.getLogger("logger");

    public static void YH_LOG_TRACE(String message, Object... args) {
        logger.trace(message, args);
    }

    public static void YH_LOG_DEBUG(String message, Object... args) {
        logger.debug(message, args);
    }

    public static void YH_LOG_INFO(String message, Object... args) {
        logger.info(message, args);
    }

    public static void YH_LOG_WARN(String message, Object... args) {
        logger.warn(message, args);
    }

    public static void YH_LOG_ERROR(String message, Object... args) {
        error_logger.error(message, args);
    }

    public static void YH_ASSERT(boolean check, String message) {
        assert (check) : message;
    }
}
