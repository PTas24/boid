var wsUri = 'ws://localhost:8080/websocket/boid-positions';
var postUri = 'http://localhost:8080/boid';
var startSimulation = "start simulation";
var stopSimulation = "stop simulation";

var canvasWidth = 200;
var canvasHeight = 200;
var canvasMargin = 10;
var speedAdjust = 1;
var numOfBoids = 75;
var cohesionRange = 200;
var separationRange = 20;
var alignmentRange = 100;
var cohesionFactor = 0.1;
var separationFactor = 0.035;
var alignmentFactor = 0.125;
var speedLimit = 12;
var simulationSpeed = 30;
var initialMaxSpeed = 4;

var numOfB = document.getElementById("numOfBoidsId");
var output = document.getElementById("numBoidsValue");
output.innerHTML = numOfB.value;

numOfB.oninput = function() {
  output.innerHTML = this.value;
}

window.onload = () => {
  // Make sure the canvas always fills the whole window
  window.addEventListener("resize", sizeCanvas, false);
  sizeCanvas();
};

const websocket = new WebSocket('ws://localhost:8080/websocket/boid-positions');
console.log('websocket state: ', websocket.readyState);

websocket.onmessage = function (e) {
    console.log(e.data);
    onMessage(e.data)
}

function onMessage(data) {
    clearTheCanvas();
    drawBoids(data);
}

function clearTheCanvas() {
  var canvas = document.getElementById("boidCanvas");
  var ctx = canvas.getContext("2d");
  ctx.clearRect(0, 0, canvas.width, canvas.height);
}

function drawBoids(data) {
  var ctx = document.getElementById("boidCanvas").getContext("2d");
  var object = JSON.parse(data);
  for (let boid of object.boids) {
    drawEachBoid(ctx, boid);
  }
}

function drawEachBoid(ctx, boid) {
  const angle = Math.atan2(boid.dy, boid.dx);
  ctx.translate(boid.x, boid.y);
  ctx.rotate(angle);
  ctx.translate(-boid.x, -boid.y);
  ctx.fillStyle = "#558cf4";
  ctx.beginPath();
  ctx.moveTo(boid.x, boid.y);
  ctx.lineTo(boid.x - 15, boid.y + 5);
  ctx.lineTo(boid.x - 15, boid.y - 5);
  ctx.lineTo(boid.x, boid.y);
  ctx.fill();
  ctx.setTransform(1, 0, 0, 1, 0, 0);
}

function flyBoid() {
  var canvas = document.getElementById("boidCanvas");
  var ctx = canvas.getContext("2d");
  ctx.fillStyle = "#FF0000";

  fetch(postUri, {
    method: 'post',
    headers: {
      "Content-type": "application/json; charset=UTF-8"
    },
    body:  `{"canvasWidth": ${canvas.width},"canvasHeight": ${canvas.height},"canvasMargin": ${canvasMargin},"speedAdjust": ${speedAdjust},"numOfBoids": ${numOfBoids},"cohesionRange": ${cohesionRange},"separationRange": ${separationRange},"alignmentRange": ${alignmentRange},"cohesionFactor": ${cohesionFactor},"separationFactor": ${separationFactor},"alignmentFactor": ${alignmentFactor},"speedLimit": ${speedLimit}, "simulationSpeed": ${simulationSpeed}, "initialMaxSpeed": ${initialMaxSpeed}}`
  })
  .then(function (data) {
    console.log('Request succeeded with JSON response', data);
  })
  .catch(function (error) {
    console.log('Request failed', error);
  });

  websocket.send(stopSimulation);
  websocket.send(startSimulation);

}

function myFunction1() {
  var canvas = document.getElementById("boidCanvas");
  var ctx = canvas.getContext("2d");
  ctx.fillStyle = "#FF0000";
  ctx.fillRect(20, 20, 150, 100);
}

function sizeCanvas() {
  const canvas = document.getElementById("boidCanvas");
  canvas.width = window.innerWidth * 0.8;
  canvas.height = window.innerHeight * 0.8;
}
