package org.tshlabs.baja.client.internal;

/**
 *
 */
public class CommandBuilder {

    private final Command cmd;

    private final StringBuilder args = new StringBuilder();

    private CommandBuilder(Command cmd) {
        this.cmd = cmd;
    }

    public static CommandBuilder command(Command cmd) {
        return new CommandBuilder(cmd);
    }

    public CommandBuilder arg(String arg) {
        this.args.append(arg);
        return this;
    }

    public CommandBuilder arg(int arg) {
        this.args.append(arg);
        return this;
    }

    public CommandBuilder arg(long arg) {
        this.args.append(arg);
        return this;
    }

    public CommandBuilder arg(boolean arg) {
        this.args.append(arg);
        return this;
    }
}
