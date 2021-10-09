package Networking;

import Game.GameManager;
import Logs.SimpleLogger;


/**
 * Event listener that handles new connections creating a new game or joining a game
 * @version 1.0
 * @author Manueluz
 */
public class NewConnectionsHandler extends GameEventHandler {

    private GameManager manager;


    /**
     * Builder for the event listener
     * @param manager The game manager that will hold the games that the listener will try to create and join
     */
    public NewConnectionsHandler(GameManager manager){
        this.manager = manager;
    }


    /**
     * Handles the message events and links the connection
     * to a new game or joins it to an already created game
     * @param event The event that this class handles
     */
    @Override
    public void onMessageEvent(NetworkEvent event) {
        String[] tokens = event.getMessage().split("_");//Split it in tokens so its easier to work with
        if(tokens.length == 3){
            if(tokens[0].equals("GAME") && tokens[1].equals("JOIN") && tokens[2].length() == 6){ //If its a join ask the game manager to find the game
                SimpleLogger.log("[NewConnectionsHandler]Player trying to connect id:" + tokens[2]);
                manager.findGame(event.getConnection(),tokens[2]); //Try to join the game
            }
            if(tokens[0].equals("GAME") && tokens[1].equals("CREATE") && tokens[2].length() == 6){//If its a valid create game message
                SimpleLogger.log("[NewConnectionsHandler]Attempting to create new game");
                manager.createGame(event.getConnection(),Integer.parseInt(tokens[2].substring(1,3)),Integer.parseInt(tokens[2].substring(3,6))); //Create a new game the manager will take care of the rest
            }
        }
    }


    /**
     * Ignore this
     */
    @Override
    public void onDisconnectEvent(NetworkEvent event) {

    }
}
