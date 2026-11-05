package com.smartschedule.service;

/** Result returned by add/edit operations with a human-readable message. */
public class OperationResult {
    private final boolean success;
    private final boolean conflict;
    private final String message;

    public OperationResult(boolean success, boolean conflict, String message) {
        this.success = success;
        this.conflict = conflict;
        this.message = message;
    }
    public static OperationResult success(String message) { return new OperationResult(true, false, message); }
    public static OperationResult failure(String message) { return new OperationResult(false, false, message); }
    public static OperationResult conflict(String message) { return new OperationResult(false, true, message); }
    public boolean isSuccess() { return success; }
    public boolean isConflict() { return conflict; }
    public String getMessage() { return message; }
}
