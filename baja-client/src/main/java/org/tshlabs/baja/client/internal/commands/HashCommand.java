package org.tshlabs.baja.client.internal.commands;

import org.tshlabs.baja.client.types.Encodable;

/**
 *
 */
public enum HashCommand implements Encodable {
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
