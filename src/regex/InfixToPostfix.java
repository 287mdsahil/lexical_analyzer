package regex;

import java.util.ArrayList;
import java.util.Stack;

public class InfixToPostfix {
    private static int getPrecedence(RegexTokenType tokenType) {
        switch (tokenType) {
            case BOPEN:     return 1;
            case UNION:     return 2;
            case CONCAT:    return 3;
            case CLOSURE:   return 4;
            default:        return 0;
        }
    }

    public static ArrayList<RegexToken> convert(Regex regex) {
        ArrayList<RegexToken> 
            infix = regex.getNormalizedTokens(), 
            postfix = new ArrayList<>();

        Stack<RegexToken> stk = new Stack<>();

        for (RegexToken token : infix) {
            if (token.isOperand()) {
                postfix.add(token);
                continue;
            }

            switch (token.type) {
                case BOPEN:
                    stk.push(token);
                    break;
                
                case BCLOSE:
                    while (!(stk.peek().type == RegexTokenType.BOPEN))
                        postfix.add(stk.pop());
                    stk.pop();
                    break;
                
                case CLOSURE:
                    postfix.add(token);
                    break;
                
                default:
                    while (!stk.empty()) {
                        RegexToken topToken = stk.peek();

                        int currentOperatorPrecedence = getPrecedence(token.type), 
                            topOperatorPrecedence = getPrecedence(topToken.type);

                        if (topOperatorPrecedence >= currentOperatorPrecedence)
                            postfix.add(stk.pop());
                        else
                            break;
                    }
                    stk.push(token);
                    break;
            }
        }

        while (!stk.empty())
            postfix.add(stk.pop());

        return postfix;
    }

    public static void main(String[] args) {
        Regex r = new Regex(args[0]);
        System.out.println(r.getNormalizedString());
        ArrayList<RegexToken> ntoks = r.getNormalizedTokens();
        for (RegexToken tok : ntoks) {
            System.out.println(tok.value + "\t" + tok.type);
        }

        System.out.println();

        ArrayList<RegexToken> postfix = InfixToPostfix.convert(r);
        for (RegexToken tok : postfix) {
            System.out.println(tok.value + "\t" + tok.type);
        }
    }
}
