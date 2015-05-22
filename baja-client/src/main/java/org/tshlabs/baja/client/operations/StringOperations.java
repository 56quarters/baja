package org.tshlabs.baja.client.operations;

import org.tshlabs.baja.client.types.Encodable;

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


    String get(String key);

    String set(String key, String value);

    String set(String key, String value, SetOperation op);

    String set(String key, String value, SetOperation op, int expiration);
}
