package game.com.anish.screen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Executors;

import game.asciiPanel.AsciiPanel;
import game.com.anish.world.Bomb;
import game.com.anish.world.Player;
import game.com.anish.world.Thing;
import game.com.anish.world.World;
import java.awt.event.KeyEvent;

public class EscScreen extends RestartScreen {

    private WorldScreen ws;

    EscScreen(WorldScreen worldScreen) {
        this.ws = worldScreen;
    }

    private void save() {
        try(ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream(new File("save.data")))) {
                oos.writeObject(this.ws.world);
                oos.flush();
                oos.close();
        } catch(IOException io) {
            io.printStackTrace();
        }
    }

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
        terminal.writeCenter("1 Restart", 7, AsciiPanel.brightYellow);
        terminal.writeCenter("2  Load  ", 9, AsciiPanel.brightYellow);
        terminal.writeCenter("3  Save  ", 11, AsciiPanel.brightYellow);
        terminal.writeCenter("4  Menu  ", 13, AsciiPanel.brightYellow);
        terminal.write("Press Esc to back", 0, 19, AsciiPanel.brightWhite);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        switch(key.getKeyCode()) {
            case KeyEvent.VK_1:
                return new WorldScreen();
            case KeyEvent.VK_2:
                load();
                return this.ws;
            case KeyEvent.VK_3:
                save();
                return this.ws;
            case KeyEvent.VK_4:
                return new StartScreen();
            case KeyEvent.VK_ESCAPE:
                return this.ws;
            default:
                return this; 
        }
    }
}
