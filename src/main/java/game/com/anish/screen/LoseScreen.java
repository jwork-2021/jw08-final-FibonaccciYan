package game.com.anish.screen;

import game.asciiPanel.AsciiPanel;

public class LoseScreen extends RestartScreen {

    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.write("You lost! Press enter to try again.", 0, 0);
    }

}
