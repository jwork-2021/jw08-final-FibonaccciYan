package game.com.anish.world;

import java.awt.Color;

public class Player extends Creature implements Runnable{

    private int xPos;
    private int yPos;
    private boolean alive;

    public Player(Color color, World world, int xPos, int yPos) {
        super(color, (char) 2, world);
        this.xPos = xPos;
        this.yPos = yPos;
        this.alive = true;
    }

    public boolean getStatus() {
        return alive;
    }

    public int getxPos(){
        return xPos;
    }

    public int getyPos(){
        return yPos;
    }

    public void setStatus(boolean status) {
        this.alive = status;
    }

    public void setxPos(int xPos){
        this.xPos = xPos;
    }

    public void setyPos(int yPos){
        this.yPos = yPos;
    }

    @Override
    public void run() {
        while(true) {  
            // Don't delete!
            System.out.print("");
            if(!alive) {
                world.remove(xPos, yPos);
                world.win();
                return;
            }
        }
    }
}
