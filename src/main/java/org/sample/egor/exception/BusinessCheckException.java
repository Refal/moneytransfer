package org.sample.egor.exception;

public class BusinessCheckException extends RuntimeException {
    BusinessCheckException(String message) {
        super(message);
    }

    public BusinessCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessCheckException(Throwable cause) {
        super(cause);
    }
}
