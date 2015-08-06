package org.tshlabs.baja.protocol;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Character sets that are used for the Redis Protocol itself and as a default
 * for string data stored in Redis.
 * <p>
 * This class thread safe.
 *
 * @see <a href="http://redis.io/topics/protocol">Redis Protocol</a>
 */
public class RespEncodings {

    /**
     * Character set used to encode strings used as Redis control characters.
     */
    public static final Charset PROTOCOL = StandardCharsets.UTF_8;

    /**
     * Default character set used for commands, arguments, and data returned by Redis.
     */
    public static final Charset DEFAULT_PAYLOAD = StandardCharsets.UTF_8;
}
