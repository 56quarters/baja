package org.tshlabs.baja.client.internal.parser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class RespParserTest {

    private static final Charset CHARSET = Charset.forName("utf8");

    @Mock
    private InputStream inputStream;

    private RespParser parser;

    @Before
    public void setup() {
        this.parser = new RespParser();
    }

    @Test(expected = IllegalStateException.class)
    public void testFindTypeEndOfStream() throws IOException {
        when(inputStream.read()).thenReturn(-1);

        parser.findType(inputStream);
    }

    @Test
    public void testFindTypeArrayType() throws IOException {
        when(inputStream.read()).thenReturn((int) '*');

        final RespType type = parser.findType(inputStream);

        assertEquals(RespType.ARRAY, type);
    }

    @Test
    public void testFindTypeBulkStringType() throws IOException {
        when(inputStream.read()).thenReturn((int) '$');

        final RespType type = parser.findType(inputStream);

        assertEquals(RespType.BULK_STRING, type);
    }

    @Test
    public void testFindTypeErrorType() throws IOException {
        when(inputStream.read()).thenReturn((int) '-');

        final RespType type = parser.findType(inputStream);

        assertEquals(RespType.ERROR, type);
    }

    @Test
    public void testFindTypeIntegerType() throws IOException {
        when(inputStream.read()).thenReturn((int) ':');

        final RespType type = parser.findType(inputStream);

        assertEquals(RespType.INTEGER, type);
    }

    @Test
    public void testFindTypeSimpleStringType() throws IOException {
        when(inputStream.read()).thenReturn((int) '+');

        final RespType type = parser.findType(inputStream);

        assertEquals(RespType.SIMPLE_STRING, type);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindTypeInvalid() throws IOException {
        when(inputStream.read()).thenReturn((int) '#');

        parser.findType(inputStream);
    }

    @Test
    public void testReadSimpleString1() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("OK\r\n".getBytes(CHARSET));
        assertEquals("OK", parser.readSimpleString(inputStream));
    }

    @Test
    public void testReadSimpleString2() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("BLAH\r\n".getBytes(CHARSET));
        assertEquals("BLAH", parser.readSimpleString(inputStream));
    }

    @Test
    public void testReadInteger1() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("0\r\n".getBytes(CHARSET));
        assertEquals(0L, parser.readInteger(inputStream));
    }

    @Test
    public void testReadInteger2() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("1308\r\n".getBytes(CHARSET));
        assertEquals(1308L, parser.readInteger(inputStream));
    }

    @Test
    public void testReadBulkString1() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("8\r\nfoo\r\nbar\r\n".getBytes(CHARSET));
        assertEquals("foo\r\nbar", parser.readBulkString(inputStream));
    }

    @Test
    public void testReadBulkString2() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("6\r\nfoobar\r\n".getBytes(CHARSET));
        assertEquals("foobar", parser.readBulkString(inputStream));
    }

    @Test
    public void testReadBulkStringEmpty() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("0\r\n".getBytes(CHARSET));
        assertEquals("", parser.readBulkString(inputStream));
    }

    @Test
    public void testReadBulkStringNull() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("-1\r\n".getBytes(CHARSET));
        assertNull(parser.readBulkString(inputStream));
    }

    @Test(expected = IllegalStateException.class)
    public void testReadBulkStringTooLong() throws IOException {
        final long badLength = 1024 * 1024 * 1024;
        final InputStream inputStream = new ByteArrayInputStream((badLength + "\r\nfoo\r\n").getBytes(CHARSET));
        parser.readBulkString(inputStream);
    }

    @Test(expected = IllegalStateException.class)
    public void testReadBulkStringShort() throws IOException {
        final long badLength = 1024;
        final InputStream inputStream = new ByteArrayInputStream((badLength + "\r\nfoo\r\n").getBytes(CHARSET));
        parser.readBulkString(inputStream);
    }
}
