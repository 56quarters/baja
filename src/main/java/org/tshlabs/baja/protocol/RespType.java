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

    /**
     * @return The byte representation of this type as a signed integer.
     */
    public int getByte() {
        return token;
    }

    /**
     * @return The string representation of this type (a single character)
     */
    public String getString() {
        return tokenString;
    }

    /**
     * Get a RESP type based on the given byte (as a signed integer)
     *
     * @param token Byte representation of the type
     * @return Type according to the RESP byte representation
     */
    public static Optional<RespType> lookup(int token) {
        return Optional.ofNullable(BYTE_LOOKUP.get(token));
    }

    /**
     * Get a RESP type based on the given string
     *
     * @param token String representation of the type
     * @return Type according to the RESP string representation
     */
    public static Optional<RespType> lookup(String token) {
        return Optional.ofNullable(STRING_LOOKUP.get(token));
    }
}

