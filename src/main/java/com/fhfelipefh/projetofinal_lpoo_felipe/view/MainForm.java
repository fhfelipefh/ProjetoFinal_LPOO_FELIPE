package com.fhfelipefh.projetofinal_lpoo_felipe.view;

import javax.swing.*;
import java.awt.*;

public class MainForm {
    private JPanel root;
    private JTabbedPane tabbedPaneMain;

    public MainForm() {
        root = new JPanel(new BorderLayout());
        tabbedPaneMain = new JTabbedPane();

        tabbedPaneMain.addTab("Reservas", new ReservasForm());
        tabbedPaneMain.addTab("Salas", new SalasForm());
        tabbedPaneMain.addTab("Usuários", new UsuariosForm());

        JMenuBar bar = new JMenuBar();
        JMenu help = new JMenu("Ajuda");
        JMenuItem sobre = new JMenuItem("Sobre");
        sobre.addActionListener(e ->
                JOptionPane.showMessageDialog(
                        root,
                        """
                                Sistema de Reservas de Salas
                                
                                Projeto acadêmico da disciplina LPOO
                                que gerencia cadastros de salas, usuários
                                e reservas, controlando conflitos de horário
                                e status das solicitações.
                                """,
                        "Sobre", JOptionPane.INFORMATION_MESSAGE));
        help.add(sobre);
        bar.add(help);

        root.add(bar, BorderLayout.NORTH);
        root.add(tabbedPaneMain, BorderLayout.CENTER);
    }

    public JComponent getRootComponent() {
        return root;
    }
}
