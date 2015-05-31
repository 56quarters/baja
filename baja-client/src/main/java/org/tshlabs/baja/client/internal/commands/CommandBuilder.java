package org.tshlabs.baja.client.internal.commands;

import org.tshlabs.baja.client.types.Encodable;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 *
 *
 *
 *
 */
public class CommandBuilder {

    private final List<String> args = new ArrayList<>();

    private CommandBuilder(Encodable cmd) {
        requireNonNull(cmd);
        this.args.add(cmd.toRepr());
    }

    public static CommandBuilder command(Encodable cmd) {
        return new CommandBuilder(cmd);
    }

    public CommandBuilder arg(String arg) {
        this.args.add(arg);
        return this;
    }

    public CommandBuilder arg(int arg) {
        this.args.add(String.valueOf(arg));
        return this;
    }

    public CommandBuilder arg(long arg) {
        this.args.add(String.valueOf(arg));
        return this;
    }

    public CommandBuilder arg(float arg) {
        this.args.add(String.valueOf(arg));
        return this;
    }

    public CommandBuilder arg(boolean arg) {
        // Redis doesn't really have boolean types so we use 1 or 0
        this.args.add((arg ? "1" : "0"));
        return this;
    }

    public CommandBuilder arg(Encodable arg) {
        this.args.add(arg.toRepr());
        return this;
    }

    public List<String> build() {
        return new ArrayList<>(args);
    }
}
