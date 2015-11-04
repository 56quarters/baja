package org.tshlabs.baja;

import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class ResultTest {

    @Test
    public void testSetRealValue() {
        final Result<Integer> res = new Result<>();
        res.setValue(123);
    }

    @Test(expected = IllegalStateException.class)
    public void testSetAlreadySet() {
        final AtomicReference<Optional<Integer>> ref = new AtomicReference<>();
        ref.set(Optional.of(123));

        final Result<Integer> res = new Result<>(ref);
        res.setValue(456);
    }

    @Test
    public void testIsDoneNotDone() {
        final Result<String> res = new Result<>();
        assertFalse(res.isDone());
    }

    @Test
    public void testIsDoneAlreadyDone() {
        final AtomicReference<Optional<String>> ref = new AtomicReference<>();
        ref.set(Optional.of("asdf"));

        final Result<String> res = new Result<>(ref);
        assertTrue(res.isDone());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetNotDone() {
        final Result<Boolean> res = new Result<>();
        res.get();
    }

    @Test
    public void testGetAlreadyDoneRealValue() {
        final AtomicReference<Optional<Boolean>> ref = new AtomicReference<>();
        ref.set(Optional.of(true));

        final Result<Boolean> res = new Result<>(ref);
        assertTrue(res.get());
    }

    @Test
    public void testGetAlreadyDoneNullValue() {
        final AtomicReference<Optional<Boolean>> ref = new AtomicReference<>();
        ref.set(Optional.empty());

        final Result<Boolean> res = new Result<>(ref);
        assertNull(res.get());
    }
}
