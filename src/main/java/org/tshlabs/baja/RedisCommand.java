package org.tshlabs.baja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Class for assembling a command and sequence arguments to send to a Redis server
 * and read the results using a {@link RedisConnection} instance.
 *
 * This class is <em>not</em> thread safe.
 */
public class RedisCommand {

    private final List<String> args = new ArrayList<>();

    private RedisCommand(String command) {
        this.args.add(Objects.requireNonNull(command));
    }

    /**
     * Create a new {@link RedisCommand} instance for building a command
     * and arguments to send to the Redis server.
     *
     * @param command Redis command that we are executing
     * @return Builder for sending a command and arguments to the Redis server
     * @see <a href="http://redis.io/commands">Redis commands</a>
     */
    public static RedisCommand cmd(String command) {
        return new RedisCommand(command);
    }

    /**
     * Append a {@code String} argument to the base Redis command.
     *
     * @param arg Argument to append to the command
     * @return fluent interface
     */
    public RedisCommand arg(String arg) {
        this.args.add(arg);
        return this;
    }

    /**
     * Append a {@code long} argument to the base Redis command.
     *
     * @param arg Argument to append to the command
     * @return fluent interface
     */
    public RedisCommand arg(long arg) {
        this.args.add(String.valueOf(arg));
        return this;
    }

    /**
     * Append an {@code int} argument to the base Redis command.
     *
     * @param arg Argument to append to the command
     * @return fluent interface
     */
    public RedisCommand arg(int arg) {
        this.args.add(String.valueOf(arg));
        return this;
    }

    /**
     * Append a {@code boolean} argument to the base Redis command.
     *
     * @param arg Argument to append to the command
     * @return fluent interface
     */
    public RedisCommand arg(boolean arg) {
        this.args.add(arg ? "1" : "0");
        return this;
    }

    /**
     * Append a {@code float} argument to the base Redis command.
     *
     * @param arg Argument to append to the command
     * @return fluent interface
     */
    public RedisCommand arg(float arg) {
        this.args.add(String.valueOf(arg));
        return this;
    }

    /**
     * Append a {@code double} argument to the base Redis command.
     *
     * @param arg Argument to append to the command
     * @return fluent interface
     */
    public RedisCommand arg(double arg) {
        this.args.add(String.valueOf(arg));
        return this;
    }

    /**
     * Get an immutable view of the arguments comprising this Redis command.
     *
     * @return Arguments that will passed to the Redis connection
     */
    public List<String> getArgs() {
        return Collections.unmodifiableList(args);
    }

    /**
     * Use the given {@link RedisConnection} to send a command to the Redis
     * server and return a facade for reading the results of the command.
     *
     * @param conn Connection to use for sending commands to the Redis server
     * @return Wrapper for reading results of the command executed.
     */
    public ExecutedRedisCommand query(RedisConnection conn) {
        return new ExecutedRedisCommand(conn, this);
    }

    /**
     * Class that represents the result of executing a previously constructed
     * {@link RedisCommand} instance using a {@link RedisConnection} instance.
     */
    public static class ExecutedRedisCommand {
        private final RedisConnection connection;

        private ExecutedRedisCommand(RedisConnection connection, RedisCommand cmd) {
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
    }
}
