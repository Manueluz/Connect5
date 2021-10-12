import Jobs.GETJob;
import com.sun.net.httpserver.*;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;


/**
 * Basic http server
 *
 * @author Manueluz
 * @version 1.0
 */
public class CatHttp {
    HttpServer httpServer;
    ArrayList<HttpContext> contexts;


    /**
     * Builder for the cat http server
     * @param port The port the server will be on
     */
    public CatHttp(int port) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(port),0);
        contexts = new ArrayList<>();
        contexts.add(httpServer.createContext("/"));
        contexts.get(0).setHandler(new CatHttpBasicHandler());
    }


    /**
     * Starts the http server
     */
    public void startCatServer(){
        httpServer.start();
    }


    /**
     * Stops the http server
     */
    public void stopCatServer(){
        httpServer.stop(0);
    }


    /**
     * Handler for a basic http request
     */
    private static class CatHttpBasicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange){
            if(httpExchange.getRequestMethod().equals("GET")){
                Thread jobThread = new Thread(new GETJob(httpExchange));
                jobThread.start();
            }
        }
    }
}
