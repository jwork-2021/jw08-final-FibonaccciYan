package game.Reactor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import game.com.anish.screen.OnlineScreen;
import game.com.anish.world.Bomb;
import game.com.anish.world.Player;

public class Client implements Runnable {

    Socket clientSocket;
    OnlineScreen os;

    public Client(OnlineScreen os) {
        this.os = os;
        try {
            clientSocket = new Socket("localhost", 7070);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            outToServer.write(message);
            outToServer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String receiveMessage() {
        String message = "";
        try {
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            message = inFromServer.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public void run() {
        System.out.println("Clinet start: " + this.os.identifier);

        int count = 0; //DIE count

        while (os.player.getStatus()) {
            // System.out.println("Client receiving message...");
            String message = this.receiveMessage();

            if(message.isEmpty()){
                continue;
            }
            System.out.println(message);

            String[] orders = message.split(" ");

            if(orders[0].equals(os.identifier)){
                continue;
            }

            Player player;
            
            if (orders[0].equals("player1")) {
                player = os.player1;
            } else if (orders[0].equals("player2")) {
                player = os.player2;
            } else if (orders[0].equals("player3")) {
                player = os.player3;
            } else if (orders[0].equals("player4")) {
                player = os.player4;
            } else {
                System.out.println("Error player: " + orders[0]);
                continue;
            }

            int xPos = player.getxPos();
            int yPos = player.getyPos();

            if (orders[1].equals("MOVE")) {

                if (orders[2].equals("A")) {
                    player.moveTo(xPos, yPos, xPos - 1, yPos);
                    player.setxPos(xPos - 1);
                    player.setyPos(yPos);
                } else if (orders[2].equals("W")) {
                    player.moveTo(xPos, yPos, xPos, yPos - 1);
                    player.setxPos(xPos);
                    player.setyPos(yPos - 1);
                } else if (orders[2].equals("D")) {
                    player.moveTo(xPos, yPos, xPos + 1, yPos);
                    player.setxPos(xPos + 1);
                    player.setyPos(yPos);
                } else if (orders[2].equals("S")) {
                    player.moveTo(xPos, yPos, xPos, yPos + 1);
                    player.setxPos(xPos);
                    player.setyPos(yPos + 1);
                } else {
                    System.out.println("Error move: " + orders[2]);
                    continue;
                }

            } else if (orders[1].equals("SET")) {

                Bomb bomb = new Bomb(os.world, xPos, yPos);
                os.world.put(bomb, xPos, yPos);
                os.world.setMaze(xPos, yPos, 3);
                os.world.entities.add(bomb);
                os.exec.execute(bomb);

            } else if (orders[1].equals("DIE")) {

                player.setStatus(false);

                count++;
                if(count == 3) {
                    os.world.gameWin();
                }

            } else {
                System.out.println("Error order: " + orders[1]);
            }
        }

        this.sendMessage(this.os.identifier + " DIE");
        os.world.gameOver();
        os.exec.shutdown(); 
        System.out.println("Clinet end: " + this.os.identifier);
    }
}
