package com.smartschedule.model;

/** Sorting options supported by the schedule list. */
public enum SortOption {
    DATE_TIME("Date / Time"), PRIORITY("Priority"), TITLE("Title");
    private final String label;
    SortOption(String label) { this.label = label; }
    @Override public String toString() { return label; }
}
