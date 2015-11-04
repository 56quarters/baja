package org.tshlabs.baja;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.tshlabs.baja.exceptions.BajaProtocolErrorException;
import org.tshlabs.baja.exceptions.BajaResourceException;
import org.tshlabs.baja.exceptions.BajaTypeMismatchException;
import org.tshlabs.baja.protocol.RespEncoder;
import org.tshlabs.baja.protocol.RespErrResponse;
import org.tshlabs.baja.protocol.RespParser;
import org.tshlabs.baja.protocol.RespType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RedisConnectionTest {

    @Mock
    private InputStream inputStream;

    @Mock
    private OutputStream outputStream;

    @Mock
    private RespEncoder encoder;

    @Mock
    private RespParser parser;

    @InjectMocks
    private RedisConnection connection;

    @Test
    public void testWriteCommandSingleItem() {
        final List<String> cmd = Collections.singletonList("COMMAND");
        connection.writeCommand(cmd);
        verify(encoder).encodeMulti(Collections.singletonList(cmd));
    }

    @Test
    public void testWriteCommandMultipleItems() {
        final List<String> cmd = new ArrayList<>();
        cmd.add("SET");
        cmd.add("x");
        cmd.add("5");

        connection.writeCommand(cmd);
        verify(encoder).encodeMulti(Collections.singletonList(cmd));
    }

    @Test(expected = BajaResourceException.class)
    public void testWriteCommandIOException() throws IOException {
        doThrow(IOException.class).when(outputStream).write(any(byte[].class));
        final List<String> cmd = Collections.singletonList("INFO");
        connection.writeCommand(cmd);
    }

    @Test
    public void testReadSimpleStringValid() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.SIMPLE_STRING);
        when(parser.readSimpleString(inputStream)).thenReturn("OK");
        assertEquals("OK", connection.readSimpleString());
    }

    @Test(expected = BajaResourceException.class)
    public void testReadSimpleStringIOException() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.SIMPLE_STRING);
        when(parser.readSimpleString(inputStream)).thenThrow(IOException.class);
        connection.readSimpleString();
    }

    @Test
    public void testReadBulkStringValid() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.BULK_STRING);
        when(parser.readBulkString(inputStream)).thenReturn("This\r\nis\r\nbulk");
        assertEquals("This\r\nis\r\nbulk", connection.readBulkString());
    }

    @Test(expected = BajaResourceException.class)
    public void testReadBulkStringIOException() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.BULK_STRING);
        when(parser.readBulkString(inputStream)).thenThrow(IOException.class);
        connection.readBulkString();
    }

    @Test
    public void testReadSimpleOrBulkStringSimple() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.SIMPLE_STRING);
        when(parser.readSimpleString(inputStream)).thenReturn("OK");
        assertEquals("OK", connection.readSimpleOrBulkString());
    }

    @Test
    public void testReadSimpleOrBulkStringBulk() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.BULK_STRING);
        when(parser.readBulkString(inputStream)).thenReturn("OK\r\nOK");
        assertEquals("OK\r\nOK", connection.readSimpleOrBulkString());
    }

    @Test(expected = BajaResourceException.class)
    public void testReadSimpleOrBulkStringIOException() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.BULK_STRING);
        when(parser.readBulkString(inputStream)).thenThrow(IOException.class);
        connection.readSimpleOrBulkString();
    }

    @Test
    public void testReadAnyTypeSuccessSimpleString() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.SIMPLE_STRING);
        when(parser.readSimpleString(inputStream)).thenReturn("OK");
        assertEquals("OK", connection.readAnyType());
    }

    @Test
    public void testReadAnyTypeSuccessBulkString() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.BULK_STRING);
        when(parser.readBulkString(inputStream)).thenReturn("foo");
        assertEquals("foo", connection.readAnyType());
    }

    @Test
    public void testReadAnyTypeSuccessLong() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.INTEGER);
        when(parser.readLong(inputStream)).thenReturn(6458L);
        assertEquals(6458L, connection.readAnyType());
    }

    @Test
    public void testReadAnyTypeSuccessArray() throws IOException {
        final List<Object> res = new ArrayList<>();
        res.add("It's");
        res.add("Tricky");

        when(parser.findType(inputStream)).thenReturn(RespType.ARRAY);
        when(parser.readArray(inputStream)).thenReturn(res);

        // Suppressing because this will either work or we've got a bug
        @SuppressWarnings("unchecked")
        final List<Object> arrayRes = (List<Object>) connection.readAnyType();
        assertEquals("It's", arrayRes.get(0));
        assertEquals("Tricky", arrayRes.get(1));
    }

    @Test(expected = BajaResourceException.class)
    public void testReadAnyTypeIOException() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.ARRAY);
        when(parser.readArray(inputStream)).thenThrow(IOException.class);
        connection.readAnyType();
    }

    @Test
    public void testReadLongSuccess() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.INTEGER);
        when(parser.readLong(inputStream)).thenReturn(6458L);
        assertEquals(6458L, connection.readLong());
    }

    @Test(expected = BajaResourceException.class)
    public void testReadLongIOException() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.INTEGER);
        when(parser.readLong(inputStream)).thenThrow(IOException.class);
        connection.readLong();
    }

    @Test
    public void testReadArraySuccess() throws IOException {
        final List<Object> res = new ArrayList<>();
        res.add(123L);
        res.add("OK");
        res.add("Something\r\nblah");

        when(parser.findType(inputStream)).thenReturn(RespType.ARRAY);
        when(parser.readArray(inputStream)).thenReturn(res);

        final List<Object> arrayRes = connection.readArray();
        assertEquals(123L, arrayRes.get(0));
        assertEquals("OK", arrayRes.get(1));
        assertEquals("Something\r\nblah", arrayRes.get(2));
    }

    @Test(expected = BajaResourceException.class)
    public void testReadArrayIOException() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.ARRAY);
        when(parser.readArray(inputStream)).thenThrow(IOException.class);
        connection.readArray();
    }

    @Test
    public void testReadStringArraySuccess() throws IOException {
        final List<Object> res = new ArrayList<>();
        res.add(123);
        res.add("OK");
        res.add("Something\r\nblah");

        when(parser.findType(inputStream)).thenReturn(RespType.ARRAY);
        when(parser.readArray(inputStream)).thenReturn(res);

        final List<String> arrayRes = connection.readStringArray();
        assertEquals("123", arrayRes.get(0));
        assertEquals("OK", arrayRes.get(1));
        assertEquals("Something\r\nblah", arrayRes.get(2));
    }

    @Test
    public void testReadStringArrayNullEntrySuccess() throws IOException {
        final List<Object> res = new ArrayList<>();
        res.add(123);
        res.add(null);
        res.add("Something\r\nblah");

        when(parser.findType(inputStream)).thenReturn(RespType.ARRAY);
        when(parser.readArray(inputStream)).thenReturn(res);

        final List<String> arrayRes = connection.readStringArray();
        assertEquals("123", arrayRes.get(0));
        assertNull(null, arrayRes.get(1));
        assertEquals("Something\r\nblah", arrayRes.get(2));
    }

    @Test(expected = BajaResourceException.class)
    public void testReadStringArrayIOException() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.ARRAY);
        when(parser.readArray(inputStream)).thenThrow(IOException.class);
        connection.readArray();
    }

    @Test(expected = BajaResourceException.class)
    public void testVerifyResponseTypeIOExceptionGettingType() throws IOException {
        when(parser.findType(inputStream)).thenThrow(IOException.class);
        connection.verifyResponseType(Collections.singleton(RespType.SIMPLE_STRING));
    }

    @Test(expected = BajaProtocolErrorException.class)
    public void testVerifyResponseTypeErrorType() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.ERROR);
        when(parser.readError(inputStream)).thenReturn(new RespErrResponse(
            "ERR wrong number of arguments for 'set' command"));
        connection.verifyResponseType(Collections.singleton(RespType.SIMPLE_STRING));
    }

    @Test(expected = BajaTypeMismatchException.class)
    public void testVerifyResponseTypeUnexpectedType() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.SIMPLE_STRING);
        connection.verifyResponseType(Collections.singleton(RespType.BULK_STRING));
    }

    @Test
    public void testVerifyResponseTypeSuccess() throws IOException {
        when(parser.findType(inputStream)).thenReturn(RespType.BULK_STRING);

        final Set<RespType> expected = new HashSet<>();
        expected.add(RespType.SIMPLE_STRING);
        expected.add(RespType.BULK_STRING);

        final RespType type = connection.verifyResponseType(expected);
        assertEquals(RespType.BULK_STRING, type);
    }

}
