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
import java.lang.reflect.Method;
import java.net.URI;

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

public class Pubsub implements Runnable {

	private static final boolean DEBUG = false;

	public final static String VERSION = "##version##";

	// myParent is a reference to the parent sketch
	PApplet myParent;

	private String mHost;
	private String mPort;
	private String mSub;

	private WebSocket mWebSocket;
	private boolean connected = false;

	private Method onSubMessage;

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

		try {
			onSubMessage = theParent.getClass().getMethod("onSubMessage",
					new Class[] { Pubsub.class });
		} catch (Exception e) {
			if (DEBUG)
				System.out
						.println("Method onSubMessage missing, did you forget to add it?");
		}
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
			System.out.println("connect(" + host + ", " + port + ", " + sub + ")");

		mHost = host;
		mPort = port;
		mSub = sub;

		if (!isConnected()) {
			try {
				mWebSocket = new WebSocket(URI.create("ws://" + mHost + ":" + mPort),
						WebSocket.Draft.DRAFT75, "sample");
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

	public void send(String msg) {
		mWebSocket.send(msg);
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

	@Override
	public void run() {

	}

}
