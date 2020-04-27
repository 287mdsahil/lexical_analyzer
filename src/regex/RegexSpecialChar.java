package regex;

public enum RegexSpecialChar {
    BCLOSE(')'), 
    BOPEN('('), 
    CLOSURE('*'), 
    CONCAT('.'), 
    EPSILON('ε'), 
    ESCAPE('\\'),
    RANGECLOSE(']'),
    RANGEOPEN('['),
    UNION('|');

    private final char charValue;

    private RegexSpecialChar(char charValue) {
        this.charValue = charValue;
    }

    public char charValue() {
        return charValue;
    }

    public static boolean isSpecialChar(char ch) {
        for (RegexSpecialChar s : values())
            if (s.charValue() == ch)
                return true;
                
        return false;
    }

    public static RegexSpecialChar getSpecialChar(char ch) {
        for (RegexSpecialChar s : values())
            if (s.charValue() == ch)
                return s;

        return null;
    }
}
