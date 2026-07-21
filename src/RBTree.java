/**
 * Rot-Schwarz-Baum mit Ereignissen für didaktische Einzelschritte.
 */
public class RBTree {

    @FunctionalInterface
    public interface StepListener {
        void onStep(String description, int... highlightedKeys);
    }

    private RBTreeNode root;
    private final RBTreeNode nil;
    private StepListener stepListener;

    public RBTree() {
        nil = new RBTreeNode(Integer.MIN_VALUE, "BLACK");
        nil.left = nil;
        nil.right = nil;
        root = nil;
    }

    public void setStepListener(StepListener stepListener) {
        this.stepListener = stepListener;
    }

    private void emitStep(String description, int... highlightedKeys) {
        if (stepListener != null) {
            stepListener.onStep(description, highlightedKeys);
        }
    }

    /** Fügt einen Schlüssel ein und dokumentiert jeden Reparaturschritt. */
    public void insert(int key) {
        RBTreeNode newNode = new RBTreeNode(key, "RED");
        newNode.left = nil;
        newNode.right = nil;

        RBTreeNode parent = null;
        RBTreeNode current = root;

        while (current != nil) {
            parent = current;
            current = key < current.key ? current.left : current.right;
        }

        newNode.parent = parent;

        if (parent == null) {
            root = newNode;
        } else if (key < parent.key) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        emitStep(
                String.format("Knoten %d wird ROT eingefügt.", key),
                key);

        if (newNode.parent == null) {
            newNode.color = "BLACK";
            emitStep(
                    String.format("Der Wurzelknoten %d wird SCHWARZ gefärbt.", key),
                    key);
            emitStep(
                    String.format("Einfügen von %d ist abgeschlossen.", key),
                    key);
            return;
        }

        fixInsert(newNode);

        if (!"BLACK".equals(root.color)) {
            root.color = "BLACK";
            emitStep(
                    String.format("Die Wurzel %d wird wieder SCHWARZ gefärbt.", root.key),
                    root.key);
        }

        emitStep(
                String.format("Einfügen von %d ist abgeschlossen.", key),
                key);
    }

    private void fixInsert(RBTreeNode node) {
        while (node.parent != null && "RED".equals(node.parent.color)) {
            RBTreeNode parent = node.parent;
            RBTreeNode grandparent = parent.parent;

            if (parent == grandparent.left) {
                RBTreeNode uncle = grandparent.right;

                if ("RED".equals(uncle.color)) {
                    int parentKey = parent.key;
                    int uncleKey = uncle.key;
                    int grandparentKey = grandparent.key;

                    parent.color = "BLACK";
                    uncle.color = "BLACK";
                    grandparent.color = "RED";

                    emitStep(
                            String.format(
                                    "Umfärbung: %d und %d werden SCHWARZ, %d wird ROT.",
                                    parentKey,
                                    uncleKey,
                                    grandparentKey),
                            parentKey,
                            uncleKey,
                            grandparentKey);

                    node = grandparent;
                } else {
                    if (node == parent.right) {
                        int pivotKey = parent.key;
                        int nodeKey = node.key;
                        node = parent;
                        rotateLeft(node);

                        emitStep(
                                String.format(
                                        "Dreiecksfall: Linksrotation um Knoten %d.",
                                        pivotKey),
                                pivotKey,
                                nodeKey);

                        parent = node.parent;
                        grandparent = parent.parent;
                    }

                    int parentKey = parent.key;
                    int grandparentKey = grandparent.key;

                    parent.color = "BLACK";
                    grandparent.color = "RED";

                    emitStep(
                            String.format(
                                    "Umfärbung vor der Rechtsrotation: %d wird SCHWARZ, %d wird ROT.",
                                    parentKey,
                                    grandparentKey),
                            parentKey,
                            grandparentKey);

                    rotateRight(grandparent);

                    emitStep(
                            String.format(
                                    "Rechtsrotation um Knoten %d stellt die Balance wieder her.",
                                    grandparentKey),
                            parentKey,
                            grandparentKey);
                }
            } else {
                RBTreeNode uncle = grandparent.left;

                if ("RED".equals(uncle.color)) {
                    int parentKey = parent.key;
                    int uncleKey = uncle.key;
                    int grandparentKey = grandparent.key;

                    parent.color = "BLACK";
                    uncle.color = "BLACK";
                    grandparent.color = "RED";

                    emitStep(
                            String.format(
                                    "Umfärbung: %d und %d werden SCHWARZ, %d wird ROT.",
                                    parentKey,
                                    uncleKey,
                                    grandparentKey),
                            parentKey,
                            uncleKey,
                            grandparentKey);

                    node = grandparent;
                } else {
                    if (node == parent.left) {
                        int pivotKey = parent.key;
                        int nodeKey = node.key;
                        node = parent;
                        rotateRight(node);

                        emitStep(
                                String.format(
                                        "Dreiecksfall: Rechtsrotation um Knoten %d.",
                                        pivotKey),
                                pivotKey,
                                nodeKey);

                        parent = node.parent;
                        grandparent = parent.parent;
                    }

                    int parentKey = parent.key;
                    int grandparentKey = grandparent.key;

                    parent.color = "BLACK";
                    grandparent.color = "RED";

                    emitStep(
                            String.format(
                                    "Umfärbung vor der Linksrotation: %d wird SCHWARZ, %d wird ROT.",
                                    parentKey,
                                    grandparentKey),
                            parentKey,
                            grandparentKey);

                    rotateLeft(grandparent);

                    emitStep(
                            String.format(
                                    "Linksrotation um Knoten %d stellt die Balance wieder her.",
                                    grandparentKey),
                            parentKey,
                            grandparentKey);
                }
            }
        }
    }

    private void rotateLeft(RBTreeNode node) {
        RBTreeNode rightChild = node.right;
        node.right = rightChild.left;

        if (rightChild.left != nil) {
            rightChild.left.parent = node;
        }

        rightChild.parent = node.parent;

        if (node.parent == null) {
            root = rightChild;
        } else if (node == node.parent.left) {
            node.parent.left = rightChild;
        } else {
            node.parent.right = rightChild;
        }

        rightChild.left = node;
        node.parent = rightChild;
    }

    private void rotateRight(RBTreeNode node) {
        RBTreeNode leftChild = node.left;
        node.left = leftChild.right;

        if (leftChild.right != nil) {
            leftChild.right.parent = node;
        }

        leftChild.parent = node.parent;

        if (node.parent == null) {
            root = leftChild;
        } else if (node == node.parent.right) {
            node.parent.right = leftChild;
        } else {
            node.parent.left = leftChild;
        }

        leftChild.right = node;
        node.parent = leftChild;
    }

    public RBTreeNode getRoot() {
        return root;
    }

    public RBTreeNode getNil() {
        return nil;
    }

    public boolean isEmpty() {
        return root == nil;
    }
}
