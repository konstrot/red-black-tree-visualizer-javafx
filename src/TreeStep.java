import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Unveränderliche Momentaufnahme eines einzelnen Animationsschritts.
 */
public final class TreeStep {

    /** Unveränderlicher Knoten der Momentaufnahme. */
    public static final class Node {
        private final int key;
        private final String color;
        private final Node left;
        private final Node right;

        private Node(int key, String color, Node left, Node right) {
            this.key = key;
            this.color = color;
            this.left = left;
            this.right = right;
        }

        public int getKey() {
            return key;
        }

        public String getColor() {
            return color;
        }

        public Node getLeft() {
            return left;
        }

        public Node getRight() {
            return right;
        }
    }

    private final Node root;
    private final String description;
    private final Set<Integer> highlightedKeys;
    private final boolean rootBlack;
    private final boolean noRedRedViolation;
    private final boolean equalBlackHeight;
    private final int blackHeight;

    private TreeStep(
            Node root,
            String description,
            Set<Integer> highlightedKeys,
            boolean rootBlack,
            boolean noRedRedViolation,
            boolean equalBlackHeight,
            int blackHeight) {
        this.root = root;
        this.description = description;
        this.highlightedKeys = highlightedKeys;
        this.rootBlack = rootBlack;
        this.noRedRedViolation = noRedRedViolation;
        this.equalBlackHeight = equalBlackHeight;
        this.blackHeight = blackHeight;
    }

    public static TreeStep capture(
            RBTree tree,
            String description,
            int... highlightedKeys) {

        Node rootCopy = copy(tree.getRoot(), tree.getNil());

        Set<Integer> highlights = new HashSet<>();
        Arrays.stream(highlightedKeys).forEach(highlights::add);

        boolean rootIsBlack = rootCopy == null
                || "BLACK".equals(rootCopy.getColor());

        boolean noRedRed = hasNoRedRedViolation(rootCopy, false);

        int calculatedBlackHeight = calculateBlackHeight(rootCopy);
        boolean equalHeight = calculatedBlackHeight >= 0;

        return new TreeStep(
                rootCopy,
                description,
                Collections.unmodifiableSet(highlights),
                rootIsBlack,
                noRedRed,
                equalHeight,
                equalHeight ? calculatedBlackHeight : 0);
    }

    private static Node copy(RBTreeNode node, RBTreeNode nil) {
        if (node == null || node == nil) {
            return null;
        }

        return new Node(
                node.key,
                node.color,
                copy(node.left, nil),
                copy(node.right, nil));
    }

    private static boolean hasNoRedRedViolation(Node node, boolean parentIsRed) {
        if (node == null) {
            return true;
        }

        boolean currentIsRed = "RED".equals(node.getColor());
        if (parentIsRed && currentIsRed) {
            return false;
        }

        return hasNoRedRedViolation(node.getLeft(), currentIsRed)
                && hasNoRedRedViolation(node.getRight(), currentIsRed);
    }

    /**
     * Liefert die Schwarzhöhe einschließlich des schwarzen NIL-Blatts.
     * Bei unterschiedlichen Schwarzhöhen wird -1 geliefert.
     */
    private static int calculateBlackHeight(Node node) {
        if (node == null) {
            return 1; // NIL-Blatt ist schwarz.
        }

        int leftHeight = calculateBlackHeight(node.getLeft());
        int rightHeight = calculateBlackHeight(node.getRight());

        if (leftHeight < 0 || rightHeight < 0 || leftHeight != rightHeight) {
            return -1;
        }

        return leftHeight + ("BLACK".equals(node.getColor()) ? 1 : 0);
    }

    public Node getRoot() {
        return root;
    }

    public String getDescription() {
        return description;
    }

    public Set<Integer> getHighlightedKeys() {
        return highlightedKeys;
    }

    public boolean isRootBlack() {
        return rootBlack;
    }

    public boolean hasNoRedRedViolation() {
        return noRedRedViolation;
    }

    public boolean hasEqualBlackHeight() {
        return equalBlackHeight;
    }

    public int getBlackHeight() {
        return blackHeight;
    }

    public boolean isValidRedBlackTree() {
        return rootBlack && noRedRedViolation && equalBlackHeight;
    }
}
