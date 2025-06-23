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
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.fhfelipefh.projetofinal_lpoo_felipe.utils.Utils.createColoredButton;

public class ReservasForm extends JPanel {
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DefaultListModel<Reserva> reservasModel = new DefaultListModel<>();
    private final JList<Reserva> reservasList = new JList<>(reservasModel);
    private final CalendarPanel calendar = new CalendarPanel();
    private JPanel details, form;
    private final JTextField tfInicio = new JTextField();
    private final JTextField tfFim = new JTextField();
    private final DefaultListModel<Sala> salaPickModel = new DefaultListModel<>();
    private final DefaultListModel<Usuario> usuarioPickModel = new DefaultListModel<>();
    private final JList<Sala> listSala = new JList<>(salaPickModel);
    private final JList<Usuario> listUsuario = new JList<>(usuarioPickModel);
    private final JComboBox<ReservaStatus> cbStatus = new JComboBox<>(ReservaStatus.values());
    private JLabel lblConflict, lblSalaAtual, lblUsuarioAtual;
    private JButton btnSave, btnEdit, btnDelete, btnCancel;
    private final ReservaController rCtrl = new ReservaController();
    private final SalaController sCtrl = new SalaController();
    private final UsuarioController uCtrl = new UsuarioController();
    private Reserva current;
    private LocalDate selDate;

    public ReservasForm() {
        super(new BorderLayout());

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.35);
        add(split, BorderLayout.CENTER);

        reservasList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservasList.setCellRenderer((l, v, i, s, f) -> {
            String txt = "%s – %s | %s | %s".formatted(
                    v.getDataHoraInicio().toLocalTime(),
                    v.getDataHoraFim().toLocalTime(),
                    v.getSala().getNome(),
                    v.getStatus());
            JLabel lbl = new JLabel(txt);
            lbl.setOpaque(true);
            lbl.setBackground(s ? l.getSelectionBackground() : l.getBackground());
            lbl.setForeground(colorForStatus(v.getStatus()));
            return lbl;
        });
        split.setLeftComponent(new JScrollPane(reservasList));

        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        split.setRightComponent(right);

        right.add(calendar, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(5, 5));
        right.add(center, BorderLayout.CENTER);

        details = new JPanel(new FlowLayout(FlowLayout.LEFT));
        details.setBackground(Color.LIGHT_GRAY);
        details.setPreferredSize(new Dimension(0, 28));
        center.add(details, BorderLayout.NORTH);

