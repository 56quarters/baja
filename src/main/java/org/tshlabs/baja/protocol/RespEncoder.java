package org.tshlabs.baja.protocol;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Collections;
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

    private static final RespEncoder DEFAULT = new RespEncoder();

    private final Charset payloadCharset;

    /**
     * Construct a new encoder using a {@link RespEncodings#DEFAULT_PAYLOAD default}
     * character set for encoding string data.
     */
    public RespEncoder() {
        this(RespEncodings.DEFAULT_PAYLOAD);
    }

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
     * Get a singleton instance of a RESP encoder with the
     * {@link RespEncodings#DEFAULT_PAYLOAD default} character set encoding.
     *
     * @return Single RESP encoder with default character set
     */
    public static RespEncoder getInstance() {
        return DEFAULT;
    }

    /**
     * Convert multiple lists of strings to a byte array as specified by the
     * payload character set and the Redis Serialization Protocol.
     * <p>
     * The output will be properly encoded for sending multiple commands and
     * their respective arguments to a Redis server.
     * <p>
     * If you only need to encode a single Redis command and arguments, you may
     * call this method and wrap your arguments via {@link Collections#singletonList(Object)},
     * <p>
     * Example:
     * <pre>
     *    List&lt;String&gt; cmd = Arrays.asList("SET", "foo", "bar");
     *    byte[] bytes = encoder.encodeMulti(Collections.singletonList(cmd));
     * </pre>
     *
     * @param commands List of multiple commands and arguments as strings
     * @return The commands and associated arguments as a byte array
     */
    public byte[] encodeMulti(List<List<String>> commands) {
        Objects.requireNonNull(commands);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (List<String> args : commands) {
            writeCommandToStream(args, out);
        }
        return out.toByteArray();
    }

    private ByteArrayOutputStream writeCommandToStream(List<String> args, ByteArrayOutputStream stream) {
        writeToStream(stream, getArrayPreamble(args));

        for (String arg : args) {
            writeToStream(stream, getArgPreamble(arg));
            writeToStream(stream, arg.getBytes(payloadCharset));
            stream.write('\r');
            stream.write('\n');
        }

        return stream;
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
