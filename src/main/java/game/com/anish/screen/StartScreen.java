package game.com.anish.screen;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.Executors;

import game.asciiPanel.AsciiPanel;
import game.com.anish.world.Bomb;
import game.com.anish.world.Player;
import game.com.anish.world.Thing;
import game.com.anish.world.World;

import java.awt.event.KeyEvent;

public class StartScreen extends RestartScreen {

    LocalScreen ws = new LocalScreen();

    private void load() {
        try(ObjectInputStream ois = new ObjectInputStream(
            new FileInputStream("save.data"))) {
                this.ws.exec.shutdownNow();
                this.ws.exec = Executors.newCachedThreadPool();
                this.ws.world = (World)ois.readObject();
                ois.close();
                for(Thing entity : this.ws.world.entities) {
                    if(entity.getClass().getSimpleName().equals("Player")) {
                        Player temp = (Player)entity;
                        if(temp.getIdentifier() == 1)
                            this.ws.player1 = temp;
                        else if(temp.getIdentifier() == 2)
                            this.ws.player2 = temp;
                        this.ws.exec.execute(temp);
                    } else if (entity.getClass().getSimpleName().equals("Bomb")) {
                        this.ws.exec.execute((Bomb)entity);
                    }
                }
        } catch(IOException io) {
            io.printStackTrace();
        } catch(ClassNotFoundException cnf) {
            cnf.printStackTrace();
        }
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.writeCenter("Welcome!", 3, AsciiPanel.brightRed);
        terminal.writeCenter("1  LocalGame  ", 7, AsciiPanel.brightYellow);
        terminal.writeCenter("2 InternetGame", 9, AsciiPanel.brightYellow);
        terminal.writeCenter("3    Load     ", 11, AsciiPanel.brightYellow);
        terminal.writeCenter("4 Instruction ", 13, AsciiPanel.brightYellow);
        terminal.writeCenter("Press 1234 to choose", 19, AsciiPanel.brightWhite);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        switch (key.getKeyCode()) {
            case KeyEvent.VK_1:
                return new LocalScreen();
            case KeyEvent.VK_2:
                return new SelectScreen();
            case KeyEvent.VK_3:
                load();
                return this.ws;
            case KeyEvent.VK_4:
                return new InstructionScreen();
            default:
                return this;
        }
    }

}
