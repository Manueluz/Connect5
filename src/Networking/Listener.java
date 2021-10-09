package Networking;

import Game.GameManager;
import Logs.SimpleLogger;

import java.io.IOException;
import java.net.*;

public class Listener extends Thread{

    private ServerSocket serverSocket;
    private boolean stop = false;
    private GameManager manager;

    public Listener(int port,GameManager manager){
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.manager = manager;
        start();
    }

    public void terminate(){
        stop=true;
    }

    @Override
    public void run() {
        while(!stop){
            try {
                GameConnection connection = new GameConnection(serverSocket.accept());
                connection.addEventHandler( new NewConnectionsHandler(manager));
                SimpleLogger.log("[Listener]New connection made!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
