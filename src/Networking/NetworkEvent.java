package Networking;

public class NetworkEvent {
    private String message;
    private GameConnection connection;
    public NetworkEvent(String message,GameConnection connection){
        this.message = message;
        this.connection = connection;
    }
    public String getMessage() {
        return message;
    }

    public GameConnection getConnection() {
        return connection;
    }
}
