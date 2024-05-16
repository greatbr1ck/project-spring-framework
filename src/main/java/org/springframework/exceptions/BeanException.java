package org.springframework.exceptions;

public class BeanException extends Exception {
    public BeanException() {
        super("Error! There must be only 1 Bean from the Class");
    }

}
