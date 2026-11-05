package com.smartschedule.model;

/** Priority levels that help users distinguish important schedules. */
public enum Priority {
    HIGH("High", 0), MEDIUM("Medium", 1), LOW("Low", 2);
    private final String label;
    private final int rank;
    Priority(String label, int rank) { this.label = label; this.rank = rank; }
    public int rank() { return rank; }
    @Override public String toString() { return label; }
}
