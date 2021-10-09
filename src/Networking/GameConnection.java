package Networking;

import Logs.SimpleLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Stack;

public class GameConnection extends Thread {

    private boolean stop = false;
    private Socket socket;
    private ArrayList<GameEventHandler> eventHandlers;
    private Stack<GameEventHandler> handlersToAdd;
    private PrintWriter networkOut;
    private BufferedReader networkInput;
    private boolean stopping;

    public GameConnection(Socket socket){
        handlersToAdd = new Stack<>();
        this.socket = socket;
        eventHandlers = new ArrayList<>();
        stopping = false;
        try {
            networkOut = new PrintWriter(socket.getOutputStream(), true);
            networkInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        start();
    }
    public boolean hasTerminated() {
        return stop;
    }
    private boolean clearHandlers = false;
    public void clearHandlers(){
        clearHandlers = true;
    }

    public void addEventHandler(GameEventHandler gameEventHandler) {
        handlersToAdd.push(gameEventHandler);
    }

    public void terminate(){
        if(stopping){return;}
        stopping = true;
        SimpleLogger.log("[Listener]Connection lost");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(GameEventHandler g : eventHandlers){
            g.onDisconnectEvent(new NetworkEvent(null,this));
        }
        stop = true;
    }
    public void sendData(String data){
        networkOut.println(data);
    }
    @Override
    public void run() {
        while(!stop){
            if(clearHandlers){
                eventHandlers.clear();
                clearHandlers = false;
            }
            while (!handlersToAdd.isEmpty()){
                eventHandlers.add(handlersToAdd.pop());
            }
            String inputLine;
            try {
                if( (inputLine = networkInput.readLine()) != null){
                    if(inputLine.equals("TERMINATE")){
                        terminate();
                        break;
                    }
                    for (GameEventHandler g : eventHandlers) {
                        g.onMessageEvent(new NetworkEvent(inputLine, this));
                    }
                 }
            } catch (IOException e) {
                terminate();
            }
        }
    }
}
