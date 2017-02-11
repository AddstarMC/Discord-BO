package au.com.addstar.discord;

import java.util.logging.Logger;
/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 9/02/2017.
 */
public class BungeeLog implements ILog {
    private Logger log;
    BungeeLog(Logger log) {

    }

    @Override
    public void debug(String msg) {
        log.finest(msg);
    }

    @Override
    public void info(String msg) {
        log.info(msg);
    }

    @Override
    public void warn(String msg) {
        log.warning(msg);
    }

    @Override
    public void error(String msg) {
        log.severe(msg);
    }
}