        form = new JPanel(new GridBagLayout());
        center.add(form, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnSave = createColoredButton("Salvar", Color.GREEN, Color.WHITE);
        btnEdit = createColoredButton("Editar", Color.BLUE, Color.WHITE);
        btnDelete = createColoredButton("Excluir", Color.RED, Color.WHITE);
        btnCancel = createColoredButton("Cancelar", Color.LIGHT_GRAY, Color.BLACK);
        buttonsPanel.add(btnSave);
        buttonsPanel.add(btnEdit);
        buttonsPanel.add(btnDelete);
        buttonsPanel.add(btnCancel);
        right.add(buttonsPanel, BorderLayout.SOUTH);

        int fieldWidth = 520;
        tfInicio.setPreferredSize(new Dimension(fieldWidth, 28));
        tfFim.setPreferredSize(new Dimension(fieldWidth, 28));

        listSala.setVisibleRowCount(6);
        listUsuario.setVisibleRowCount(6);
        listSala.setPrototypeCellValue(new Sala() {{
            setNome("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        }});
        listUsuario.setPrototypeCellValue(new Usuario() {{
            setNome("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            setEmail("xxxxxxxxxxxxxxxxxxxx@xxxxxxxxx.xx");
        }});

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

        cbStatus.setRenderer((list, v, idx, sel, foc) -> {
            JLabel lbl = new JLabel(v.toString());
            lbl.setOpaque(true);
            lbl.setForeground(colorForStatus(v));
            lbl.setBackground(sel ? list.getSelectionBackground() : list.getBackground());
            return lbl;
        });

        calendar.addPropertyChangeListener("selectedDate", e -> {
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
            loadReservas();
        });

        btnEdit.addActionListener(e -> enableEdit());

        if (calendar.getSelectedDate() == null) calendar.setSelectedDate(LocalDate.now());
        selDate = calendar.getSelectedDate();
        loadReservas();
        showCreate();
    }

    public void loadReservas() {
        reservasModel.clear();
        LocalDateTime ini = selDate.atStartOfDay();
        LocalDateTime fim = selDate.atTime(LocalTime.MAX);
        rCtrl.findByPeriodo(ini, fim).forEach(reservasModel::addElement);
    }

    private void refreshSalaPick() {
        salaPickModel.clear();
        sCtrl.findAll().forEach(salaPickModel::addElement);
    }

    private void refreshUsuarioPick() {
        usuarioPickModel.clear();
        uCtrl.findAll().forEach(usuarioPickModel::addElement);
    }

    private void updateCurrentLabels(Sala s, Usuario u) {
        lblSalaAtual.setText("Sala atual: " + (s != null ? s.getNome() : "-"));
        lblUsuarioAtual.setText("Usuário atual: " + (u != null ? u.getNome() + " (" + u.getEmail() + ")" : "-"));
    }

    private void showCreate() {
        current = null;
        details.removeAll();
        details.add(new JLabel("Nova reserva - " + selDate));
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
        refreshUsuarioPick();

        JScrollPane spSala = new JScrollPane(listSala);
        spSala.setPreferredSize(new Dimension(520, 120));
        JScrollPane spUsu = new JScrollPane(listUsuario);
        spUsu.setPreferredSize(new Dimension(520, 120));

        c = gc(0, 2);
        form.add(new JLabel("Sala:"), c);
        c.gridx = 1;
        listSala.setEnabled(true);
        form.add(spSala, c);

        lblSalaAtual = new JLabel("Sala atual: -");
        c = gc(0, 3);
        c.gridwidth = 2;
        form.add(lblSalaAtual, c);

        c = gc(0, 4);
        form.add(new JLabel("Usuário:"), c);
        c.gridx = 1;
        listUsuario.setEnabled(true);
        form.add(spUsu, c);

        lblUsuarioAtual = new JLabel("Usuário atual: -");
        c = gc(0, 5);
        c.gridwidth = 2;
        form.add(lblUsuarioAtual, c);

        c = gc(0, 6);
        form.add(new JLabel("Status:"), c);
        c.gridx = 1;
        cbStatus.setEnabled(true);
        cbStatus.setSelectedItem(ReservaStatus.PENDENTE);
        form.add(cbStatus, c);

        lblConflict = new JLabel(" ");
        lblConflict.setForeground(Color.RED);
        c = gc(0, 7);
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        form.add(lblConflict, c);

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
        refreshUsuarioPick();
        listSala.setSelectedValue(current.getSala(), true);
        listSala.setEnabled(false);
        listUsuario.setSelectedValue(current.getUsuario(), true);
        listUsuario.setEnabled(false);

        JScrollPane spSala = new JScrollPane(listSala);
        spSala.setPreferredSize(new Dimension(520, 120));
        JScrollPane spUsu = new JScrollPane(listUsuario);
        spUsu.setPreferredSize(new Dimension(520, 120));

        c = gc(0, 2);
        form.add(new JLabel("Sala:"), c);
        c.gridx = 1;
        form.add(spSala, c);

        lblSalaAtual = new JLabel();
        c = gc(0, 3);
        c.gridwidth = 2;
        form.add(lblSalaAtual, c);

        c = gc(0, 4);
        form.add(new JLabel("Usuário:"), c);
        c.gridx = 1;
        form.add(spUsu, c);

        lblUsuarioAtual = new JLabel();
        c = gc(0, 5);
        c.gridwidth = 2;
        form.add(lblUsuarioAtual, c);

        c = gc(0, 6);
        form.add(new JLabel("Status:"), c);
        c.gridx = 1;
        cbStatus.setSelectedItem(current.getStatus());
        cbStatus.setEnabled(false);
        form.add(cbStatus, c);

        updateCurrentLabels(current.getSala(), current.getUsuario());

        lblConflict = new JLabel(" ");
        lblConflict.setForeground(Color.RED);
        c = gc(0, 7);
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        form.add(lblConflict, c);

        btnSave.setEnabled(false);
        btnEdit.setEnabled(true);
        btnDelete.setEnabled(true);
        btnCancel.setEnabled(true);

        revalidatePanels();
    }

    private void save() {
        lblConflict.setText(" ");
        try {
            LocalDateTime ini = LocalDateTime.parse(tfInicio.getText(), fmt);
            LocalDateTime fim = LocalDateTime.parse(tfFim.getText(), fmt);
            Sala sala = listSala.getSelectedValue();
            Usuario usr = listUsuario.getSelectedValue();
            ReservaStatus st = (ReservaStatus) cbStatus.getSelectedItem();

            if (sala == null && current != null) sala = current.getSala();
            if (usr == null && current != null) usr = current.getUsuario();

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
        } catch (IllegalStateException ex) {
            lblConflict.setText(ex.getMessage());
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
        c.fill = x == 1 ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
        c.weightx = x == 1 ? 1 : 0;
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
