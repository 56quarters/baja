package org.tshlabs.baja;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tshlabs.baja.protocol.RespEncoder;
import org.tshlabs.baja.protocol.RespParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Basic integration test that talks to a local Redis instance
 * to ensure that we can select a database, get, and set keys.
 * <p>
 * NOTE: that this test will perform destructive operations in
 * the local Redis database since it expects to be running on a
 * development or CI machine.
 */
public class RedisCommandIT {

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private RedisConnection connection;

    @Before
    public void setup() throws IOException {
        final Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/test.properties"));

        final String redisHost = properties.getProperty("test.redis.host");
        final Integer redisPort = Integer.valueOf(properties.getProperty("test.redis.port"));
        final Integer redisDatabase = Integer.valueOf(properties.getProperty("test.redis.database"));

        this.socket = new Socket(redisHost, redisPort);
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();

        this.connection = new RedisConnection(
                inputStream, outputStream, RespEncoder.getInstance(), RespParser.getInstance());

        RedisCommand.cmd("SELECT")
                .arg(redisDatabase)
                .query(this.connection)
                .discard();
    }

    @After
    public void teardown() {
        try {
            this.outputStream.close();
        } catch (IOException e) {
            // nothing
        }

        try {
            this.inputStream.close();
        } catch (IOException e) {
            // nothing
        }

        try {
            this.socket.close();
        } catch (IOException e) {
            // nothing
        }
    }

    @Test
    public void testGetNonExistent() {
        assertNull(RedisCommand.cmd("GET").arg("foo").query(connection).asString());
    }

    @Test
    public void testGetExistent() {
        assertEquals("OK", RedisCommand.cmd("SET").arg("baz").arg("bing").query(connection).asString());
        assertEquals("bing", RedisCommand.cmd("GET").arg("baz").query(connection).asString());
    }

    @Test
    public void testMultiGet() {
        final Transaction transaction = connection.transaction();
        final Result<String> r1 = RedisCommand.cmd("SET").arg("k1").arg("v1").queue(transaction).asString();
        final Result<String> r2 = RedisCommand.cmd("SET").arg("k2").arg("v2").queue(transaction).asString();
        final Result<String> r3 = RedisCommand.cmd("GET").arg("k1").queue(transaction).asString();
        final Result<String> r4 = RedisCommand.cmd("GET").arg("k2").queue(transaction).asString();
        transaction.execute();

        assertEquals("OK", r1.get());
        assertEquals("OK", r2.get());
        assertEquals("v1", r3.get());
        assertEquals("v2", r4.get());
    }
}
