package org.tshlabs.baja.client.exceptions;

/**
 *
 */
public class BajaRedisException extends RuntimeException {
    public BajaRedisException() {
    }

    public BajaRedisException(String message) {
        super(message);
    }

    public BajaRedisException(String message, Throwable cause) {
        super(message, cause);
    }

    public BajaRedisException(Throwable cause) {
        super(cause);
    }

    public BajaRedisException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
