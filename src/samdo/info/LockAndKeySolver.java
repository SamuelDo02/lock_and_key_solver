package samdo.info;

import samdo.info.gui.AppWindow;
import samdo.info.gui.puzzlesolverview.nodegroup.NodeGroup;
import samdo.info.gui.puzzlesolverview.nodegroup.NodeGroupController;
import samdo.info.gui.puzzlesolverview.nodegroup.NodeGroupView;

import javax.swing.SwingUtilities;
import java.awt.*;

public class LockAndKeySolver {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppWindow window = new AppWindow("Lock And Key Solver", new Dimension(500, 500));

            NodeGroup nodeGroup = new NodeGroup();
            NodeGroupView nodeGroupView = new NodeGroupView();
            new NodeGroupController(nodeGroup, nodeGroupView);

            window.replaceView(nodeGroupView);
        });
    }
}
