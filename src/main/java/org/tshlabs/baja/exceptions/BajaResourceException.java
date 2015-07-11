package org.tshlabs.baja.exceptions;

/**
 *
 */
public class BajaResourceException extends BajaRuntimeException {
    public BajaResourceException() {
    }

    public BajaResourceException(String message) {
        super(message);
    }

    public BajaResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public BajaResourceException(Throwable cause) {
        super(cause);
    }

    public BajaResourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
