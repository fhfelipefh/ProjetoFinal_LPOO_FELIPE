package com.fhfelipefh.projetofinal_lpoo_felipe.view;

import javax.swing.*;

public class MainForm {
    private JTabbedPane tabbedPaneMain;

    public MainForm() {
        tabbedPaneMain = new JTabbedPane();

        ReservasForm reservasForm = new ReservasForm();
        tabbedPaneMain.addTab("Reservas", reservasForm);

        JPanel salasPanel = new SalasForm();
        tabbedPaneMain.addTab("Salas", salasPanel);

        JPanel usuariosPanel = new UsuariosForm();
        tabbedPaneMain.addTab("Usuários", usuariosPanel);
    }

    public JComponent getRootComponent() {
        return tabbedPaneMain;
    }
}
