import org.json.*;
import pubsub.io.processing.*;

// Declare the pubsub lib
Pubsub hub;

// We need this id if we want to unsubscribe.
int sub_id = 0;

// Set the color you want to paint with
int c = color( 255, 0, 0 );

void setup() {
  size( 200, 200 );
  // instansiate the library and connect to a sub.
  hub = new Pubsub( this );
  hub.connect( "processing" );
}

void onOpen() {
  // No subscriptions in this sketch
}

void draw() {
  // Nothing to draw in this sketch
}

void mouseDragged() {
  JSONObject doc = new JSONObject();
  try {
    doc.put( "color", c );
    doc.put( "x", mouseX );
    doc.put( "y", mouseY );
  }
  catch(JSONException e) {
  }
  hub.publish( doc );
}