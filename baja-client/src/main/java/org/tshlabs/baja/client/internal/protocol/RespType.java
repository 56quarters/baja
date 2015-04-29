package org.tshlabs.baja.client.internal.protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 */
public enum RespType {
    ARRAY('*'),
    BULK_STRING('$'),
    ERROR('-'),
    INTEGER(':'),
    SIMPLE_STRING('+');

    private static final Map<Integer, RespType> LOOKUP;

    static {
        Map<Integer, RespType> types = new HashMap<>();
        for (RespType type : values()) {
            types.put(type.getToken(), type);
        }

        LOOKUP = Collections.unmodifiableMap(types);
    }

    private final int token;

    RespType(int token) {
        this.token = token;
    }

    public int getToken() {
        return token;
    }

    public static Optional<RespType> fromChar(int token) {
        return Optional.ofNullable(LOOKUP.get(token));
    }
}

