package com.smartschedule;

import com.smartschedule.persistence.ScheduleRepository;
import com.smartschedule.service.ScheduleManager;
import com.smartschedule.ui.SmartScheduleFrame;

import javax.swing.*;
import java.nio.file.Path;

/** Entry point of the SmartSchedule beta release. */
public class SmartScheduleApp {
    /** Starts the Java Swing application. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) { }
            Path data = Path.of(System.getProperty("user.home"), ".smartschedule", "schedules.tsv");
            ScheduleManager manager = new ScheduleManager(new ScheduleRepository(data));
            new SmartScheduleFrame(manager).setVisible(true);
        });
    }
}
