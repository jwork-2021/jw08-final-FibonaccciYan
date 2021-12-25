package game.com.anish.screen;

import game.asciiPanel.AsciiPanel;

public class WinScreen extends RestartScreen {

    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.write("You won! Press enter to go again.", 0, 0);
    }

}
