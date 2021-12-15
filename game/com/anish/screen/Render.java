package game.com.anish.screen;

import game.Main;

public class Render implements Runnable {

    private Main mainActivity;

    public Render(Main mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        while(true) {
            try{
                Thread.sleep(30);
                mainActivity.repaint();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    
}
