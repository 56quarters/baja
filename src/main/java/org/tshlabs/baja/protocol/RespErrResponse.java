package org.tshlabs.baja.protocol;

/**
 * Simple holder for an error response from the Redis server
 * <p>
 * This class is thread safe.
 */
public class RespErrResponse {

    private final String message;

    public RespErrResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
