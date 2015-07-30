package org.tshlabs.baja;

import org.tshlabs.baja.exceptions.BajaProtocolErrorException;
import org.tshlabs.baja.exceptions.BajaResourceException;
import org.tshlabs.baja.exceptions.BajaTypeMismatchException;
import org.tshlabs.baja.protocol.RespEncoder;
import org.tshlabs.baja.protocol.RespErrResponse;
import org.tshlabs.baja.protocol.RespParser;
import org.tshlabs.baja.protocol.RespType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;


/**
 * Class for sending commands and arguments to a Redis server parsing the
 * responses.
 * <p>
 * Commands are encoded and responses parsed using and implementation of
 * the REdis Serialization Protocol (RESP).
 * <p>
 * The connection operates on {@link InputStream} and {@link OutputStream}
 * implementations that are expected to be managed outside of the connection.
 * <p>
 * This class is <em>not</em> thread safe.
 */
public class RedisConnection {

    private final OutputStream outputStream;

    private final InputStream inputStream;

    private final RespEncoder encoder;

    private final RespParser parser;

    /**
     * Construct a new instance with the given input stream, output stream, RESP
     * encoder, and RESP parser.
     *
     * @param inputStream  Input stream to read responses from the server
     * @param outputStream Output stream to send commands to the server
     * @param encoder      RESP encoder for converting arguments to the wire format
     * @param parser       RESP decoder for parsing results into Java objects
     * @throws NullPointerException If any arguments are null
     */
    public RedisConnection(
            InputStream inputStream,
            OutputStream outputStream,
            RespEncoder encoder,
            RespParser parser) {
        this.inputStream = requireNonNull(inputStream);
        this.outputStream = requireNonNull(outputStream);
        this.encoder = requireNonNull(encoder);
        this.parser = requireNonNull(parser);
    }

    /**
     * Encode and send the given arguments to the Redis server
     * <p>
     * This is a blocking operation.
     *
     * @param args Command and arguments to send as strings
     * @return fluent interface
     * @throws BajaResourceException If there was an error writing to the output stream
     */
    public RedisConnection writeCommand(List<String> args) {
        IOFunction.runCommand(() -> {
            outputStream.write(encoder.encode(args));
            return null;
        });

        return this;
    }

    /**
     * Read a simple string response from the server, throwing an exception
     * if the response is not a simple string type.
     * <p>
     * This is a blocking operation.
     *
     * @return The response as a string
     * @throws BajaTypeMismatchException  If the response was not a simple string
     * @throws BajaResourceException      If there was an error reading from the stream
     * @throws BajaProtocolErrorException If the server responded with an error result
     */
    public String readSimpleString() {
        verifyResponseType(Collections.singleton(RespType.SIMPLE_STRING));
        return IOFunction.runCommand(() -> parser.readSimpleString(inputStream));
    }

    /**
     * Read a bulk string response from the server, throwing an exception
     * if the response is not a bulk string type.
     * <p>
     * This is a blocking operation.
     *
     * @return The response as a string
     * @throws BajaTypeMismatchException  If the response was not a bulk string
     * @throws BajaResourceException      If there was an error reading from the stream
     * @throws BajaProtocolErrorException If the server responded with an error result
     */
    public String readBulkString() {
        verifyResponseType(Collections.singleton(RespType.BULK_STRING));
        return IOFunction.runCommand(() -> parser.readBulkString(inputStream));
    }

    /**
     * Read a simple or bulk string response from the server, throwing an exception
     * if the result is not one of those two types.
     * <p>
     * This is a blocking operation.
     *
     * @return The response as a string
     * @throws BajaTypeMismatchException  If the response was not a simple or bulk string
     * @throws BajaResourceException      If there was an error reading from the stream
     * @throws BajaProtocolErrorException If the server responded with an error result
     */
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

    /**
     * Read a 64 bit integer response from the server, throwing an exception if the
     * result is not a 64 bit integer type.
     * <p>
     * This is a blocking operation.
     *
     * @return The response as a {@code long}
     * @throws BajaTypeMismatchException  If the response was not a 64 bit integer
     * @throws BajaResourceException      If there was an error reading from the stream
     * @throws BajaProtocolErrorException If the server responded with an error result
     */
    public long readLong() {
        verifyResponseType(Collections.singleton(RespType.INTEGER));
        return IOFunction.runCommand(() -> parser.readLong(inputStream));
    }

    /**
     * Read an "array" response from the server, throwing an exception if the result
     * is not an array type.
     * <p>
     * It is the responsibility of the caller to know the expected types of each entry
     * in the list.
     * <p>
     * This is a blocking operation.
     *
     * @return The response as a {@code List} of objects
     * @throws BajaTypeMismatchException  If the response was not an array
     * @throws BajaResourceException      If there was an error reading from the stream
     * @throws BajaProtocolErrorException If the server responded with an error result
     */
    public List<Object> readArray() {
        verifyResponseType(Collections.singleton(RespType.ARRAY));
        return IOFunction.runCommand(() -> parser.readArray(inputStream));
    }

    /**
     * Read an "array" response from the server and convert each entry to a string using
     * the default string representation ({@link String#valueOf}), throwing an exception
     * if the result is not an array type.
     * <p>
     * Null values in the array will be preserved as {@code null}s.
     * <p>
     * This is a blocking operation.
     *
     * @return The response as a {@code List} of strings
     * @throws BajaTypeMismatchException  If the response was not an array
     * @throws BajaResourceException      If there was an error reading from the stream
     * @throws BajaProtocolErrorException If the server responded with an error result
     */
    public List<String> readStringArray() {
        verifyResponseType(Collections.singleton(RespType.ARRAY));
        return IOFunction.runCommand(() -> parser.readArray(inputStream)).stream()
                .map(i -> i == null ? null : String.valueOf(i))
                .collect(Collectors.toList());
    }

    /**
     * Ensure that the type of the result in the {@link InputStream} matches one of
     * the supplied, expected types and return the actual type of the result.
     *
     * @param expected Allowed types for the result in the input stream
     * @return The actual type in the input stream
     * @throws BajaProtocolErrorException If the type in the input stream is of
     *                                    {@link RespType#ERROR}
     * @throws BajaTypeMismatchException  If the type in the input stream is not
     *                                    an error or one of the expected types.
     */
    // VisibleForTesting
    RespType verifyResponseType(Set<RespType> expected) {
        final RespType type = IOFunction.runCommand(() -> parser.findType(inputStream));

        if (type == RespType.ERROR) {
            final RespErrResponse err = IOFunction.runCommand(() -> parser.readError(inputStream));
            throw new BajaProtocolErrorException(err.getMessage());
        }

        if (!expected.contains(type)) {
            throw new BajaTypeMismatchException(
                    "Unexpected type. Expected one of " + expected + ", got " + type);
        }

        return type;
    }

    /**
     * Simple functional interface for converting closures that throw
     * {@link IOException} to our {@link BajaResourceException} exception.
     */
    @FunctionalInterface
    private interface IOFunction<R> {

        R call() throws IOException;

        static <R> R runCommand(IOFunction<R> func) {
            try {
                return func.call();
            } catch (IOException e) {
                throw new BajaResourceException(e);
            }
        }
    }
}
