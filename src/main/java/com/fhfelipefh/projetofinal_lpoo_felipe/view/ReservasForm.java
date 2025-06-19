package com.fhfelipefh.projetofinal_lpoo_felipe.view;

import com.fhfelipefh.projetofinal_lpoo_felipe.control.ReservaController;
import com.fhfelipefh.projetofinal_lpoo_felipe.control.SalaController;
import com.fhfelipefh.projetofinal_lpoo_felipe.control.UsuarioController;
import com.fhfelipefh.projetofinal_lpoo_felipe.model.Reserva;
import com.fhfelipefh.projetofinal_lpoo_felipe.model.ReservaStatus;
import com.fhfelipefh.projetofinal_lpoo_felipe.model.Sala;
import com.fhfelipefh.projetofinal_lpoo_felipe.model.Usuario;
import com.github.lgooddatepicker.components.CalendarPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.fhfelipefh.projetofinal_lpoo_felipe.utils.Utils.createColoredButton;

public class ReservasForm extends JPanel {
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private JSplitPane split;
    private DefaultListModel<Reserva> reservasModel;
    private JList<Reserva> reservasList;
    private CalendarPanel calendar;
    private JPanel details;
    private JPanel form;
    private JTextField tfInicio;
    private JTextField tfFim;
    private JTextField tfSalaFiltro;
    private JTextField tfUsuarioFiltro;
    private DefaultListModel<Sala> salaPickModel;
    private DefaultListModel<Usuario> usuarioPickModel;
    private JList<Sala> listSala;
    private JList<Usuario> listUsuario;
    private JComboBox<ReservaStatus> cbStatus;
    private JButton btnSave, btnEdit, btnDelete, btnCancel;
    private ReservaController rCtrl = new ReservaController();
    private SalaController sCtrl = new SalaController();
    private UsuarioController uCtrl = new UsuarioController();
    private Reserva current;
    private LocalDate selDate;

    public ReservasForm() {
        super(new BorderLayout());
        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.4);
        add(split, BorderLayout.CENTER);

        reservasModel = new DefaultListModel<>();
        reservasList = new JList<>(reservasModel);
        reservasList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservasList.setCellRenderer((l, v, i, s, f) -> {
            String t = "%s – %s | %s | %s".formatted(
                    v.getDataHoraInicio().toLocalTime(),
                    v.getDataHoraFim().toLocalTime(),
                    v.getSala().getNome(),
                    v.getStatus());
            JLabel lbl = new JLabel(t);
            lbl.setOpaque(true);
            lbl.setBackground(s ? l.getSelectionBackground() : l.getBackground());
            lbl.setForeground(colorForStatus(v.getStatus()));
            return lbl;
        });
        split.setLeftComponent(new JScrollPane(reservasList));

        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        split.setRightComponent(right);

        calendar = new CalendarPanel();
        right.add(calendar, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(5, 5));
        right.add(center, BorderLayout.CENTER);

        details = new JPanel(new FlowLayout(FlowLayout.LEFT));
        details.setBackground(Color.LIGHT_GRAY);
        details.setPreferredSize(new Dimension(0, 30));
        center.add(details, BorderLayout.NORTH);

        form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        center.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnSave = createColoredButton("Salvar", Color.GREEN, Color.WHITE);
        btnEdit = createColoredButton("Editar", Color.BLUE, Color.WHITE);
        btnDelete = createColoredButton("Excluir", Color.RED, Color.WHITE);
        btnCancel = createColoredButton("Cancelar", Color.LIGHT_GRAY, Color.BLACK);
        buttons.add(btnSave);
        buttons.add(btnEdit);
        buttons.add(btnDelete);
        buttons.add(btnCancel);
        right.add(buttons, BorderLayout.SOUTH);

        tfInicio = new JTextField();
        tfInicio.setPreferredSize(new Dimension(180, 25));
        tfFim = new JTextField();
        tfFim.setPreferredSize(new Dimension(180, 25));

        tfSalaFiltro = new JTextField();
        tfSalaFiltro.setPreferredSize(new Dimension(180, 25));
        tfUsuarioFiltro = new JTextField();
        tfUsuarioFiltro.setPreferredSize(new Dimension(180, 25));

        salaPickModel = new DefaultListModel<>();
        usuarioPickModel = new DefaultListModel<>();
        listSala = new JList<>(salaPickModel);
        listUsuario = new JList<>(usuarioPickModel);
        listSala.setVisibleRowCount(4);
        listUsuario.setVisibleRowCount(4);

