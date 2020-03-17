package regex;

public class RegexToken {
    public final RegexTokenType type;
    public final char value;

    public RegexToken(RegexTokenType type, char value) {
        this.type = type;
        this.value = value;
    }

    public static RegexToken getToken(RegexSpecialChar spec) {
        switch (spec) {
            case BCLOSE:    return new RegexToken(RegexTokenType.BCLOSE, spec.charValue());
            case BOPEN:     return new RegexToken(RegexTokenType.BOPEN, spec.charValue());
            case CLOSURE:   return new RegexToken(RegexTokenType.CLOSURE, spec.charValue());
            case CONCAT:    return new RegexToken(RegexTokenType.CONCAT, spec.charValue());
            case EPSILON:   return new RegexToken(RegexTokenType.EPSILON, spec.charValue());
            case UNION:     return new RegexToken(RegexTokenType.UNION, spec.charValue());

            default:        return null;
        }
    }
}
