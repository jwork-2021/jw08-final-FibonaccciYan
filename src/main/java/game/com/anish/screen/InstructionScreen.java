package game.com.anish.screen;

import game.asciiPanel.AsciiPanel;
import java.awt.event.KeyEvent;

public class InstructionScreen extends RestartScreen {

    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.writeCenter("INSTRUCTION", 2, AsciiPanel.brightRed);
        terminal.write("This is a simple BNB", 0, 5, AsciiPanel.brightYellow);
        terminal.write(". In this game, you ", 0, 6, AsciiPanel.brightYellow);
        terminal.write("can press WASD/ARROW", 0, 7, AsciiPanel.brightYellow);
        terminal.write("to move charactor or", 0, 8, AsciiPanel.brightYellow);
        terminal.write(" ENTER to set bombs.", 0, 9, AsciiPanel.brightYellow);
        terminal.write("Bomb will explode in", 0, 10, AsciiPanel.brightYellow);
        terminal.write("3 seconds. You will ", 0, 11, AsciiPanel.brightYellow);
        terminal.write("also get BOOMED. Use", 0, 12, AsciiPanel.brightYellow);
        terminal.write("bomb to break walls.", 0, 13, AsciiPanel.brightYellow);
        terminal.write("Hope you enjoy!", 0, 15, AsciiPanel.brightYellow);
        terminal.write("(return with ENTER)", 0, 19, AsciiPanel.brightWhite);
    }
    
    @Override
    public Screen respondToUserInput(KeyEvent key) {
        switch(key.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                return new StartScreen();
            default:
                return this;
        }
    }

}