package com.fhfelipefh.projetofinal_lpoo_felipe.view;

import com.fhfelipefh.projetofinal_lpoo_felipe.control.ReservaController;
import com.fhfelipefh.projetofinal_lpoo_felipe.control.SalaController;
import com.fhfelipefh.projetofinal_lpoo_felipe.control.UsuarioController;
import com.fhfelipefh.projetofinal_lpoo_felipe.model.Reserva;
import com.fhfelipefh.projetofinal_lpoo_felipe.model.Sala;
import com.fhfelipefh.projetofinal_lpoo_felipe.model.Usuario;
import com.github.lgooddatepicker.components.CalendarPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.fhfelipefh.projetofinal_lpoo_felipe.utils.Utils.createColoredButton;

public class ReservasForm extends JPanel {
    private JSplitPane splitPanelVertical;
    private DefaultListModel<Reserva> reservasModel;
    private JList<Reserva> reservasList;

    private JPanel rightPanel;
    private JPanel detailsPanel;
    private JPanel formPanel;
    private JPanel buttonsPanel;

    private CalendarPanel calendarPanel;
    private JTextField tfInicio;
    private JTextField tfFim;
    private JComboBox<Sala> cbSala;
    private JComboBox<Usuario> cbUsuario;

    private JButton btnSave;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnCancel;

    private ReservaController reservaController = new ReservaController();
    private SalaController salaController = new SalaController();
    private UsuarioController usuarioController = new UsuarioController();

    private Reserva currentReserva;
    private LocalDate selectedDate;

    public ReservasForm() {
        super(new BorderLayout());

        splitPanelVertical = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPanelVertical.setResizeWeight(0.4);
        add(splitPanelVertical, BorderLayout.CENTER);

        reservasModel = new DefaultListModel<>();
        reservasList = new JList<>(reservasModel);
        reservasList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservasList.setCellRenderer((list, r, idx, sel, foc) -> {
            String txt = String.format("%s – %s | Sala: %s",
                    r.getDataHoraInicio().toLocalTime(),
                    r.getDataHoraFim().toLocalTime(),
                    r.getSala().getNome());
            JLabel lbl = new JLabel(txt);
            lbl.setOpaque(true);
            lbl.setBackground(sel ? list.getSelectionBackground() : list.getBackground());
            lbl.setForeground(sel ? list.getSelectionForeground() : list.getForeground());
            return lbl;
        });
        splitPanelVertical.setLeftComponent(new JScrollPane(reservasList));

        rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        splitPanelVertical.setRightComponent(rightPanel);

        calendarPanel = new CalendarPanel();
        rightPanel.add(calendarPanel, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(5, 5));
        rightPanel.add(center, BorderLayout.CENTER);

        detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        detailsPanel.setBackground(Color.LIGHT_GRAY);
        detailsPanel.setPreferredSize(new Dimension(0, 30));
        center.add(detailsPanel, BorderLayout.NORTH);

        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        center.add(formPanel, BorderLayout.CENTER);

        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnSave = createColoredButton("Salvar", Color.GREEN, Color.WHITE);
        btnEdit = createColoredButton("Editar", Color.BLUE, Color.WHITE);
        btnDelete = createColoredButton("Excluir", Color.RED, Color.WHITE);
        btnCancel = createColoredButton("Cancelar", Color.LIGHT_GRAY, Color.BLACK);
        buttonsPanel.add(btnCancel);
        buttonsPanel.add(btnSave);
        buttonsPanel.add(btnEdit);
        buttonsPanel.add(btnDelete);
        rightPanel.add(buttonsPanel, BorderLayout.SOUTH);

        tfInicio = new JTextField();
        tfInicio.setPreferredSize(new Dimension(200, 25));
        tfFim = new JTextField();
        tfFim.setPreferredSize(new Dimension(200, 25));
        cbSala = new JComboBox<>();
        cbUsuario = new JComboBox<>();

        calendarPanel.addPropertyChangeListener("date", evt -> {
            selectedDate = calendarPanel.getSelectedDate();
            reservasList.clearSelection();
            loadReservations();
            showCreateForm();
        });

        reservasList.addListSelectionListener((ListSelectionListener) e -> {
            if (!e.getValueIsAdjusting()) {
                currentReserva = reservasList.getSelectedValue();
                if (currentReserva != null) showDetailForm();
                else showCreateForm();
            }
        });

        btnSave.addActionListener(e -> saveOrUpdateReserva());
        btnDelete.addActionListener(e -> deleteReserva());
        btnCancel.addActionListener(e -> {
            reservasList.clearSelection();
            showCreateForm();
        });
        btnEdit.addActionListener(e -> enableEditing());

        if (calendarPanel.getSelectedDate() == null)
            calendarPanel.setSelectedDate(LocalDate.now());
        selectedDate = calendarPanel.getSelectedDate();
        loadReservations();
        showCreateForm();

        SwingUtilities.invokeLater(() -> splitPanelVertical.setDividerLocation(0.4));
    }

