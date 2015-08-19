package org.tshlabs.baja;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class RedisCommandTest {

    @Test
    public void testSingleArg() {
        final RedisCommand cmd = RedisCommand.cmd("INFO");
        assertEquals(Arrays.asList("INFO"), cmd.getArgs());
    }

    @Test
    public void testArgString() {
        final RedisCommand cmd = RedisCommand.cmd("GET").arg("foo");
        assertEquals(Arrays.asList("GET", "foo"), cmd.getArgs());
    }

    @Test
    public void testArgLong() {
        final RedisCommand cmd = RedisCommand.cmd("SETEX").arg("foo").arg(123L);
        assertEquals(Arrays.asList("SETEX", "foo", "123"), cmd.getArgs());
    }

    @Test
    public void testArgInt() {
        final RedisCommand cmd = RedisCommand.cmd("SETEX").arg("foo").arg(123);
        assertEquals(Arrays.asList("SETEX", "foo", "123"), cmd.getArgs());
    }

    @Test
    public void testArgFloat() {
        final RedisCommand cmd = RedisCommand.cmd("INCRBYFLOAT").arg("foo").arg(3.14);
        assertEquals(Arrays.asList("INCRBYFLOAT", "foo", "3.14"), cmd.getArgs());
    }

    @Test
    public void testArgDouble() {
        final RedisCommand cmd = RedisCommand.cmd("INCRBYFLOAT").arg("foo").arg(3.14D);
        assertEquals(Arrays.asList("INCRBYFLOAT", "foo", "3.14"), cmd.getArgs());
    }

    @Test
    public void testQuery() {
        final RedisConnection conn = mock(RedisConnection.class);
        final RedisCommand.ExecutedRedisCommand res = RedisCommand.cmd("SET")
                .arg("foo")
                .arg(123)
                .query(conn);

        verify(conn).writeCommand(eq(Arrays.asList("SET", "foo", "123")));
    }


}
