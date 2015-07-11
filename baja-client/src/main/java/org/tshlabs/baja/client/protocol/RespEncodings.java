package org.tshlabs.baja.client.protocol;

import javax.annotation.concurrent.Immutable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 */
@Immutable
public class RespEncodings {

    public static Charset PROTOCOL = StandardCharsets.UTF_8;

    public static Charset DEFAULT_PAYLOAD = StandardCharsets.UTF_8;
}
