import pubsub.io.processing.*;
import org.json.simple.JSONObject; // Needs the .jar file!

Pubsub hub;

int subscription_id = 0;

void setup() {
  hub = new Pubsub( this );
  hub.connect( /*"echo.websocket.org", "80",*/ "processing" );
}

// Subscribe only when we've managed to open a connection!
void onOpen() {
  println( "onOpen" );
  JSONObject obj = new JSONObject();
  subscription_id = hub.subscribe( obj, "mySubscription" );
}

// Create your own callback method (must match the name in the subscription method!)
void mySubscription(JSONObject doc) {
  print( "mySubscription " );
  println( "result " + doc );
}

void draw() {
}

void mousePressed() {
  /*JSONObject doc = new JSONObject();
   doc.put( "x", mouseX );
   doc.put( "y", mouseY );
   
   hub.publish( doc );*/

  // Only used for testing against echo.websocket.org
  JSONObject msg = new JSONObject();
  msg.put("id", subscription_id);
  JSONObject doc = new JSONObject();
  doc.put( "x", mouseX );
  doc.put( "y", mouseY );
  msg.put("doc", doc );

  hub.send( msg.toString() );
}

void stop() {
  // Tell the server we're not interested in the information anymore...
  hub.unsubscribe( subscription_id );
  super.stop();
}
