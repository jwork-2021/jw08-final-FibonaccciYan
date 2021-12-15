package game.com.anish.screen;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import game.com.anish.world.Bomb;
// import game.com.anish.world.Creature;
// import game.com.anish.world.Thing;
import game.com.anish.world.Player;
import game.com.anish.world.World;
import game.com.anish.world.Wall;

import game.asciiPanel.AsciiPanel;

import game.maze.MazeGenerator;

public class WorldScreen implements Screen {

    public static final int SIZE = 20;

    private World world;
    private Player player1;
    private Player player2;
    private ExecutorService exec = Executors.newCachedThreadPool();

    String[] mazeSteps;
    String[] visualSteps;

    public WorldScreen() {
        initWorld();

        player1 = new Player(new Color(255, 255, 0), world, 0, 0);
        player2 = new Player(new Color(0, 255, 0), world, SIZE - 1, SIZE - 1);
        world.put(player1, 0, 0);
        world.setMaze(0, 0, 2);
        world.put(player2, SIZE - 1, SIZE - 1);
        world.setMaze(SIZE - 1, SIZE - 1, 2);
        world.entities.add(player1);
        world.entities.add(player2);

        exec.execute(player1);
        exec.execute(player2);
    } 

    private void initWorld() {
        world = new World();
        MazeGenerator mazeGenerator = new MazeGenerator(SIZE);
        mazeGenerator.generateMaze();
        world.maze = mazeGenerator.getMaze();

        for(int i = 0; i < world.maze.length; i++){
            for(int j = 0; j < world.maze[0].length; j++){
                if(world.maze[i][j] == 0){
                    world.put(new Wall(world), i, j);
                }
            }
        }
    }

    private boolean isValidMove(int xPos, int yPos){
        if(xPos >= 0 && xPos < SIZE && yPos >= 0 && yPos < SIZE && world.maze[xPos][yPos] == 1){
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
        int xPos1 = player1.getxPos();
        int yPos1 = player1.getyPos();
        int xPos2 = player2.getxPos();
        int yPos2 = player2.getyPos();

        if(world.running) {
            switch(key.getKeyCode()){
                // player1 control
                case KeyEvent.VK_LEFT:// left arrow
                    if(player1.getStatus() == true && isValidMove(xPos1 - 1, yPos1)){
                        player1.moveTo(xPos1, yPos1, xPos1 - 1, yPos1);
                        player1.setxPos(xPos1 - 1);
                        player1.setyPos(yPos1);
                    }
                    break;
                case KeyEvent.VK_UP:// up arrow
                    if(player1.getStatus() == true && isValidMove(xPos1, yPos1 - 1)){
                        player1.moveTo(xPos1, yPos1, xPos1, yPos1 - 1);
                        player1.setxPos(xPos1);
                        player1.setyPos(yPos1 - 1);
                    }
                    break;
                case KeyEvent.VK_RIGHT:// right arrow
                    if(player1.getStatus() == true && isValidMove(xPos1 + 1, yPos1)){
                        player1.moveTo(xPos1, yPos1, xPos1 + 1, yPos1);
                        player1.setxPos(xPos1 + 1);
                        player1.setyPos(yPos1);
                    }
                    break;
                case KeyEvent.VK_DOWN:// down arrow
                    if(player1.getStatus() == true && isValidMove(xPos1, yPos1 + 1)){
                        player1.moveTo(xPos1, yPos1, xPos1, yPos1 + 1);
                        player1.setxPos(xPos1);
                        player1.setyPos(yPos1 + 1);
                    }
                    break;
                case KeyEvent.VK_ENTER:// enter
                    if(player1.getStatus() == true) {
                        Bomb bomb = new Bomb(world, xPos1, yPos1);
                        world.put(bomb, xPos1, yPos1);
                        world.setMaze(xPos1, yPos1, 3);
                        world.entities.add(bomb);
                        exec.execute(bomb);
                    }
                    break;
                // player2 control
                case KeyEvent.VK_A:// A
                    if(player2.getStatus() == true && isValidMove(xPos2 - 1, yPos2)){
                        player2.moveTo(xPos2, yPos2, xPos2 - 1, yPos2);
                        player2.setxPos(xPos2 - 1);
                        player2.setyPos(yPos2);
                    }
                    break;
                case KeyEvent.VK_W:// W
                    if(player2.getStatus() == true && isValidMove(xPos2, yPos2 - 1)){
                        player2.moveTo(xPos2, yPos2, xPos2, yPos2 - 1);
                        player2.setxPos(xPos2);
                        player2.setyPos(yPos2 - 1);
                    }
                    break;
                case KeyEvent.VK_D:// D
                    if(player2.getStatus() == true && isValidMove(xPos2 + 1, yPos2)){
                        player2.moveTo(xPos2, yPos2, xPos2 + 1, yPos2);
                        player2.setxPos(xPos2 + 1);
                        player2.setyPos(yPos2);
                    }
                    break;
                case KeyEvent.VK_S:// S
                    if(player2.getStatus() == true && isValidMove(xPos2, yPos2 + 1)){
                        player2.moveTo(xPos2, yPos2, xPos2, yPos2 + 1);
                        player2.setxPos(xPos2);
                        player2.setyPos(yPos2 + 1);
                    }
                    break;
                case KeyEvent.VK_SPACE:// space
                    if(player2.getStatus() == true) {
                        Bomb bomb = new Bomb(world, xPos2, yPos2);
                        world.put(bomb, xPos2, yPos2);
                        world.setMaze(xPos2, yPos2, 3);
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