    private void loadReservations() {
        reservasModel.clear();
        LocalDateTime ini = selectedDate.atStartOfDay();
        LocalDateTime fim = selectedDate.atTime(LocalTime.MAX);
        reservaController.findByPeriodo(ini, fim)
                .forEach(reservasModel::addElement);
    }

    private void showCreateForm() {
        currentReserva = null;
        detailsPanel.removeAll();
        detailsPanel.add(new JLabel("Nova reserva em " + selectedDate));

        formPanel.removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Início (yyyy-MM-ddTHH:mm):"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        tfInicio.setText(selectedDate.atTime(9, 0).toString());
        formPanel.add(tfInicio, c);

        c.gridy = 1;
        c.gridx = 0;
        c.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Fim    (yyyy-MM-ddTHH:mm):"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        tfFim.setText(selectedDate.atTime(10, 0).toString());
        formPanel.add(tfFim, c);

        cbSala.removeAllItems();
        salaController.findAll().forEach(cbSala::addItem);
        c.gridy = 2;
        c.gridx = 0;
        c.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Sala:"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        formPanel.add(cbSala, c);

        cbUsuario.removeAllItems();
        usuarioController.findAll().forEach(cbUsuario::addItem);
        c.gridy = 3;
        c.gridx = 0;
        c.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Usuário:"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        formPanel.add(cbUsuario, c);

        btnSave.setEnabled(true);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        btnCancel.setEnabled(false);

        detailsPanel.revalidate();
        formPanel.revalidate();
    }

    private void showDetailForm() {
        detailsPanel.removeAll();
        detailsPanel.add(new JLabel("Detalhes da reserva"));

        formPanel.removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        c.gridy = 0;
        c.gridx = 0;
        c.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Início:"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        tfInicio.setText(currentReserva.getDataHoraInicio().toString());
        tfInicio.setEditable(false);
        formPanel.add(tfInicio, c);

        c.gridy = 1;
        c.gridx = 0;
        c.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Fim:"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        tfFim.setText(currentReserva.getDataHoraFim().toString());
        tfFim.setEditable(false);
        formPanel.add(tfFim, c);

        c.gridy = 2;
        c.gridx = 0;
        c.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Sala:"), c);
        cbSala.removeAllItems();
        salaController.findAll().forEach(cbSala::addItem);
        cbSala.setSelectedItem(currentReserva.getSala());
        cbSala.setEnabled(false);
        formPanel.add(cbSala, c);

        c.gridy = 3;
        c.gridx = 0;
        c.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Usuário:"), c);
        cbUsuario.removeAllItems();
        usuarioController.findAll().forEach(cbUsuario::addItem);
        cbUsuario.setSelectedItem(currentReserva.getUsuario());
        cbUsuario.setEnabled(false);
        formPanel.add(cbUsuario, c);

        btnSave.setEnabled(false);
        btnEdit.setEnabled(true);
        btnDelete.setEnabled(true);
        btnCancel.setEnabled(true);

        detailsPanel.revalidate();
        formPanel.revalidate();
    }

    private void saveOrUpdateReserva() {
        try {
            LocalDateTime ini = LocalDateTime.parse(tfInicio.getText());
            LocalDateTime fim = LocalDateTime.parse(tfFim.getText());
            Sala sala = (Sala) cbSala.getSelectedItem();
            Usuario usr = (Usuario) cbUsuario.getSelectedItem();

            if (currentReserva == null) {
                reservaController.create(ini, fim, sala, usr);
            } else {
                reservaController.update(
                        currentReserva.getId(),
                        ini, fim, sala, usr
                );
            }

            loadReservations();
            showCreateForm();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteReserva() {
        if (currentReserva != null) {
            int ok = JOptionPane.showConfirmDialog(this,
                    "Excluir reserva?", "Confirmação",
                    JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                reservaController.delete(currentReserva.getId());
                loadReservations();
                showCreateForm();
            }
        }
    }

    private void enableEditing() {
        tfInicio.setEditable(true);
        tfFim.setEditable(true);
        cbSala.setEnabled(true);
        cbUsuario.setEnabled(true);
        btnSave.setEnabled(true);
        btnEdit.setEnabled(false);
    }
}
