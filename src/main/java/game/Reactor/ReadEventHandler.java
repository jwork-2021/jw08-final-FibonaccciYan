package game.Reactor;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ReadEventHandler implements EventHandler {

    private Selector demultiplexer;
    private ByteBuffer inputBuffer = ByteBuffer.allocate(2048);
    private ByteBuffer outputBuffer = ByteBuffer.allocate(2048);
    private int playerCount;

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public ReadEventHandler(Selector demultiplexer) {
        this.demultiplexer = demultiplexer;
    }

    public void handleEvent(SelectionKey handle) throws Exception {
        System.out.println("===== Read Event Handler =====");

        SocketChannel socketChannel = (SocketChannel) handle.channel();

        socketChannel.read(inputBuffer); // Read data from client

        // Rewind the buffer to start reading from the beginning
        inputBuffer.flip();

        byte[] buffer = new byte[inputBuffer.limit()];
        inputBuffer.get(buffer);

        String message = new String(buffer);
        System.out.println("Received message from client : " + message);
        if (message.equals("Client ask for identifier")) {
            outputBuffer = ByteBuffer.wrap(("Player" + this.playerCount).getBytes());
        } else if (message.equals("Client ask for map")) {
            if (this.playerCount == 4) {
                
            }
        }

        // Rewind the buffer to the previous state.
        inputBuffer.flip();
        // Register the interest for writable readiness event for
        // this channel in order to echo back the message
        socketChannel.register(demultiplexer, SelectionKey.OP_WRITE, outputBuffer);

    }
}
