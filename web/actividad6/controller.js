class Controller {
  constructor(){
    this.points = [];
    this.transformedPoints = [];
    this.canAdd = true;
    this.centerPoint = {x: 0, y:0};
    this.rotation = 180 * Math.PI / 180; //tiene que estar en radianes :'v
    this.proportion = 1;
    this.reflexion = {x: false, y: false, scale: 100};
    this.translation = {x: 0, y: 0}
  }

  addPoint(point){
    if(this.canAdd){
      if(this.points.length == 0){
        this.points.push(point);
        this.transformedPoints.push({...point});
        return true;
      }
      else{
        if(Math.abs(this.points[0].x - point.x) < 5 && Math.abs(this.points[0].y - point.y) < 5){
          this.setCenterPoint();
          this.canAdd = false;
         // console.log(this.transformedPoints);
          return false
        }
        this.points.push(point);
        this.transformedPoints.push({...point});
        return true;
      }
    }
  }

  setRotation(degrees){
    degrees = degrees * Math.PI / 180;
    this.rotation = degrees;
    this.transformFigure();
  }

  setProportion(scale){
    this.proportion = scale/100;
    this.transformFigure();
  }

  setReflex(canReflex, reflexion, proportion) {
    if(canReflex){
      if(reflexion){
        this.reflexion.y = false;
        this.reflexion.x = true;
      }
      else{
        this.reflexion.y = true;
        this.reflexion.x = false;
      }
    }
    else {
      this.reflexion.y = false;
      this.reflexion.x = false;
    }
    this.reflexion.scale = proportion;   
    this.transformFigure();
  }

  setTranslate(x, y){
    this.translation.x = x;
    this.translation.y = y;
    this.transformFigure();
  }

  transformFigure(){
    let i = 0;
    //for rotation
    i = 0;
    for(let point of this.points){
      this.transformedPoints[i].y = this.centerPoint.y + ((this.centerPoint.x - point.x) * Math.sin(this.rotation)) + ((this.centerPoint.y - point.y) * Math.cos(this.rotation));
      this.transformedPoints[i].x = this.centerPoint.x + ((this.centerPoint.x - point.x) * Math.cos(this.rotation)) - ((this.centerPoint.y - point.y) * Math.sin(this.rotation));
      i++;
    }
    //for proportion
    for(let point of this.transformedPoints){
      point.x = this.centerPoint.x + (point.x - this.centerPoint.x) * this.proportion;
      point.y = this.centerPoint.y + (point.y - this.centerPoint.y) * this.proportion;
    }

    //for reflexion
    //console.log(this.reflexion);
    if(this.reflexion.x){
      for(let point of this.transformedPoints){
        let diff = point.x - this.centerPoint.x;
        point.x = point.x - (diff * 2 * this.reflexion.scale / 100);
      }
    }
    else if(this.reflexion.y){
       for(let point of this.transformedPoints){
        let diff = point.y - this.centerPoint.y;
        point.y = point.y - (diff * 2 * this.reflexion.scale / 100);
      }
    }

    //for translation
    for(let point of this.transformedPoints){
      point.x += this.translation.x;
      point.y += this.translation.y;
    }
  }

  setCenterPoint(){
    let max = Number.MIN_VALUE;
    let min = Number.MAX_VALUE;
    for(let point of this.points){
      if(max < point.x) max = point.x;
      if(min > point.x) min = point.x;
    }
    this.centerPoint.x = (max + min) /2;
    max = Number.MIN_VALUE;
    min = Number.MAX_VALUE;
    for(let point of this.points){
      if(max < point.y) max = point.y;
      if(min > point.y) min = point.y;
    }
    this.centerPoint.y = (max + min) /2;
  }

  getPoints(){
    return this.points;
  }

  getTransformedPoints(){
    return this.transformedPoints;
  }

  isEndedFigure(){
    return !this.canAdd;
  }
}