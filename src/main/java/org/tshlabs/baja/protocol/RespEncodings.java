package org.tshlabs.baja.protocol;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 *
 * This class thread safe.
 */
public class RespEncodings {

    public static final Charset PROTOCOL = StandardCharsets.UTF_8;

    public static final Charset DEFAULT_PAYLOAD = StandardCharsets.UTF_8;
}
