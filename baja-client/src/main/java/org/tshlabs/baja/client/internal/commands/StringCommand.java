package org.tshlabs.baja.client.internal.commands;

/**
 *
 */
public enum StringCommand implements Command {
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
