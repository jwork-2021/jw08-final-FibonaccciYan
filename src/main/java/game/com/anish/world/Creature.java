package game.com.anish.world;

import java.awt.Color;
import java.util.List;
import java.util.Iterator;

public class Creature extends Thing {

    Creature(Color color, char glyph, World world) {
        super(color, glyph, world);
    }

    public void moveTo(int pxPos, int pyPos, int xPos, int yPos) {
        List<Thing> tmpList = this.world.getEntities(pxPos, pyPos);
        if(!tmpList.isEmpty()) {
            Iterator<Thing> it = tmpList.iterator();
            while(it.hasNext()){
                Thing next = it.next();
                if(next.getClass().getName() == "game.com.anish.world.Player") {
                    this.world.remove(pxPos, pyPos);
                    this.world.setMaze(pxPos, pyPos, 1);
                } else if (next.getClass().getName() == "game.com.anish.world.Bomb") {
                    this.world.put(new Bomb(this.world, pxPos, pyPos), pxPos, pyPos);
                    this.world.setMaze(pxPos, pyPos, 3);
                }
            }
        }
        this.world.put(this, xPos, yPos);
        this.world.setMaze(xPos, yPos, 2);
    }

}
