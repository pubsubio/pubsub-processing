import pubsub.io.processing.*;
// import org.json.simple.JSONObject; // Might not need this one...

Pubsub hub;

int subscription_id = 0;

void setup(){
  hub = new Pubsub( this );
  
  hub.connect( "echo.websocket.org", "80", "" );
  
  JSONObject obj = new JSONObject();
  subscription_id = hub.subscribe( obj, "mySubscription" );
}

// Create your own callback method (must match the name in the subscription method!)
void mySubscription(JSONObject doc){
  println( doc );
}

void draw(){
}

void mousePressed(){
  JSONObject doc = new JSONObject();
  doc.put( "x", mouseX );
  doc.put( "y", mouseY );
  
  hub.publish( doc );
}

void stop(){
  hub.unsubscribe( subscription_id );
  super.stop();
}