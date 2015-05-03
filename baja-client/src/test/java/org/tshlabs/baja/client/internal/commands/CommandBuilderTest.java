package org.tshlabs.baja.client.internal.commands;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class CommandBuilderTest {

    private enum MyCommands implements Command {
        GET,
        SET,
        SETEX;

        @Override
        public String toRepr() {
            return name();
        }
    }

    @Test(expected = NullPointerException.class)
    public void testCommandNullInput() {
        CommandBuilder.command(null);
    }

    @Test
    public void testArgStringArg() {
        final List<String> cmd = CommandBuilder
                .command(MyCommands.GET)
                .arg("foo")
                .build();

        assertEquals("GET", cmd.get(0));
        assertEquals("foo", cmd.get(1));
    }

    @Test
    public void testArgIntArg() {
        final List<String> cmd = CommandBuilder
                .command(MyCommands.SET)
                .arg("foo")
                .arg(123)
                .build();

        assertEquals("SET", cmd.get(0));
        assertEquals("foo", cmd.get(1));
        assertEquals("123", cmd.get(2));
    }

    @Test
    public void testArgLongArg() {
        final List<String> cmd = CommandBuilder
                .command(MyCommands.SET)
                .arg("bar")
                .arg(123L)
                .build();

        assertEquals("SET", cmd.get(0));
        assertEquals("bar", cmd.get(1));
        assertEquals("123", cmd.get(2));
    }

    @Test
    public void testArgBooleanArg() {
        final List<String> cmd = CommandBuilder
                .command(MyCommands.SET)
                .arg("baz")
                .arg(true)
                .build();

        assertEquals("SET", cmd.get(0));
        assertEquals("baz", cmd.get(1));
        assertEquals("1", cmd.get(2));
    }

    @Test
    public void testArgMixedArgs() {
        final List<String> cmd = CommandBuilder
                .command(MyCommands.SETEX)
                .arg("foo")
                .arg(123L)
                .arg(60)
                .build();

        assertEquals("SETEX", cmd.get(0));
        assertEquals("foo", cmd.get(1));
        assertEquals("123", cmd.get(2));
        assertEquals("60", cmd.get(3));
    }
}
