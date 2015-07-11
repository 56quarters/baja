package org.tshlabs.baja.exceptions;

/**
 *
 */
public class BajaTypeMismatchException extends BajaRuntimeException {

    public BajaTypeMismatchException() {
    }

    public BajaTypeMismatchException(String message) {
        super(message);
    }

    public BajaTypeMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public BajaTypeMismatchException(Throwable cause) {
        super(cause);
    }

    public BajaTypeMismatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
