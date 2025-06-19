package com.fhfelipefh.projetofinal_lpoo_felipe.view;

import javax.swing.*;

public class MainForm {
    private JTabbedPane tabbedPaneMain;
    private JSplitPane splitPanelVertical;

    public MainForm() {
        splitPanelVertical.setLeftComponent(null);
        splitPanelVertical.setRightComponent(null);
        splitPanelVertical.setResizeWeight(0.5);
        SwingUtilities.invokeLater(() ->
                splitPanelVertical.setDividerLocation(0.5)
        );
    }

    public JComponent getRootComponent() {
        return tabbedPaneMain;
    }

}
