package org.tshlabs.baja.client;

import org.tshlabs.baja.client.internal.operations.StringOperationsImpl;
import org.tshlabs.baja.client.operations.HashOperations;
import org.tshlabs.baja.client.operations.StringOperations;

/**
 *
 */
public class BajaClientImpl implements BajaClient {

    @Override
    public HashOperations getHashOperations() {
        return null;
    }

    @Override
    public StringOperations getStringOperations() {
        return null;
    }

    public static void main(String[] args) {
        final StringOperations ops = new StringOperationsImpl();
        ops.get("blah");
    }
}
