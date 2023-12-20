package samdo.info.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * A class that displays and manages views within a window.
 */
public class AppWindow {
    private final JFrame window;

    private Map<String, JComponent> persistentViews;

    public AppWindow(final String appName,
                     final Dimension size) {
        // Prepare window
        window = new JFrame(appName);
        window.setLayout(new GridLayout(1, 1));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setFocusTraversalKeysEnabled(false); // Remove default key behaviors
        window.setSize(size);
        window.setVisible(true);
    }

    /**
     * Associates a persistent view with a tag.
     * @param persistentView The view.
     * @param persistentTag The associated tag.
     */
    public void registerPersistentView(final JComponent persistentView,
                                       final String persistentTag) {
        persistentViews.put(persistentTag, persistentView);
    }

    /**
     * Replaces currently displayed view with new view.
     * Does nothing if view is null.
     * @param view The new view.
     */
    public void replaceView(final JComponent view) {
        if (view == null) return;
        window.getContentPane().removeAll();
        window.getContentPane().add(view);
        window.getContentPane().revalidate();
        window.getContentPane().repaint();
    }

    /**
     * Replace currently displayed view with persistent view.
     * Does nothing if persistent view does not exist.
     * @param persistentTag Tag associated with persistent view.
     */
    public void replaceView(final String persistentTag) {
        if (persistentViews.containsKey(persistentTag)) {
            replaceView(persistentViews.get(persistentTag));
        }
    }
}
