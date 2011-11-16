# pubsub-processing

## Requirements
*This library requires the JSON.simple library for encoding/decoding JSON messages.*

## Install
* TODO bullet list for installing the processing library in Processing IDE

## Getting started
**Create the Pubsub object, this is used for connecting to a pubsub server and subscribing/publishing**

/* Standard instantiation of libraries in Processing */
Pubsub hub = new Pubsub(this);

**Connect - there are three different ways of connecting to a hub.**

/*
 * Connect to the default sub at hub.pubsub.io
 */
hub.connect();

/*
 * Connect to a specific sub at hub.pubsub.io
 */
hub.connect("processing");

/*
 * Connect to a specific sub at a specific hub
 */
hub.connect("127.0.0.1", "10000", "processing");
 
**Subscribe**

/*
 * All subscriptions must be done inside the "onOpen" method, and all subscriptions must be JSON formatted!
 */
void onOpen(){
  // Create the JSON formatted subscription
  JSONObject subscription = new JSONObject();
  // Register the subscription, this requires adding another method called "mySubscription"
  hub.subscribe( subscription, "mySubscription");
}

/*
 * Method names are user-defined, must match the name set in the "hub.subscribe" method.
 */
void mySubscription(JSONObject doc){
  println( doc );
}

*Publish*

/*
 * Make sure not to publish anything before the hub is connected (when onOpen has been called!), otherwise your application might fail.
 */
void mousePressed(){
  // Create the JSON formatted message that will be published
  JSONObject doc = new JSONObject();
  doc.put("x", mouseX);
  doc.put("y", mouseY);
  hub.publish(doc);
}