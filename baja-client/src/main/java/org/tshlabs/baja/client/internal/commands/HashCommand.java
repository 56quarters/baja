package org.tshlabs.baja.client.internal.commands;

/**
 *
 */
public enum HashCommand implements Command {
    HDEL,
    HEXISTS,
    HGET,
    HGETALL,
    HINCRBY,
    HINCRBYFLOAT,
    HKEYS,
    HLEN,
    HMGET,
    HMSET,
    HSET,
    HSETNX,
    HSTRLEN,
    HVALS,
    HSCAN;

    @Override
    public String toRepr() {
        return name();
    }
}
