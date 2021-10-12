package Game.ChatManager;


import Networking.GameConnection;

import java.util.HashMap;

/**
 * Class that takes care of the in game chat
 * @version 1.0
 * @author Manueluz
 */
public class ChatManager {
    HashMap<GameConnection,Integer> players;


    /**
     * Builder for the chat manager
     * @param players A hashmap with a list of all the players this chat connects
     */
    public ChatManager(HashMap<GameConnection,Integer> players){
        this.players = players;
    }


    /**
     * Sends a message to all the players in the player list
     * @param message The message to send
     * @param authorID The id of the player that sent the message
     */
    public void distributeMessage(String message, int authorID){
        players.forEach((connection, integer) -> connection.sendData("CHAT_MSG_" + message +"_ID_"+authorID));
    }
}
