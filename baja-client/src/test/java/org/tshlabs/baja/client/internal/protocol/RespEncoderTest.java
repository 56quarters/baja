package org.tshlabs.baja.client.internal.protocol;

import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class RespEncoderTest {

    private List<String> args;

    @Before
    public void setup() {
        args = new ArrayList<>();
        args.add("get");
        args.add("foobar");
    }

    @Test
    public void testGetArrayPreamble() {
        final byte[] bytes = RespEncoder.getArrayPreamble(args);
        final String preamble = new String(bytes, StandardCharsets.US_ASCII);
        assertEquals("*2\r\n", preamble);
    }

    @Test
    public void testGetArgPreamble() {
        final byte[] getBytes = RespEncoder.getArgPreamble(args.get(0));
        final String getPreamble = new String(getBytes, StandardCharsets.US_ASCII);
        assertEquals("$3\r\n", getPreamble);

        final byte[] fooBytes = RespEncoder.getArgPreamble(args.get(1));
        final String fooPreamble = new String(fooBytes, StandardCharsets.US_ASCII);
        assertEquals("$6\r\n", fooPreamble);
    }
}
