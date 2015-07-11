package org.tshlabs.baja.protocol;

/**
 *
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
