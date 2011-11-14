/**
 * you can put a one sentence description of your library here.
 *
 * ##copyright##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author		##author##
 * @modified	##date##
 * @version		##version##
 */

package pubsub.io.processing;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;

import org.json.simple.JSONObject;

import processing.core.PApplet;

/**
 * This is a template class and can be used to start a new processing library or
 * tool. Make sure you rename this class as well as the name of the example
 * package 'template' to your own lobrary or tool naming convention.
 * 
 * @example Hello
 * 
 *          (the tag @example followed by the name of an example included in
 *          folder 'examples' will automatically include the example in the
 *          javadoc.)
 * 
 */

public class Pubsub implements WebSocketListener {

	private static final boolean DEBUG = true;

	public final static String VERSION = "##version##";

	// myParent is a reference to the parent sketch
	PApplet myParent;

	private String mHost;
	private String mPort;
	private String mSub;

	private WebSocket mWebSocket;
	private boolean connected = false;

	private HashMap<Integer, Method> callbacks;

	/**
	 * a Constructor, usually called in the setup() method in your sketch to
	 * initialize and start the library.
	 * 
	 * @example Hello
	 * @param theParent
	 */
	public Pubsub(PApplet theParent) {
		myParent = theParent;
		welcome();

		callbacks = new HashMap<Integer, Method>();
	}

	public void dispose() {
		mWebSocket.close();
	}

	private void welcome() {
		System.out.println("##name## ##version## by ##author##");
	}

	/**
	 * Connect to the default sub at hub.pubsub.io.
	 */
	public void connect() {
		// connect("hub.pubsub.io", "10547", "/");
		connect("79.125.4.43", "10547", "/");
	}

	/**
	 * Connect to a specified sub at hub.pubsub.io.
	 * 
	 * @param sub
	 */
	public void connect(String sub) {
		// connect("hub.pubsub.io", "10547", sub);
		connect("79.125.4.43", "10547", sub);
	}

	/**
	 * Connect to a specified sub on a specified pubsub hub.
	 * 
	 * @param url
	 * @param port
	 */
	public void connect(String host, String port, String sub) {
		if (DEBUG)
			System.out.println("connect(" + host + ", " + port + ", " + sub
					+ ")");

		mHost = host;
		mPort = port;
		mSub = sub;

		if (!isConnected()) {
			try {
				mWebSocket = new WebSocket(URI.create("ws://" + mHost + ":"
						+ mPort + "/" + mSub), WebSocket.Draft.DRAFT75,
						"sample");
				mWebSocket.addWebSocketListener(this);
				mWebSocket.connect();
			} catch (IOException e) {
				e.printStackTrace();
				connected = false;
			}
		} else {
			if (DEBUG)
				System.out.println("Pubsub.io already connected, ignoring");
		}
	}

	/**
	 * Hook up to a specific sub.
	 * 
	 * @param sub
	 */
	public void sub(String sub) {
		mWebSocket.send(PubsubParser.sub(sub));
	}

	/**
	 * Subscribe to a filter, with a specified handler_callback, on the
	 * connected sub. The handler_callback should be a declared constant, and it
	 * should be used in the Handler of your activity!
	 * 
	 * @param json_filter
	 * @param handler_callback
	 */
	public int subscribe(JSONObject json_filter, String method_callback) {
		int callback_id = callbacks.size() + 1;

		System.out.println("dEBUG: id = " + callback_id + "  method = "
				+ method_callback);

		// Create the method (TODO add some sort of check that it doesn't exist
		// already)
		Method m = null;
		try {
			m = myParent.getClass().getMethod(method_callback,
					new Class[] { String.class });
		} catch (Exception e) {
			if (DEBUG)
				System.out.println(e.getMessage());
		}

		// Add the callback
		if (m != null) {
			callbacks.put(callback_id, m);
		} else {
			if (DEBUG)
				System.out
						.println("Faild to create "
								+ method_callback
								+ ". You'll probably not recieve anything from the hub, dude.");
		}

		// Send the message to the server to subscribe
		mWebSocket.send(PubsubParser.subscribe(json_filter, callback_id));

		return callback_id;
	}

	/**
	 * Unsubscribe the specified handler_callback.
	 * 
	 * @param handler_callback
	 */
	public void unsubscribe(Integer handler_callback) {
		mWebSocket.send(PubsubParser.unsubscribe(handler_callback));
	}

	/**
	 * Publish a document to the connected sub.
	 * 
	 * @param doc
	 */
	public void publish(JSONObject json_doc) {
		mWebSocket.send(PubsubParser.publish(json_doc));
	}

	/**
	 * Are we connected already?
	 * 
	 * @return boolean
	 */
	private boolean isConnected() {
		return connected;
	}

	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}

	/**
	 * React to messages...
	 */
	@Override
	public void onMessage(JSONObject msg) {
		if (DEBUG)
			System.out.println(msg.toJSONString());

		int callback_id = (Integer) msg.get("id");
		JSONObject doc = (JSONObject) msg.get("doc");

		Method eventMethod = callbacks.get(callback_id);
		if (eventMethod != null) {
			try {
				eventMethod.invoke(myParent, doc);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onOpen() {
	}

	@Override
	public void onClose() {
	}

	@Override
	public void onError(JSONObject msg) {
	}

}
