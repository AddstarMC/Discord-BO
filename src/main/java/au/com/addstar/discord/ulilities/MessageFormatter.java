package au.com.addstar.discord.ulilities;

import sx.blah.discord.util.MessageBuilder.Styles;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 13/12/2016.
 */
public class MessageFormatter {

    public static String addStyle(Styles style, String content){
        return style.getMarkdown()+content+style.getReverseMarkdown();
    }

}
