package org.tshlabs.baja.client.internal.operations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tshlabs.baja.client.operations.StringOperations;

/**
 *
 */
public class StringOperationsImpl implements StringOperations {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringOperationsImpl.class);

    @Override
    public int append(String key, String value) {
        return 0;
    }

    @Override
    public int bitcount(String key) {
        return 0;
    }

    @Override
    public int bitcount(String key, int start, int end) {
        return 0;
    }

    @Override
    public int bitop(BitOperation op, String destKey, String key, String... keys) {
        return 0;
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public String set(String key, String value) {
        return null;
    }

    @Override
    public String set(String key, String value, SetOperation op) {
        return null;
    }

    @Override
    public String set(String key, String value, SetOperation op, int expiration) {
        return null;
    }
}
