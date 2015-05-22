package org.tshlabs.baja.client.exceptions;

/**
 *
 */
public class BajaRuntimeException extends RuntimeException {
    public BajaRuntimeException() {
    }

    public BajaRuntimeException(String message) {
        super(message);
    }

    public BajaRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BajaRuntimeException(Throwable cause) {
        super(cause);
    }

    public BajaRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
