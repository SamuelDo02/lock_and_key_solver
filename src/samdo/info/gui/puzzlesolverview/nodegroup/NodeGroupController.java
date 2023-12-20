package samdo.info.gui.puzzlesolverview.nodegroup;

import samdo.info.gui.puzzlesolverview.node.Node;
import samdo.info.gui.puzzlesolverview.node.NodeController;
import samdo.info.gui.puzzlesolverview.node.NodeView;

import java.util.HashMap;
import java.util.Map;

public class NodeGroupController implements NodeGroupView.Listener {
    private final NodeGroup nodeGroup;
    private final NodeGroupView nodeGroupView;

    private final Map<NodeView, Node> viewNodeMap = new HashMap<>();

    public NodeGroupController(final NodeGroup nodeGroup, final NodeGroupView nodeGroupView) {
        this.nodeGroup = nodeGroup;

        this.nodeGroupView = nodeGroupView;
        this.nodeGroupView.addListener(this);
    }

    @Override
    public void addedNodeView(final NodeView nodeView) {
        final Node node = new Node();

        new NodeController(node, nodeView);
        viewNodeMap.put(nodeView, node);

        nodeGroup.addNode(node);
    }

    @Override
    public void clearedClipboardNodeView() {
        nodeGroup.setClipboardNode(null);
    }

    @Override
    public void copiedNodeView(final NodeView nodeViewOriginal) {
        final Node nodeCopy = viewNodeMap.get(nodeViewOriginal).getCopy();
        nodeGroup.setClipboardNode(nodeCopy);
    }

    @Override
    public void pastedNodeView(final NodeView nodeViewCopy) {
        final Node nodeCopy = nodeGroup.getClipboardNode().getCopy();

        new NodeController(nodeCopy, nodeViewCopy);
        viewNodeMap.put(nodeViewCopy, nodeCopy);

        nodeGroup.addNode(nodeCopy);
    }
}
