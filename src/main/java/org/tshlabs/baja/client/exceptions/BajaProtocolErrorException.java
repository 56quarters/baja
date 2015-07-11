package org.tshlabs.baja.client.exceptions;

/**
 *
 */
public class BajaProtocolErrorException extends BajaRuntimeException {
    public BajaProtocolErrorException() {
    }

    public BajaProtocolErrorException(String message) {
        super(message);
    }

    public BajaProtocolErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public BajaProtocolErrorException(Throwable cause) {
        super(cause);
    }

    public BajaProtocolErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
