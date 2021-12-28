package game.Reactor;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

import game.com.anish.screen.OnlineScreen;

public class ReactorManager implements Runnable {

    private Reactor reactor;
    public static final int SERVER_PORT = 7070;

    public ReactorManager(OnlineScreen os) {
        try {
            reactor = new Reactor(os);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startReactor(int port) throws Exception {

        ServerSocketChannel server = ServerSocketChannel.open();
        server.socket().bind(new InetSocketAddress(port));
        server.configureBlocking(false);

        reactor.registerChannel(SelectionKey.OP_ACCEPT, server);

        reactor.registerEventHandler(
                SelectionKey.OP_ACCEPT, new AcceptEventHandler(reactor.getDemultiplexer()));

        reactor.registerEventHandler(
                SelectionKey.OP_READ, new ReadEventHandler(reactor.getDemultiplexer()));

        reactor.registerEventHandler(
                SelectionKey.OP_WRITE, new WriteEventHandler(reactor.getDemultiplexer()));

        reactor.run(); // Run the dispatcher loop

    }

    public void sendMessage(String message) {
        reactor.sendMessage(message);
    }

    @Override
    public void run() {
        System.out.println("Server Started at port : " + SERVER_PORT);
        try {
            this.startReactor(SERVER_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