        listSala.setCellRenderer((l, v, i, s, f) -> {
            JLabel lbl = new JLabel(v.getNome());
            lbl.setOpaque(true);
            lbl.setBackground(s ? l.getSelectionBackground() : l.getBackground());
            lbl.setForeground(s ? l.getSelectionForeground() : l.getForeground());
            return lbl;
        });

        listUsuario.setCellRenderer((l, v, i, s, f) -> {
            JLabel lbl = new JLabel(v.getNome() + " (" + v.getEmail() + ")");
            lbl.setOpaque(true);
            lbl.setBackground(s ? l.getSelectionBackground() : l.getBackground());
            lbl.setForeground(s ? l.getSelectionForeground() : l.getForeground());
            return lbl;
        });

        cbStatus = new JComboBox<>(ReservaStatus.values());
        cbStatus.setRenderer((list, value, index, isSel, cellHasFocus) -> {
            JLabel lbl = new JLabel(value.toString());
            lbl.setOpaque(true);
            lbl.setBackground(isSel ? list.getSelectionBackground() : list.getBackground());
            lbl.setForeground(colorForStatus(value));
            return lbl;
        });

        calendar.addPropertyChangeListener("date", e -> {
            selDate = calendar.getSelectedDate();
            reservasList.clearSelection();
            loadReservas();
            showCreate();
        });

        reservasList.addListSelectionListener((ListSelectionListener) e -> {
            if (!e.getValueIsAdjusting()) {
                current = reservasList.getSelectedValue();
                if (current == null) showCreate();
                else showDetail();
            }
        });

        btnSave.addActionListener(e -> save());
        btnDelete.addActionListener(e -> delete());
        btnCancel.addActionListener(e -> {
            reservasList.clearSelection();
            showCreate();
        });
        btnEdit.addActionListener(e -> enableEdit());

        tfSalaFiltro.getDocument().addDocumentListener(searchListener(this::applySalaFilter));
        tfUsuarioFiltro.getDocument().addDocumentListener(searchListener(this::applyUsuarioFilter));

