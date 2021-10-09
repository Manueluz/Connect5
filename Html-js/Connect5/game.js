let socket = new WebSocket("ws://90.175.129.152:9090/test");


var button = document.getElementById('connect');
var field = document.getElementById("gameID");


button.addEventListener("click",loadGameSelector,false);
field.addEventListener("keyup", ({key}) => {
    if (key === "Enter") {
        tryJoinGame();
    }
})


function loadGameSelector(){
  loadHTML('<div id ="padding"><div><p id="numberPlayers">[ Choose number of players ]</p><br><button id = "startGame2" class = "button">2 players</button><p></p><button id = "startGame3" class = "button">3 players</button><p></p><button id = "startGame4" class = "button">4 players</button><p></p><input type = "text" class = "button" id = "playerNum" placeholder="Or specify the number"></input></div></div>');
  document.getElementById('startGame2').addEventListener("click",startGame2,false);
  document.getElementById('startGame3').addEventListener("click",startGame3,false);
  document.getElementById('startGame4').addEventListener("click",startGame4,false);
  document.getElementById("playerNum").addEventListener("keyup", ({key}) => {
    if (key === "Enter") {
        startGameAny();
    }
  })
}
function startGameAny(){
  var num = 2;
  var field = document.getElementById('playerNum');
  num = Number(field.value);
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
  socket.send("GAME_CREATE_0" + numC + "020");
}
function startGame2() {
  socket.send("GAME_CREATE_002020");
}
function startGame3() {
  socket.send("GAME_CREATE_003020");
}
function startGame4() {
  socket.send("GAME_CREATE_004020");
}

function tryJoinGame() {
  var id = field.value;
  field.value = "";
  socket.send("GAME_JOIN_"+id);
}

var ctx;
var canvas;
function beginGame(boardSize){
    loadHTML('<div id="wrapper"><canvas id="canvas" width = "1200" height="1200"></canvas><div id = GameID><p id = "id">#GameID</p><div id="chat"><div id="writezonediv"><input type="text" name="chat" id="writezone"></div></div></div></div>');
    canvas = document.getElementById("canvas");
    if (!canvas.getContext) {
        return;
    }
    ctx = canvas.getContext('2d');
    ctx.translate(0.5, 0.5);
    ctx.canvas.addEventListener('click', function(event){
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
        socket.send("MOVE_X_" + x + "_Y_" + y);
    });
}

function drawTile(x,y,id){
  ctx.fillStyle = colors[id];
  ctx.strokeStyle = colors[id];
  var separation = 3;
  var canvasWidth = ctx.canvas.width/20;
  var canvasHeight = ctx.canvas.height/20;
  console.log(canvasWidth/20 + " " + canvasHeight/20);
  ctx.fillRect(x*canvasWidth+separation,y*canvasHeight+separation,canvasWidth-separation*2,canvasHeight-separation*2);
}

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


socket.onmessage = function(event) {
  var msg = event.data;
  console.log(msg);
  if(msg == "ERR_404"){
    field.placeholder = "Error: Cant find game!";
  }

  var tokens = msg.split("_");
  if(tokens.length == 6){
    if(tokens[0] == "GAME" && tokens[1] == "HEADERS" && tokens[2] == "PLAYER" ){
      generateColors();
      beginGame(Number(tokens[5]));
    }
  }
  if(tokens.length == 4){
      if(tokens[0] == "GAME" && tokens[2] == "WIN"){
      win = Number(tokens[3]);
    }
  }
  if(tokens.length == 3){
    if(tokens[0] == "GAME" && tokens[1] == "ID"){
      document.getElementById("id").innerHTML = "ID:" + tokens[2];
    }
  }
  if(tokens.length == 11){
    if(tokens[0] == "MOVE"){
      canvas.style.borderColor = colors[Number(tokens[10])];
      drawTile(Number(tokens[2]),Number(tokens[4]),Number(tokens[6]))
      if(tokens[8] == "T"){
        canvas.style.backgroundColor ="#3F4145";
      }else{
        canvas.style.backgroundColor ="#202225";
      }
    }
  }
  if(tokens.length == 3){
    if(tokens[0] == "START"){
      canvas.style.borderColor = colors[1];
      if(tokens[2] == "T"){
        canvas.style.backgroundColor ="#3F4145";
      }else{
        canvas.style.backgroundColor ="#202225";
      }
    }
  }
};

socket.onclose = function(event) {
  if(win > 0){
    loadHTML('<div id ="padding"><p id="Error">This color won!</p></div>');
    document.getElementById("Error").style.backgroundColor = colors[win];
    return;
  }
  loadHTML('<div id ="padding"><p id="Error">Connection lost</p></div>');
};

socket.onerror = function(error) {
  loadHTML('<div id ="padding"><p id="Error">Connection lost</p></div>');
};

function loadHTML(page){
  document.getElementById("Body").innerHTML= page;
}
