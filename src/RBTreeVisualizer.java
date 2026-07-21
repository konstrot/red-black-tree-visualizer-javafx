import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Zeichnet eine Momentaufnahme des Rot-Schwarz-Baums einschließlich NIL-Blättern
 * und Schwarzhöhe jedes Wurzel-NIL-Pfads.
 */
public class RBTreeVisualizer {
    private static final double NODE_RADIUS = 22;
    private static final double TOP_MARGIN = 65;
    private static final double SIDE_MARGIN = 45;
    private static final double BOTTOM_MARGIN = 55;

    private double nextLeafX;
    private double leafSpacing;
    private double verticalGap;

    private static final class VisualNode {
        private final TreeStep.Node node;
        private final boolean nil;
        private final int depth;
        private final int pathBlackCount;
        private VisualNode left;
        private VisualNode right;
        private double x;
        private double y;

        private VisualNode(
                TreeStep.Node node,
                boolean nil,
                int depth,
                int pathBlackCount) {
            this.node = node;
            this.nil = nil;
            this.depth = depth;
            this.pathBlackCount = pathBlackCount;
        }
    }

    public void draw(Canvas canvas, TreeStep step) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawLegend(gc, canvas.getWidth());

        if (step.getRoot() == null) {
            gc.setFill(Color.DIMGRAY);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 26));
            drawCenteredText(gc, "Der Baum ist leer.", canvas.getWidth() / 2, 180);
            return;
        }

        VisualNode visualRoot = buildVisualTree(step.getRoot(), 0, 0);
        int nilLeafCount = countNilLeaves(visualRoot);
        int maxDepth = findMaxDepth(visualRoot);

        double availableWidth = canvas.getWidth() - 2 * SIDE_MARGIN;
        leafSpacing = nilLeafCount > 1
                ? availableWidth / (nilLeafCount - 1)
                : availableWidth;
        nextLeafX = SIDE_MARGIN;

        double availableHeight = canvas.getHeight() - TOP_MARGIN - BOTTOM_MARGIN;
        verticalGap = Math.min(92, availableHeight / Math.max(1, maxDepth));

        assignCoordinates(visualRoot);
        drawConnections(gc, visualRoot);
        drawNodes(gc, visualRoot, step);
    }

    private VisualNode buildVisualTree(
            TreeStep.Node node,
            int depth,
            int blackCountBeforeNode) {

        if (node == null) {
            return new VisualNode(
                    null,
                    true,
                    depth,
                    blackCountBeforeNode + 1); // NIL ist schwarz.
        }

        int blackCount = blackCountBeforeNode
                + ("BLACK".equals(node.getColor()) ? 1 : 0);

        VisualNode visualNode = new VisualNode(
                node,
                false,
                depth,
                blackCount);

        visualNode.left = buildVisualTree(node.getLeft(), depth + 1, blackCount);
        visualNode.right = buildVisualTree(node.getRight(), depth + 1, blackCount);
        return visualNode;
    }

    private int countNilLeaves(VisualNode node) {
        if (node.nil) {
            return 1;
        }
        return countNilLeaves(node.left) + countNilLeaves(node.right);
    }

    private int findMaxDepth(VisualNode node) {
        if (node.nil) {
            return node.depth;
        }
        return Math.max(findMaxDepth(node.left), findMaxDepth(node.right));
    }

    private void assignCoordinates(VisualNode node) {
        node.y = TOP_MARGIN + node.depth * verticalGap;

        if (node.nil) {
            node.x = nextLeafX;
            nextLeafX += leafSpacing;
            return;
        }

        assignCoordinates(node.left);
        assignCoordinates(node.right);
        node.x = (node.left.x + node.right.x) / 2.0;
    }

    private void drawConnections(GraphicsContext gc, VisualNode node) {
        if (node.nil) {
            return;
        }

        gc.setStroke(Color.GRAY);
        gc.setLineWidth(2);

        gc.strokeLine(node.x, node.y, node.left.x, node.left.y);
        gc.strokeLine(node.x, node.y, node.right.x, node.right.y);

        drawConnections(gc, node.left);
        drawConnections(gc, node.right);
    }

    private void drawNodes(GraphicsContext gc, VisualNode node, TreeStep step) {
        if (node.nil) {
            drawNilLeaf(gc, node, step.hasEqualBlackHeight());
            return;
        }

        boolean highlighted = step.getHighlightedKeys().contains(node.node.getKey());

        if (highlighted) {
            gc.setStroke(Color.GOLDENROD);
            gc.setLineWidth(6);
            gc.strokeOval(
                    node.x - NODE_RADIUS - 5,
                    node.y - NODE_RADIUS - 5,
                    2 * NODE_RADIUS + 10,
                    2 * NODE_RADIUS + 10);
        }

        gc.setFill("RED".equals(node.node.getColor()) ? Color.CRIMSON : Color.BLACK);
        gc.fillOval(
                node.x - NODE_RADIUS,
                node.y - NODE_RADIUS,
                2 * NODE_RADIUS,
                2 * NODE_RADIUS);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1.5);
        gc.strokeOval(
                node.x - NODE_RADIUS,
                node.y - NODE_RADIUS,
                2 * NODE_RADIUS,
                2 * NODE_RADIUS);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        drawCenteredText(gc, String.valueOf(node.node.getKey()), node.x, node.y + 5);

        drawNodes(gc, node.left, step);
        drawNodes(gc, node.right, step);
    }

    private void drawNilLeaf(
            GraphicsContext gc,
            VisualNode node,
            boolean equalBlackHeight) {

        double width = 32;
        double height = 20;

        gc.setFill(Color.BLACK);
        gc.fillRoundRect(
                node.x - width / 2,
                node.y - height / 2,
                width,
                height,
                5,
                5);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        drawCenteredText(gc, "NIL", node.x, node.y + 3);

        gc.setFill(equalBlackHeight ? Color.DARKGREEN : Color.FIREBRICK);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        drawCenteredText(
                gc,
                "BH=" + node.pathBlackCount,
                node.x,
                node.y + 24);
    }

    private void drawLegend(GraphicsContext gc, double canvasWidth) {
        double x = canvasWidth - 430;
        double y = 24;

        gc.setFill(Color.CRIMSON);
        gc.fillOval(x, y - 10, 18, 18);
        gc.setFill(Color.DIMGRAY);
        gc.setFont(Font.font("Arial", 12));
        gc.fillText("ROT", x + 25, y + 4);

        gc.setFill(Color.BLACK);
        gc.fillOval(x + 85, y - 10, 18, 18);
        gc.setFill(Color.DIMGRAY);
        gc.fillText("SCHWARZ", x + 110, y + 4);

        gc.setStroke(Color.GOLDENROD);
        gc.setLineWidth(4);
        gc.strokeOval(x + 205, y - 12, 22, 22);
        gc.setFill(Color.DIMGRAY);
        gc.fillText("am aktuellen Schritt beteiligt", x + 235, y + 4);
    }

    private void drawCenteredText(
            GraphicsContext gc,
            String text,
            double centerX,
            double baselineY) {

        Text helper = new Text(text);
        helper.setFont(gc.getFont());
        double textWidth = helper.getLayoutBounds().getWidth();
        gc.fillText(text, centerX - textWidth / 2.0, baselineY);
    }
}
