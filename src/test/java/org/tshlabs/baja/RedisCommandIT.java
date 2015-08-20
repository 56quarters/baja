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
 *
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
                inputStream, outputStream, new RespEncoder(), new RespParser());

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
}
