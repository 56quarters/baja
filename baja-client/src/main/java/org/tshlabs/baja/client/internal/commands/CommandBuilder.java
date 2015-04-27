package org.tshlabs.baja.client.internal.commands;

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

    public String build() {
        if (args.length() == 0) {
            return cmd.toRepr();
        }
        return cmd.toRepr() + " " + args.toString();
    }
}
