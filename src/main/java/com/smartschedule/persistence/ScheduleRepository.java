package com.smartschedule.persistence;

import com.smartschedule.model.Category;
import com.smartschedule.model.Priority;
import com.smartschedule.model.Schedule;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based persistence component for SmartSchedule.
 * Data is stored in a compact tab-separated text format in the local data directory.
 */
public class ScheduleRepository {
    private final Path dataFile;

    /** Creates a repository using the specified local data file. */
    public ScheduleRepository(Path dataFile) { this.dataFile = dataFile; }

    /** Writes all schedule objects to local storage. */
    public boolean saveSchedules(List<Schedule> schedules) {
        try {
            Path parent = dataFile.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            List<String> lines = new ArrayList<>();
            for (Schedule s : schedules) lines.add(serialize(s));
            Files.write(dataFile, lines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    /** Reads previously stored schedules; invalid records are ignored safely. */
    public List<Schedule> loadSchedules() {
        List<Schedule> result = new ArrayList<>();
        if (!Files.exists(dataFile)) return result;
        try {
            for (String line : Files.readAllLines(dataFile, StandardCharsets.UTF_8)) {
                Schedule s = deserialize(line);
                if (s != null) result.add(s);
            }
        } catch (IOException ignored) { }
        return result;
    }

    public Path getDataFile() { return dataFile; }

    private String serialize(Schedule s) {
        return s.getId() + "\t" + esc(s.getTitle()) + "\t" + s.getDate() + "\t" + s.getStartTime()
                + "\t" + s.getEndTime() + "\t" + s.getCategory().name() + "\t" + s.getPriority().name()
                + "\t" + esc(s.getDescription());
    }

    private Schedule deserialize(String line) {
        try {
            String[] p = line.split("\\t", -1);
            if (p.length != 8) return null;
            return new Schedule(Integer.parseInt(p[0]), unesc(p[1]), LocalDate.parse(p[2]), LocalTime.parse(p[3]),
                    LocalTime.parse(p[4]), Category.valueOf(p[5]), Priority.valueOf(p[6]), unesc(p[7]));
        } catch (RuntimeException ex) { return null; }
    }

    private String esc(String v) { return v.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n"); }
    private String unesc(String v) {
        StringBuilder b = new StringBuilder();
        boolean slash = false;
        for (char c : v.toCharArray()) {
            if (slash) { b.append(c == 't' ? '\t' : c == 'n' ? '\n' : c); slash = false; }
            else if (c == '\\') slash = true;
            else b.append(c);
        }
        if (slash) b.append('\\');
        return b.toString();
    }
}
