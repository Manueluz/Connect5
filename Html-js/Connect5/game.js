let socket = new WebSocket("ws://90.175.129.152:9090/test"); //Start the socket


//Get the button and input (Create and join game)
var button = document.getElementById('connect');
var field = document.getElementById("gameID");

//Add the listeners so the player can create and join games
button.addEventListener("click",loadGameSelector,false);
field.addEventListener("keyup", ({key}) => {
    if (key === "Enter") {
        tryJoinGame();
    }
})


//Loads a player number selector to create a game
function loadGameSelector(){

  //Loads the html with the buttons
  loadHTML('<div id ="padding"><div><p id="numberPlayers"><a id="CreateGreen">[</a><a><strong> Choose number of players</strong></a><a id="CreateGreen"> ]</a></p><br><button id = "startGame2" class = "button1"><strong id="CreateGreen">2</strong><strong> players</strong></button><p></p><button id = "startGame3" class = "button1"><strong id="CreateGreen">3</strong><strong> players</strong></button><p></p><button id = "startGame4" class = "button1"><strong id="CreateGreen">4</strong><strong> players</strong></button><p></p><input type = "text" class = "button1" id = "playerNum" placeholder="Or specify the number"></input></div></div>');

  //Add click listener to each button
  document.getElementById('startGame2').addEventListener("click",startGame2,false);
  document.getElementById('startGame3').addEventListener("click",startGame3,false);
  document.getElementById('startGame4').addEventListener("click",startGame4,false);

  //Custom input for custom player number
  document.getElementById("playerNum").addEventListener("keyup", ({key}) => {
    if (key === "Enter") {
        startGameAny();
    }
  })
}

//Starts the player with a given amount of players
function startGameAny(){
  var num = 2;

  //Get the number input
  var field = document.getElementById('playerNum');
  num = Number(field.value);

  //Check its valid
  if(num < 2){
    return;
  }
  if(num >10){
    return;
  }
  var numC
  if(num < 10){
    numC = "0"+num;
  }

  //Send it to the server
  socket.send("GAME_CREATE_0" + numC + "020");
}

//Create games with X amount of players (2, 3, 4)
function startGame2() {
  socket.send("GAME_CREATE_002020");
}
function startGame3() {
  socket.send("GAME_CREATE_003020");
}
function startGame4() {
  socket.send("GAME_CREATE_004020");
}


//Tries to join a game
function tryJoinGame() {
  var id = field.value; //Get the id from the input
  field.value = "";

  //Ask the server to join
  socket.send("GAME_JOIN_"+id);
}

var ctx;
var canvas;

//Loads everything once the player joins a game
function beginGame(boardSize){

    //Load the Game html content
    loadHTML('<div id="wrapper"><canvas id="canvas" width = "1200" height="1200"></canvas><div id = GameID><p id = "id">#GameID</p><div id="chat"><div id = "messages"></div><div id="writezonediv"><input type="text" name="chat" id="writezone"></div></div></div></div>');

    //Custom input for chat
    document.getElementById("writezone").addEventListener("keyup", ({key}) => {
      if (key === "Enter") {
          socket.send("CHAT_MSG_" + document.getElementById("writezone").value); //Send the message to the server
          document.getElementById("writezone").value = ""; //Clean the input field
      }
    })
    //Get the canvas once it loaded and get its context
    canvas = document.getElementById("canvas");
    if (!canvas.getContext) {
        return;
    }
    ctx = canvas.getContext('2d');

    //add the click listener
    ctx.canvas.addEventListener('click', function(event){
      //####################################################################
      // Evil magic to account for game scrolling
      var totalOffsetX = 0;
      var totalOffsetY = 0;
      var canvasX = 0;
      var canvasY = 0;
      var currentElement = this;

      do{
          totalOffsetX += currentElement.offsetLeft - currentElement.scrollLeft;
          totalOffsetY += currentElement.offsetTop - currentElement.scrollTop;
      }
      while(currentElement = currentElement.offsetParent)

      var mouseX = event.pageX - totalOffsetX;
      var mouseY = event.pageY - totalOffsetY;
      //####################################################################
      //Transform the the canvas coords to the board coords by subtracting the board square size in pixels
      var x = 0;
      var y = 0;

      var canvasWidth = ctx.canvas.clientWidth;
      var canvasHeight = ctx.canvas.clientHeight;

      while (mouseX > (canvasWidth/20)) {
        x = x + 1;
        mouseX = mouseX-(canvasWidth/20);
      }
      while (mouseY > (canvasHeight/20)) {
        y = y + 1;
        mouseY = mouseY-(canvasHeight/20);
      }

      //Send the server the move
      socket.send("MOVE_X_" + x + "_Y_" + y);
    });
}


