package au.com.addstar.discord.redis;

/**
 * Created for the AddstarMC Project.
 * Created by Narimm on 2/02/2017.
 */
public class CommandException extends Exception {

    private static final long serialVersionUID = 3286244591128017744L;

    public CommandException(String error) {
        super(error);
    }
}
