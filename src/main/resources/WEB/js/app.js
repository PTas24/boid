var wsUri = 'ws://localhost:8080/websocket/boid-positions';
var postUri = 'http://localhost:8080/boid';
var startSimulation = "start simulation";
var stopSimulation = "stop simulation";

var canvasWidth = 200;
var canvasHeight = 200;
var canvasMargin = 50;
var speedAdjust = 1;
var numOfBoids = 75;
var cohesionRange = 175;
var separationRange = 20;
var alignmentRange = 75;
var cohesionFactor = 0.01;
var separationFactor = 0.035;
var alignmentFactor = 0.125;
var speedLimit = 12;
var simulationSpeed = 30;
var initialMaxSpeed = 4;

var numOfB = document.getElementById("numOfBoidsId");
var numBoidsValue = document.getElementById("numBoidsValue");
numBoidsValue.innerHTML = numOfB.value;

numOfB.oninput = function () {
  numBoidsValue.innerHTML = this.value;
}

var cohesionRangeId = document.getElementById("cohesionRangeId");
var cohesionRangeValue = document.getElementById("cohesionRangeValue");
cohesionRangeValue.innerHTML = cohesionRangeId.value;

cohesionRangeId.oninput = function () {
  cohesionRangeValue.innerHTML = this.value;
}

var separationRangeId = document.getElementById("separationRangeId");
var separationRangeValue = document.getElementById("separationRangeValue");
separationRangeValue.innerHTML = separationRangeId.value;

separationRangeId.oninput = function () {
  separationRangeValue.innerHTML = this.value;
}

var alignmentRangeId = document.getElementById("alignmentRangeId");
var alignmentRangeValue = document.getElementById("alignmentRangeValue");
alignmentRangeValue.innerHTML = alignmentRangeId.value;

alignmentRangeId.oninput = function () {
  alignmentRangeValue.innerHTML = this.value;
}

var cohesionFactorId = document.getElementById("cohesionFactorId");
var cohesionFactorValue = document.getElementById("cohesionFactorValue");
cohesionFactorValue.innerHTML = cohesionFactorId.value;

cohesionFactorId.oninput = function () {
  cohesionFactorValue.innerHTML = this.value;
}

var separationFactorId = document.getElementById("separationFactorId");
var separationFactorValue = document.getElementById("separationFactorValue");
separationFactorValue.innerHTML = separationFactorId.value;

separationFactorId.oninput = function () {
  separationFactorValue.innerHTML = this.value;
}

var alignmentFactorId = document.getElementById("alignmentFactorId");
var alignmentFactorValue = document.getElementById("alignmentFactorValue");
alignmentFactorValue.innerHTML = alignmentFactorId.value;

alignmentFactorId.oninput = function () {
  alignmentFactorValue.innerHTML = this.value;
}

var speedLimitId = document.getElementById("speedLimitId");
var speedLimitValue = document.getElementById("speedLimitValue");
speedLimitValue.innerHTML = speedLimitId.value;

speedLimitId.oninput = function () {
  speedLimitValue.innerHTML = this.value;
}

var simulationSpeedId = document.getElementById("simulationSpeedId");
var simulationSpeedValue = document.getElementById("simulationSpeedValue");
simulationSpeedValue.innerHTML = simulationSpeedId.value;

simulationSpeedId.oninput = function () {
  simulationSpeedValue.innerHTML = this.value;
}

var initialMaxSpeedId = document.getElementById("initialMaxSpeedId");
var initialMaxSpeedValue = document.getElementById("initialMaxSpeedValue");
initialMaxSpeedValue.innerHTML = initialMaxSpeedId.value;

initialMaxSpeedId.oninput = function () {
  initialMaxSpeedValue.innerHTML = this.value;
}

window.onload = () => {
  // Make sure the canvas always fills the whole window
  window.addEventListener("resize", sizeCanvas, false);
  sizeCanvas();
};

const websocket = new WebSocket('ws://localhost:8080/websocket/boid-positions');
console.log('websocket state: ', websocket.readyState);

websocket.onmessage = function (e) {
    // console.log(e.data);
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
  // ctx.fillStyle = "#558cf4";
  ctx.fillStyle = "#000011";
  ctx.beginPath();
  ctx.moveTo(boid.x, boid.y);
  ctx.lineTo(boid.x - 15, boid.y + 5);
  ctx.lineTo(boid.x - 15, boid.y - 5);
  ctx.lineTo(boid.x, boid.y);
  ctx.fill();
  ctx.setTransform(1, 0, 0, 1, 0, 0);
}

function refreshConfig() {
  var canvas = document.getElementById("boidCanvas");
  var ctx = canvas.getContext("2d");
  ctx.fillStyle = "#FF0000";

  var bodyJson = `{"canvasWidth": ${canvas.width},
        "canvasHeight": ${canvas.height},
        "canvasMargin": ${canvasMargin},
        "speedAdjust": ${speedAdjust},
        "numOfBoids": ${numOfBoidsId.value},
        "cohesionRange": ${cohesionRangeId.value},
        "separationRange": ${separationRangeId.value},
        "alignmentRange": ${alignmentRangeId.value},
        "cohesionFactor": ${cohesionFactorId.value},
        "separationFactor": ${separationFactorId.value},
        "alignmentFactor": ${alignmentFactorId.value},
        "speedLimit": ${speedLimitId.value},
        "simulationSpeed": ${simulationSpeedId.value},
        "initialMaxSpeed": ${initialMaxSpeedId.value}}`;

  console.log("post body json: ", bodyJson)

  fetch(postUri, {
    method: 'post',
    headers: {
      "Content-type": "application/json; charset=UTF-8"
    },
    body: bodyJson
  })
  .then(function (data) {
    console.log('Request succeeded with JSON response', data);
  })
  .catch(function (error) {
    console.log('Request failed', error);
  });
}

function flyBoid() {
  console.log('stop/start simulation');
  var simulationType = document.getElementById("simulationMode").value;
  websocket.send(stopSimulation);
  websocket.send(startSimulation + ':' + simulationType);
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
