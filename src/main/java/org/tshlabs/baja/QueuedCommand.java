package org.tshlabs.baja;

import java.util.List;
import java.util.Objects;

/**
 * Class that represents the result of queuing a previously constructed
 * {@link RedisCommand} instance using a {@link Transaction} instance to
 * be executed in the future as part of a Redis transaction.
 * <p>
 * This class is <em>not</em> thread safe.
 *
 * @see <a href="http://redis.io/commands#transactions">Redis Transactions</a>
 */
public class QueuedCommand {

    private final Transaction transaction;
    private final RedisCommand command;

    QueuedCommand(Transaction transaction, RedisCommand command) {
        this.transaction = Objects.requireNonNull(transaction);
        this.command = Objects.requireNonNull(command);
    }

    /**
     * Get a holder for the future results of the queued command as a Java
     * {@code String}, which may be a "simple string" or "bulk string" on the
     * Redis Server side.
     *
     * @return Holder for queued command results as a string
     */
    public Result<String> asString() {
        final Result<String> res = new Result<>();
        transaction.queue(command, res);
        return res;
    }

    /**
     * Get a holder for the future results of the queued command as a Java
     * {@code long}, which corresponds to the "integer" type on the Redis Server side.
     *
     * @return Holder for queued command results as a long
     */
    public Result<Long> asLong() {
        final Result<Long> res = new Result<>();
        transaction.queue(command, res);
        return res;
    }

    /**
     * Get a holder for the future results of the queued command as a {@code List}
     * of objects.
     * <p>
     * The actual types of the objects is undefined and should be known by the caller.
     *
     * @return Holder for queued command results as a list
     */
    public Result<List<Object>> asArray() {
        final Result<List<Object>> res = new Result<>();
        transaction.queue(command, res);
        return res;
    }

    /**
     * Get a holder for the future results of a queued command as a {@code List} of
     * {@code String}s.
     * <p>
     * Each entry in the list of results will be converted to a {@code String} from whatever
     * its original type was using {@code String#valueOf}. Null values will be preserved.
     *
     * @return Holder for queued command results as a list of strings
     */
    public Result<List<String>> asStringArray() {
        final Result<List<String>> res = new Result<>();
        transaction.queue(command, res);
        return res;
    }

    /**
     * Get a holder for future results of a queued command as an {@code Object}.
     * <p>
     * This may be useful for Redis commands that return multiple types based on arguments
     * supplied to the commands. In this case, it is up to the caller of this method to
     * inspect the results and determine the correct course of action.
     *
     * @return Holder for queued command results as an object.
     */
    public Result<Object> asObject() {
        final Result<Object> res = new Result<>();
        transaction.queue(command, res);
        return res;
    }

    /**
     * Discard the future results of a queued command.
     */
    public void discard() {
        transaction.queue(command, new Result<>());
    }
}
