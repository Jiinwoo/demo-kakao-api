package me.jung.demokakaoapi.advice.exception;

public class CCommunicationException extends RuntimeException {

    public CCommunicationException() {
        super();
    }

    public CCommunicationException(String message) {
        super(message);
    }

    public CCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
