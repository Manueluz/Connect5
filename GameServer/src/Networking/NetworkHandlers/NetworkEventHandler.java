package Networking.NetworkHandlers;

import Networking.NetworkEvent;


/**
 * Abstract class for a game event handler
 * @author Manueluz
 * @version 1.0
 */
public abstract class NetworkEventHandler {


    /**
     * Method to call an message event
     * @param event the message event
     */
    public abstract void onMessageEvent(NetworkEvent event);


    /**
     * Method to call an disconnect event
     * @param event the disconnect event
     */
    public abstract void onDisconnectEvent(NetworkEvent event);
}