//Draws a tile onto the map
function drawTile(x,y,id){
  //Get the colors
  ctx.fillStyle = colors[id];
  ctx.strokeStyle = colors[id];

  //Separation between the squares so it looks more like a grid
  var separation = 3;
  var canvasWidth = ctx.canvas.width/20;
  var canvasHeight = ctx.canvas.height/20;

  //draw it!
  ctx.fillRect(x*canvasWidth+separation,y*canvasHeight+separation,canvasWidth-separation*2,canvasHeight-separation*2);
}

//// TODO: gotta add more colors

//Stats the colors for the players
var colors = {};
function generateColors(){
  colors[1] = "#ff8888";
  colors[2] = "#88ff88";
  colors[3] = "#8888ff";
  colors[4] = "#ff88c4";
  colors[5] = "#ffc488";
  colors[6] = "#88ffc4";
  colors[7] = "#ff00b4";
  colors[8] = "#FF7777";
  colors[9] = "#664a2f";
  colors[10]= "#7777ff";
}

var win = 0;
socket.onopen = function(e) {

};

//Handle the server messages
socket.onmessage = function(event) {
  var msg = event.data;
  console.log(msg);
  if(msg == "ERR_404"){ //If the server didnt find the game we asked for
    field.placeholder = "Error: Cant find game!"; //Tell the player
  }

  var tokens = msg.split("_");//Split it cause tokens are easier to work with

  if(tokens.length == 6){
    if(tokens[0] == "GAME" && tokens[1] == "HEADERS" && tokens[2] == "PLAYER" ){ //Valid game headers
      generateColors(); //Start the colors
      beginGame(Number(tokens[5])); //Start the game with the board size
    }
  }
  if(tokens.length == 5){
    if(tokens[0] == "CHAT" && tokens[1] == "MSG"){ //Valid message headers
          document.getElementById("messages").insertAdjacentHTML('beforeend','<p style="color :'+colors[Number(tokens[4])] +'; "> '+ tokens[2]+'</p>') //Add the message to the messages div
    }
  }
  if(tokens.length == 4){
      if(tokens[0] == "GAME" && tokens[2] == "WIN"){ //Valid player win message
      win = Number(tokens[3]); //Update the message thats shown when the socket closes to tell the player who won
    }
  }
  if(tokens.length == 3){
    if(tokens[0] == "GAME" && tokens[1] == "ID"){ //Valid game id message
      document.getElementById("id").innerHTML = "ID:" + tokens[2]; //Update the page id so the player knows the id
    }
  }
  if(tokens.length == 11){
    if(tokens[0] == "MOVE"){ //Valid move message
      canvas.style.borderColor = colors[Number(tokens[10])]; //Change the color to indicate the current player
      drawTile(Number(tokens[2]),Number(tokens[4]),Number(tokens[6])) //Draw the tile

      //Update the board so the player knows its his turn
      if(tokens[8] == "T"){
        canvas.style.backgroundColor ="#3F4145";
      }else{
        canvas.style.backgroundColor ="#202225";
      }
    }
  }

  if(tokens.length == 3){
    if(tokens[0] == "START"){ //Valid game start message
      canvas.style.borderColor = colors[1];

      //Update the board colors
      if(tokens[2] == "T"){
        canvas.style.backgroundColor ="#3F4145";
      }else{
        canvas.style.backgroundColor ="#202225";
      }
    }
  }
};

socket.onclose = function(event) {
  if(win > 0){ //If there is a winner display the win message
    loadHTML('<div id ="padding"><p id="Error">This color won!</p></div>');
    document.getElementById("Error").style.backgroundColor = colors[win];
    return;
  }
  loadHTML('<div id ="padding1"><p id="Error">Connection lost</p></div>'); //if not display an error
};

socket.onerror = function(error) {
  loadHTML('<div id ="padding1"><p id="Error">Connection lost</p></div>'); //Display an error so the player knows he lost connection
};

function loadHTML(page){
  document.getElementById("Body").innerHTML= page; //Loads an html string into the contect div of the page
}
