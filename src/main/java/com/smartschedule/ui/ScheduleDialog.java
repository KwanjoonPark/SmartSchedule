package com.smartschedule.ui;

import com.smartschedule.model.Category;
import com.smartschedule.model.Priority;
import com.smartschedule.model.Schedule;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/** Modal Swing form used for both adding and editing schedules. */
public class ScheduleDialog extends JDialog {
    private final JTextField title = new JTextField(22);
    private final JTextField date = new JTextField(10);
    private final JTextField start = new JTextField(7);
    private final JTextField end = new JTextField(7);
    private final JComboBox<Category> category = new JComboBox<>(new Category[]{Category.STUDY, Category.WORK, Category.MEETING, Category.EXERCISE, Category.PERSONAL});
    private final JComboBox<Priority> priority = new JComboBox<>(Priority.values());
    private final JTextArea description = new JTextArea(4, 22);
    private Schedule result;

    /** Creates an Add/Edit dialog; pass null for a new schedule. */
    public ScheduleDialog(Frame owner, Schedule existing) {
        super(owner, existing == null ? "Add Schedule" : "Edit Schedule", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(14, 16, 8, 16));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5); g.fill = GridBagConstraints.HORIZONTAL;
        addRow(form, g, 0, "Title", title);
        addRow(form, g, 1, "Date (YYYY-MM-DD)", date);
        JPanel times = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        times.add(start); times.add(new JLabel("to")); times.add(end);
        addRow(form, g, 2, "Time (HH:MM)", times);
        addRow(form, g, 3, "Category", category);
        addRow(form, g, 4, "Priority", priority);
        description.setLineWrap(true); description.setWrapStyleWord(true);
        addRow(form, g, 5, "Description", new JScrollPane(description));

        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        save.addActionListener(e -> onSave(existing));
        cancel.addActionListener(e -> dispose());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancel); buttons.add(save);
        add(form, BorderLayout.CENTER); add(buttons, BorderLayout.SOUTH);
        if (existing != null) fill(existing); else { date.setText(LocalDate.now().toString()); start.setText("09:00"); end.setText("10:00"); }
        pack(); setResizable(false); setLocationRelativeTo(owner);
    }

    private void addRow(JPanel p, GridBagConstraints g, int y, String label, Component c) {
        g.gridy=y; g.gridx=0; g.weightx=0; p.add(new JLabel(label), g);
        g.gridx=1; g.weightx=1; p.add(c, g);
    }
    private void fill(Schedule s) {
        title.setText(s.getTitle()); date.setText(s.getDate().toString()); start.setText(s.getStartTime().toString());
        end.setText(s.getEndTime().toString()); category.setSelectedItem(s.getCategory()); priority.setSelectedItem(s.getPriority());
        description.setText(s.getDescription());
    }
    private void onSave(Schedule existing) {
        try {
            int id = existing == null ? 0 : existing.getId();
            result = new Schedule(id, title.getText().trim(), LocalDate.parse(date.getText().trim()),
                    LocalTime.parse(start.getText().trim()), LocalTime.parse(end.getText().trim()),
                    (Category) category.getSelectedItem(), (Priority) priority.getSelectedItem(), description.getText().trim());
            dispose();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Use YYYY-MM-DD and HH:MM formats.", "Invalid date/time", JOptionPane.ERROR_MESSAGE);
        }
    }
    /** Returns the entered schedule, or null when the user canceled. */
    public Schedule getResult() { return result; }
}
