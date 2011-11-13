import pubsub.io.processing.*;

Pubsub hub;

void setup(){
  hub = new Pubsub(this);
  
  hub.connect( "echo.websocket.org", "80", "");
}

void draw(){
}

void mousePressed(){
  hub.send( "x:" + mouseX + ", y:" + mouseY );
}