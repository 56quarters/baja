package org.tshlabs.baja.client.internal.operations;

import org.tshlabs.baja.client.operations.HashOperations;

import java.util.List;

/**
 *
 */
public class HashOperationsImpl implements HashOperations {
    @Override
    public long hdel(String key, String field, String... fields) {
        return 0;
    }

    @Override
    public long hexists(String key, String field) {
        return 0;
    }

    @Override
    public String hget(String key, String field) {
        return null;
    }

    @Override
    public List<String> hgetall(String key) {
        return null;
    }

    @Override
    public long hincrby(String key, String field, long increment) {
        return 0;
    }

    @Override
    public String hincrbyfloat(String key, String field, float increment) {
        return null;
    }

    @Override
    public List<String> hkeys(String key) {
        return null;
    }

    @Override
    public long hlen(String key) {
        return 0;
    }

    @Override
    public List<String> hmget(String key, String field, String... fields) {
        return null;
    }

    @Override
    public String hmset(String key, String field, String value, String... others) {
        return null;
    }

    @Override
    public long hset(String key, String field, String value) {
        return 0;
    }

    @Override
    public long hsetnx(String key, String field, String value) {
        return 0;
    }

    @Override
    public long hstrlen(String key, String field) {
        return 0;
    }

    @Override
    public List<String> hvals(String key) {
        return null;
    }
}
