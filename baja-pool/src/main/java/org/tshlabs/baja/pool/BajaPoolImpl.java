package org.tshlabs.baja.pool;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 */
public class BajaPoolImpl<T> implements BajaPool<T> {

    private final Object lock = new Object();

    private final List<T> resources = new LinkedList<>();

    private final BajaPoolConfig<T> config;

    public BajaPoolImpl(BajaPoolConfig<T> config) {
        if (config == null) {
            throw new NullPointerException();
        }

        this.config = config;
    }

    @Override
    public T borrowResource() {
        final Supplier<T> supplier = config.getResourceSupplier();
        return supplier.get();
    }

    @Override
    public void returnResource(T resource) {
        returnBrokenResource(resource);
    }

    @Override
    public void returnBrokenResource(T resource) {
        final Function<T, Boolean> cleanup = config.getResourceCleanup();
        cleanup.apply(resource);
    }
}
