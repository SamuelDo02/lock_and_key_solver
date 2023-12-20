package samdo.info.gui.puzzlesolverview.nodegroup;

import samdo.info.gui.puzzlesolverview.node.NodeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NodeGroupView extends JLayeredPane {
    public NodeGroupView() {
        // Mouse listeners
        addMouseListeners();

        // Key bindings
        addHotkeyBindings();
        addCopyPasteKeyBindings();
    }

    // --- LISTENERS ---
    private final List<Listener> listeners = new ArrayList<>();

    protected interface Listener {
        void addedNodeView(final NodeView nodeView);

        void clearedClipboardNodeView();
        void copiedNodeView(final NodeView nodeViewOriginal);
        void pastedNodeView(final NodeView nodeViewCopy);
    }

    protected void addListener(final Listener listener) {
        listeners.add(listener);
    }

    // --- NODES ---
    private NodeView selectedNodeView = null;

    protected void addNodeView(final NodeView nodeView) {
        add(nodeView, JLayeredPane.DEFAULT_LAYER);
        selectNodeView(nodeView);
    }

    protected void removeNodeView(final NodeView nodeView) {
        if (nodeView == selectedNodeView) {
            selectedNodeView = null;
        }

        remove(nodeView);
        nodeView.setSelected(false);
    }

    protected void unselectNodeView() {
        if (selectedNodeView != null) {
            selectedNodeView.setSelected(false);
            selectedNodeView = null;
        }
    }

    private void selectNodeView(final NodeView nodeView) {
        if (selectedNodeView != null) {
            selectedNodeView.setSelected(false);
        }

        selectedNodeView = nodeView;
        selectedNodeView.setSelected(true);
        moveToFront(nodeView);
    }

    private Optional<NodeView> selectTopNodeViewAtPoint(final Point point) {
        Component[] components = getComponentsInLayer(JLayeredPane.DEFAULT_LAYER);
        List<Component> positionSortedComponents = Arrays.stream(components).sorted(
                (Component comp1, Component comp2) ->
                        Integer.compare(getPosition(comp1), getPosition(comp2))
        ).collect(Collectors.toList());

        for (final Component component: positionSortedComponents) {
            if (component instanceof NodeView) {
                final NodeView nodeView = (NodeView) component;
                final Point relativePoint = SwingUtilities.convertPoint(this, point, nodeView);
                if (nodeView.contains(relativePoint)) {
                    selectNodeView(nodeView);
                    return Optional.of(nodeView);
                }
            }
        }

        return Optional.empty();
    }

    // --- MODES ---
    private CursorMode cursorMode = new EditMode();

    private interface CursorMode {
        void mousePressed(final MouseEvent e);
        void mouseReleased(final MouseEvent e);
        void mouseDragged(final MouseEvent e);
    }

    private class EditMode implements CursorMode {
        private Point cursorPressStartPoint = null;
        private NodeView cursorPressNodeView = null;
        private Point cursorPressNodeViewStartPoint = null;

        @Override
        public void mousePressed(final MouseEvent e) {
            selectTopNodeViewAtPoint(e.getPoint()).ifPresentOrElse(nodeView -> {
                cursorPressStartPoint = e.getPoint();
                cursorPressNodeView = nodeView;
                cursorPressNodeViewStartPoint = nodeView.getLocation();
            }, NodeGroupView.this::unselectNodeView);
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            cursorPressStartPoint = null;
            cursorPressNodeView = null;
            cursorPressNodeViewStartPoint = null;
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            if (cursorPressStartPoint != null
                    && cursorPressNodeView != null
                    && cursorPressNodeViewStartPoint != null) {
                final int dx = e.getX() - cursorPressStartPoint.x;
                final int dy = e.getY() - cursorPressStartPoint.y;

                cursorPressNodeView.setLocation(cursorPressNodeViewStartPoint.x + dx,
                        cursorPressNodeViewStartPoint.y + dy);
                repaint();
            }
        }
    }

    private class CreateMode implements CursorMode {
        private static final int MIN_NODE_SIZE = 10;

        private NodeView tentativeNodeView = null;
        private Point selectionStart = null;
        private Point selectionEnd = null;

        @Override
        public void mousePressed(final MouseEvent e) {
            selectionStart = e.getPoint();
            selectionEnd = e.getPoint();
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            if (tentativeNodeView != null) {
                // Remove tentative selection
                removeNodeView(tentativeNodeView);

                // Add NodeView permanently if dimensions are sufficient
                if (tentativeNodeView.getWidth() >= MIN_NODE_SIZE
                        && tentativeNodeView.getHeight() >= MIN_NODE_SIZE) {
                    addNodeView(tentativeNodeView);

                    // Notify of added NodeView
                    for (final Listener listener : listeners) {
                        listener.addedNodeView(tentativeNodeView);
                    }
                }

                revalidate();
                repaint();

                // Reset back to edit mode
                cursorMode = new EditMode();
            }

            // Reset selection
            tentativeNodeView = null;
            selectionStart = null;
            selectionEnd = null;
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            // Remove previous tentative selection
            if (tentativeNodeView != null) {
                removeNodeView(tentativeNodeView);
            }

            // Update tentative selection
            if (selectionStart != null) {
                selectionEnd = e.getPoint();

                tentativeNodeView = new NodeView(selectionStart, selectionEnd);
                addNodeView(tentativeNodeView);

                revalidate();
                repaint();
            }
        }
    }

    // --- MOUSE LISTENERS ---
    private boolean mousePressed = false;

    private void addMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                cursorMode.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
                cursorMode.mouseReleased(e);
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                cursorMode.mouseDragged(e);
            }
        });
    }

    // --- KEY BINDINGS ---
    private class KeyActionDecorator extends AbstractAction {
        private final Action originalAction;

        private KeyActionDecorator(final Action originalAction) {
            this.originalAction = originalAction;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!mousePressed) {
                originalAction.actionPerformed(e);
            }
        }
    }

    private void addHotkeyBindings() {
        final InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        final ActionMap actionMap = getActionMap();

        // 1 - Select mode
        final String editModeKey = "edit-mode";
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_1, 0), editModeKey);
        actionMap.put(editModeKey, new KeyActionDecorator(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cursorMode = new EditMode();
            }
        }));

        // 2 - Create mode
        final String createModeKey = "create-mode";
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0), createModeKey);
        actionMap.put(createModeKey, new KeyActionDecorator(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cursorMode = new CreateMode();
            }
        }));
    }

    private static final int PASTE_OFFSET = 5;
    private NodeView clipboardNodeView = null;
    private void addCopyPasteKeyBindings() {
        final InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        final ActionMap actionMap = getActionMap();

        final int modifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        // Copy
        final String copyKey = "copy";
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, modifier), copyKey);
        actionMap.put(copyKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // During NodeView creation
                if (!(cursorMode instanceof EditMode) && mousePressed) return;

                // Nothing selected
                if (selectedNodeView == null) {
                    clipboardNodeView = null;
                    for (final Listener listener : listeners) {
                        listener.clearedClipboardNodeView();
                    }
                    return;
                }

                clipboardNodeView = selectedNodeView.getCopy();
                for (final Listener listener : listeners) {
                    listener.copiedNodeView(selectedNodeView);
                }
            }
        });

        // Paste
        final String pasteKey = "paste";
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, modifier), pasteKey);
        actionMap.put(pasteKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // During NodeView creation
                if (!(cursorMode instanceof EditMode) && mousePressed) return;

                // Nothing copied
                if (clipboardNodeView == null) return;

                // Copy again to avoid interfering with future pastes
                final NodeView nodeViewCopy = clipboardNodeView.getCopy();

                // Set at selected location if available
                if (selectedNodeView != null) {
                    nodeViewCopy.setLocation(selectedNodeView.getLocation());
                }

                // Offset to avoid overlap
                nodeViewCopy.setLocation(nodeViewCopy.getLocation().x + PASTE_OFFSET,
                        nodeViewCopy.getLocation().y + PASTE_OFFSET);

                // Add copy
                addNodeView(nodeViewCopy);
                for (final Listener listener : listeners) {
                    listener.pastedNodeView(nodeViewCopy);
                }

                revalidate();
                repaint();
            }
        });
    }
}
