import org.json.*;
import pubsub.io.processing.*;

// Declare the pubsub lib
Pubsub hub;

// We need this id if we want to unsubscribe.
int sub_id = 0;

// Sketch variables
int x, y, c;

void setup() {
  size( 200, 200 );
  // instansiate the library and connect to a sub.
  hub = new Pubsub( this );
  hub.connect( "processing" );
}

// This method is called on successfull connect
void onOpen() {
  // Do subscriptions in here!
  JSONObject mySub = new JSONObject();
  sub_id = hub.subscribe( mySub, "mySub" );
}

// Create your own callback method (must match the name in the subscribe() method!)
void mySub(JSONObject doc) {
  try {
    c = doc.getInt("color");
    x = doc.getInt("x");
    y = doc.getInt("y");
    println( "x: " + x + "   y: " + y );
  }
  catch(JSONException e) {
  }
}

void draw() {
  noStroke();
  fill( c );
  ellipse( x, y, 10, 10 );
}