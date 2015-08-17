package org.tshlabs.baja;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class RedisCommandTest {

    private static <T> List<T> newList(T... things) {
        final List<T> out = new ArrayList<>();
        Collections.addAll(out, things);
        return out;
    }

    @Test
    public void testSingleArg() {
        final RedisCommand cmd = RedisCommand.cmd("INFO");
        assertEquals(newList("INFO"), cmd.getArgs());
    }

    @Test
    public void testArgString() {
        final RedisCommand cmd = RedisCommand.cmd("GET").arg("foo");
        assertEquals(newList("GET", "foo"), cmd.getArgs());
    }

    @Test
    public void testArgLong() {
        final RedisCommand cmd = RedisCommand.cmd("SETEX").arg("foo").arg(123L);
        assertEquals(newList("SETEX", "foo", "123"), cmd.getArgs());
    }

    @Test
    public void testArgInt() {
        final RedisCommand cmd = RedisCommand.cmd("SETEX").arg("foo").arg(123);
        assertEquals(newList("SETEX", "foo", "123"), cmd.getArgs());
    }

    @Test
    public void testArgFloat() {
        final RedisCommand cmd = RedisCommand.cmd("INCRBYFLOAT").arg("foo").arg(3.14);
        assertEquals(newList("INCRBYFLOAT", "foo", "3.14"), cmd.getArgs());
    }

    @Test
    public void testArgDouble() {
        final RedisCommand cmd = RedisCommand.cmd("INCRBYFLOAT").arg("foo").arg(3.14D);
        assertEquals(newList("INCRBYFLOAT", "foo", "3.14"), cmd.getArgs());
    }

    @Test
    public void testQuery() {
        final RedisConnection conn = mock(RedisConnection.class);
        final RedisCommand.ExecutedRedisCommand res = RedisCommand.cmd("SET")
                .arg("foo")
                .arg(123)
                .query(conn);

        verify(conn).writeCommand(eq(newList("SET", "foo", "123")));
    }
}
