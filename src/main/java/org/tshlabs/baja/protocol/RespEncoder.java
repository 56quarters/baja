package org.tshlabs.baja.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 *
 * This class is thread safe.
 */
public class RespEncoder {

    private final Charset payloadCharset;

    public RespEncoder(Charset payloadCharset) {
        this.payloadCharset = requireNonNull(payloadCharset);
    }

    public byte[] encode(List<String> args) {
        requireNonNull(args);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        quietWrite(out, getArrayPreamble(args));

        for (String arg : args) {
            quietWrite(out, getArgPreamble(arg));
            quietWrite(out, arg.getBytes(payloadCharset));
            out.write('\r');
            out.write('\n');
        }

        return out.toByteArray();
    }

    private static void quietWrite(ByteArrayOutputStream stream, byte[] bytes) {
        try {
            stream.write(bytes);
        } catch (IOException e) {
            throw new IllegalStateException("" +
                    "IOException when writing to a an in-memory byte array. This " +
                    "should be impossible. Please ensure hell has not frozen over", e);
        }
    }

    // VisibleForTesting
    static byte[] getArrayPreamble(List<String> args) {
        return (RespType.ARRAY.getString() + args.size() +
                "\r\n").getBytes(RespEncodings.PROTOCOL);
    }

    // VisibleForTesting
    static byte[] getArgPreamble(String arg) {
        return (RespType.BULK_STRING.getString() + arg.length() +
                "\r\n").getBytes(RespEncodings.PROTOCOL);
    }
}