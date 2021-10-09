package Networking;

import Logs.SimpleLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Stack;


/**
 * Handles a socket connection providing comfort features as event calling,
 * runs on its own Thread so no need to wait for a connection
 * @author Manueluz
 * @version 1.0
 */
public class GameConnection extends Thread {

    private boolean stop = false;
    private Socket socket;
    private ArrayList<GameEventHandler> eventHandlers;
    private Stack<GameEventHandler> handlersToAdd;
    private PrintWriter networkOut;
    private BufferedReader networkInput;
    private boolean stopping;
    private boolean clearHandlers = false;

    /**
     * Builder for the game connection
     * @param socket The socket that this class will handle
     */
    public GameConnection(Socket socket){
        handlersToAdd = new Stack<>();
        this.socket = socket;
        eventHandlers = new ArrayList<>();
        stopping = false;

        //Get the I/O of the socket
        try {
            networkOut = new PrintWriter(socket.getOutputStream(), true);
            networkInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        start(); //Start to run the thread
    }


    /**
     * Checks if the socket has lost connection
     * @return If the socket has disconnected
     */
    public boolean hasTerminated() {
        return stop;
    }


    /**
     * Adds an event handler that will be called when events
     * such as messages and disconnects occur
     * @param gameEventHandler The handler to add
     */
    public void addEventHandler(GameEventHandler gameEventHandler) {
        handlersToAdd.push(gameEventHandler);
    }


    /**
     * Sets the flag to clear the handlers of the connection
     * once the flag is set the handlers will be cleared once the loop id freed
     */
    public void clearHandlers(){
        clearHandlers = true;
    }


    /**
     * Terminates the connection stopping the thread closing the socket
     * and calling the onDisconnectEvent on the event handlers it has
     */
    public void terminate(){
        if(stopping){return;}
        stopping = true;
        SimpleLogger.log("[Listener]Connection lost");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(GameEventHandler g : eventHandlers){ //Call the disconnect event for all the handlers
            g.onDisconnectEvent(new NetworkEvent(null,this));
        }
        stop = true;
    }


    /**
     * Sends data thought the socket it handles in form of an string
     * @param data The message to send to the other socket
     */
    public void sendData(String data){
        networkOut.println(data);
    }


    /**
     * Run method of the thread, it waits for new messages and also clears the handlers when the flag is set
     * and empties the handlers to add stack adding the handlers to the game connection
     */
    @Override
    public void run() {
        while(!stop){
            if(clearHandlers){ //if the clear flag is set clear the handlers
                eventHandlers.clear();
                clearHandlers = false;
            }
            while (!handlersToAdd.isEmpty()){//While there are handlers to add in the stack add them
                eventHandlers.add(handlersToAdd.pop());
            }

            String inputLine;
            try {
                if( (inputLine = networkInput.readLine()) != null){//Wait for a new message
                    if(inputLine.equals("TERMINATE")){//If the remote endpoint asks us to terminate
                        terminate();
                        break;
                    }
                    for (GameEventHandler g : eventHandlers) { //Pass the message in form of an event to the event handlers
                        g.onMessageEvent(new NetworkEvent(inputLine, this));
                    }
                 }
            } catch (IOException e) {
                terminate();
            }
        }
    }
}
