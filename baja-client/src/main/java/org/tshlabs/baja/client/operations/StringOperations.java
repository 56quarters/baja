package org.tshlabs.baja.client.operations;

import org.tshlabs.baja.client.types.Encodable;

import java.util.List;

/**
 *
 */
public interface StringOperations {

    enum BitOperation implements Encodable {
        AND, OR, XOR, NOT;

        @Override
        public String toRepr() {
            return name();
        }
    }

    enum SetOperation implements Encodable {
        EX, PX, NX, XX;

        @Override
        public String toRepr() {
            return name();
        }
    }

    long append(String key, String value);

    long bitcount(String key);

    long bitcount(String key, long start, long end);

    long bitop(BitOperation op, String destKey, String key, String... keys);

    long bitpos(String key, int bit);

    long bitpos(String key, int bit, long start);

    long bitpos(String key, int bit, long start, long end);

    long decr(String key);

    long decrby(String key, long value);

    String get(String key);

    long getbit(String key, long offset);

    String getrange(String key, long start, long end);

    String getset(String key, String value);

    long incr(String key);

    long incrby(String key, long value);

    String incrbyfloat(String key, float value);

    List<String> mget(String key, String... keys);

    String mset(String key, String value, String... others);

    long msetnx(String key, String value, String... others);

    String psetex(String key, long expirationMillis, String value);

    String set(String key, String value);

    String set(String key, String value, SetOperation op);

    String set(String key, String value, SetOperation op, long expiration);

    long setbit(String key, long offset, int bit);

    String setex(String key, long expirationSecs, String value);

    long setnx(String key, String value);

    long setrange(String key, long offset, String value);

    long strlen(String key);
}
