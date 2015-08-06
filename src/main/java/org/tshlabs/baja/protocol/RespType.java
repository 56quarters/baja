package org.tshlabs.baja.protocol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Enum representing the types supported by the Redis Protocol.
 * <p>
 * This class is thread safe.
 *
 * @see <a href="http://redis.io/topics/protocol">Redis Protocol</a>
 */
public enum RespType {
    ARRAY("*"),
    BULK_STRING("$"),
    ERROR("-"),
    INTEGER(":"),
    SIMPLE_STRING("+");

    private static final Map<Integer, RespType> BYTE_LOOKUP;

    private static final Map<String, RespType> STRING_LOOKUP;

    static {
        final Map<Integer, RespType> byteTypes = new HashMap<>();
        for (RespType type : values()) {
            byteTypes.put(type.getByte(), type);
        }

        BYTE_LOOKUP = Collections.unmodifiableMap(byteTypes);

        final Map<String, RespType> stringTypes = new HashMap<>();
        for (RespType type : values()) {
            stringTypes.put(type.getString(), type);
        }

        STRING_LOOKUP = Collections.unmodifiableMap(stringTypes);
    }

    private final int token;
    private final String tokenString;

    RespType(String tokenString) {
        this.token = tokenString.getBytes(RespEncodings.PROTOCOL)[0];
        this.tokenString = tokenString;
    }

    public int getByte() {
        return token;
    }

    public String getString() {
        return tokenString;
    }

    public static Optional<RespType> lookup(int token) {
        return Optional.ofNullable(BYTE_LOOKUP.get(token));
    }

    public static Optional<RespType> lookup(String token) {
        return Optional.ofNullable(STRING_LOOKUP.get(token));
    }
}

