package com.london.common;

public class ApplicationException extends Exception {
    private static final long serialVersionUID = 3117443782817835777L;
    private final String exceptionMessage;

    /**
     * Constructor.
     * @param cause Cause
     * @param message exception message
     */
    public ApplicationException(Throwable cause, String message) {
        super(message);
        if(cause != null) {
            this.initCause(cause);
        }
        this.exceptionMessage = message;
    }
}
