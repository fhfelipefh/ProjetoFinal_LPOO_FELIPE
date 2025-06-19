package com.fhfelipefh.projetofinal_lpoo_felipe.view;

import com.fhfelipefh.projetofinal_lpoo_felipe.control.UsuarioController;
import com.fhfelipefh.projetofinal_lpoo_felipe.model.Usuario;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

import static com.fhfelipefh.projetofinal_lpoo_felipe.utils.Utils.createColoredButton;

public class UsuariosForm extends JPanel {
    private JSplitPane splitPanelVertical;
    private JTextField tfSearch;
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

        tfSearch = new JTextField();
        tfSearch.setPreferredSize(new Dimension(200, 25));
        tfSearch.setForeground(Color.GRAY);
        tfSearch.setText("Pesquisar por nome...");
        tfSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tfSearch.getText().equals("Pesquisar por nome...")) {
                    tfSearch.setText("");
                    tfSearch.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (tfSearch.getText().isEmpty()) {
                    tfSearch.setForeground(Color.GRAY);
                    tfSearch.setText("Pesquisar por nome...");
                    filterUsers("");
                }
            }
        });

        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onChange();
            }

            private void onChange() {
                String text = tfSearch.getText();
                if (text.equals("Pesquisar por nome...")) text = "";
                filterUsers(text.trim());
            }
        });

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

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(tfSearch, BorderLayout.NORTH);
        usuariosScrollPane = new JScrollPane(usuariosList);
        leftPanel.add(usuariosScrollPane, BorderLayout.CENTER);
        splitPanelVertical.setLeftComponent(leftPanel);

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

        btnSave = createColoredButton("Salvar", Color.GREEN, Color.WHITE);
        btnEdit = createColoredButton("Editar", Color.BLUE, Color.WHITE);
        btnDelete = createColoredButton("Excluir", Color.RED, Color.WHITE);
        btnCancel = createColoredButton("Cancelar", Color.LIGHT_GRAY, Color.BLACK);
        buttonsPanel.add(btnSave);
        buttonsPanel.add(btnEdit);
        buttonsPanel.add(btnDelete);
        buttonsPanel.add(btnCancel);

        usuariosList.addListSelectionListener((ListSelectionListener) e -> {
            if (!e.getValueIsAdjusting()) showUser(usuariosList.getSelectedValue());
        });
        btnSave.addActionListener(e -> saveUser());
        btnEdit.addActionListener(e -> enableEditing());
        btnDelete.addActionListener(e -> deleteUser());
        btnCancel.addActionListener(e -> {
            usuariosList.clearSelection();
            showUser(null);
        });

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
        List<Usuario> list = controller.findAll();
        usuariosModel.clear();
        list.forEach(usuariosModel::addElement);
        String filtro = tfSearch.getText();
        if (!filtro.equals("Pesquisar por nome...")) filterUsers(filtro.trim());
    }

    private void showUser(Usuario user) {
        currentUser = user;
        detailsPanel.removeAll();
        if (user == null) {
            detailsPanel.add(new JLabel("Novo Usuário"));
            clearFields();
            enableForm(true);
        } else {
            detailsPanel.add(new JLabel("Detalhes do Usuário"));
            tfNome.setText(user.getNome());
            tfNome.setEditable(false);
            tfEmail.setText(user.getEmail());
            tfEmail.setEditable(false);
            btnSave.setEnabled(false);
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
            btnCancel.setEnabled(true);
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
        btnDelete.setEnabled(false);
        btnCancel.setEnabled(true);
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

    private void enableForm(boolean editable) {
        tfNome.setEditable(editable);
        tfEmail.setEditable(editable);
        btnSave.setEnabled(editable);
        btnEdit.setEnabled(!editable);
        btnDelete.setEnabled(!editable);
        btnCancel.setEnabled(editable);
    }

    private void clearFields() {
        tfNome.setText("");
        tfEmail.setText("");
        tfNome.setEditable(true);
        tfEmail.setEditable(true);
        btnSave.setEnabled(true);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        btnCancel.setEnabled(true);
    }

    private void filterUsers(String query) {
        List<Usuario> all = controller.findAll();
        usuariosModel.clear();
        if (query.isEmpty()) {
            all.forEach(usuariosModel::addElement);
        } else {
            String q = query.toLowerCase();
            for (Usuario u : all) {
                if (u.getNome().toLowerCase().contains(q)) {
                    usuariosModel.addElement(u);
                }
            }
        }
        usuariosList.clearSelection();
    }
}