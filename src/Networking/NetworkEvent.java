package Networking;


import Networking.GameConnection;

/**
 * Network event, packet that holds all the info of a network event
 * @version 1.0
 * @author Manueluz
 */
public class NetworkEvent {

    private String message;
    private GameConnection connection;


    /**
     * Builder for the event
     * @param message The message of the event
     * @param connection The connection that created the event
     */
    public NetworkEvent(String message,GameConnection connection){
        this.message = message;
        this.connection = connection;
    }


    /**
     * @return the message of the event
     */
    public String getMessage() {
        return message;
    }


    /**
     * @return the game connection that created the event
     */
    public GameConnection getConnection() {
        return connection;
    }
}
