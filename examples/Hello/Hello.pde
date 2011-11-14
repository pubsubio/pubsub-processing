import pubsub.io.processing.*;
// import org.json.simple.JSONObject; // Might not need this one...

Pubsub hub;

void setup(){
  hub = new Pubsub(this);
  
  hub.connect( "echo.websocket.org", "80", "");
  
  JSONObject obj = new JSONObject();
  obj.put("val":"<0");
  hub.subscribe( obj, "mySubscription" );
}

// Create your own callback method (must match the name in the subscription method!)
void mySubscription(JSONObject doc){
	println( doc );
}

void draw(){
}

void mousePressed(){
  hub.send( "x:" + mouseX + ", y:" + mouseY );
}