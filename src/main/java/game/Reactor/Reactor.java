package game.Reactor;

import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import game.com.anish.screen.OnlineScreen;
import game.com.anish.world.Bomb;
import game.com.anish.world.Player;

/* This is the Initiation Dispatcher or the Reactor */
public class Reactor {
    private Map<Integer, EventHandler> registeredHandlers = new ConcurrentHashMap<Integer, EventHandler>();
    private Selector demultiplexer;

    private int playerCount = 1;// server player
    private int broadcastCount = 1;
    private OnlineScreen os;
    private String maze = "";
    private String serverMessage = "";
    private boolean changed = false;

    public Reactor(OnlineScreen os) throws Exception {
        demultiplexer = Selector.open();
        this.os = os;
        setMaze();
    }

    public Selector getDemultiplexer() {
        return demultiplexer;
    }

    public void registerEventHandler(int eventType, EventHandler eventHandler) {
        registeredHandlers.put(eventType, eventHandler);
    }

    public void registerChannel(int eventType, SelectableChannel channel) throws Exception {
        channel.register(demultiplexer, eventType);
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setMaze() {
        int[][] tempMaze = this.os.world.maze;
        for (int i = 0; i < tempMaze.length; i++) {
            for (int j = 0; j < tempMaze[0].length; j++) {
                this.maze += String.valueOf(tempMaze[i][j]);
            }
        }
        this.maze += "\n";
    }

    public void serverHandle(String message) {
        String[] orders = message.split(" ");

        // if (orders[0].equals(os.identifier)) {
        //     continue;
        // }

        Player player;
        if (orders[0].equals("player2")) {
            player = os.player2;
        } else if (orders[0].equals("player3")) {
            player = os.player3;
        } else if (orders[0].equals("player4")) {
            player = os.player4;
        } else {
            // System.out.println("Error player: " + orders[0]);
            return;
        }

        int xPos = player.getxPos();
        int yPos = player.getyPos();

        if (orders[1].equals("MOVE")) {

            if (orders[2].equals("A")) {
                player.moveTo(xPos, yPos, xPos - 1, yPos);
                player.setxPos(xPos - 1);
                player.setyPos(yPos);
            } else if (orders[2].equals("W")) {
                player.moveTo(xPos, yPos, xPos, yPos - 1);
                player.setxPos(xPos);
                player.setyPos(yPos - 1);
            } else if (orders[2].equals("D")) {
                player.moveTo(xPos, yPos, xPos + 1, yPos);
                player.setxPos(xPos + 1);
                player.setyPos(yPos);
            } else if (orders[2].equals("S")) {
                player.moveTo(xPos, yPos, xPos, yPos + 1);
                player.setxPos(xPos);
                player.setyPos(yPos + 1);
            } else {
                System.out.println("Error move: " + orders[2]);
                return;
            }

        } else if (orders[1].equals("SET")) {

            Bomb bomb = new Bomb(os.world, xPos, yPos);
            os.world.put(bomb, xPos, yPos);
            os.world.setMaze(xPos, yPos, 3);
            os.world.entities.add(bomb);
            os.exec.execute(bomb);

        } else if (orders[1].equals("DIE")) {

            player.setStatus(false);

        } else {
            System.out.println("Error order: " + orders[1]);
        }
    }

    public void sendMessage(String message) {
        serverMessage = message;
    }

    public void run() {

        ByteBuffer inputBuffer = ByteBuffer.allocate(2048);
        ByteBuffer outputBuffer = ByteBuffer.allocate(2048);

        try {   
            // Loop indefinitely
            while (true) {
                Thread.sleep(16);

                if(!os.player.getStatus()){
                    if(!changed) {
                        sendMessage(os.identifier + " DIE");
                        os.world.gameOver();
                        changed = true;
                    }
                }

                demultiplexer.select();

                Set<SelectionKey> readyHandles = demultiplexer.selectedKeys();
                Iterator<SelectionKey> handleIterator = readyHandles.iterator();

                while (handleIterator.hasNext()) {
                    SelectionKey handle = handleIterator.next();

                    if (handle.isAcceptable()) {
                        // System.out.println("===== Accept Event Handler =====");

                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) handle.channel();
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        if (socketChannel != null) {
                            socketChannel.configureBlocking(false);
                            socketChannel.register(demultiplexer, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        }
                        handleIterator.remove();
                    }

                    if (handle.isReadable()) {
                        // System.out.println("===== Read Event Handler =====");

                        SocketChannel socketChannel = (SocketChannel) handle.channel();

                        inputBuffer.clear();
                        outputBuffer.clear();

                        socketChannel.read(inputBuffer); // Read data from client
                        inputBuffer.flip();

                        byte[] buffer = new byte[inputBuffer.limit()];
                        inputBuffer.get(buffer);
                        inputBuffer.flip();

                        String message = new String(buffer);
                        if (message.isEmpty()) {
                            if (broadcastCount == 1) {
                                if(serverMessage.isEmpty()){
                                    continue;
                                }
                                message = serverMessage;
                                serverMessage = "";
                            }
                        }
                        if (!message.isEmpty()) {
                            System.out.println("Received message from client : " + message);
                        }
                        if (message.equals("Client ask for identifier")) {

                            playerCount++;
                            String identifier = "player" + String.valueOf(this.playerCount);

                            // send identifier
                            outputBuffer.put((identifier + "\n").getBytes());
                            outputBuffer.flip();

                        } else if (message.equals("Client ask for maze")) {

                            // send maze
                            outputBuffer.put(maze.getBytes());
                            outputBuffer.flip();

                        } else if (!message.isEmpty()) {

                            if (broadcastCount == 1) {
                                broadcastCount = playerCount;
                            }

                            outputBuffer.put((message + "\n").getBytes());
                            outputBuffer.flip();

                            if(os.player.getStatus()){
                                serverHandle(message);
                            }
                        }

                    }

                    if (handle.isWritable()) {
                        // System.out.println("===== Write Event Handler =====");

                        SocketChannel socketChannel = (SocketChannel) handle.channel();

                        socketChannel.write(outputBuffer);
                        outputBuffer.flip();

                        if (broadcastCount > 1) {
                            broadcastCount--;
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
