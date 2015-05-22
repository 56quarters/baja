package org.tshlabs.baja.client.internal.commands;

import org.tshlabs.baja.client.types.Encodable;

/**
 *
 */
public enum StringCommand implements Encodable {
    APPEND,
    BITCOUNT,
    BITOP,
    BITPOS,
    DECR,
    DECRBY,
    GET,
    GETBIT,
    GETRANGE,
    GETSET,
    INCR,
    INCRBY,
    INCRBYFLOAT,
    MGET,
    MSET,
    MSETNX,
    PSETEX,
    SET,
    SETBIT,
    SETEX,
    SETNX,
    SETRANGE,
    STRLEN;

    @Override
    public String toRepr() {
        return name();
    }
}
