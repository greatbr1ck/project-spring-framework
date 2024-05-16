package org.springframework.exceptions;

public class PropertyNotFoundException extends Exception{
    public PropertyNotFoundException() {
        super("Property not found in Property Source file");
    }
}
