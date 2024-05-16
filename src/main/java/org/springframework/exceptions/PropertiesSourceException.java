package org.springframework.exceptions;

public class PropertiesSourceException extends Exception{
  public PropertiesSourceException() {
    super("The PropertiesSource file could not be found");
  }
}

