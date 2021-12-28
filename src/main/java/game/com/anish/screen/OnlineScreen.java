package game.com.anish.screen;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import game.com.anish.world.Bomb;
import game.com.anish.world.Player;
import game.com.anish.world.Wall;
import game.com.anish.world.World;
import game.Reactor.Client;
import game.Reactor.ReactorManager;
import game.asciiPanel.AsciiPanel;

import game.maze.MazeGenerator;

public class OnlineScreen implements Screen {

    public static final int SIZE = 20;

    public World world;
    public Player player;// local player
    public Player player1;// server player
    public Player player2;
    public Player player3;
    public Player player4;
    public ExecutorService exec = Executors.newCachedThreadPool();

    boolean isServer;
    Client client;
    ReactorManager server;
    public String identifier = "";

    public OnlineScreen(boolean isServer) {

        world = new World();
        initWorld();
        
        this.isServer = isServer;

        if (!this.isServer) {// client
            this.client = new Client(this);

            // get identifier
            client.sendMessage("Client ask for identifier");
            identifier = client.receiveMessage();
            if (identifier.equals("player2")) {
                player = player2;
            } else if (identifier.equals("player3")) {
                player = player3;
            } else if (identifier.equals("player4")) {
                player = player4;
            } else {
                System.out.println("Error identfier:" + identifier);
            }

            // get maze
            client.sendMessage("Client ask for maze");
            String message = client.receiveMessage();
            int len = message.length();
            for (int i = 0; i < len; i++) {
                world.setMaze(i/SIZE, i%SIZE, message.charAt(i) - '0');
            }
            setWall();

            exec.execute(client);
        } else {// server

            identifier = "player1";
            player = player1;

            MazeGenerator mazeGenerator = new MazeGenerator(SIZE);
            mazeGenerator.generateMaze();
            world.maze = mazeGenerator.getMaze();

            server = new ReactorManager(this);
            setWall();

            exec.execute(server);
        }


    }

    private void initWorld() {

        player1 = new Player(new Color(255, 255, 0), world, 0, 0, 1);
        player2 = new Player(new Color(255, 0, 255), world, SIZE - 1, 0, 2);
        player3 = new Player(new Color(0, 255, 0), world, SIZE - 1, SIZE - 1, 3);
        player4 = new Player(new Color(255, 0, 0), world, 0, SIZE - 1, 4);

        world.put(player1, 0, 0);
        world.put(player2, SIZE - 1, 0);
        world.put(player3, SIZE - 1, SIZE - 1);
        world.put(player4, 0, SIZE - 1);
        world.entities.add(player1);
        world.entities.add(player2);
        world.entities.add(player3);
        world.entities.add(player4);

        exec.execute(player1);
        exec.execute(player2);
        exec.execute(player3);
        exec.execute(player4);
    }

    public void setWall() {

        world.setMaze(0, 0, 2);
        world.setMaze(SIZE - 1, 0, 2);
        world.setMaze(SIZE - 1, SIZE - 1, 2);
        world.setMaze(0, SIZE - 1, 2);

        for (int i = 0; i < world.maze.length; i++) {
            for (int j = 0; j < world.maze[0].length; j++) {
                if (world.maze[i][j] == 0) {
                    world.put(new Wall(world), i, j);
                }
            }
        }

    }

    private boolean isValidMove(int xPos, int yPos) {
        if (xPos >= 0 && xPos < SIZE && yPos >= 0 && yPos < SIZE && world.maze[xPos][yPos] == 1) {
            return true;
        }
        return false;
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        for (int x = 0; x < World.WIDTH; x++) {
            for (int y = 0; y < World.HEIGHT; y++) {
                terminal.write(world.get(x, y).getGlyph(), x, y, world.get(x, y).getColor());
            }
        }
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        int xPos = player.getxPos();
        int yPos = player.getyPos();

        if (world.running) {
            switch (key.getKeyCode()) {
                case KeyEvent.VK_A:// A
                    if (player.getStatus() == true && isValidMove(xPos - 1, yPos)) {
                        if(!this.isServer) {
                            client.sendMessage(identifier + " MOVE A");
                        } else {
                            server.sendMessage(identifier + " MOVE A");
                        }

                        player.moveTo(xPos, yPos, xPos - 1, yPos);
                        player.setxPos(xPos - 1);
                        player.setyPos(yPos);

                    }
                    break;
                case KeyEvent.VK_W:// W
                    if (player.getStatus() == true && isValidMove(xPos, yPos - 1)) {
                        if(!this.isServer) {
                            client.sendMessage(identifier + " MOVE W");
                        } else {
                            server.sendMessage(identifier + " MOVE W");
                        }

                        player.moveTo(xPos, yPos, xPos, yPos - 1);
                        player.setxPos(xPos);
                        player.setyPos(yPos - 1);
                    }
                    break;
                case KeyEvent.VK_D:// D
                    if (player.getStatus() == true && isValidMove(xPos + 1, yPos)) {
                        if(!this.isServer) {
                            client.sendMessage(identifier + " MOVE D");
                        } else {
                            server.sendMessage(identifier + " MOVE D");
                        }

                        player.moveTo(xPos, yPos, xPos + 1, yPos);
                        player.setxPos(xPos + 1);
                        player.setyPos(yPos);
                    }
                    break;
                case KeyEvent.VK_S:// S
                    if (player.getStatus() == true && isValidMove(xPos, yPos + 1)) {
                        if(!this.isServer) {
                            client.sendMessage(identifier + " MOVE S");
                        } else {
                            server.sendMessage(identifier + " MOVE S");
                        }

                        player.moveTo(xPos, yPos, xPos, yPos + 1);
                        player.setxPos(xPos);
                        player.setyPos(yPos + 1);
                    }
                    break;
                case KeyEvent.VK_SPACE:// space
                    if (player.getStatus() == true) {
                        if(!this.isServer) {
                            client.sendMessage(identifier + " SET" );
                        } else {
                            server.sendMessage(identifier + " SET");
                        }

                        Bomb bomb = new Bomb(world, xPos, yPos);
                        world.put(bomb, xPos, yPos);
                        world.setMaze(xPos, yPos, 3);
                        world.entities.add(bomb);
                        exec.execute(bomb);
                    }
                    break;
                default:
                    break;
            }
        }
        return this;
    }

}
