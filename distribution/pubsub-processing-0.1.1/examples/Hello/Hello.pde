import pubsub.io.processing.*;
import org.json.simple.JSONObject; // Might not need this one...

Pubsub hub;

int subscription_id = 0;

void setup() {
  hub = new Pubsub( this );
  hub.connect( "echo.websocket.org", "80", "processing" );
}

// Subscribe only when we've opened a connection!
void onOpen() {
  println( "onOpen" );
  JSONObject mySubscription = new JSONObject();
  subscription_id = hub.subscribe( mySubscription, "mySubscription" );
  
  JSONObject mySubscription2 = new JSONObject();
  JSONObject valuerange = new JSONObject();
  valuerange.put("$gt",1);
  mySubscription2.put("id", valuerange);
  subscription_id = hub.subscribe( mySubscription2, "mySubscription2" );
}

// Create your own callback method (must match the name in the subscription method!)
void mySubscription(JSONObject doc) {
  println( "mySubscription" );
  println( "result " + doc );
}

// Create your own callback method (must match the name in the subscription method!)
void mySubscription2(JSONObject doc) {
  println( "mySubscription2" );
  println( "result " + doc );
}

void draw() {
}

void mousePressed() {
  /*JSONObject doc = new JSONObject();
   doc.put( "x", mouseX );
   doc.put( "y", mouseY );
   
   hub.publish( doc );*/

  JSONObject msg = new JSONObject();
  msg.put("id", subscription_id);
  JSONObject doc = new JSONObject();
  doc.put( "x", mouseX );
  doc.put( "y", mouseY );
  msg.put("doc", doc );

  // Only used for testing against echo.websocket.org
  hub.send( msg.toString() );
}

void stop() {
  hub.unsubscribe( subscription_id );
  super.stop();
}
