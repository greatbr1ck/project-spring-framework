package org.springframework.exceptions;

public class IncorrectClassPropertyException extends Exception{
    public IncorrectClassPropertyException() {
        super("Incorrect property class");
    }
}
