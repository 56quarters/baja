package org.tshlabs.baja.client.internal.operations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tshlabs.baja.client.internal.Connection;
import org.tshlabs.baja.client.internal.commands.CommandBuilder;
import org.tshlabs.baja.client.internal.commands.StringCommand;
import org.tshlabs.baja.client.operations.StringOperations;

import javax.annotation.concurrent.NotThreadSafe;

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
        connection.writeCommand(command(StringCommand.BITOP).build());
        return connection.readInteger();
    }

    @Override
    public long bitcount(String key, long start, long end) {
        connection.writeCommand(command(StringCommand.BITOP).arg(start).arg(end).build());
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
    public String get(final String key) {
        connection.writeCommand(command(StringCommand.GET).arg(key).build());
        return connection.readBulkString();
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
    public String set(String key, String value, SetOperation op, int expiration) {
        connection.writeCommand(command(StringCommand.SET)
                .arg(key).arg(value).arg(op).arg(expiration).build());
        return connection.readSimpleOrBulkString();
    }
}
