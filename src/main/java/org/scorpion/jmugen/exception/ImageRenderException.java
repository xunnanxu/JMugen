package org.scorpion.jmugen.exception;

public class ImageRenderException extends GenericException {

    public ImageRenderException() {
    }

    public ImageRenderException(String message) {
        super(message);
    }

    public ImageRenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageRenderException(Throwable cause) {
        super(cause);
    }

    public ImageRenderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
