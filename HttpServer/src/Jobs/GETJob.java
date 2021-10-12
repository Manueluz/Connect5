package Jobs;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.file.Files;


/**
 * Class for a get job, it will handle a given http get request and then finalize
 *
 * @author Manueluz
 * @version 1.0
 */
public class GETJob implements Runnable {

    HttpExchange httpExchange;


    /**
     * Builder for the get job
     * @param httpExchange The http exchange this job will be handling
     */
    public GETJob(HttpExchange httpExchange){
        this.httpExchange = httpExchange;
    }


    @Override
    public void run() {
        try {
            //Get the requested file
            String requestedFile = httpExchange.getRequestURI().getRawPath().substring(1);
            System.out.println(requestedFile);
            File page;
            if (!requestedFile.contains(".")) {
                page = new File("HttpServer/ServerRoot/" + requestedFile  + "index.html");
            }else {
                page = new File("HttpServer/ServerRoot/" + requestedFile);
            }


            //Add the correct headers for the file type
            if (httpExchange.getRequestURI().getRawPath().substring(1).contains(".js")) {
                httpExchange.getResponseHeaders().add("Content-Type", "application/javascript");
            }
            if (httpExchange.getRequestURI().getRawPath().substring(1).contains(".html")) {
                httpExchange.getResponseHeaders().add("Content-Type", "text/html");
            }
            if (httpExchange.getRequestURI().getRawPath().substring(1).contains(".css")) {
                httpExchange.getResponseHeaders().add("Content-Type", "text/css");
            }

            //Send headers
            httpExchange.sendResponseHeaders(200, page.length());

            //Get the file input stream
            OutputStream out = httpExchange.getResponseBody();

            //Send the file
            out.write(Files.readAllBytes(page.toPath()));

            //Close all
            out.close();
            httpExchange.close();
        }catch (IOException e){
            System.out.println("GET job failed: " + e.getMessage());
        }
    }
}
