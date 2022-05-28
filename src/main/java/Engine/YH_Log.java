package Engine;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YH_Log {
    static private final Logger error_logger = LoggerFactory.getLogger("error_logger");
    static private final Logger logger = LoggerFactory.getLogger("logger");

    static public void trace(String message, Object ...args){
        logger.trace(message, args);
    }

    static public void debug(String message, Object ...args){
        logger.debug(message, args);
    }

    static public void info(String message, Object ...args){
        logger.info(message, args);
    }

    static public void warn(String message, Object ...args){
        logger.warn(message, args);
    }

    static public void error(String message, Object ...args){
        error_logger.error(message, args);
    }

    static public void _assert(boolean check, String message) {
        assert (check) : message;
    }
}
