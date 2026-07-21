/**
 * Knoten eines Rot-Schwarz-Baums.
 */
public class RBTreeNode {
    public int key;
    public String color; // "RED" oder "BLACK"
    public RBTreeNode left;
    public RBTreeNode right;
    public RBTreeNode parent;

    public RBTreeNode(int key, String color) {
        this.key = key;
        this.color = color;
    }

    @Override
    public String toString() {
        return String.format("(%d, %s)", key, color);
    }
}
