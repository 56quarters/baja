package org.tshlabs.baja.protocol;

import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


public class RespEncoderTest {

    private RespEncoder encoder;

    @Before
    public void setup() {
        this.encoder = new RespEncoder(StandardCharsets.UTF_8);
    }

    @Test
    public void testGetArrayPreamble() {
        final List<String> args = new ArrayList<>();
        args.add("GET");
        args.add("foobar");

        final byte[] bytes = RespEncoder.getArrayPreamble(args);
        final String preamble = new String(bytes, StandardCharsets.US_ASCII);
        assertEquals("*2\r\n", preamble);
    }

    @Test
    public void testGetArgPreamble() {
        final List<String> args = new ArrayList<>();
        args.add("GET");
        args.add("foobar");

        final byte[] getBytes = RespEncoder.getArgPreamble(args.get(0));
        final String getPreamble = new String(getBytes, StandardCharsets.UTF_8);
        assertEquals("$3\r\n", getPreamble);

        final byte[] fooBytes = RespEncoder.getArgPreamble(args.get(1));
        final String fooPreamble = new String(fooBytes, StandardCharsets.UTF_8);
        assertEquals("$6\r\n", fooPreamble);
    }

    @Test(expected = NullPointerException.class)
    public void testEncodeNullInput() {
        encoder.encodeMulti(null);
    }

    @Test
    public void testEncodeEmptyList() {
        final List<String> args = new ArrayList<>();
        final byte[] expected = "*0\r\n".getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(expected, encoder.encodeMulti(Collections.singletonList(args)));
    }

    @Test
    public void testEncodeListLength() {
        final List<String> args = new ArrayList<>();
        args.add("LLEN");
        args.add("alist");

        final byte[] expected = "*2\r\n$4\r\nLLEN\r\n$5\r\nalist\r\n".getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(expected, encoder.encodeMulti(Collections.singletonList(args)));
    }

    @Test
    public void testEncodeSetAndExpire() {
        final List<String> args = new ArrayList<>();
        args.add("SETEX");
        args.add("foo");
        args.add("bar");
        args.add("60");

        final byte[] expected = ("*4\r\n" +
                "$5\r\nSETEX\r\n" +
                "$3\r\nfoo\r\n" +
                "$3\r\nbar\r\n" +
                "$2\r\n60\r\n").getBytes(StandardCharsets.UTF_8);

        assertArrayEquals(expected, encoder.encodeMulti(Collections.singletonList((args))));

    }
}
