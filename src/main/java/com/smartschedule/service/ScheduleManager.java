package com.smartschedule.service;

import com.smartschedule.model.Category;
import com.smartschedule.model.Priority;
import com.smartschedule.model.Schedule;
import com.smartschedule.model.SortOption;
import com.smartschedule.persistence.ScheduleRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Application service that implements schedule validation, conflict checking,
 * CRUD operations, organization, and automatic persistence.
 */
public class ScheduleManager {
    private final ScheduleRepository repository;
    private final List<Schedule> schedules = new ArrayList<>();
    private int nextId = 1;

    /** Creates a manager and restores previously saved schedule data. */
    public ScheduleManager(ScheduleRepository repository) {
        this.repository = repository;
        schedules.addAll(repository.loadSchedules());
        nextId = schedules.stream().mapToInt(Schedule::getId).max().orElse(0) + 1;
    }

    /**
     * Validates and adds a new schedule, then automatically persists the list.
     * @param schedule schedule to register
     * @return operation result indicating success or a validation/conflict message
     */
    public OperationResult addSchedule(Schedule schedule) {
        OperationResult v = validateSchedule(schedule);
        if (!v.isSuccess()) return v;
        Optional<Schedule> conflict = findConflict(schedule, -1);
        if (conflict.isPresent()) return OperationResult.conflict("Time conflict with: " + conflict.get().getTitle());
        schedule.setId(nextId++);
        schedules.add(schedule);
        if (!saveSchedules()) return OperationResult.failure("Schedule was added, but local saving failed.");
        return OperationResult.success("Schedule added successfully.");
    }

    /** Validates and replaces an existing schedule identified by ID. */
    public OperationResult editSchedule(int scheduleId, Schedule updatedSchedule) {
        int index = indexOf(scheduleId);
        if (index < 0) return OperationResult.failure("The selected schedule no longer exists.");
        OperationResult v = validateSchedule(updatedSchedule);
        if (!v.isSuccess()) return v;
        Optional<Schedule> conflict = findConflict(updatedSchedule, scheduleId);
        if (conflict.isPresent()) return OperationResult.conflict("Time conflict with: " + conflict.get().getTitle());
        updatedSchedule.setId(scheduleId);
        schedules.set(index, updatedSchedule);
        if (!saveSchedules()) return OperationResult.failure("Schedule was edited, but local saving failed.");
        return OperationResult.success("Schedule updated successfully.");
    }

    /** Deletes the schedule with the supplied ID and persists the new list. */
    public boolean deleteSchedule(int scheduleId) {
        boolean removed = schedules.removeIf(s -> s.getId() == scheduleId);
        return removed && saveSchedules();
    }

    /** Searches, filters, and sorts schedules for display in the Swing JTable. */
    public List<Schedule> organizeSchedules(String keyword, Category category, SortOption sortOption) {
        String q = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        Category c = category == null ? Category.ALL : category;
        SortOption sort = sortOption == null ? SortOption.DATE_TIME : sortOption;
        Comparator<Schedule> comparator = switch (sort) {
            case PRIORITY -> Comparator.comparingInt((Schedule s) -> s.getPriority().rank())
                    .thenComparing(Schedule::startDateTime);
            case TITLE -> Comparator.comparing(s -> s.getTitle().toLowerCase(Locale.ROOT));
            case DATE_TIME -> Comparator.comparing(Schedule::startDateTime);
        };
        return schedules.stream()
                .filter(s -> q.isEmpty() || s.getTitle().toLowerCase(Locale.ROOT).contains(q)
                        || s.getDescription().toLowerCase(Locale.ROOT).contains(q))
                .filter(s -> c == Category.ALL || s.getCategory() == c)
                .sorted(comparator)
                .toList();
    }

    /** Saves the current schedule list to local storage. */
    public boolean saveSchedules() { return repository.saveSchedules(schedules); }

    /** Reloads schedule data from local storage and replaces the current list. */
    public List<Schedule> loadSchedules() {
        schedules.clear();
        schedules.addAll(repository.loadSchedules());
        nextId = schedules.stream().mapToInt(Schedule::getId).max().orElse(0) + 1;
        return getAllSchedules();
    }

    /** Returns a defensive copy of all schedules. */
    public List<Schedule> getAllSchedules() { return new ArrayList<>(schedules); }
    /** Finds a schedule by its ID. */
    public Schedule getSchedule(int id) { return schedules.stream().filter(s -> s.getId() == id).findFirst().orElse(null); }

    /** Performs required-field and time-range validation. */
    public OperationResult validateSchedule(Schedule s) {
        if (s == null) return OperationResult.failure("Schedule information is missing.");
        if (s.getTitle() == null || s.getTitle().isBlank()) return OperationResult.failure("Title is required.");
        if (s.getDate() == null || s.getStartTime() == null || s.getEndTime() == null)
            return OperationResult.failure("Date, start time, and end time are required.");
        if (!s.getStartTime().isBefore(s.getEndTime())) return OperationResult.failure("End time must be later than start time.");
        if (s.getCategory() == null || s.getCategory() == Category.ALL) return OperationResult.failure("Please select a category.");
        if (s.getPriority() == null) return OperationResult.failure("Please select a priority.");
        return OperationResult.success("Valid schedule.");
    }

    /** Finds a conflicting schedule, excluding one schedule ID during editing. */
    public Optional<Schedule> findConflict(Schedule target, int excludeId) {
        return schedules.stream().filter(s -> s.getId() != excludeId && target.overlaps(s)).findFirst();
    }

    private int indexOf(int id) {
        for (int i = 0; i < schedules.size(); i++) if (schedules.get(i).getId() == id) return i;
        return -1;
    }

    /** Convenience method used by development/demo scripts. */
    public static Schedule sample(String title, LocalDate date, LocalTime start, LocalTime end, Category cat, Priority pri, String desc) {
        return new Schedule(0, title, date, start, end, cat, pri, desc);
    }
}
