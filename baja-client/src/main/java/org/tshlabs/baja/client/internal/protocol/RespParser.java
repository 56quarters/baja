package org.tshlabs.baja.client.internal.protocol;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.tshlabs.baja.client.internal.BajaUtils.checkNotNull;

/**
 *
 */
public class RespParser {

    private static final int BULK_STRING_MAX_LEN = 1024 * 1024 * 512;

    private static final char CR = '\r';

    private static final char LF = '\n';

    private final Charset payloadCharset;

    public RespParser(Charset payloadCharset) {
        this.payloadCharset = checkNotNull(payloadCharset);
    }

    public RespType findType(InputStream stream) throws IOException {
        checkNotNull(stream);
        final int type = verifyNoEof(stream.read());

        final Optional<RespType> dataType = RespType.lookup(type);
        if (!dataType.isPresent()) {
            throw new IllegalArgumentException("Could not parse invalid type " + type);
        }

        return dataType.get();
    }

    public List<Object> readArray(InputStream stream) throws IOException {
        checkNotNull(stream);
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

    public String readBulkString(InputStream stream) throws IOException {
        checkNotNull(stream);
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
        return new String(buffer, payloadCharset);
    }

    public RespErrResponse readError(InputStream stream) throws IOException {
        checkNotNull(stream);
        return new RespErrResponse(readLine(stream));
    }

    public long readInteger(InputStream stream) throws IOException {
        checkNotNull(stream);
        // Redis Serialization Protocol (RESP) specifies that integer types
        // are 64bit which is a long in Java, so we use a long here but
        // call it an integer. Maybe this is dumb.
        return Long.parseLong(readLine(stream));
    }

    public String readSimpleString(InputStream stream) throws IOException {
        checkNotNull(stream);
        return readLine(stream);
    }

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


    private static int verifyNoEof(int c) {
        if (c == -1) {
            throw new IllegalStateException("Unexpected EOF reading stream");
        }

        return c;
    }

    // VisibleForTesting
    static boolean expectNewline(int c, InputStream stream) throws IOException {
        if (CR == c) {
            return expectNewline(verifyNoEof(stream.read()), stream);
        }

        if (LF == c) {
            return true;
        }

        throw new IllegalStateException("Expected newline (\\r or \\n), got " + c);
    }
}
