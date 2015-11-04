package org.tshlabs.baja.protocol;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Class for parsing results and error responses from a Redis server into
 * Java types.
 * <p>
 * Redis bulk strings are encoded using a user supplied character set, all
 * other response types are encoded using the {@link RespEncodings#PROTOCOL}
 * encoding.
 * <p>
 * This class is thread safe.
 *
 * @see <a href="http://redis.io/topics/protocol">Redis Protocol</a>
 */
public class RespParser {

    private static final RespParser DEFAULT = new RespParser();

    private static final int BULK_STRING_MAX_LEN = 1024 * 1024 * 512;

    private static final char CR = '\r';

    private static final char LF = '\n';

    private final Charset payloadCharset;

    /**
     * Construct a new parser using a default character set for parsing
     * bulk string data.
     */
    public RespParser() {
        this(RespEncodings.DEFAULT_PAYLOAD);
    }

    /**
     * Construct a new parser using the given character set for parsing
     * bulk string data.
     *
     * @param payloadCharset Character set to use for parsing bulk string data
     * @throws NullPointerException If payloadCharset is null
     */
    public RespParser(Charset payloadCharset) {
        this.payloadCharset = Objects.requireNonNull(payloadCharset);
    }

    /**
     * Get a singleton instance of a RESP parser with the
     * {@link RespEncodings#DEFAULT_PAYLOAD default} character set encoding.
     *
     * @return Singleton RESP parser with default character set
     */
    public static RespParser getInstance() {
        return DEFAULT;
    }

    /**
     * Read and determine the type of a response from the input stream.
     *
     * @param stream Input stream to read the type from
     * @return Type of the response being sent with the input stream
     * @throws IOException              If the stream could not be read
     * @throws IllegalArgumentException If the response was not one of the
     *                                  known types
     * @throws IllegalStateException    If EOF was encountered reading the
     *                                  stream
     */
    public RespType findType(InputStream stream) throws IOException {
        Objects.requireNonNull(stream);
        final int type = verifyNoEof(stream.read());

        return RespType.lookup(type).orElseThrow((() ->
            new IllegalArgumentException("Could not parse invalid type " + type)));
    }

    /**
     * Read an array response from the input stream.
     * <p>
     * Before using this method to read an array, callers are expected
     * to use the {@link #findType(InputStream)} method to determine that
     * the response is an array type.
     * <p>
     * Arrays may be empty or null.
     *
     * @param stream Input stream to read the array from
     * @return The array response as a list of objects
     * @throws IOException           If the stream could not be read
     * @throws IllegalStateException If EOF was encountered reading the
     *                               stream
     */
    public List<Object> readArray(InputStream stream) throws IOException {
        Objects.requireNonNull(stream);
        final long arraySize = readLong(stream);

        if (arraySize == 0) { // special case empty array
            return Collections.emptyList();
        }

        if (arraySize < 0) { // special case null array
            return null;
        }

        final List<Object> out = new ArrayList<>();
        for (long i = 0; i < arraySize; i++) {
            final RespType type = findType(stream);

            switch (type) {
                case ARRAY:
                    out.add(readArray(stream));
                    break;
                case BULK_STRING:
                    out.add(readBulkString(stream));
                    break;
                case ERROR:
                    out.add(readError(stream));
                    break;
                case INTEGER:
                    out.add(readLong(stream));
                    break;
                case SIMPLE_STRING:
                    out.add(readSimpleString(stream));
                    break;
            }
        }

        return out;
    }

    /**
     * Read an Redis bulk string response from the input stream with the
     * previously supplied character set.
     * <p>
     * Before using this method to read a bulk string, callers are expected
     * to use the {@link #findType(InputStream)} method to determine that
     * the response is a bulk string type.
     * <p>
     * Bulk strings may be empty or null.
     *
     * @param stream Input stream to read the bulk string from
     * @return The bulk string response
     * @throws IOException           If the stream could not be read
     * @throws IllegalStateException If EOF was encountered reading the
     *                               stream or the expected number of bytes
     *                               was not able to be read
     */
    public String readBulkString(InputStream stream) throws IOException {
        Objects.requireNonNull(stream);
        final long strLen = readLong(stream);
        if (strLen == 0) { // special case empty string
            return "";
        }

        if (strLen < 0) { // special case null string
            return null;
        }

        // The Redis protocol says the bulk strings won't be longer than
        // 512M, so we check that here to make sure the length isn't something
        // bigger than we can or should allocate.
        if (strLen > BULK_STRING_MAX_LEN) {
            throw new IllegalStateException(
                "Got unexpected length for bulk string " + strLen + " bytes");
        }

        // See if we can read the entire bulk string in one go into a single
        // buffer. If there is enough data in the stream, we should get it.
        final byte[] buffer = new byte[(int) strLen];
        final int read = verifyNoEof(stream.read(buffer, 0, (int) strLen));

        if (read != strLen) {
            throw new IllegalStateException(
                "Expected to read " + strLen + " bytes, got " + read);
        }

        expectNewline(verifyNoEof(stream.read()), stream);
        return new String(buffer, payloadCharset);
    }

    /**
     * Read a Redis error response from the input stream.
     * <p>
     * Before using this method to read an error, callers are expected to
     * use the {@link #findType(InputStream)} method to determine that the
     * response is an error type.
     *
     * @param stream Input stream to read the error response from
     * @return The Redis error response
     * @throws IOException           If the stream could not be read
     * @throws IllegalStateException If EOF was encountered reading the
     *                               stream
     */
    public RespErrResponse readError(InputStream stream) throws IOException {
        Objects.requireNonNull(stream);
        return new RespErrResponse(readLine(stream));
    }

    /**
     * Read a Redis "integer" (which is a 64 bit signed number) as a long
     * from the input stream.
     * <p>
     * Before using this method to read the long, callers are expected to
     * use the {@link #findType(InputStream)} method to determine that the
     * response is an integer type.
     *
     * @param stream Input stream to read the integer response from
     * @return The Redis 64 bit signed integer
     * @throws IOException           If the stream could not be read
     * @throws IllegalStateException If EOF was encountered reading the
     *                               stream
     */
    public long readLong(InputStream stream) throws IOException {
        Objects.requireNonNull(stream);
        // Redis Serialization Protocol (RESP) specifies that integer types
        // are 64bit which is a long in Java, so we just treat this as a long
        // and call it a long everywhere even though it corresponds to the
        // 'integer' type in RESP.
        return Long.parseLong(readLine(stream));
    }

    /**
     * Read a Redis simple string from the input stream.
     * <p>
     * Before using this method to read the string, callers are expected to
     * use the {@link #findType(InputStream)} method to determine that the
     * response is a simple string type.
     * <p>
     * Simple strings are different from bulk strings since they are not
     * binary safe and are typically only generated by the server itself as
     * a result of running some command.
     *
     * @param stream Input stream to read the simple string response from
     * @return The Redis simple string response
     * @throws IOException           If the stream could not be read
     * @throws IllegalStateException If EOF was encountered reading the
     *                               stream
     */
    public String readSimpleString(InputStream stream) throws IOException {
        Objects.requireNonNull(stream);
        return readLine(stream);
    }

    /**
     * Read the {@link InputStream} until encountering a {@code \r\n} and return
     * the results as a UTF-8 encoded string, not including the {@code \r\n}.
     */
    // VisibleForTesting
    static String readLine(InputStream stream) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        int res;

        while (true) {
            res = verifyNoEof(stream.read());

            if (CR == res && expectNewline(res, stream)) {
                break;
            }

            os.write(res);
        }

        return os.toString(RespEncodings.PROTOCOL.name());
    }

    /**
     * Raise an error if the given {@code int} indicates that we have reached
     * the end of the stream (return value of {@code -1}), return the given int
     * otherwise.
     */
    private static int verifyNoEof(int c) {
        if (c == -1) {
            throw new IllegalStateException("Unexpected EOF reading stream");
        }

        return c;
    }

    /**
     * Verify that the CR and LF characters occur one after the other in the
     * given {@link InputStream}, otherwise raise an {@code IllegalStateException}.
     */
    // VisibleForTesting
    static boolean expectNewline(int c, InputStream stream) throws IOException {
        if (CR == c) {
            return expectLf(verifyNoEof(stream.read()));
        }

        throw new IllegalStateException("Expected CR (\\r), got " + c);
    }

    /* This method could just as easily be another condition in the 'if' statement
     * in expectNewline() but this way we can raise a more specific exception when
     * we don't get the expected CRLF in the stream.
     */
    private static boolean expectLf(int c) {
        if (LF == c) {
            return true;
        }

        throw new IllegalStateException("Expected LF (\\n), got " + c);
    }
}
