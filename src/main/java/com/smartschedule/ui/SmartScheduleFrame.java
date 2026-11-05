package com.smartschedule.ui;

import com.smartschedule.model.Category;
import com.smartschedule.model.Schedule;
import com.smartschedule.model.SortOption;
import com.smartschedule.service.OperationResult;
import com.smartschedule.service.ScheduleManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Main Swing window for the SmartSchedule beta application. */
public class SmartScheduleFrame extends JFrame {
    private final ScheduleManager manager;
    private final JTextField search = new JTextField(18);
    private final JComboBox<Category> category = new JComboBox<>(Category.values());
    private final JComboBox<SortOption> sort = new JComboBox<>(SortOption.values());
    private final JLabel status = new JLabel("Ready");
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Date", "Time", "Title", "Category", "Priority", "Description"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);

    /** Builds the main application window around a ScheduleManager. */
    public SmartScheduleFrame(ScheduleManager manager) {
        super("SmartSchedule - Personal Schedule Management");
        this.manager = manager;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 590));
        setSize(1120, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8,8));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(12,14,0,14));
        JLabel appTitle = new JLabel("SmartSchedule");
        appTitle.setFont(appTitle.getFont().deriveFont(Font.BOLD, 24f));
        JLabel subtitle = new JLabel("Organize schedules, priorities, categories, and conflicts in one place.");
        titlePanel.add(appTitle, BorderLayout.NORTH); titlePanel.add(subtitle, BorderLayout.SOUTH);
        add(titlePanel, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(8,8));
        center.setBorder(BorderFactory.createEmptyBorder(4,14,8,14));
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.add(new JLabel("Search")); toolbar.add(search);
        toolbar.add(new JLabel("Category")); toolbar.add(category);
        toolbar.add(new JLabel("Sort")); toolbar.add(sort);
        JButton add = new JButton("+ Add Schedule");
        toolbar.add(add);
        center.add(toolbar, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.setAutoCreateRowSorter(false);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton edit = new JButton("Edit Selected");
        JButton delete = new JButton("Delete Selected");
        JButton refresh = new JButton("Reload from File");
        actions.add(edit); actions.add(delete); actions.add(refresh);
        bottom.add(actions, BorderLayout.WEST); bottom.add(status, BorderLayout.EAST);
        center.add(bottom, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        add.addActionListener(e -> addSchedule());
        edit.addActionListener(e -> editSchedule());
        delete.addActionListener(e -> deleteSchedule());
        refresh.addActionListener(e -> { manager.loadSchedules(); refreshTable(); setStatus("Reloaded local data"); });
        category.addActionListener(e -> refreshTable());
        sort.addActionListener(e -> refreshTable());
        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshTable(); }
            public void removeUpdate(DocumentEvent e) { refreshTable(); }
            public void changedUpdate(DocumentEvent e) { refreshTable(); }
        });
        refreshTable();
    }

    private void addSchedule() {
        ScheduleDialog d = new ScheduleDialog(this, null); d.setVisible(true);
        Schedule s = d.getResult(); if (s == null) return;
        showResult(manager.addSchedule(s));
    }
    private void editSchedule() {
        Integer id = selectedId(); if (id == null) return;
        Schedule old = manager.getSchedule(id); if (old == null) return;
        ScheduleDialog d = new ScheduleDialog(this, old); d.setVisible(true);
        Schedule updated = d.getResult(); if (updated == null) return;
        showResult(manager.editSchedule(id, updated));
    }
    private void deleteSchedule() {
        Integer id = selectedId(); if (id == null) return;
        Schedule s = manager.getSchedule(id);
        int ok = JOptionPane.showConfirmDialog(this, "Delete '" + s.getTitle() + "'?", "Confirm deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) {
            boolean success = manager.deleteSchedule(id);
            if (success) { refreshTable(); setStatus("Schedule deleted"); }
            else JOptionPane.showMessageDialog(this, "Deletion or saving failed.", "Delete", JOptionPane.ERROR_MESSAGE);
        }
    }
    private Integer selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a schedule first.", "No selection", JOptionPane.INFORMATION_MESSAGE); return null; }
        return (Integer) model.getValueAt(row, 0);
    }
    private void showResult(OperationResult r) {
        if (r.isSuccess()) { refreshTable(); setStatus(r.getMessage()); }
        else JOptionPane.showMessageDialog(this, r.getMessage(), r.isConflict() ? "Schedule conflict" : "Validation error",
                r.isConflict() ? JOptionPane.WARNING_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }
    private void refreshTable() {
        List<Schedule> data = manager.organizeSchedules(search.getText(), (Category) category.getSelectedItem(), (SortOption) sort.getSelectedItem());
        model.setRowCount(0);
        for (Schedule s : data) model.addRow(new Object[]{s.getId(), s.getDate(), s.getStartTime()+" - "+s.getEndTime(), s.getTitle(), s.getCategory(), s.getPriority(), s.getDescription()});
        status.setText(data.size() + " schedule(s) shown");
    }
    private void setStatus(String text) { status.setText(text); }
}