        if (calendar.getSelectedDate() == null) calendar.setSelectedDate(LocalDate.now());
        selDate = calendar.getSelectedDate();
        loadReservas();
        showCreate();
        SwingUtilities.invokeLater(() -> split.setDividerLocation(0.4));
    }

    private DocumentListener searchListener(Runnable r) {
        return new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { r.run(); }
            public void removeUpdate(DocumentEvent e) { r.run(); }
            public void changedUpdate(DocumentEvent e) { r.run(); }
        };
    }

    private void loadReservas() {
        reservasModel.clear();
        LocalDateTime ini = selDate.atStartOfDay();
        LocalDateTime fim = selDate.atTime(LocalTime.MAX);
        rCtrl.findByPeriodo(ini, fim).forEach(reservasModel::addElement);
    }

    private void refreshSalaPick() {
        salaPickModel.clear();
        List<Sala> todas = sCtrl.findAll();
        LocalDateTime ini = selDate.atStartOfDay();
        LocalDateTime fim = selDate.atTime(LocalTime.MAX);
        todas.stream()
                .filter(s -> rCtrl.isDisponivel(s.getId(), ini, fim))
                .forEach(salaPickModel::addElement);
    }

    private void applySalaFilter() {
        String q = tfSalaFiltro.getText().toLowerCase();
        refreshSalaPick();
        for (int i = salaPickModel.getSize() - 1; i >= 0; i--) {
            if (!salaPickModel.getElementAt(i).getNome().toLowerCase().contains(q)) salaPickModel.remove(i);
        }
    }

    private void applyUsuarioFilter() {
        String q = tfUsuarioFiltro.getText().toLowerCase();
        usuarioPickModel.clear();
        uCtrl.findAll().forEach(u -> {
            if ((u.getNome() + u.getEmail()).toLowerCase().contains(q)) usuarioPickModel.addElement(u);
        });
    }

    private void showCreate() {
        current = null;
        details.removeAll();
        details.add(new JLabel("Nova reserva " + selDate));
        form.removeAll();
        GridBagConstraints c = gc(0, 0);
        form.add(new JLabel("Início:"), c);
        c.gridx = 1;
        tfInicio.setText(selDate.atTime(9, 0).format(fmt));
        tfInicio.setEditable(true);
        form.add(tfInicio, c);

        c = gc(0, 1);
        form.add(new JLabel("Fim:"), c);
        c.gridx = 1;
        tfFim.setText(selDate.atTime(10, 0).format(fmt));
        tfFim.setEditable(true);
        form.add(tfFim, c);

        refreshSalaPick();
        applyUsuarioFilter();

        c = gc(0, 2);
        form.add(new JLabel("Filtro Sala:"), c);
        c.gridx = 1;
        form.add(tfSalaFiltro, c);
        c = gc(0, 3);
        form.add(new JLabel("Salas:"), c);
        c.gridx = 1;
        listSala.setEnabled(true);
        form.add(new JScrollPane(listSala), c);

        c = gc(0, 4);
        form.add(new JLabel("Filtro Usuário:"), c);
        c.gridx = 1;
        form.add(tfUsuarioFiltro, c);
        c = gc(0, 5);
        form.add(new JLabel("Usuários:"), c);
        c.gridx = 1;
        listUsuario.setEnabled(true);
        form.add(new JScrollPane(listUsuario), c);

        c = gc(0, 6);
        form.add(new JLabel("Status:"), c);
        c.gridx = 1;
        cbStatus.setEnabled(true);
        cbStatus.setSelectedItem(ReservaStatus.PENDENTE);
        form.add(cbStatus, c);

        btnSave.setEnabled(true);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        btnCancel.setEnabled(false);

        revalidatePanels();
    }

    private void showDetail() {
        details.removeAll();
        details.add(new JLabel("Detalhes da reserva"));
        form.removeAll();
        GridBagConstraints c = gc(0, 0);
        form.add(new JLabel("Início:"), c);
        c.gridx = 1;
        tfInicio.setText(current.getDataHoraInicio().format(fmt));
        tfInicio.setEditable(false);
        form.add(tfInicio, c);

        c = gc(0, 1);
        form.add(new JLabel("Fim:"), c);
        c.gridx = 1;
        tfFim.setText(current.getDataHoraFim().format(fmt));
        tfFim.setEditable(false);
        form.add(tfFim, c);

        refreshSalaPick();
        applyUsuarioFilter();
        listSala.setSelectedValue(current.getSala(), true);
        listSala.setEnabled(false);
        listUsuario.setSelectedValue(current.getUsuario(), true);
        listUsuario.setEnabled(false);

        c = gc(0, 2);
        form.add(new JLabel("Sala:"), c);
        c.gridx = 1;
        form.add(new JScrollPane(listSala), c);

        c = gc(0, 3);
        form.add(new JLabel("Usuário:"), c);
        c.gridx = 1;
        form.add(new JScrollPane(listUsuario), c);

        c = gc(0, 4);
        form.add(new JLabel("Status:"), c);
        c.gridx = 1;
        cbStatus.setSelectedItem(current.getStatus());
        cbStatus.setEnabled(false);
        form.add(cbStatus, c);

        btnSave.setEnabled(false);
        btnEdit.setEnabled(true);
        btnDelete.setEnabled(true);
        btnCancel.setEnabled(true);

        revalidatePanels();
    }

    private void save() {
        try {
            LocalDateTime ini = LocalDateTime.parse(tfInicio.getText(), fmt);
            LocalDateTime fim = LocalDateTime.parse(tfFim.getText(), fmt);
            Sala sala = listSala.getSelectedValue();
            Usuario usr = listUsuario.getSelectedValue();
            ReservaStatus st = (ReservaStatus) cbStatus.getSelectedItem();
            if (sala == null || usr == null) {
                JOptionPane.showMessageDialog(this, "Selecione sala e usuário", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (current == null) {
                rCtrl.create(ini, fim, sala, usr, st);
            } else {
                rCtrl.update(current.getId(), ini, fim, sala, usr, st);
            }
            loadReservas();
            showCreate();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void delete() {
        if (current != null) {
            int ok = JOptionPane.showConfirmDialog(this, "Excluir reserva?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                rCtrl.delete(current.getId());
                loadReservas();
                showCreate();
            }
        }
    }

    private void enableEdit() {
        tfInicio.setEditable(true);
        tfFim.setEditable(true);
        listSala.setEnabled(true);
        listUsuario.setEnabled(true);
        cbStatus.setEnabled(true);
        btnSave.setEnabled(true);
        btnEdit.setEnabled(false);
    }

    private GridBagConstraints gc(int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = x == 0 ? GridBagConstraints.EAST : GridBagConstraints.WEST;
        return c;
    }

    private Color colorForStatus(ReservaStatus st) {
        return switch (st) {
            case APROVADA -> new Color(0, 128, 0);
            case PENDENTE -> new Color(255, 153, 0);
            case CANCELADA -> Color.RED;
        };
    }

    private void revalidatePanels() {
        details.revalidate();
        form.revalidate();
        form.repaint();
    }
}
