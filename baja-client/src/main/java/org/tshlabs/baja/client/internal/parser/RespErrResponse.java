package org.tshlabs.baja.client.internal.parser;

/**
 *
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
