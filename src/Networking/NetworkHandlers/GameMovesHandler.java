package Networking.NetworkHandlers;

import Game.Row5.Row5Lolies;
import Networking.NetworkEvent;

/**
 * Event listener that receives events related to the game and sends
 * them to the game itself so it can deal with them
 * @author Manueluz
 * @version 1.0
 */

public class GameMovesHandler extends NetworkEventHandler {

    //Gotta hold the game we are listening for
    private Row5Lolies game;


    /**
     * Builder for the game listener
     * @param game The game we are listening for
     */
    public GameMovesHandler(Row5Lolies game){
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
