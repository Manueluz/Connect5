import Managers.GameManager;
import Networking.Listener;

import java.io.IOException;


public class GameServerMain {
    public static void main(String[] args) throws IOException {

        Linker linker = new Linker();
        linker.start(9090);

        CatHttp httpServer = new CatHttp(9092);
        httpServer.startCatServer();

        GameManager manager = new GameManager();
        Listener listener = new Listener(9091,manager);
    }
}
