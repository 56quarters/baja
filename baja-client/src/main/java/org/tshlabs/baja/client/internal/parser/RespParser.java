package org.tshlabs.baja.client.internal.parser;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 *
 */
public class RespParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(RespParser.class);

    private static final int BULK_STRING_MAX_LEN = 1024 * 1024 * 512;

    private static final String CHARSET = "UTF-8";

    private static final char CR = '\r';

    private static final char NL = '\n';

    // VisibleForTesting
    RespType findType(InputStream stream) throws IOException {
        final int type = stream.read();
        if (type == -1) {
            throw new IllegalStateException("Stream end already reached");
        }

        // cast is safe since we already know it's between 0 and 255.
        final char charType = (char) type;
        final Optional<RespType> dataType = RespType.fromChar(charType);
        if (!dataType.isPresent()) {
            throw new IllegalArgumentException("Could not parse invalid type " + charType);
        }

        return dataType.get();
    }

    // VisibleForTesting
    String readArray(InputStream stream) {
        return null;
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
        return new String(buffer, CHARSET);
    }

    // VisibleForTesting
    String readError(InputStream stream) throws IOException {
        // ???
        return null;
    }

    // VisibleForTesting
    long readInteger(InputStream stream) throws IOException {
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
            //ensureValidRes(res);

            if (CR == res) {
                expectNewline(res, stream);
                break;
            }

            os.write(res);
        }

        return os.toString(CHARSET);
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

    static void ensureValidRes(int c) {
        if (c == -1) {
            throw new IllegalStateException("Unexpected EOF reading stream");
        }
    }
}
