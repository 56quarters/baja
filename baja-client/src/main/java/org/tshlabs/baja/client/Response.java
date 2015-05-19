package org.tshlabs.baja.client;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

/**
 *
 */
public class Response<T> implements Supplier<T> {

    private volatile Optional<T> value = Optional.empty();

    public T get() {
        return value.orElseThrow(NoSuchElementException::new);
    }

    public Response<T> set(T value) {
        this.value = Optional.ofNullable(value);
        return this;
    }

}
