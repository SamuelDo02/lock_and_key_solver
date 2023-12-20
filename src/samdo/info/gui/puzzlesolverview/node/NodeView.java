package samdo.info.gui.puzzlesolverview.node;

import javax.swing.*;
import java.awt.*;

public class NodeView extends JPanel {
    // Graphics constants
    private static final Color BACKGROUND_COLOR = Color.WHITE;

    private static final Color BORDER_COLOR = Color.BLACK;
    private static final int BORDER_WIDTH = 1;

    private static final Color SELECTION_BORDER_COLOR = Color.CYAN;
    private static final int SELECTION_BORDER_WIDTH = 2;

    /**
     * Construct a NodeView from a Node and two unnormalized selection points.
     * The NodeView's dimensions conform to a maximum square given the selection points.
     * The top-left corner of the NodeView is the minimum point of the selection points
     * across all dimensions after square normalization.
     * @param point1 First selection point
     * @param point2 Second selection point.
     */
    public NodeView(final Point point1, final Point point2) {
        // Conform to square area
        final int minSize = Math.max(Math.abs(point2.x - point1.x),
                Math.abs(point2.y - point1.y));
        final int xSign = point2.x - point1.x > 0 ? 1 : -1;
        final int ySign = point2.y - point1.y > 0 ? 1 : -1;
        final Point squarePoint2 = new Point(point1.x + xSign * minSize,
                point1.y + ySign * minSize);

        // Normalize start and end point
        final Point startPoint = new Point(Math.min(point1.x, squarePoint2.x),
                Math.min(point1.y, squarePoint2.y));
        final Point endPoint = new Point(Math.max(point1.x, squarePoint2.x),
                Math.max(point1.y, squarePoint2.y));

        // Calculate dimensions
        final int width = endPoint.x - startPoint.x;
        final int height = endPoint.y - startPoint.y;

        // Set position and dimension
        setSize(new Dimension(width, height));
        setLocation(startPoint);

        // Set visuals
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createLineBorder(BORDER_COLOR, BORDER_WIDTH));
    }

    /**
     * Graphically show whether the node view is selected .
     * @param selected True if node is selected, false otherwise.
     */
    public void setSelected(final boolean selected) {
        if (selected) {
            setBorder(BorderFactory.createLineBorder(SELECTION_BORDER_COLOR, SELECTION_BORDER_WIDTH));
        } else {
            setBorder(BorderFactory.createLineBorder(BORDER_COLOR, BORDER_WIDTH));
        }
    }

    public NodeView getCopy() {
        final Point startPoint = getLocation();
        final Point endPoint = new Point(startPoint.x + getWidth() - 1,
                startPoint.y + getHeight() - 1);
        return new NodeView(startPoint, endPoint);
    }
}
