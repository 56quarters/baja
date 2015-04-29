package org.tshlabs.baja.client.internal.protocol;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class RespParserTest {

    private static final Charset CHARSET = Charset.forName("utf8");

    @Mock
    private InputStream inputStream;

    private RespParser parser;

    @Before
    public void setup() {
        this.parser = new RespParser(CHARSET);
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

    @Test
    public void testReadArrayEmpty() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("0\r\n".getBytes(CHARSET));
        final List<Object> res = parser.readArray(inputStream);
        assertTrue(res.isEmpty());
    }

    @Test
    public void testReadArrayNull() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("-1\r\n".getBytes(CHARSET));
        final List<Object> res = parser.readArray(inputStream);
        assertNull(res);
    }

    @Test
    public void testReadArrayTwoStrings() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream(
                "2\r\n$3\r\nfoo\r\n$3\r\nbar\r\n".getBytes(CHARSET));
        final List<Object> res = parser.readArray(inputStream);

        assertEquals(2, res.size());
        assertEquals("foo", res.get(0));
        assertEquals("bar", res.get(1));
    }

    @Test
    public void testReadArrayThreeIntegers() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream(
                "3\r\n:1\r\n:2\r\n:3\r\n".getBytes(CHARSET));
        final List<Object> res = parser.readArray(inputStream);

        assertEquals(3, res.size());
        assertEquals(1L, res.get(0));
        assertEquals(2L, res.get(1));
        assertEquals(3L, res.get(2));
    }

    @Test
    public void testReadArrayFourIntegersOneString() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream(
                "5\r\n:1\r\n:2\r\n:3\r\n:4\r\n$6\r\nfoobar\r\n".getBytes(CHARSET));
        final List<Object> res = parser.readArray(inputStream);

        assertEquals(5, res.size());
        assertEquals(1L, res.get(0));
        assertEquals(2L, res.get(1));
        assertEquals(3L, res.get(2));
        assertEquals(4L, res.get(3));
        assertEquals("foobar", res.get(4));
    }

    @Test
    public void testReadArrayOfTwoArrays() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream(
                "2\r\n*3\r\n:1\r\n:2\r\n:3\r\n*2\r\n+foo\r\n-bar\r\n".getBytes(CHARSET));
        final List<Object> res = parser.readArray(inputStream);

        assertEquals(2, res.size());

        final Object arr1 = res.get(0);
        final Object arr2 = res.get(1);

        assertTrue(List.class.isAssignableFrom(arr1.getClass()));
        assertTrue(List.class.isAssignableFrom(arr2.getClass()));

        final List<Object> arr1List = (List<Object>) arr1;
        final List<Object> arr2List = (List<Object>) arr2;

        assertEquals(3, arr1List.size());
        assertEquals(1L, arr1List.get(0));
        assertEquals(2L, arr1List.get(1));
        assertEquals(3L, arr1List.get(2));

        assertEquals(2, arr2List.size());
        assertEquals("foo", arr2List.get(0));

        final RespErrResponse err = (RespErrResponse) arr2List.get(1);
        assertEquals("bar", err.getMessage());
    }

    @Test
    public void testReadArrayNullEntry() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream(
                "3\r\n$3\r\nfoo\r\n$-1\r\n$3\r\nbar\r\n".getBytes(CHARSET));
        final List<Object> res = parser.readArray(inputStream);

        assertEquals(3, res.size());
        assertEquals("foo", res.get(0));
        assertNull(res.get(1));
        assertEquals("bar", res.get(2));
    }

    @Test
    public void testExpectNewlineValid() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("\r\n".getBytes(CHARSET));
        assertTrue(RespParser.expectNewline(inputStream.read(), inputStream));
    }

    @Test(expected = IllegalStateException.class)
    public void testExpectNewlineInvalidFirst() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("\0".getBytes(CHARSET));
        RespParser.expectNewline(inputStream.read(), inputStream);
    }

    @Test(expected = IllegalStateException.class)
    public void testExpectNewlineInvalidSecond() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("\r\0".getBytes(CHARSET));
        RespParser.expectNewline(inputStream.read(), inputStream);
    }
}
