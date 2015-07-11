package org.tshlabs.baja;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 */
public class RedisCommand {

    private final List<String> args = new ArrayList<>();

    private RedisCommand(String command) {
        this.args.add(Objects.requireNonNull(command));
    }

    public static RedisCommand cmd(String command) {
        return new RedisCommand(command);
    }

    public RedisCommand arg(String arg) {
        this.args.add(arg);
        return this;
    }

    public RedisCommand arg(long arg) {
        this.args.add(String.valueOf(arg));
        return this;
    }

    public RedisCommand arg(int arg) {
        this.args.add(String.valueOf(arg));
        return this;
    }

    public RedisCommand arg(boolean arg) {
        this.args.add(arg ? "1" : "0");
        return this;
    }

    public RedisCommand arg(float arg) {
        this.args.add(String.valueOf(arg));
        return this;
    }

    public RedisCommand arg(double arg) {
        this.args.add(String.valueOf(arg));
        return this;
    }

    public ExecutedRedisCommand query(RedisConnection conn) {
        conn.writeCommand(new ArrayList<>(args));
        return new ExecutedRedisCommand(conn);
    }

    public static class ExecutedRedisCommand {
        private final RedisConnection connection;

        private ExecutedRedisCommand(RedisConnection connection) {
            this.connection = Objects.requireNonNull(connection);
        }

        public String asString() {
            return connection.readSimpleOrBulkString();
        }

        public long asLong() {
            return connection.readLong();
        }

        public List<Object> asArray() {
            return connection.readArray();
        }

        public List<String> asStringArray() {
            return connection.readStringArray();
        }
    }
}
