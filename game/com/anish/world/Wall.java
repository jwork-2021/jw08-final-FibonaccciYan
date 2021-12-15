package game.com.anish.world;

import game.asciiPanel.AsciiPanel;

public class Wall extends Thing {

    public Wall(World world) {
        super(AsciiPanel.cyan, (char) 177, world);
    }

}
