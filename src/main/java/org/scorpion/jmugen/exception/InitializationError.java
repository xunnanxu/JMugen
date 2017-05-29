package org.scorpion.jmugen.exception;

public class InitializationError extends Error implements Fatal {

    public InitializationError(String message) {
        super(message);
    }

    public InitializationError(String message, String reason) {
        super(message + ":\n" + reason);
    }

    public InitializationError(String message, Throwable cause) {
        super(message, cause);
    }
}
