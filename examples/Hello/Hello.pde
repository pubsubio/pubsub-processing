import org.json.*;

import pubsub.io.processing.*;

Pubsub hub;

int subscription_id = 0;
int subscription_id2 = 0;

void setup() {
  hub = new Pubsub( this );
  hub.DEBUG = true;
  //hub.setDraft(76);
  hub.connect( "127.0.0.1", "10000", "processing" );
}

// Subscribe only when we've opened a connection!
void onOpen() {
  println( "onOpen" );
  JSONObject mySubscription = new JSONObject();
  subscription_id = hub.subscribe( mySubscription, "mySubscription" );

  JSONObject valuerange = new JSONObject();
  try {
    valuerange.put("$gt", 50);
  }
  catch(JSONException e) {
    e.printStackTrace();
  }

  JSONObject mySubscription2 = new JSONObject();  
  try {
    mySubscription2.put("x", valuerange);
  }
  catch(JSONException e) {
    e.printStackTrace();
  }
  subscription_id2 = hub.subscribe( mySubscription2, "mySubscription2" );
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


  // FAKING A PUBLISH FROM THE SERVER!!!
  // Only used for testing against echo.websocket.org
  JSONObject msg = new JSONObject();

  try {
    if (mouseButton == LEFT) {
      msg.put("id", subscription_id);
    }
    else {
      msg.put("id", subscription_id2);
    }
  }
  catch(JSONException e) {
    e.printStackTrace();
  }

  JSONObject doc = new JSONObject();
  try {
    doc.put( "x", mouseX );
    doc.put( "y", mouseY );
  }
  catch(JSONException e) {
  }

  try {
    msg.put("doc", doc );
  }
  catch(JSONException e) {
  }
  hub.send( msg.toString() );
}

void stop() {
  hub.unsubscribe( subscription_id );
  hub.unsubscribe( subscription_id2 );
  super.stop();
}
