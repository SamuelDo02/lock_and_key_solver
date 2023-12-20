package samdo.info.gui.puzzlesolverview.nodegroup;

import samdo.info.gui.puzzlesolverview.node.Node;

import java.util.ArrayList;
import java.util.List;

public class NodeGroup {
    private final List<Node> nodes = new ArrayList<>();

    private Node clipboardNode = null;

    protected void addNode(final Node node) {
        nodes.add(node);
    }

    protected void setClipboardNode(final Node node) {
        clipboardNode = node;
    }

    protected Node getClipboardNode() {
        return clipboardNode;
    }
}
