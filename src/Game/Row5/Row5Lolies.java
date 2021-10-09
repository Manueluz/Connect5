package Game.Row5;

import Game.GameManager;
import Networking.GameConnection;
import Networking.NetworkHandlers.GameMovesHandler;

import java.util.HashMap;
import java.util.Map;
/**
 * Main class of the connect five game
 * Holds all the logic of the game + so connection handling
 *
 * @version 1.0
 * @author Manueluz
 */
public class Row5Lolies {

    private HashMap<GameConnection,Integer> players;
    private int gameSize;
    private int boardSize;
    private boolean inProgress;
    private int[][] board; //Grid storing the ids of the players
    private int currentID;
    private int currentMove;
    private String gameId;
    private GameManager manager;
    private Boolean gameEnding;


    /**
     * Constructor for a new game
     * @param gameSize The number of players of the game
     * @param boardSize The size of the board the game will be played on
     * @param gameId The id the game manager gave the game
     * @param manager The manager that's in charge of this game
     */
    public Row5Lolies(int gameSize, int boardSize , String gameId , GameManager manager){
        this.boardSize = boardSize;
        this.gameSize = gameSize;
        currentID = 1;
        currentMove = 1;
        players = new HashMap<>();
        inProgress = false;
        this.gameId = gameId;
        this.manager = manager;
        gameEnding = false;
    }


    public boolean isInProgress() {
        return inProgress;
    }


    /**
     * Ends the game, cleaning up everything related to the game so its targeted by GC
     * @param winnerId The player that won the game
     */
    public void endGame(int winnerId){
        if(gameEnding){return;}//If we are already on the process of ending skip
        gameEnding = true;
        manager.removeGame(gameId);//Remove our reference from the manager

        //Loop thought the players to clean their handlers and to tell them who won
        for (Map.Entry<GameConnection, Integer> entry : players.entrySet()) {
            GameConnection player = entry.getKey();
            player.sendData("GAME_ENDED_WIN_"+winnerId);
            player.clearHandlers();
            player.terminate();//Make the thread stop
        }

        //Clean the players so they too are targeted by GC
        players.clear();
    }


    /**
     * Adds a player to the game if there is room for him
     * @param connection The player to add to the game if there is room
     */
    public void addPlayer(GameConnection connection){
        //If its in progress
        if(isInProgress()){
            connection.sendData("ERR_222"); //Tell them game is already going on
            return;
        }

        //If we got room
        if(players.size() < gameSize){
            players.put(connection,currentID); //Associate him with an id
            connection.addEventHandler(new GameMovesHandler(this)); //Register the movement listener so we know the player moves
            currentID++;
            sendHeaders(connection); //Send him info about the game
        }

        //If we full start
        if(players.size() == gameSize){
            startGame();
        }
    }


    /**
     * Called by players when they make a move to place an square on the board
     * @param x Coords of the move
     * @param y Coords of the move
     * @param connection The player doing the move
     */
    public void move(int x, int y, GameConnection connection){
        //If we haven't started skip
        if(!isInProgress()){
            return;
        }
        //Get his id
        int id = players.get(connection);
        try{
            if(id == currentMove && board[x][y] == 0){ //Check its his turn and the zone is empty
                board[x][y] = id;
                incrementCurrentMove(); //Move on onto the next id
                notifyPlayers(x,y,id,checkWin(x,y,id)); //Tell everyone the move

                //Check for wins
                if(checkWin(x,y,id)){
                    Thread.sleep(100);
                    endGame(id);//If yes then we end the game
                }

                return;
            }
        }catch (ArrayIndexOutOfBoundsException | InterruptedException ignore){} //movement might be illegal

        connection.sendData("INV"); //Tell the player his move was invalid
    }


    /**
     * Handles the disconnection of a player
     * Removing him from the list and skipping his move if it was the players turn
     * @param connection The player that disconnected
     */
    public void handleDisconnect(GameConnection connection){

        if(gameEnding){return;} //if its ending its normal for players to disconnect as they are being terminated so we ignore it

        if(!players.isEmpty() && players.get(connection) != null){
            if(players.get(connection) == currentMove){//If it was his turn
                incrementCurrentMove();//Skip turn
                notifyPlayers();//Tell the rest
            }
            players.remove(connection);
            if(players.size() == 1){//If only one player remaining give him the win
                endGame((Integer) players.values().toArray()[0]);
            }else if(players.size() < 1){ //If none are remaining just close the game
                endGame(0);
            }
        }
    }


