package au.com.addstar.discord.ulilities;


/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 13/12/2016.
 */
public class MessageFormatter {

    public static String addStyle(Styles style, String content){
        return style.getMarkdown()+content+style.getReverseMarkdown();
    }
    public enum Styles {
        ITALICS("*"),
        BOLD("**"),
        BOLD_ITALICS("***"),
        STRIKEOUT("~~"),
        CODE("``` "),
        INLINE_CODE("`"),
        UNDERLINE("__"),
        UNDERLINE_ITALICS("__*"),
        UNDERLINE_BOLD("__**"),
        UNDERLINE_BOLD_ITALICS("__***"),
        CODE_WITH_LANG("```");
    
        final String markdown, reverseMarkdown;
    
        Styles(String markdown) {
            this.markdown = markdown;
            this.reverseMarkdown = new StringBuilder(markdown).reverse().toString();
        }
    
        /**
         * Gets the markdown formatting for the style.
         *
         * @return The markdown formatting.
         */
        public String getMarkdown() {
            return markdown;
        }
    
        /**
         * Reverses the markdown formatting to be appended to the end of a
         * formatted string.
         *
         * @return The reversed markdown formatting.
         */
        public String getReverseMarkdown() {
            return reverseMarkdown;
        }
    
        @Override
        public String toString() {
            return markdown;
        }
    }
}
