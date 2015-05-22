package org.tshlabs.baja.client.internal;

import org.tshlabs.baja.client.exceptions.BajaResourceException;

import java.io.IOException;

/**
 *
 */
@FunctionalInterface
public interface IOFunction<R> {

    R call() throws IOException;

    static <R> R runCommand(IOFunction<R> func) {
        try {
            return func.call();
        } catch (IOException e) {
            throw new BajaResourceException(e);
        }
    }
}
