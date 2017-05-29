package org.scorpion.jmugen.exception;

public class GenericIOException extends GenericException {

    public GenericIOException(String message) {
        super(message);
    }

    public GenericIOException(Throwable cause) {
        super(cause);
    }

    public GenericIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
