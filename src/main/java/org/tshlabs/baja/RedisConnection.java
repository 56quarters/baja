package org.tshlabs.baja;

import java.util.List;

/**
 *
 */
public interface RedisConnection {
    RedisConnection writeCommand(List<String> args);

    String readSimpleString();

    String readBulkString();

    String readSimpleOrBulkString();

    long readInteger();

    List<Object> readArray();

    List<String> readStringArray();
}
