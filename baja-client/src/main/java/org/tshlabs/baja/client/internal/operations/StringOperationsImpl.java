package org.tshlabs.baja.client.internal.operations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tshlabs.baja.client.internal.commands.CommandBuilder;
import org.tshlabs.baja.client.internal.commands.StringCommand;
import org.tshlabs.baja.client.internal.conn.Connection;
import org.tshlabs.baja.client.operations.StringOperations;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;
import java.util.stream.Collectors;

import static org.tshlabs.baja.client.internal.commands.CommandBuilder.command;

/**
 *
 */
@NotThreadSafe
public class StringOperationsImpl implements StringOperations {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringOperationsImpl.class);

    private final Connection connection;

    public StringOperationsImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long append(final String key, final String value) {
        connection.writeCommand(command(StringCommand.APPEND).arg(key).arg(value).build());
        return connection.readInteger();
    }

    @Override
    public long bitcount(String key) {
        connection.writeCommand(command(StringCommand.BITOP).arg(key).build());
        return connection.readInteger();
    }

    @Override
    public long bitcount(String key, long start, long end) {
        connection.writeCommand(command(StringCommand.BITOP)
                .arg(key).arg(start).arg(end).build());
        return connection.readInteger();
    }

    @Override
    public long bitop(BitOperation op, String destKey, String key, String... keys) {
        final CommandBuilder builder = command(StringCommand.BITOP)
                .arg(op).arg(destKey).arg(key);

        for (String otherKey : keys) {
            builder.arg(otherKey);
        }

        connection.writeCommand(builder.build());
        return connection.readInteger();
    }

    @Override
    public long bitpos(String key, int bit) {
        connection.writeCommand(command(StringCommand.BITPOS).arg(key).arg(bit).build());
        return connection.readInteger();
    }

    @Override
    public long bitpos(String key, int bit, long start) {
        connection.writeCommand(command(StringCommand.BITPOS)
                .arg(key).arg(bit).arg(start).build());
        return connection.readInteger();
    }

    @Override
    public long bitpos(String key, int bit, long start, long end) {
        connection.writeCommand(command(StringCommand.BITPOS)
                .arg(key).arg(bit).arg(start).arg(end).build());
        return connection.readInteger();
    }

    @Override
    public long decr(String key) {
        connection.writeCommand(command(StringCommand.DECR).arg(key).build());
        return connection.readInteger();
    }

    @Override
    public long decrby(String key, long value) {
        connection.writeCommand(command(StringCommand.DECRBY)
                .arg(key).arg(value).build());
        return connection.readInteger();
    }

    @Override
    public String get(final String key) {
        connection.writeCommand(command(StringCommand.GET).arg(key).build());
        return connection.readBulkString();
    }

    @Override
    public long getbit(String key, long offset) {
        connection.writeCommand(command(StringCommand.GETBIT)
                .arg(key).arg(offset).build());
        return connection.readInteger();
    }

    @Override
    public String getrange(String key, long start, long end) {
        connection.writeCommand(command(StringCommand.GETRANGE)
                .arg(key).arg(start).arg(end).build());
        return connection.readBulkString();
    }

    @Override
    public String getset(String key, String value) {
        connection.writeCommand(command(StringCommand.GETSET)
                .arg(key).arg(value).build());
        return connection.readBulkString();
    }

    @Override
    public long incr(String key) {
        connection.writeCommand(command(StringCommand.INCR)
                .arg(key).build());
        return connection.readInteger();
    }

    @Override
    public long incrby(String key, long value) {
        connection.writeCommand(command(StringCommand.INCRBY)
                .arg(key).arg(value).build());
        return connection.readInteger();
    }

    @Override
    public String incrbyfloat(String key, float value) {
        connection.writeCommand(command(StringCommand.INCRBYFLOAT)
                .arg(key).arg(value).build());
        return connection.readBulkString();
    }

    @Override
    public List<String> mget(String key, String... keys) {
        final CommandBuilder builder = command(StringCommand.MGET).arg(key);
        for (String otherKey : keys) {
            builder.arg(otherKey);
        }

        connection.writeCommand(builder.build());
        return connection.readArray().stream()
                .map(i -> i == null ? null : String.valueOf(i))
                .collect(Collectors.toList());
    }

    @Override
    public String mset(String key, String value, String... others) {
        final CommandBuilder builder = command(StringCommand.MSET).arg(key).arg(value);
        for (String other : others) {
            builder.arg(other);
        }

        connection.writeCommand(builder.build());
        return connection.readSimpleString();
    }

    @Override
    public long msetnx(String key, String value, String... others) {
        final CommandBuilder builder = command(StringCommand.MSETNX).arg(key).arg(value);
        for (String other : others) {
            builder.arg(other);
        }

        connection.writeCommand(builder.build());
        return connection.readInteger();
    }

    @Override
    public String psetex(String key, long expirationMillis, String value) {
        connection.writeCommand(command(StringCommand.PSETEX)
                .arg(key).arg(expirationMillis).arg(value).build());
        return connection.readSimpleString();
    }

    @Override
    public String set(String key, String value) {
        connection.writeCommand(command(StringCommand.SET).arg(value).build());
        return connection.readSimpleString();
    }

    @Override
    public String set(String key, String value, SetOperation op) {
        connection.writeCommand(command(StringCommand.SET)
                .arg(key).arg(value).arg(op).build());
        return connection.readSimpleOrBulkString();
    }

    @Override
    public String set(String key, String value, SetOperation op, long expiration) {
        connection.writeCommand(command(StringCommand.SET)
                .arg(key).arg(value).arg(op).arg(expiration).build());
        return connection.readSimpleOrBulkString();
    }

    @Override
    public long setbit(String key, long offset, int bit) {
        connection.writeCommand(command(StringCommand.SETBIT)
                .arg(key).arg(offset).arg(bit).build());
        return connection.readInteger();
    }

    @Override
    public String setex(String key, long expirationSecs, String value) {
        connection.writeCommand(command(StringCommand.SETEX)
                .arg(key).arg(expirationSecs).arg(value).build());
        return connection.readSimpleString();
    }

    @Override
    public long setnx(String key, String value) {
        connection.writeCommand(command(StringCommand.SETNX)
                .arg(key).arg(value).build());
        return connection.readInteger();
    }

    @Override
    public long setrange(String key, long offset, String value) {
        connection.writeCommand(command(StringCommand.SETRANGE)
                .arg(key).arg(offset).arg(value).build());
        return connection.readInteger();
    }

    @Override
    public long strlen(String key) {
        connection.writeCommand(command(StringCommand.STRLEN)
                .arg(key).build());
        return connection.readInteger();
    }
}