    /**
     * Starts the game once all the players are there
     */
    private void startGame(){
        inProgress = true;
        genBoard(boardSize); //Create the board
        notifyPlayers(); //Tell them its started
    }


    /**
     * Increments the current turn and skips the turn if the players has disconnected
     */
    private void incrementCurrentMove(){
        currentMove++;
        if(currentMove > gameSize){ //Loop around
            currentMove = 1;
        }
        if(!players.containsValue(currentMove)){incrementCurrentMove();} //If the player has disconnected recursion!
    }


    /**
     * Notifies the players about a move and tells them if its their turn or not
     * @param x Coords of the move
     * @param y Coords of the move
     * @param id id of the player making the move
     * @param win If its a winner move or not
     */
    private void notifyPlayers(int x, int y, int id, boolean win){
        for (Map.Entry<GameConnection, Integer> entry : players.entrySet()) {
            GameConnection player = entry.getKey();
            Integer key = entry.getValue();
            if (player.hasTerminated()) {continue; } //If he has disconnected skip him;

            String turn = (key == currentMove)&&(!win) ? "T" : "F"; //Check if its his turn using his id also if its a winner move its no ones turn

            player.sendData("MOVE_X_" + x + "_Y_" + y + "_ID_"+ id +"_TURN_" + turn + "_NTURN_"+currentMove); //Send them the move
        }
    }


    /**
     * Updates the turn of the players
     * Useful for when the game starts and to notify disconnects
     */
    private void notifyPlayers(){
        players.forEach((player,key) ->{
            String turn = key == currentMove ? "T" : "F"; //Check if it is their turn
            player.sendData("START_TURN_"+turn); //Send them the data
        });
    }


    /**
     * Gens the board to play the game
     * @param boardSize The size of the game board
     */
    private void genBoard(int boardSize){
        board = new int[boardSize][boardSize];
        for(int x = 0; x < boardSize; x++){
            for(int y = 0; y < boardSize; y++){
                board[x][y] = 0;
            }
        }
    }


    /**
     *Checks if a move is a winner move
     * @param x Coords of the move
     * @param y Coords of the move
     * @param id Id of the player making the move
     */
    private boolean checkWin(int x,int y,int id){
        return checkRow(x,y,-1,-1,id)|| //UP LEFT
                checkRow(x,y,0,-1,id)|| //UP
                checkRow(x,y,1,-1,id)|| //UP RIGHT
                checkRow(x,y,-1,0,id);  //LEFT
    }


    /**
     * Checks the row in a certain direction to search for 5 squares in a row
     * It first travels the coords in the step direction until there is no more cells in that direction
     * then it turns around and counts the cells until there is no more cells in the direction
     * @param x,y Coords of the move
     * @param xStep,yStep The direction in witch to move to look for winner rows
     * @param id Id of the player making the move
     */
    private boolean checkRow(int x, int y, int xStep,int yStep,int id){
        int cellCount = 1 ;//Last cell when moving the the step direction never gets count so we start at 1 to make up for that
        while(checkCell(x+xStep,y+yStep,id)){ //travel in the step direction until there aren't more cells
            x += xStep;
            y += yStep;
        }
        while(checkCell(x-xStep,y-yStep,id)){//Travel back counting the cells until there aren't more cells
            x -= xStep;
            y -= yStep;
            cellCount++;
        }
        return cellCount >= 5;//If we got 5 in row the player won
    }


    /**
     * Checks a certain cell to see if a player has an square on it
     * @param x,y Coords of the cell
     * @param id Id of the player we are cheeking the cell for
     */
    private boolean checkCell(int x, int y, int id){
        try{
           return board[x][y] == id;
        }catch (ArrayIndexOutOfBoundsException ignore){} //Check might be out of the bounds of the array;
        return false;
    }


    /**
     * Sends the player info about the game
     * @param connection The player to which send the information about the game
     */
    private void sendHeaders(GameConnection connection){
        connection.sendData("GAME_HEADERS_PLAYER_" + gameSize +"_BOARD_"+boardSize);
        connection.sendData("GAME_ID_"+gameId);
    }
}
