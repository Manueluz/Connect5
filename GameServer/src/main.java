import Managers.GameManager;
import Networking.Listener;


public class main {
    public static void main(String[] args){
        Linker linker = new Linker();
        linker.start(9090);
        GameManager manager = new GameManager();
        Listener listener = new Listener(9091,manager);
    }
}
