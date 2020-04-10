package regex;

import java.util.ArrayList;

public class Regex {
    private final String raw, normalized;
    private ArrayList<RegexToken> tokens, normalizedTokens;

    public Regex(String regex) {
        raw = regex;
        tokens = tokenize(raw);
        tokens = replaceRanges(tokens);
        normalizedTokens = normalize(tokens);
        normalized = convertTokensToString(normalizedTokens);
    }

    public String getRawString() {
        return raw;
    }

    public String getNormalizedString() {
        return normalized;
    }

    public ArrayList<RegexToken> getTokens() {
        return new ArrayList<>(tokens);
    }

    public ArrayList<RegexToken> getNormalizedTokens() {
        return new ArrayList<>(normalizedTokens);
    }

    public static ArrayList<RegexToken> tokenize(String raw) {
        ArrayList<RegexToken> tokens = new ArrayList<>();

        for (int idx = 0; idx < raw.length(); ) {
            char first = raw.charAt(idx);
            idx++;

            if (!RegexSpecialChar.isSpecialChar(first)) {
                tokens.add(new RegexToken(RegexTokenType.CHAR, first));
                continue;                
            }
            
            if (first == RegexSpecialChar.ESCAPE.charValue()) {
                char second = raw.charAt(idx);
                idx++;

                tokens.add(new RegexToken(RegexTokenType.CHAR, second));
                continue;
            }

            tokens.add(RegexToken.getToken(RegexSpecialChar.getSpecialChar(first)));
        }

        return tokens;
    }

    private static boolean isConcatOperatorNeeded(RegexToken left, RegexToken right) {
        RegexTokenType l = left.type, r = right.type;

        boolean 
            lhs = l == RegexTokenType.CHAR 
                    || l == RegexTokenType.EPSILON 
                    || l == RegexTokenType.BCLOSE
                    || l == RegexTokenType.CLOSURE,
            rhs = r == RegexTokenType.CHAR 
                    || r == RegexTokenType.EPSILON 
                    || r == RegexTokenType.BOPEN;

        if (lhs && rhs)
            return true;

        return false;
    }

    public static ArrayList<RegexToken> replaceRanges(ArrayList<RegexToken> tokens) {
        ArrayList<RegexToken> expanded = new ArrayList<>();

        for (int idx = 0; idx < tokens.size(); idx++) {
            RegexToken curr = tokens.get(idx);
            if (curr.type != RegexTokenType.RANGEOPEN) {
                expanded.add(curr);
                continue;
            }

            idx++;
            expanded.add(RegexToken.getToken(RegexSpecialChar.BOPEN));
            while ((curr = tokens.get(idx)).type != RegexTokenType.RANGECLOSE) {
                RegexToken next = tokens.get(idx + 1);
                if (next.value < curr.value)
                    throw new IllegalArgumentException("Range next falls before range first");

                for (char ch = curr.value; ch <= next.value; ch++) {
                    expanded.add(new RegexToken(RegexTokenType.CHAR, ch));
                    expanded.add(RegexToken.getToken(RegexSpecialChar.UNION));
                }
                idx += 2;
            }
            // we have an extra union operator at the end, overwrite it with closing brackets
            expanded.set(expanded.size() - 1, RegexToken.getToken(RegexSpecialChar.BCLOSE));
        }

        return expanded;
    }

    public static ArrayList<RegexToken> normalize(ArrayList<RegexToken> tokens) {
        ArrayList<RegexToken> norm = new ArrayList<>();

        RegexToken curr, prev = tokens.get(0);
        norm.add(prev);

        for (int idx = 1; idx < tokens.size(); idx++) {
            curr = tokens.get(idx);

            if (isConcatOperatorNeeded(prev, curr)) {
                norm.add(new RegexToken(RegexTokenType.CONCAT, 
                                        RegexSpecialChar.CONCAT.charValue()));
            }

            norm.add(curr);

            prev = curr;
        }

        return norm;
    }

    public static String convertTokensToString(ArrayList<RegexToken> tokens) {
        StringBuilder buffer = new StringBuilder();

        for (RegexToken token : tokens) {
            if (token.type == RegexTokenType.CHAR && RegexSpecialChar.isSpecialChar(token.value)) {
                buffer.append(RegexSpecialChar.ESCAPE.charValue());
                buffer.append(token.value);
                continue;
            }

            buffer.append(token.value);
        }

        return buffer.toString();
    }

    public static void main(String args[]) {
        Regex r = new Regex(args[0]);

        System.out.println(r.getRawString());
        System.out.println(r.getNormalizedString());

        ArrayList<RegexToken> ntoks = r.getNormalizedTokens();
        for (RegexToken tok : ntoks) {
            System.out.println(tok.value + "\t" + tok.type);
        }
        
    }
}
