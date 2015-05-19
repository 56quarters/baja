package org.tshlabs.baja.client.internal;

import org.tshlabs.baja.client.exceptions.BajaRedisException;
import org.tshlabs.baja.client.internal.protocol.RespEncoder;
import org.tshlabs.baja.client.internal.protocol.RespErrResponse;
import org.tshlabs.baja.client.internal.protocol.RespParser;
import org.tshlabs.baja.client.internal.protocol.RespType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static java.util.Objects.requireNonNull;


/**
 *
 */
public class Connection {

    private final OutputStream outputStream;

    private final InputStream inputStream;

    private final RespEncoder encoder;

    private final RespParser parser;

    public Connection(
            InputStream inputStream,
            OutputStream outputStream,
            RespEncoder encoder,
            RespParser parser) {
        this.inputStream = requireNonNull(inputStream);
        this.outputStream = requireNonNull(outputStream);
        this.encoder = requireNonNull(encoder);
        this.parser = requireNonNull(parser);
    }



    public void writeCommand(List<String> args) throws IOException {
        outputStream.write(encoder.encode(args));
    }

    public String readString() throws IOException {
        verifyResponseType(RespType.BULK_STRING);
        return parser.readBulkString(inputStream);
    }

    public long readInteger() throws IOException {
        verifyResponseType(RespType.INTEGER);
        return parser.readInteger(inputStream);
    }

    public List<Object> readArray() throws IOException {
        verifyResponseType(RespType.ARRAY);
        return parser.readArray(inputStream);
    }

    private void verifyResponseType(RespType expected) throws IOException {
        final RespType type = parser.findType(inputStream);

        if (type == RespType.ERROR) {
            final RespErrResponse err = parser.readError(inputStream);
            throw new BajaRedisException(err.getMessage());
        }

        if (type != expected) {
            throw new BajaRedisException(
                    "Unexpected type. Expected " + expected + ", got " + type);
        }
    }
}
