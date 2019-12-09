var canvas = document.querySelector('.myCanvas');
var ctx = canvas.getContext('2d');
const swRotar = document.getElementById("grados");
const tbToolbar = document.getElementById("toolbar");
const swEscalar = document.getElementById("porcentaje");
const swReflejar = document.getElementById("reflejo");
const width = canvas.width = window.innerWidth;
const height = canvas.height = window.innerHeight;
var controlador = new Controller();
var rotacion = 180;
var escala = 100;
var reflexionScala = 100;
var reflexion = false;
var translacion = {x: 0, y: 0};
inicializarCanvas();

var transformaciones = {
  "divRotar": false,
  "divPorcentaje": false,
  "divReflejo": false,
  "divTranslado": false,
}

function showHide(element){
  transformaciones[element] = !transformaciones[element];
  var div = document.getElementById(element);
  if(transformaciones[element]){
    div.style.display = "inherit";
  }
  else{
    div.style.display = "none";
  }
}

function inicializarCanvas(){
  ctx.font = "20px Arial";
  ctx.beginPath();
  for(let i = 0; i < width; i+=20){
    //console.log(i);
    if(i % 60 == 0) ctx.fillText(i + "", i, 20);
    ctx.moveTo(i, 0);
    ctx.lineTo(i, height);   
    ctx.strokeStyle = '#A3A3A3';
    ctx.stroke();
  }
  for(let i = 0; i < height; i+=20){
    //console.log(i);
    if(i % 60 == 0) ctx.fillText(i + "", 0, i+20);
    ctx.moveTo(0, i);
    ctx.lineTo(width, i);   
    ctx.strokeStyle = '#A3A3A3';
    ctx.stroke();
  }
}

function drawCircle(event){
  var pos = getCursorPosition(event);
  if(controlador.addPoint(pos)){
    console.log(pos);
    var centerX = pos.x;
    var centerY = pos.y;
    var radius = 5;
    ctx.beginPath();
    ctx.arc(centerX, centerY, radius, 0, 2 * Math.PI, false);
    ctx.fill();
  }
  else {
    drawFigure();
  }
}

function drawFigure() {
  ctx.beginPath();
  ctx.strokeStyle = '#000000';
  let initial = controlador.getPoints();
  initial = initial[initial.length-1];
  ctx.moveTo(initial.x, initial.y);
  for(point of controlador.getPoints()){
    ctx.lineTo(point.x, point.y);   
    ctx.stroke();
  }
  ctx.strokeStyle = '#A3A3A3';
}

function drawTransformedFigure() {
  ctx.beginPath();
  ctx.strokeStyle = '#00bb00';
  let initial = controlador.getTransformedPoints();
  initial = initial[initial.length-1];
  ctx.moveTo(initial.x, initial.y);
  for(point of controlador.getTransformedPoints()){
    ctx.lineTo(point.x, point.y);   
    ctx.stroke();
  }
  ctx.strokeStyle = '#A3A3A3';
}

function getCursorPosition(e){
  return {
     x: e.clientX,
     y: e.clientY - tbToolbar.clientHeight,
  }
}

function resetAll(){
  delete controlador;
  controlador = new Controller();
  console.clear();
  ctx.clearRect(0, 0, canvas.width, canvas.height);
  inicializarCanvas();
}

function girar(){
  if(transformaciones.divRotar){
    if(controlador.isEndedFigure()) {
      controlador.setRotation(rotacion);
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      inicializarCanvas();
      drawFigure();
      drawTransformedFigure();
    }
  }
  else{
    if(controlador.isEndedFigure()) {
      controlador.setRotation(180);
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      inicializarCanvas();
      drawFigure();
      drawTransformedFigure();
    }
  }
}

function proporcion(){
  if(transformaciones.divPorcentaje){
    if(controlador.isEndedFigure()) {
      controlador.setProportion(escala);
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      inicializarCanvas();
      drawFigure();
      drawTransformedFigure();
    }
  }
  else{
    if(controlador.isEndedFigure()) {
      controlador.setProportion(100);
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      inicializarCanvas();
      drawFigure();
      drawTransformedFigure();
    }
  }
}

function reflejar(){
  if(transformaciones.divReflejo){
    if(controlador.isEndedFigure()) {
      controlador.setReflex(true, reflexion, reflexionScala);
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      inicializarCanvas();
      drawFigure();
      drawTransformedFigure();
    }
  }
  else{
    if(controlador.isEndedFigure()) {
      controlador.setReflex(false, reflexion, reflexionScala);
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      inicializarCanvas();
      drawFigure();
      drawTransformedFigure();
    }
  }
}

function update(){
  reflejar();
  proporcion();
  girar();
  transladar(translacion.x, translacion.y);
}

function transladar(x, y){
  if(controlador.isEndedFigure()) {
    if(x != NaN && y != NaN){
      controlador.setTranslate(x, y);
      translacion.x = x;
      translacion.y = y;
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      inicializarCanvas();
      drawFigure();
      drawTransformedFigure();
    }
  }
}

function animation(){
  let rotacionInicial = 180;
  let escaladoInicial = 100;
  let reflexionInicial = 100;
  let translacionInicial = {x: 0, y: 0};
  let rotacionActual = rotacion;
  let escalaActual = escala;
  let reflexionScalaActual = reflexionScala;
  let reflexionActual = reflexion;
  let translacionActual = {x: 0, y:0};
  let translacionStep = {x: 0, y:0};
  translacionActual.x = translacion.x;
  translacionActual.y = translacion.y;
  
  let rotacionStep = rotacionInicial - rotacionActual;
  let escaladoStep = escalaActual - escaladoInicial;
  let reflexionStep = reflexionInicial - reflexionActual;
  translacionStep.x = translacionActual.x;
  translacionStep.y = translacionActual.y;

  rotacion = rotacionStep;
  escala  = 100;
  reflexionScala  = 0
  translacion.x = 0;
  translacion.y = 0;
  console.log(rotacionStep* (1/100), escaladoStep * (1/100) , reflexionStep * (1/100), translacionStep.x, translacionStep.y);
  setValues(rotacionStep * (1/100), escaladoStep * (1/100),  0, translacionStep.x * (1/100), translacionStep.y * (1/100));

}

function setValues(rotation, escalado, reflexScala, translacionx, translaciony){
  if(reflexScala < 100){
    setTimeout(function(){
        console.log(reflexScala);
        rotacion += rotation;
        escala += escalado;
        reflexionScala  = reflexScala;
        translacion.x += translacionx;
        translacion.y += translaciony;
        update();
        reflexScala++;
        setValues(rotation, escalado, reflexScala, translacionx, translaciony);
    }, 50);
  }

}