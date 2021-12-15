package game.com.anish.world;

import java.util.Iterator;
import java.util.List;

import game.asciiPanel.AsciiPanel;

public class Bomb extends Creature implements Runnable {

    private int xPos;
    private int yPos;
    private static final int[][] DIREC = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}, {0, 0}};

    public Bomb(World world, int xPos, int yPos) {
        super(AsciiPanel.red, (char)15, world);
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public int getxPos() {
        return this.xPos;
    }

    public int getyPos() {
        return this.yPos;
    }

    public void explode() {
        for(int i = 0; i < DIREC.length; i++) {
            int tmpX = xPos + DIREC[i][0];
            int tmpY = yPos + DIREC[i][1];
            // handle walls
            if(world.getMaze(tmpX, tmpY) == 0) {
                world.remove(tmpX, tmpY);
                world.setMaze(tmpX, tmpY, 1);
            } else if(world.getMaze(tmpX, tmpY) != -1) {
                // handle players and bombs
                List<Thing> tmpList = world.getEntities(tmpX, tmpY);
                if(!tmpList.isEmpty()) {
                    Iterator<Thing> it = tmpList.iterator();
                    while(it.hasNext()){
                        Thing next = it.next();
                        if(next.getClass().getName() == "game.com.anish.world.Player") {
                            Player target = (Player)next;
                            target.setStatus(false);
                            world.removeEntities(target);
                        } else if (next.getClass().getName() == "game.com.anish.world.Bomb") {
                            // TODO: support chain reaction
                            Bomb target = (Bomb)next;
                            if(tmpX == xPos && tmpY == yPos) {
                                world.removeEntities(target);
                                world.remove(tmpX, tmpY);
                            }
                        }
                    }
                }
            }
        }
        world.setMaze(xPos, yPos, 1);
    }

    @Override
    public void run() {
        try{
            Thread.sleep(3000);
        } catch(InterruptedException e) {
            explode();
            return;
        }
        explode();
    }
}
