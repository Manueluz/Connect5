package Networking;

import Managers.GameManager;
import Logs.SimpleLogger;
import Networking.NetworkHandlers.NewConnectionsHandler;

import java.io.IOException;
import java.net.*;


/**
 * Listener for new socket connections
 * @version 1.0
 * @author Manueluz
 */
public class Listener extends Thread{

    private ServerSocket serverSocket;
    private boolean stop = false;
    private GameManager manager;


    /**
     * Builder for the listener
     * @param port Port in which the listener will be listening on.
     * @param manager Game manager for the new connections to join or create a game
     */
    public Listener(int port,GameManager manager){
        try {
            serverSocket = new ServerSocket(port); //Start the socket
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.manager = manager;
        start(); //Start running the thread
    }


    /**
     * Stops the thread
     */
    public void terminate(){
        stop=true;
    }


    /**
     * Run method of the thread it waits for new connections and then
     * links them with an event handler and the game manager
     */
    @Override
    public void run() {
        while(!stop){
            try {
                GameConnection connection = new GameConnection(serverSocket.accept()); //Create a game connection
                connection.addEventHandler( new NewConnectionsHandler(manager)); //Set an events handler that takes care of joining and creating games
                SimpleLogger.log("[Listener]New connection made!");
            } catch (IOException e) {
                terminate();
            }
        }
    }
}
