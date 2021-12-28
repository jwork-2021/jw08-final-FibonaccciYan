package game.Reactor;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class WriteEventHandler implements EventHandler {

    private Selector demultiplexer;

    public WriteEventHandler(Selector demultiplexer) {
        this.demultiplexer = demultiplexer;
    }

    public void handleEvent(SelectionKey handle) throws Exception {
        System.out.println("===== Write Event Handler =====");

        SocketChannel socketChannel = (SocketChannel) handle.channel();
        Integer[][] maze = { { 1, 0 }, { 0, 1 } };
        String output = "";
        for (int i = 0; i < maze[0].length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                output += maze[i][j].toString();
            }
        }
        output += "\n";
        ByteBuffer bb = ByteBuffer.wrap(output.getBytes());
        // ByteBuffer bb = ByteBuffer.wrap("Hello Client!\n".getBytes());
        // ByteBuffer inputBuffer = (ByteBuffer) handle.attachment();
        socketChannel.write(bb);
        // socketChannel.register(demultiplexer, SelectionKey.OP_WRITE, inputBuffer);
        // socketChannel.close();
    }
}
