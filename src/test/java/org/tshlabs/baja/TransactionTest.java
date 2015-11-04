package org.tshlabs.baja;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.tshlabs.baja.exceptions.BajaProtocolErrorException;
import org.tshlabs.baja.exceptions.BajaResourceException;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionTest {

    @Mock
    private RedisConnection connection;

    @InjectMocks
    private Transaction transaction;

    @Test
    public void testExecuteWithOneResult() {
        when(connection.readAnyType()).thenReturn("QUEUED");
        when(connection.readArray()).thenReturn(Collections.singletonList(123L));

        final Result<Long> res = RedisCommand.cmd("GET").arg("foo").queue(transaction).asLong();
        transaction.execute();

        assertEquals(123L, (long) res.get());
    }

    @Test
    public void testExecuteWithTwoResults() {
        when(connection.readAnyType())
            .thenReturn("QUEUED")
            .thenReturn("QUEUED");

        when(connection.readArray()).thenReturn(Arrays.asList(
            123L, "zip"));

        final Result<Long> res1 = RedisCommand.cmd("GET").arg("foo").queue(transaction).asLong();
        final Result<String> res2 = RedisCommand.cmd("GET").arg("baz").queue(transaction).asString();
        transaction.execute();

        assertEquals(123L, (long) res1.get());
        assertEquals("zip", res2.get());
    }

    @Test(expected = BajaProtocolErrorException.class)
    public void testExecuteBadCommand() {
        when(connection.readAnyType())
            .thenThrow(new BajaProtocolErrorException(
                "ERR: Wrong number of arguments"));

        RedisCommand.cmd("GET").arg("foo").queue(transaction).asLong();
        transaction.execute();
    }

    @Test(expected = BajaResourceException.class)
    public void testExecuteResourceException() {
        when(connection.readAnyType())
            .thenThrow(new BajaResourceException("I/O Error!"));

        RedisCommand.cmd("GET").arg("foo").queue(transaction).asLong();
        transaction.execute();
    }

    @Test(expected = IllegalStateException.class)
    public void testExecuteEofWhileWrite() {
        when(connection.writeMultiCommand(anyList()))
            .thenThrow(new IllegalStateException("EOF"));

        RedisCommand.cmd("GET").arg("foo").queue(transaction).asLong();
        transaction.execute();
    }

    @Test(expected = IllegalStateException.class)
    public void testExecuteEofWhileReadingCommandResult() {
        when(connection.readAnyType())
            .thenThrow(new IllegalStateException("EOF"));

        RedisCommand.cmd("GET").arg("foo").queue(transaction).asLong();
        transaction.execute();
    }

    @Test(expected = IllegalStateException.class)
    public void testExecuteEofWhileReadingExecResult() {
        when(connection.readAnyType())
            .thenReturn("QUEUED");

        when(connection.readArray())
            .thenThrow(new IllegalStateException("EOF"));

        RedisCommand.cmd("GET").arg("foo").queue(transaction).asLong();
        transaction.execute();
    }

}
