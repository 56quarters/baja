package org.tshlabs.baja.client.internal.parser;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 */
public class RespParser {

    private static final int BULK_STRING_MAX_LEN = 1024 * 1024 * 512;

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final char CR = '\r';

    private static final char NL = '\n';

    private final String charset;

    public RespParser() {
        this.charset = DEFAULT_CHARSET;
    }

    public RespParser(String charset) {
        this.charset = charset;
    }

    // VisibleForTesting
    RespType findType(InputStream stream) throws IOException {
        final int type = stream.read();
        if (type == -1) {
            throw new IllegalStateException("Stream end already reached");
        }

        final Optional<RespType> dataType = RespType.fromChar(type);
        if (!dataType.isPresent()) {
            throw new IllegalArgumentException("Could not parse invalid type " + type);
        }

        return dataType.get();
    }

    // VisibleForTesting
    List<Object> readArray(InputStream stream) throws IOException {
        final long arraySize = readInteger(stream);
        final List<Object> out = new ArrayList<>();

        if (arraySize == 0) { // special case empty array
            return out;
        }

        if (arraySize < 0) { // special case null array
            return null;
        }

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
                    out.add(readInteger(stream));
                    break;
                case SIMPLE_STRING:
                    out.add(readSimpleString(stream));
                    break;
            }
        }

        return out;
    }

    // VisibleForTesting
    String readBulkString(InputStream stream) throws IOException {
        final long strLen = readInteger(stream);
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
        final int read = stream.read(buffer, 0, (int) strLen);

        if (read != strLen) {
            throw new IllegalStateException(
                    "Expected to read " + strLen + " bytes, got " + read);
        }

        expectNewline(stream.read(), stream);
        return new String(buffer, charset);
    }

    // VisibleForTesting
    RespErrResponse readError(InputStream stream) throws IOException {
        return new RespErrResponse(readLine(stream));
    }

    // VisibleForTesting
    long readInteger(InputStream stream) throws IOException {
        // REdis Serialization Protocol (RESP) specifies that integer types
        // are 64bit which is a long in Java, so we use a long here but
        // call it an integer. Maybe this is dumb.
        return Long.parseLong(readLine(stream));
    }

    // VisibleForTesting
    String readSimpleString(InputStream stream) throws IOException {
        return readLine(stream);
    }

    // VisibleForTesting
    String readLine(InputStream stream) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        int res;

        while (true) {
            res = stream.read();
            if (CR == res) {
                expectNewline(res, stream);
                break;
            }

            os.write(res);
        }

        return os.toString(charset);
    }

    // VisibleForTesting
    static boolean expectNewline(int c, InputStream stream) throws IOException {
        if (CR == c) {
            return expectNewline(stream.read(), stream);
        }

        if (NL == c) {
            return true;
        }

        throw new IllegalStateException("Expected newline (\\r or \\n), got " + c);
    }
}
