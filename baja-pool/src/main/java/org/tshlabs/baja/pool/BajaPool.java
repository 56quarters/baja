package org.tshlabs.baja.pool;

/**
 *
 */
public interface BajaPool<T> {

    T borrowResource();

    void returnResource(T resource);

    void returnBrokenResource(T resource);
}
