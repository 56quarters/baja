package org.tshlabs.baja.client.protocol;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 *
 */
public class RespTypeTest {

    @Test
    public void testByteLookupArray() {
        assertEquals(RespType.ARRAY, RespType.lookup('*').get());
    }

    @Test
    public void testByteLookupBulkString() {
        assertEquals(RespType.BULK_STRING, RespType.lookup('$').get());
    }

    @Test
    public void testByteLookupError() {
        assertEquals(RespType.ERROR, RespType.lookup('-').get());
    }

    @Test
    public void testByteLookupInteger() {
        assertEquals(RespType.INTEGER, RespType.lookup(':').get());
    }

    @Test
    public void testByteLookupSimpleString() {
        assertEquals(RespType.SIMPLE_STRING, RespType.lookup('+').get());
    }

    @Test
    public void testByteLookupInvalid() {
        assertFalse(RespType.lookup('%').isPresent());
    }

    @Test
    public void testStringLookupArray() {
        assertEquals(RespType.ARRAY, RespType.lookup("*").get());
    }

    @Test
    public void testStringLookupBulkString() {
        assertEquals(RespType.BULK_STRING, RespType.lookup("$").get());
    }

    @Test
    public void testStringLookupError() {
        assertEquals(RespType.ERROR, RespType.lookup("-").get());
    }

    @Test
    public void testStringLookupInteger() {
        assertEquals(RespType.INTEGER, RespType.lookup(":").get());
    }

    @Test
    public void testStringLookupSimpleString() {
        assertEquals(RespType.SIMPLE_STRING, RespType.lookup("+").get());
    }

    @Test
    public void testStringLookupInvalid() {
        assertFalse(RespType.lookup("%").isPresent());
    }
}
