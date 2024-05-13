package org.springframework.exceptions;

public class PropertyFormatException extends Exception{
    public PropertyFormatException () {
        super("Incorrect property format in Properties Source file");
    }
}
