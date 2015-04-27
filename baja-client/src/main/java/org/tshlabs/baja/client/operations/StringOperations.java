package org.tshlabs.baja.client.operations;

/**
 *
 */
public interface StringOperations {

    enum BitOperation {
        AND, OR, XOR, NOT
    }

    enum SetOperation {
        EX, PX, NX, XX
    }

    int append(String key, String value);

    int bitcount(String key);

    int bitcount(String key, int start, int end);

    int bitop(BitOperation op, String destKey, String key, String... keys);


    String get(String key);

    String set(String key, String value);

    String set(String key, String value, SetOperation op);

    String set(String key, String value, SetOperation op, int expiration);
}
