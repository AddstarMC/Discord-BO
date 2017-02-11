package au.com.addstar.discord;

import org.slf4j.Logger;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 9/02/2017.
 */
public class BotLog implements ILog{

    private Logger log;
    public BotLog(Logger log) {
        this.log = log;
    }

    @Override
    public void debug(String msg) {
        log.debug(msg);
    }

    @Override
    public void info(String msg) {
        log.info(msg);
    }

    @Override
    public void warn(String msg) {
        log.warn(msg);
    }

    @Override
    public void error(String msg) {
        log.error(msg);
    }
}
