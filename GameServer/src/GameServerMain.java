import Logs.SimpleLogger;
import Managers.GameManager;
import Networking.Listener;

import java.io.IOException;


public class GameServerMain {
    public static void main(String[] args) throws IOException {
        SimpleLogger.log("[Main]Starting the websocket");
        Linker linker = new Linker();
        linker.start(9090);
        SimpleLogger.log("[Main]Websocket started!");
        SimpleLogger.log("[Main]Starting the CatHttp server");
        CatHttp httpServer = new CatHttp(9092);
        httpServer.startCatServer();
        SimpleLogger.log("[Main]CatHttp server started!");
        SimpleLogger.log("[Main]Starting the game server");
        GameManager manager = new GameManager();
        Listener listener = new Listener(9091,manager);
        SimpleLogger.log("[Main]Game server Started!");
    }
}
