package Managers;

import Game.Connect5.Connect5;
import Networking.GameConnection;
import Logs.SimpleLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Game manager that holds and handles the games and player connections to them
 *
 * @version 1.0
 * @author Manueluz
 */

public class GameManager{

    private HashMap<String, Connect5> games;
    private ArrayList<Connect5> publicGames;

    public GameManager(){
        publicGames = new ArrayList<>();
        games = new HashMap<>();
    }


    /**
     * Searches for a public game for the given connection, if there are none available public games it creates a new public game.
     * @param connection the player trying to join a public game
     */
    public void joinPublicGame(GameConnection connection){
        if(publicGames.isEmpty()){//If there aren't any games going on create a new one
            publicGames.add(new Connect5(2,20,generateID(),this));
            SimpleLogger.log("[GameManager]Created new public game");
        }
        for (Connect5 game : publicGames) {//Find a game that is not in progress
            if (!game.isInProgress()) {
                game.addPlayer(connection);
                SimpleLogger.log("[GameManager]Player connected to public game");
                return;
            } else {
                publicGames.remove(game);
                if (publicGames.isEmpty()) { //Check if after removing a game the list becomes empty, if yes create a game and add the player to it
                    publicGames.add(new Connect5(2, 20, generateID(), this));
                    SimpleLogger.log("[GameManager]Created new public game");
                    publicGames.get(0).addPlayer(connection);
                    SimpleLogger.log("[GameManager]Player connected to public game");
                }
            }
        }
    }


    /**
     * Creates a game with a given player number and a board size and adds the creator of the game to the game
     * @param connection The creator of the game to be added to the game.
     * @param size the number of players of the game.
     * @param boardSize the size of the board the game will be played on
     */
    public void createGame(GameConnection connection, int size, int boardSize){
        String id = generateID();//Get an id for the new game
        Connect5 game = new Connect5(size,boardSize,id,this);
        game.addPlayer(connection);
        connection.clearHandlers();//Clean the player event handlers so they don't interfere
        games.put(id,game);
        SimpleLogger.log("[GameManager]Created new game with id:" + id + " number of players:" + size + " size of the board:" + boardSize);
    }


    /**
     *Finds a game for a connection using a game id, if it finds a game with the given id it links the connection to the game.
     * @param connection The connection to which link the game if found
     * @param id The id of the game to look for
     */
    public void findGame(GameConnection connection, String id){
        if(games.containsKey(id)){
            SimpleLogger.log("[GameManager]Player connected to id:" + id);
            games.get(id).addPlayer(connection);   //Add the player to the game
            connection.clearHandlers();   //Clean the player event handlers so they don't interfere
        }else{
            SimpleLogger.log("[GameManager]Player used invalid id");
            connection.sendData("ERR_404"); //Let them know the game doesn't exist
        }
    }


    /**
     * Removes a game if its on the list
     * @param id the game id of the game that you want to remove
     */
    public void removeGame(String id){
        if(games.containsKey(id)) {
            SimpleLogger.log("[GameManager]Game Ended!");
            games.remove(id);
        }
    }


    /**
     *Generates a new id for a new game and checks the id is not on the list already.
     * @return random generated id for the game (6-Digits A-Z 1-9)
     */
    private String generateID() {
        Random rand = new Random();
        char[] text = new char[6];

        //Loop 6 times each time selecting a random ascii char to join to the string
        for (int i = 0; i < 6; i++) {
            text[i] = "QWERTYUIOPASDFGHJKLZXCVBNM147852369".charAt(rand.nextInt("QWERTYUIOPASDFGHJKLZXCVBNM147852369".length()));
        }

        //If that id is already in use recursion!
        if(games.containsKey(new String(text))){
            return generateID();
        }
        return new String(text);
    }
}
