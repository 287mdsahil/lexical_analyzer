package lexer;

public class LexToken {
    public final int id;
    public final String tokenType, lexeme;

    public LexToken(int id, String tokenType, String lexeme) {
        this.id = id;
        this.tokenType = tokenType;
        this.lexeme = lexeme;
    }
}
