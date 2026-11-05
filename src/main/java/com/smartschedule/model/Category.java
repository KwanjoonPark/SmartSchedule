package com.smartschedule.model;

/** Categories used to classify schedules in SmartSchedule. */
public enum Category {
    ALL("All"), STUDY("Study"), WORK("Work"), MEETING("Meeting"), EXERCISE("Exercise"), PERSONAL("Personal");

    private final String label;
    Category(String label) { this.label = label; }
    @Override public String toString() { return label; }
}
