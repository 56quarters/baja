package org.tshlabs.baja;

import java.util.List;
import java.util.Objects;

/**
 * Class that represents the result of executing a previously constructed
 * {@link RedisCommand} instance using a {@link RedisConnection} instance.
 * <p>
 * This class is <em>not</em> thread safe.
 */
public class ExecutedCommand {
    private final RedisConnection connection;

    ExecutedCommand(RedisConnection connection, RedisCommand cmd) {
        this.connection = Objects.requireNonNull(connection);
        this.connection.writeCommand(Objects.requireNonNull(cmd.getArgs()));
    }

    /**
     * Get the results of the executed command as a Java {@code String}, which
     * may be a "simple string" or "bulk string" on the Redis Server side.
     *
     * @return Command results as a string
     * @throws org.tshlabs.baja.exceptions.BajaTypeMismatchException  If the result of the command
     *                                                                was not a string type
     * @throws org.tshlabs.baja.exceptions.BajaResourceException      If there was an I/O error
     * @throws org.tshlabs.baja.exceptions.BajaProtocolErrorException If the Redis server responded
     *                                                                with an error
     */
    public String asString() {
        return connection.readSimpleOrBulkString();
    }

    /**
     * Get the results of the executed command as a Java {@code long}, which
     * corresponds to the "integer" type on the Redis Server side.
     *
     * @return Command results as a long
     * @throws org.tshlabs.baja.exceptions.BajaTypeMismatchException  If the result of the command
     *                                                                was not a long type
     * @throws org.tshlabs.baja.exceptions.BajaResourceException      If there was an I/O error
     * @throws org.tshlabs.baja.exceptions.BajaProtocolErrorException If the Redis server responded
     *                                                                with an error
     */
    public long asLong() {
        return connection.readLong();
    }

    /**
     * Get the results of the executed command as a {@code List} of objects.
     * <p>
     * The actual types of the objects is undefined and should be known by the caller.
     *
     * @return Command results as a list
     * @throws org.tshlabs.baja.exceptions.BajaTypeMismatchException  If the result of the command
     *                                                                was not an array type
     * @throws org.tshlabs.baja.exceptions.BajaResourceException      If there was an I/O error
     * @throws org.tshlabs.baja.exceptions.BajaProtocolErrorException If the Redis server responded
     *                                                                with an error
     */
    public List<Object> asArray() {
        return connection.readArray();
    }

    /**
     * Get the results of the executed command as a {@code List} of {@code String}s.
     * <p>
     * Each entry in the list of results is converted to a {@code String} from whatever
     * its original type was using {@code String#valueOf}. Null values will be preserved.
     *
     * @return Command results as a list of strings
     * @throws org.tshlabs.baja.exceptions.BajaTypeMismatchException  If the result of the command
     *                                                                was not an array type
     * @throws org.tshlabs.baja.exceptions.BajaResourceException      If there was an I/O error
     * @throws org.tshlabs.baja.exceptions.BajaProtocolErrorException If the Redis server responded
     *                                                                with an error
     */
    public List<String> asStringArray() {
        return connection.readStringArray();
    }

    /**
     * Get the results of the executed command as an {@code Object}.
     * <p>
     * This may be useful for Redis commands that return multiple types based on arguments
     * supplied to the commands. In this case, it is up to the caller of this method to
     * inspect the results and determine the correct course of action.
     *
     * @return Command results as an object.
     * @throws org.tshlabs.baja.exceptions.BajaResourceException      If there was an I/O error
     * @throws org.tshlabs.baja.exceptions.BajaProtocolErrorException If the Redis server responded
     *                                                                with an error
     */
    public Object asObject() {
        return connection.readAnyType();
    }

    /**
     * Read and then immediately discard the results of an executed command.
     * <p>
     * Note that if the result of the command was an error from the Redis server
     * this will still be raised as an {@link org.tshlabs.baja.exceptions.BajaProtocolErrorException
     * exception}.
     *
     * @throws org.tshlabs.baja.exceptions.BajaResourceException      If there was an I/O error
     * @throws org.tshlabs.baja.exceptions.BajaProtocolErrorException If the Redis server responded
     *                                                                with an error
     */
    public void discard() {
        connection.readAnyType();
    }
}
