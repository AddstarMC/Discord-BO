package au.com.addstar.discord;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 9/02/2017.
 */
public interface ILog {

    void debug(String msg);
    void info(String msg) ;
    void warn(String msg);
    void error(String msg);
}
