package com.fhfelipefh.projetofinal_lpoo_felipe.view;

import javax.swing.*;

public class MainForm {
    private JTabbedPane tabbedPaneMain;

    public MainForm() {
        tabbedPaneMain = new JTabbedPane();

        ReservasForm reservasForm = new ReservasForm();
        tabbedPaneMain.addTab("Reservas", reservasForm);

        JPanel salasPanel = new JPanel();
        tabbedPaneMain.addTab("Salas", salasPanel);

        JPanel usuariosPanel = new JPanel();
        tabbedPaneMain.addTab("Usuários", usuariosPanel);
    }

    public JComponent getRootComponent() {
        return tabbedPaneMain;
    }
}
