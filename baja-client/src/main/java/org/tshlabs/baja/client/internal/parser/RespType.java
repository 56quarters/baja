package org.tshlabs.baja.client.internal.parser;

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

    private static final Map<Character, RespType> LOOKUP;

    static {
        Map<Character, RespType> types = new HashMap<>();
        for (RespType type : values()) {
            types.put(type.getToken(), type);
        }

        LOOKUP = Collections.unmodifiableMap(types);
    }

    private final char token;

    RespType(char token) {
        this.token = token;
    }

    public char getToken() {
        return token;
    }

    public static Optional<RespType> fromChar(char token) {
        return Optional.ofNullable(LOOKUP.get(token));
    }
}

