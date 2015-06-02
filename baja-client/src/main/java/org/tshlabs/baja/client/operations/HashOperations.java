package org.tshlabs.baja.client.operations;

import java.util.List;

/**
 *
 */
public interface HashOperations {

    long hdel(String key, String field, String... fields);

    long hexists(String key, String field);

    String hget(String key, String field);

    List<String> hgetall(String key);

    long hincrby(String key, String field, long increment);

    String hincrbyfloat(String key, String field, float increment);

    List<String> hkeys(String key);

    long hlen(String key);

    List<String> hmget(String key, String field, String... fields);

    String hmset(String key, String field, String value, String... others);

    long hset(String key, String field, String value);

    long hsetnx(String key, String field, String value);

    long hstrlen(String key, String field);

    List<String> hvals(String key);

}
