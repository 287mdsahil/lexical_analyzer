package regex;

import java.util.ArrayList;
import java.util.Stack;

public class RegexTree {
    private RegexTreeNode root;

    public RegexTree(Regex regex) {
        root = makeTree(regex);
    }

    private static RegexTreeNode makeTree(Regex regex) {
        Stack<RegexTreeNode> operands = new Stack<>();
        ArrayList<RegexToken> postfix = InfixToPostfix.convert(regex);

        for (RegexToken token : postfix) {
            if (token.isOperand()) {
                operands.push(new RegexTreeNode(token));
            }
            else if (token.type == RegexTokenType.CLOSURE) {
                RegexTreeNode left = operands.pop();
                operands.push(new RegexTreeNode(token, left, null));
            }
            else {
                RegexTreeNode right = operands.pop(), left = operands.pop();
                operands.push(new RegexTreeNode(token, left, right));
            }
        }

        if (operands.size() != 1)
            throw new IllegalStateException("There should be exactly one operand on the stack");

        return operands.pop();
    }

    public RegexTreeNode getRoot() {
        return root;
    }

    private void prettyPrint(RegexTreeNode node, StringBuilder prepend, StringBuilder buffer) {
        if (node == null) {
            buffer.append(prepend);
            buffer.append("null");
            buffer.append("\n");
            return;
        }

        buffer.append(prepend);
        buffer.append(node.getValue());
        buffer.append('\n');

        prepend.append("    ");
        prettyPrint(node.getLeftChild(), prepend, buffer);
        prettyPrint(node.getRightChild(), prepend, buffer);
        prepend.setLength(prepend.length() - 4);
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        
        prettyPrint(root, new StringBuilder(), buffer);

        return buffer.toString();
    }

    public static void main(String[] args) {
        Regex r = new Regex(args[0]);

        RegexTree tree = new RegexTree(r);

        System.out.println(tree);
    }
}
