package regex;

import java.util.ArrayList;
import java.util.Stack;

import utils.StringEscapeUtils;

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

    private void prettyPrint(RegexTreeNode node, String prefix, String childPrefix, 
                             StringBuilder buffer) {
        buffer.append(prefix);
        if (node.getType() == RegexTreeNodeType.CHAR && RegexSpecialChar.isSpecialChar(node.getValue()))
            buffer.append(RegexSpecialChar.ESCAPE.charValue());
        buffer.append(StringEscapeUtils.getRepresentation(node.getValue()));
        buffer.append('\n');

        RegexTreeNode leftChild = node.getLeftChild(), rightChild = node.getRightChild();

        if (leftChild != null) {
            if (rightChild != null) {
                prettyPrint(leftChild, childPrefix + "|-- ", childPrefix + "|   ", buffer);
                prettyPrint(rightChild, childPrefix + "\\-- ", childPrefix + "    ", buffer);
            }
            else {
                prettyPrint(leftChild, childPrefix + "\\-- ", childPrefix + "    ", buffer);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        
        prettyPrint(root, "    ", "    ", buffer);

        return buffer.toString();
    }

    public static void main(String[] args) {
        Regex r = new Regex(args[0]);

        RegexTree tree = new RegexTree(r);

        System.out.println(tree);
    }
}
