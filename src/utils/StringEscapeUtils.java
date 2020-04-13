package utils;

import java.awt.event.KeyEvent;

/**
 * The only reason this class exists is because Java does not provide any support
 * for escaping or unescaping strings, or even characters. Uff!
 */
public class StringEscapeUtils {
    public static final char 
        ESCAPE_START = '\\',    /** Start of escape sequence */
        UNICODE_ESCAPE = 'u';   /** First char of unicode represenation like '\u0045' == 'A' */

    /**
     * Take the second character after an assumed backward slash '\' and
     * return a single character corresponding to the combined escape sequence.
     * 
     * @param second the character after the backward slash.
     * @return The escape sequence character or {@code second} if invalid escape sequence.
     */
    public static char escape(char second) {
        switch (second) {
            case '0':   return '\0';
            case 'b':   return '\b';
            case 'f':   return '\f';
            case 'n':   return '\n';
            case 'r':   return '\r';
            case 't':   return '\t';

            default:    return second;
        }
    }

    /**
     * Take a character and if it is an escape sequence character, return the
     * second character after the backward slash in its representation.
     * 
     * <p>
     * e.g. {@code unescape('\n') -> 'n'}
     * 
     * <p>
     * Otherwise return back the argument itself, {@code ch}.
     * 
     * @param ch a character, possibly from the escape sequence.
     * @return The second character of the escape sequence.
     */
    public static char unescape(char ch) {
        switch (ch) {
            case '\0':   return '0';
            case '\b':   return 'b';
            case '\f':   return 'f';
            case '\n':   return 'n';
            case '\r':   return 'r';
            case '\t':   return 't';

            default:    return ch;
        }
    }

    /**
     * Check whether a character is part of the escape sequence.
     * 
     * @param ch a character.
     * @return The value {@code true} if {@code ch} is an escape sequence character, 
     * otherwise {@code false}.
     */
    public static boolean isEscapeSequence(char ch) {
        switch (ch) {
            case '\0':  // fall through
            case '\b':  // fall through
            case '\f':  // fall through
            case '\n':  // fall through
            case '\r':  // fall through
            case '\t':  return true;

            default:    return false;
        }
    }

    /**
     * Check whether a character is printable in the current environment.
     * 
     * @param ch a character.
     * @return Whether the character can be printed or not.
     */
    public static boolean isPrintableChar(char ch) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
        return 
            !Character.isISOControl(ch) 
            && ch != KeyEvent.CHAR_UNDEFINED 
            && block != null 
            && block != Character.UnicodeBlock.SPECIALS;
    }

    /**
     * Return a string representation of the character in an
     * user-readable form.
     * 
     * @param ch a character.
     * @return An user-readable {@code String} representation of the character.
     */
    public static String getRepresentation(char ch) {
        if (isPrintableChar(ch))
            return String.valueOf(ch);

        if (isEscapeSequence(ch))
            return "" + ESCAPE_START + unescape(ch);

        return String.format("\\u%04x", (int) ch);
    }

    /**
     * Escape a string with non-printable characters.
     * 
     * @param str a string.
     * @return The string with the unprintable characters converted into a
     * user-readable form, as per {@link #getRepresentation(char)}.
     */
    public static String escape(String str) {
        StringBuilder buffer = new StringBuilder();
        for (int idx = 0; idx < str.length(); idx++)
            buffer.append(getRepresentation(str.charAt(idx)));
        return buffer.toString();
    }

    /**
     * Unescape a string with escape sequences present.
     * 
     * @param str a string.
     * @return The string with the escape sequences removed and collapsed into
     * a single {@code char}.
     */
    public static String unescape(String str) {
        StringBuilder buffer = new StringBuilder();

        for (int idx = 0; idx < str.length(); ) {
            char first = str.charAt(idx++);
            
            if (first != ESCAPE_START) {
                buffer.append(first);
                continue;
            }

            char second = str.charAt(idx++);
            if (second != UNICODE_ESCAPE) {
                char escaped = escape(second);
                if (escaped == second)
                    buffer.append("" + first + second);
                else
                    buffer.append(escaped);
            }
            else {
                buffer.append((char) Integer.parseInt(str.substring(idx, idx + 4), 16));
                idx += 4;
            }
        }

        return buffer.toString();
    }
}
