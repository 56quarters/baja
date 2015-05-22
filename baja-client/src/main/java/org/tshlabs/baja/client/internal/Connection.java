package org.tshlabs.baja.client.internal;

import org.tshlabs.baja.client.exceptions.BajaProtocolErrorException;
import org.tshlabs.baja.client.exceptions.BajaRuntimeException;
import org.tshlabs.baja.client.internal.protocol.RespEncoder;
import org.tshlabs.baja.client.internal.protocol.RespErrResponse;
import org.tshlabs.baja.client.internal.protocol.RespParser;
import org.tshlabs.baja.client.internal.protocol.RespType;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;


/**
 *
 */
@NotThreadSafe
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

    public void writeCommand(List<String> args) {
        IOFunction.runCommand(() -> {
            outputStream.write(encoder.encode(args));
            return null;
        });
    }

    public String readSimpleString() {
        verifyResponseType(Collections.singleton(RespType.SIMPLE_STRING));
        return IOFunction.runCommand(() -> parser.readSimpleString(inputStream));
    }

    public String readBulkString() {
        verifyResponseType(Collections.singleton(RespType.BULK_STRING));
        return IOFunction.runCommand(() -> parser.readBulkString(inputStream));
    }

    public String readSimpleOrBulkString() {
        final Set<RespType> expected = new HashSet<>();
        expected.add(RespType.BULK_STRING);
        expected.add(RespType.SIMPLE_STRING);

        final RespType type = verifyResponseType(expected);
        if (type == RespType.BULK_STRING) {
            return IOFunction.runCommand(() -> parser.readBulkString(inputStream));
        }

        return IOFunction.runCommand(() -> parser.readSimpleString(inputStream));
    }

    public long readInteger() {
        verifyResponseType(Collections.singleton(RespType.INTEGER));
        return IOFunction.runCommand(() -> parser.readInteger(inputStream));
    }

    public List<Object> readArray() {
        verifyResponseType(Collections.singleton(RespType.ARRAY));
        return IOFunction.runCommand(() -> parser.readArray(inputStream));
    }

    /**
     * Ensure that the type of the result in the {@link InputStream} matches one of
     * the supplied, expected types and return the actual type of the result.
     *
     * @param expected Allowed types for the result in the input stream
     * @return The actual type in the input stream
     * @throws BajaProtocolErrorException If the type in the input stream is of
     *                                    {@link RespType#ERROR}
     * @throws BajaRuntimeException       If the type in the input stream is not
     *                                    an error or one of the expected types.
     */
    private RespType verifyResponseType(Set<RespType> expected) {
        final RespType type = IOFunction.runCommand(() -> parser.findType(inputStream));

        if (type == RespType.ERROR) {
            final RespErrResponse err = IOFunction.runCommand(() -> parser.readError(inputStream));
            throw new BajaProtocolErrorException(err.getMessage());
        }

        if (!expected.contains(type)) {
            throw new BajaRuntimeException(
                    "Unexpected type. Expected one of " + expected + ", got " + type);
        }

        return type;
    }
}
