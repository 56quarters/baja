package org.tshlabs.baja;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A {@code Transaction} accepts {@link RedisCommand} instances and queued
 * (future) {@link Result} instances to be later executed in the context of
 * a Redis transaction.
 * <p>
 * The transaction does not have an {@code .abort()} method since all commands
 * are executed at once. If a caller does not want to execute already queued
 * commands, simply don't call the {@link #execute()} method.
 * <p>
 * This class is <em>not</em> thread safe.
 *
 * @see <a href="http://redis.io/commands#transactions">Redis Transactions</a>
 */
public class Transaction {

    private final RedisConnection connection;
    private final List<RedisCommand> queuedCommands = new ArrayList<>();
    private final List<Result<?>> queuedResults = new ArrayList<>();

    /**
     * Construct a new transaction instance that will make use of the given connection.
     *
     * @param connection Connection for executing this transaction
     * @throws NullPointerException If connection is null
     */
    Transaction(RedisConnection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    /**
     * Queue the given command for future execution along with a holder for the result.
     *
     * @param command Command to execute inside a transaction
     * @param result  Holder for the result of the command once the transaction executes
     * @param <V>     Type of the result of the command
     * @return fluent interface
     */
    <V> Transaction queue(RedisCommand command, Result<V> result) {
        queuedCommands.add(Objects.requireNonNull(command));
        queuedResults.add(Objects.requireNonNull(result));
        return this;
    }

    /*
     * Build a list of commands that wrap the queued RedisCommand instances
     * in a Redis transaction.
     */
    private static List<List<String>> getTransactionCommands(List<RedisCommand> queued) {
        final List<List<String>> commands = new ArrayList<>();

        commands.add(Collections.singletonList("MULTI"));
        commands.addAll(queued.stream()
            .map(RedisCommand::getArgs)
            .collect(Collectors.toList()));
        commands.add(Collections.singletonList("EXEC"));

        return commands;
    }

    /*
     * Execute the given commands as a transaction, discarding all results
     * except the last one (the "EXEC" command) and returning the output
     * of it as a list.
     */
    private static List<Object> getTransactionResults(RedisConnection connection, List<List<String>> commands) {
        final int resultsToDiscard = commands.size() - 1;
        connection.writeMultiCommand(commands);

        // Discard the output from starting the transaction and the "QUEUED"
        // response after every command that's run. All the results will be
        // returned as output from the "EXEC" command, that's the only one we
        // care about.
        for (int i = 0; i < resultsToDiscard; i++) {
            connection.readAnyType();
        }

        return connection.readArray();
    }

    /**
     * Begin a Redis transaction, execute each of the queued commands, commit the
     * transaction, and populate the associated {@link Result} instances.
     *
     * @throws org.tshlabs.baja.exceptions.BajaResourceException      If there was an I/O error
     *                                                                executing the transaction
     * @throws org.tshlabs.baja.exceptions.BajaProtocolErrorException If the Redis server responded
     *                                                                with an error for any
     *                                                                commands in the transaction
     */
    public void execute() {
        final List<List<String>> commands = getTransactionCommands(queuedCommands);
        final List<Object> response = getTransactionResults(connection, commands);

        final Iterator<Object> responseIt = response.listIterator();
        final Iterator<Result<?>> resultsIt = queuedResults.listIterator();

        while (responseIt.hasNext() && resultsIt.hasNext()) {
            final Result<?> resultItem = resultsIt.next();
            final Object responseItem = responseIt.next();
            resultItem.setValue(responseItem);
        }
    }
}
