package game.com.anish.world;

import java.awt.Color;

public class Thing {

    protected World world;

    public Tile<? extends Thing> tile;

    public int getX() {
        return this.tile.getxPos();
    }

    public int getY() {
        return this.tile.getyPos();
    }

    public void setTile(Tile<? extends Thing> tile) {
        this.tile = tile;
    }

    Thing(Color color, char glyph, World world) {
        this.color = color;
        this.glyph = glyph;
        this.world = world;
    }

    private Color color;

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color){
        this.color = color;
    }

    private char glyph;

    public char getGlyph() {
        return this.glyph;
    }

    public void setGlyph(char glyph){
        this.glyph = glyph;
    }

}
