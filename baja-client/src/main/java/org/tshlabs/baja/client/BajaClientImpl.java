package org.tshlabs.baja.client;

import org.tshlabs.baja.client.internal.Connection;
import org.tshlabs.baja.client.internal.commands.StringCommand;
import org.tshlabs.baja.client.internal.protocol.RespEncoder;
import org.tshlabs.baja.client.internal.protocol.RespEncodings;
import org.tshlabs.baja.client.internal.protocol.RespParser;
import org.tshlabs.baja.client.operations.HashOperations;
import org.tshlabs.baja.client.operations.StringOperations;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import static org.tshlabs.baja.client.internal.commands.CommandBuilder.command;

/**
 *
 */
public class BajaClientImpl implements BajaClient {

    @Override
    public HashOperations getHashOperations() {
        return null;
    }

    @Override
    public StringOperations getStringOperations() {
        return null;
    }

    public static void main(String[] args) throws IOException {
        final Socket socket = new Socket("localhost", 6379);
        final RespEncoder encoder = new RespEncoder(RespEncodings.DEFAULT_PAYLOAD);
        final RespParser parser = new RespParser(RespEncodings.DEFAULT_PAYLOAD);

        final Connection conn = new Connection(
                socket.getInputStream(),
                socket.getOutputStream(),
                encoder,
                parser);

        final List<String> cmd = command(StringCommand.GET)
                .arg("foo")
                .build();

        conn.writeCommand(cmd);
        System.out.println(conn.readBulkString());
    }
}
