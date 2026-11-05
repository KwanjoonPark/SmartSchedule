package com.smartschedule.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Domain object representing one personal schedule.
 * Each item stores the seven fields defined in the SmartSchedule requirements.
 */
public class Schedule {
    private int id;
    private String title;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Category category;
    private Priority priority;
    private String description;

    /** Creates a schedule object. */
    public Schedule(int id, String title, LocalDate date, LocalTime startTime, LocalTime endTime,
                    Category category, Priority priority, String description) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.category = category;
        this.priority = priority;
        this.description = description == null ? "" : description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description == null ? "" : description; }

    /** Returns the start of the schedule as one date-time value. */
    public LocalDateTime startDateTime() { return LocalDateTime.of(date, startTime); }
    /** Returns the end of the schedule as one date-time value. */
    public LocalDateTime endDateTime() { return LocalDateTime.of(date, endTime); }

    /** Returns true when this schedule time range overlaps another schedule. */
    public boolean overlaps(Schedule other) {
        if (other == null || !date.equals(other.date)) return false;
        return startTime.isBefore(other.endTime) && endTime.isAfter(other.startTime);
    }

    @Override public boolean equals(Object obj) {
        return obj instanceof Schedule s && id == s.id;
    }
    @Override public int hashCode() { return Objects.hash(id); }
}
