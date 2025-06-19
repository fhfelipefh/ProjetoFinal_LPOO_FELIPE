// src/main/java/com/fhfelipefh/projetofinal_lpoo_felipe/view/SalasForm.java
package com.fhfelipefh.projetofinal_lpoo_felipe.view;

import com.fhfelipefh.projetofinal_lpoo_felipe.control.SalaController;
import com.fhfelipefh.projetofinal_lpoo_felipe.model.Sala;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

import static com.fhfelipefh.projetofinal_lpoo_felipe.utils.Utils.createColoredButton;
import static com.fhfelipefh.projetofinal_lpoo_felipe.utils.Utils.createCurrencyField;

public class SalasForm extends JPanel {
    private JSplitPane splitPanelVertical;
    private JScrollPane salasScrollPane;
    private DefaultListModel<Sala> salasModel;
    private JList<Sala> salasList;
    private JPanel rightPanel;
    private JPanel detailsPanel;
    private JPanel formPanel;
    private JPanel buttonsPanel;
    private JTextField tfNome;
    private JTextField tfCapacidade;
    private JTextField tfLocalizacao;
    private JFormattedTextField tfPrecoHora;
    private JButton btnSave;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnCancel;
    private final SalaController controller = new SalaController();
    private Sala currentSala;

    public SalasForm() {
        super(new BorderLayout());
        splitPanelVertical = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPanelVertical.setResizeWeight(0.4);
        add(splitPanelVertical, BorderLayout.CENTER);

        salasModel = new DefaultListModel<>();
        salasList = new JList<>(salasModel);
        salasList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        salasList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            String text = String.format("%s | %s | (Capacidade: %d)", value.getNome(), value.getLocalizacao(), value.getCapacidade());
            JLabel lbl = new JLabel(text);
            lbl.setOpaque(true);
            lbl.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            lbl.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            return lbl;
        });

        salasScrollPane = new JScrollPane(salasList);
        splitPanelVertical.setLeftComponent(salasScrollPane);

        rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        splitPanelVertical.setRightComponent(rightPanel);

        detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        detailsPanel.setBackground(Color.LIGHT_GRAY);
        detailsPanel.setPreferredSize(new Dimension(0, 30));
        rightPanel.add(detailsPanel, BorderLayout.NORTH);

        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        rightPanel.add(formPanel, BorderLayout.CENTER);

        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.add(buttonsPanel, BorderLayout.SOUTH);

        tfNome = createTextField();
        tfCapacidade = createTextField();
        tfLocalizacao = createTextField();
        tfPrecoHora = createCurrencyField();
        placeField(formPanel, "Nome:", tfNome, 0);
        placeField(formPanel, "Capacidade:", tfCapacidade, 1);
        placeField(formPanel, "Localização:", tfLocalizacao, 2);
        placeField(formPanel, "Preço Hora:", tfPrecoHora, 3);

        btnSave = createColoredButton("Salvar", Color.GREEN, Color.WHITE);
        btnEdit = createColoredButton("Editar", Color.BLUE, Color.WHITE);
        btnDelete = createColoredButton("Excluir", Color.RED, Color.WHITE);
        btnCancel = createColoredButton("Cancelar", Color.LIGHT_GRAY, Color.BLACK);

        buttonsPanel.add(btnSave);
        buttonsPanel.add(btnEdit);
        buttonsPanel.add(btnDelete);
        buttonsPanel.add(btnCancel);

        salasList.addListSelectionListener((ListSelectionListener) e -> {
            if (!e.getValueIsAdjusting()) showSala(salasList.getSelectedValue());
        });

        btnSave.addActionListener(e -> saveSala());
        btnEdit.addActionListener(e -> enableEditing());
        btnDelete.addActionListener(e -> deleteSala());
        btnCancel.addActionListener(e -> {
            salasList.clearSelection();
            showSala(null);
        });

        carregarSalas();
        showSala(null);
        SwingUtilities.invokeLater(() -> splitPanelVertical.setDividerLocation(0.4));
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(200, 25));
        return tf;
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

    private void carregarSalas() {
        salasModel.clear();
        List<Sala> list = controller.findAll();
        list.forEach(salasModel::addElement);
    }

    private void showSala(Sala sala) {
        currentSala = sala;
        detailsPanel.removeAll();
        formPanel.setVisible(true);
        btnSave.setEnabled(true);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        btnCancel.setEnabled(false);
        if (sala == null) {
            detailsPanel.add(new JLabel("Nova Sala"));
            clearFields();
            enableForm(true);
        } else {
            detailsPanel.add(new JLabel("Detalhes da Sala"));
            tfNome.setText(sala.getNome());
            tfNome.setEditable(false);
            tfCapacidade.setText(sala.getCapacidade().toString());
            tfCapacidade.setEditable(false);
            tfLocalizacao.setText(sala.getLocalizacao());
            tfLocalizacao.setEditable(false);
            tfPrecoHora.setValue(sala.getPrecoHora());
            tfPrecoHora.setEditable(false);
            btnSave.setEnabled(false);
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
            btnCancel.setEnabled(true);
        }
        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    private void saveSala() {
        String nome = tfNome.getText().trim();
        String capStr = tfCapacidade.getText().trim();
        String loc = tfLocalizacao.getText().trim();
        BigDecimal preco = (BigDecimal) tfPrecoHora.getValue();
        if (nome.isEmpty() || capStr.isEmpty() || loc.isEmpty() ||  preco == null) {
            JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Integer cap = Integer.valueOf(capStr);
            if (currentSala == null) controller.create(nome, cap, loc, preco);
            else {
                currentSala.setNome(nome);
                currentSala.setCapacidade(cap);
                currentSala.setLocalizacao(loc);
                currentSala.setPrecoHora(preco);
                controller.update(currentSala);
            }
            carregarSalas();
            showSala(null);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Capacidade e Preço devem ser numéricos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enableEditing() {
        enableForm(true);
        btnSave.setEnabled(true);
        btnEdit.setEnabled(false);
    }

    private void deleteSala() {
        if (currentSala != null) {
            int ok = JOptionPane.showConfirmDialog(this, "Excluir sala?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                controller.delete(currentSala.getId());
                carregarSalas();
                showSala(null);
            }
        }
    }

    private void enableForm(boolean editable) {
        tfNome.setEditable(editable);
        tfCapacidade.setEditable(editable);
        tfLocalizacao.setEditable(editable);
        tfPrecoHora.setEditable(editable);
        btnCancel.setEnabled(editable);
    }

    private void clearFields() {
        tfNome.setText("");
        tfCapacidade.setText("");
        tfLocalizacao.setText("");
        tfPrecoHora.setValue(null);
    }
}
