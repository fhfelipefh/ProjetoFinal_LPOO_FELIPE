package com.fhfelipefh.projetofinal_lpoo_felipe.view;

import com.fhfelipefh.projetofinal_lpoo_felipe.control.UsuarioController;
import com.fhfelipefh.projetofinal_lpoo_felipe.model.Usuario;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

public class UsuariosForm extends JPanel {
    private JSplitPane splitPanelVertical;
    private JScrollPane usuariosScrollPane;
    private DefaultListModel<Usuario> usuariosModel;
    private JList<Usuario> usuariosList;
    private JPanel rightPanel;
    private JPanel detailsPanel;
    private JPanel formPanel;
    private JPanel buttonsPanel;
    private JTextField tfNome;
    private JTextField tfEmail;
    private JButton btnSave;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnCancel;
    private final UsuarioController controller = new UsuarioController();
    private Usuario currentUser;

    public UsuariosForm() {
        super(new BorderLayout());
        splitPanelVertical = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPanelVertical.setResizeWeight(0.4);
        add(splitPanelVertical, BorderLayout.CENTER);

        usuariosModel = new DefaultListModel<>();
        usuariosList = new JList<>(usuariosModel);
        usuariosList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usuariosList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel(value.getNome() + " (" + value.getEmail() + ")");
            lbl.setOpaque(true);
            lbl.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            lbl.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return lbl;
        });
        usuariosScrollPane = new JScrollPane(usuariosList);
        splitPanelVertical.setLeftComponent(usuariosScrollPane);

        rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        splitPanelVertical.setRightComponent(rightPanel);

        detailsPanel = new JPanel();
        detailsPanel.setBackground(Color.LIGHT_GRAY);
        rightPanel.add(detailsPanel, BorderLayout.NORTH);

        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        rightPanel.add(formPanel, BorderLayout.CENTER);

        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(buttonsPanel, BorderLayout.SOUTH);

        tfNome = new JTextField();
        tfNome.setPreferredSize(new Dimension(200, 25));
        tfEmail = new JTextField();
        tfEmail.setPreferredSize(new Dimension(200, 25));
        placeField(formPanel, "Nome:", tfNome, 0);
        placeField(formPanel, "Email:", tfEmail, 1);

        btnSave = new JButton("Salvar");
        btnSave.setBackground(Color.GREEN);

        btnEdit = new JButton("Editar");
        btnEdit.setBackground(Color.BLUE);

        btnDelete = new JButton("Excluir");
        btnDelete.setBackground(Color.RED);

        btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(Color.LIGHT_GRAY);

        buttonsPanel.add(btnSave);
        buttonsPanel.add(btnEdit);
        buttonsPanel.add(btnDelete);

        usuariosList.addListSelectionListener((ListSelectionListener) e -> {
            if (!e.getValueIsAdjusting()) {
                Usuario sel = usuariosList.getSelectedValue();
                showUser(sel);
            }
        });

        btnSave.addActionListener(e -> saveUser());
        btnEdit.addActionListener(e -> enableEditing());
        btnDelete.addActionListener(e -> deleteUser());

        carregarUsuarios();
        showUser(null);

        SwingUtilities.invokeLater(() -> splitPanelVertical.setDividerLocation(0.4));
    }

    private void placeField(JPanel panel, String label, JTextField field, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(new JLabel(label), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        panel.add(field, c);
    }

    private void carregarUsuarios() {
        usuariosModel.clear();
        List<Usuario> list = controller.findAll();
        list.forEach(usuariosModel::addElement);
    }

    private void showUser(Usuario user) {
        currentUser = user;
        if (user == null) {
            detailsPanel.removeAll();
            detailsPanel.add(new JLabel("Novo Usuário"));
            tfNome.setText("");
            tfEmail.setText("");
            tfNome.setEditable(true);
            tfEmail.setEditable(true);
            btnSave.setEnabled(true);
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } else {
            detailsPanel.removeAll();
            detailsPanel.add(new JLabel("Detalhes do Usuário"));
            tfNome.setText(user.getNome());
            tfNome.setEditable(false);
            tfEmail.setText(user.getEmail());
            tfEmail.setEditable(false);
            btnSave.setEnabled(false);
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        }
        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    private void saveUser() {
        String nome = tfNome.getText().trim();
        String email = tfEmail.getText().trim();
        if (nome.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e email são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (currentUser == null) {
            controller.create(nome, email);
        } else {
            currentUser.setNome(nome);
            currentUser.setEmail(email);
            controller.update(currentUser);
        }
        carregarUsuarios();
        showUser(null);
    }

    private void enableEditing() {
        tfNome.setEditable(true);
        tfEmail.setEditable(true);
        btnSave.setEnabled(true);
        btnEdit.setEnabled(false);
    }

    private void deleteUser() {
        if (currentUser != null) {
            int ok = JOptionPane.showConfirmDialog(this, "Excluir usuário?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                controller.delete(currentUser.getId());
                carregarUsuarios();
                showUser(null);
            }
        }
    }
}
