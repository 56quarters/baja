package org.tshlabs.baja;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Class for assembling a command and sequence arguments to send to a Redis server
 * and read the results using a {@link RedisConnection} instance.
 * <p>
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
     * @param connection Connection to use for sending commands to the Redis server
     * @return Wrapper for reading results of the command executed.
     */
    public ExecutedCommand query(RedisConnection connection) {
        return new ExecutedCommand(Objects.requireNonNull(connection), this);
    }

    /**
     * Use the given {@link Transaction} to queue a command for later execution
     * in the context of Redis transaction and return a facade for reading the
     * {@link Result future results} of the command.
     *
     * @param transaction Transaction for executing commands in the future
     * @return Wrapper for reading queued results of the command
     */
    public QueuedCommand queue(Transaction transaction) {
        return new QueuedCommand(Objects.requireNonNull(transaction), this);
    }

}
