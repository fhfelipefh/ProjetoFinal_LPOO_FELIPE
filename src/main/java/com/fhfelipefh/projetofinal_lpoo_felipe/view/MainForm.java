package com.fhfelipefh.projetofinal_lpoo_felipe.view;

import javax.swing.*;

public class MainForm {
    private JTabbedPane tabbedPaneMain;
    private JSplitPane splitPanelVertical;
    private JSplitPane splitPaneRightVertical;

    public MainForm() {
        splitPanelVertical.setResizeWeight(0.5);

        splitPaneRightVertical.setResizeWeight(0.5);

        SwingUtilities.invokeLater(() -> {
            splitPanelVertical.setDividerLocation(0.5);
            splitPaneRightVertical.setDividerLocation(0.5);
        });
    }

    public JComponent getRootComponent() {
        return tabbedPaneMain;
    }
}
