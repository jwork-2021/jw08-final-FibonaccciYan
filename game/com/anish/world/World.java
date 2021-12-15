package game.com.anish.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class World {

    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;

    public List<Thing> entities;
    public int[][] maze;
    public boolean running; 

    private Tile<Thing>[][] tiles;

    public World() {

        if (tiles == null) {
            tiles = new Tile[WIDTH][HEIGHT];
        }

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                tiles[i][j] = new Tile<>(i, j);
                tiles[i][j].setThing(new Floor(this));
            }
        }

        running = true;
        entities = new ArrayList<Thing>();
    }

    public Thing get(int x, int y) {
        return this.tiles[x][y].getThing();
    }

    public void put(Thing t, int x, int y) {
        this.tiles[x][y].setThing(t);
    }

    public void remove(int x, int y){
        if(0 <= x && x <= WIDTH -1 && 0 <= y && y <= HEIGHT - 1)
            this.tiles[x][y].setThing(new Floor(this));
    }

    public List<Thing> getEntities(int x, int y) {
        List<Thing> temp = new ArrayList<Thing>();
        for(int i = 0; i < entities.size(); i++) {
            Thing target = entities.get(i);
            if(target.getX() == x && target.getY() == y)
                temp.add(target);
        }
        return temp;
    }

    public void removeEntities(Thing target) {
        Iterator<Thing> it = entities.iterator();
        while(it.hasNext()) {
            Thing i = it.next();
            if(i == target) {
                it.remove();
                break;
            }
        }
    }

    // -1: invalid get 
    // 0: Wall
    // 1: Way
    // 2: Player
    public int getMaze(int x, int y) {
        if(0 <= x && x <= WIDTH - 1 && 0 <= y && y <= HEIGHT - 1) {
            return maze[x][y];
        } else {
            return -1;
        }
    }

    public void setMaze(int x, int y, int target) {
        if(0 <= x && x <= WIDTH - 1 && 0 <= y && y <= HEIGHT - 1)
            maze[x][y] = target;
    }

    public void win() {
        running =  false;

        for(int i = 0; i < WIDTH; i++) {
            for(int j = 0; j < HEIGHT; j++) {
                put(new Thing(new Color(0, 0, 0), ' ', this), i, j);
            }
        }

        put(new Thing(new Color(0, 0, 255), 'Y', this), 6, HEIGHT/2 - 1);
        put(new Thing(new Color(0, 0, 255), 'O', this), 7, HEIGHT/2 - 1);
        put(new Thing(new Color(0, 0, 255), 'U', this), 8, HEIGHT/2 - 1);
        put(new Thing(new Color(0, 0, 255), 'W', this), 10, HEIGHT/2 - 1);
        put(new Thing(new Color(0, 0, 255), 'I', this), 11, HEIGHT/2 - 1);
        put(new Thing(new Color(0, 0, 255), 'N', this), 12, HEIGHT/2 - 1);
        put(new Thing(new Color(0, 0, 255), '!', this), 13, HEIGHT/2 - 1);
    }
}
