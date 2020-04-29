package lexer;

public class LexToken {
    public final int id, row, col;
    public final String tokenType, lexeme;

    public LexToken(int id, int row, int col, String tokenType, String lexeme) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.tokenType = tokenType;
        this.lexeme = lexeme;
    }

    public static String getFormattedHeading() {
        return String.format("%5s %5s %5s %-15s%s", "ID", "ROW", "COL", "TYPE", "LEXEME");
    }

    public String getFormattedString() {
        return String.format("%5d %5d %5d %-15s%s", id, row, col, tokenType, lexeme);
    }
}
