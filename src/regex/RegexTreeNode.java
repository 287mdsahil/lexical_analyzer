package regex;

public class RegexTreeNode {
    private RegexTreeNodeType type;
    private char value;
    private RegexTreeNode left, right;

    public RegexTreeNode(RegexTreeNodeType type, char value) {
        this.type = type;
        this.value = value;
    }

    public RegexTreeNode(RegexTreeNodeType type, char value, 
                         RegexTreeNode left, RegexTreeNode right) {
        this.type = type;
        this.value = value;
        this.left = left;
        this.right = right;
    }

    public RegexTreeNode(RegexToken token) {
        this.type = getNodeType(token.type);
        this.value = token.value;
    }

    public RegexTreeNode(RegexToken token, RegexTreeNode left, RegexTreeNode right) {
        this(token);
        this.left = left;
        this.right = right;
    }

    // public RegexTreeNode(RegexTreeNode node) {
    //     this(node.getType(), node.getValue(), node.getLeftChild(), node.getRightChild());
    // }

    public static RegexTreeNodeType getNodeType(RegexTokenType type) {
        switch (type) {
            case CHAR:      return RegexTreeNodeType.CHAR;
            case CLOSURE:   return RegexTreeNodeType.CLOSURE;
            case CONCAT:    return RegexTreeNodeType.CONCAT;
            case EPSILON:   return RegexTreeNodeType.EPSILON;
            case UNION:     return RegexTreeNodeType.UNION;
            default:        return null;
        }
    }

    public void setLeftChild(RegexTreeNode node) {
        left = node;
    }

    public void setRightChild(RegexTreeNode node) {
        right = node;
    }

    public RegexTreeNodeType getType() {
        return type;
    }

    public char getValue() {
        return value;
    }

    public RegexTreeNode getLeftChild() {
        return left;
    }

    public RegexTreeNode getRightChild() {
        return right;
    }
}
