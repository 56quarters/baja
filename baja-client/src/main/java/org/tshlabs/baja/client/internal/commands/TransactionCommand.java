package org.tshlabs.baja.client.internal.commands;

import org.tshlabs.baja.client.types.Encodable;

/**
 *
 */
public enum TransactionCommand implements Encodable {
    DISCARD,
    EXEC,
    MULTI,
    UNWATCH,
    WATCH;

    @Override
    public String toRepr() {
        return name();
    }
}
