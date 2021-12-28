package game.com.anish.screen;

import game.asciiPanel.AsciiPanel;
import java.awt.event.KeyEvent;

public class SelectScreen extends RestartScreen {
    
    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.writeCenter("CHOOSE TO BE", 6, AsciiPanel.brightRed);
        terminal.writeCenter("1 Server", 9, AsciiPanel.brightYellow);
        terminal.writeCenter("2 Client", 11, AsciiPanel.brightYellow);
        terminal.write("(return with ENTER)", 0, 19, AsciiPanel.brightWhite);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        switch(key.getKeyCode()) {
            case KeyEvent.VK_1:
                return new OnlineScreen(true);
            case KeyEvent.VK_2:
                return new OnlineScreen(false);
            case KeyEvent.VK_ENTER:
                return new StartScreen();
            default:
                return this;
        }
    }
}
