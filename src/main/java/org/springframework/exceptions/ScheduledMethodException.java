package org.springframework.exceptions;

public class ScheduledMethodException extends Exception {
    public ScheduledMethodException() {
        super("Scheduled method must not have any parameters");
    }
}
