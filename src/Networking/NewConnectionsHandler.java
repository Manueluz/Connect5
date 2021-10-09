package Networking;

import Game.GameManager;
import Logs.SimpleLogger;

public class NewConnectionsHandler extends GameEventHandler {
    private GameManager manager;
    public NewConnectionsHandler(GameManager manager){
        this.manager = manager;
    }
    @Override
    public void onMessageEvent(NetworkEvent event) {
        String[] tokens = event.getMessage().split("_");
        if(tokens.length == 3){
            if(tokens[0].equals("GAME") && tokens[1].equals("JOIN") && tokens[2].length() == 6){
                SimpleLogger.log("[NewConnectionsHandler]Player trying to connect id:" + tokens[2]);
                manager.findGame(event.getConnection(),tokens[2]);
            }
            if(tokens[0].equals("GAME") && tokens[1].equals("CREATE") && tokens[2].length() == 6){
                SimpleLogger.log("[NewConnectionsHandler]Attempting to create new game");
                manager.createGame(event.getConnection(),Integer.parseInt(tokens[2].substring(1,3)),Integer.parseInt(tokens[2].substring(3,6)));
            }
        }
    }

    @Override
    public void onDisconnectEvent(NetworkEvent event) {

    }
}
