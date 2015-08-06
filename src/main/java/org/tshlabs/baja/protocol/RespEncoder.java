package org.tshlabs.baja.protocol;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

/**
 * Class for encoding an argument list to a byte array as expected by
 * the Redis server.
 * <p>
 * The {@link RespEncodings#PROTOCOL protocol} encoding will be used for
 * Redis protocol control characters and a user specified encoding used for
 * the list of arguments.
 * <p>
 * This class is thread safe.
 *
 * @see <a href="http://redis.io/topics/protocol">Redis Protocol</a>
 */
public class RespEncoder {

    private final Charset payloadCharset;

    /**
     * Construct a new encoder using the given character set for encoding
     * string data.
     *
     * @param payloadCharset Character set to use for encoding commands and arguments
     * @throws NullPointerException If payloadCharset is null
     */
    public RespEncoder(Charset payloadCharset) {
        this.payloadCharset = Objects.requireNonNull(payloadCharset);
    }

    /**
     * Convert a list of strings to a byte array as specified by the payload
     * character set and the Redis Serialization Protocol.
     *
     * @param args List of a command and arguments as strings
     * @return The command and arguments as a byte array
     */
    public byte[] encode(List<String> args) {
        Objects.requireNonNull(args);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeToStream(out, getArrayPreamble(args));

        for (String arg : args) {
            writeToStream(out, getArgPreamble(arg));
            writeToStream(out, arg.getBytes(payloadCharset));
            out.write('\r');
            out.write('\n');
        }

        return out.toByteArray();
    }

    private static void writeToStream(ByteArrayOutputStream stream, byte[] bytes) {
        // Pass the offset and length here explicitly so that we're calling
        // the .write() method defined in ByteArrayOutputStream (instead of the
        // one defined in OutputStream) which doesn't include an IOException as
        // part of its signature. This way we don't have to pretend to handle an
        // exception that will never be raised.
        stream.write(bytes, 0, bytes.length);
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
