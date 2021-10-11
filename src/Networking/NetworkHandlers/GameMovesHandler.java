package Networking.NetworkHandlers;

import Game.Connect5.Connect5;
import Networking.NetworkEvent;

/**
 * Event listener that receives events related to the game and sends
 * them to the game itself so it can deal with them
 * @author Manueluz
 * @version 1.0
 */

public class GameMovesHandler extends NetworkEventHandler {

    //Gotta hold the game we are listening for
    private Connect5 game;


    /**
     * Builder for the game listener
     * @param game The game we are listening for
     */
    public GameMovesHandler(Connect5 game){
        this.game = game;
    }


    /**
     * When we get a message check if its a movement, if it is tell the game the move
     * @param event The network event with all the necessary info
     */
    @Override
    public void onMessageEvent(NetworkEvent event) {
        String[] tokens = event.getMessage().split("_"); //Split it in tokens so its easier to work with

        if(tokens.length == 5){
            if(tokens[0].equals("MOVE") && tokens[1].equals("X") && tokens[3].equals("Y")){ //If its a valid movement message
                game.move(Integer.parseInt(tokens[2]),Integer.parseInt(tokens[4]),event.getConnection()); //Tell the game the move
            }
        }
        if(tokens.length == 3){
            if(tokens[0].equals("CHAT") && tokens[1].equals("MSG")){ //If its a valid chat message
                game.handleMessage(event.getConnection(),tokens[2]); //Handle the message
            }
        }
    }


    /**
     * Tells the game there has been a disconnect so it can deal with it
     * @param event The network event with all the necessary info
     */
    @Override
    public void onDisconnectEvent(NetworkEvent event) {
        game.handleDisconnect(event.getConnection());
    }
}
