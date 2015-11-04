package org.tshlabs.baja;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Holder for the future result of a {@link RedisCommand} that has been queued for
 * execution as part of a transaction.
 * <p>
 * This class is thread safe.
 */
public class Result<V> {

    /**
     * Reference to a potentially null {@link Optional} instance that acts as a
     * holder for the result of a command queued as part of a transaction.
     * <p>
     * The reference being null represents the transaction not being executed yet.
     * The reference being {@code Optional.empty()} represents the transaction being
     * executed but the result of the command being null.
     */
    private final AtomicReference<Optional<V>> ref;

    public Result() {
        this.ref = new AtomicReference<>();
    }

    // VisibleForTesting
    Result(AtomicReference<Optional<V>> ref) {
        this.ref = ref;
    }

    /**
     * Set the result of the queued command to the given value
     *
     * @param value The nullable value to set as the command result
     * @throws IllegalStateException If the value has already been set
     */
    @SuppressWarnings("unchecked")
    void setValue(Object value) {
        if (!this.ref.compareAndSet(null, Optional.ofNullable((V) value))) {
            throw new IllegalStateException("Transaction has already been executed");
        }
    }

    /**
     * @return True if this result is available, false otherwise
     */
    public boolean isDone() {
        return ref.get() != null;
    }

    /**
     * @return The result of the executed command if available
     * @throws IllegalStateException If the transaction has not been executed
     */
    public V get() {
        final Optional<V> val = ref.get();
        if (val == null) {
            throw new IllegalStateException("Transaction has not been executed");
        }

        return val.orElse(null);
    }

}
