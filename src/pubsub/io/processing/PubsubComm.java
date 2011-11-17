package pubsub.io.processing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Thread to handle communication with Pubsub.
 * 
 * @author Andreas Goransson
 * 
 */
public class PubsubComm extends Thread {
	// Socket & streams
	private final Socket mSocket;
	private final InputStream mInputStream;
	private final OutputStream mOutputStream;

	// Listenerlist (really only one listener, the sketch!)
	private Vector WebSocketListeners;

	public PubsubComm(Socket socket) {
		mSocket = socket;
		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		// Get the BluetoothSocket input and output streams
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
		}

		mInputStream = tmpIn;
		mOutputStream = tmpOut;

		WebSocketListeners = new Vector();
	}

	public boolean isConnected() {
		return mSocket.isConnected();
	}

	@Override
	public synchronized void start() {
		if (mSocket.isConnected())
			createWebSocketEvent(WebSocketEvent.ON_OPEN, null);
		super.start();
	}

	public void addWebSocketListener(WebSocketListener wsl) {
		// add main frame to vector of listeners
		if (WebSocketListeners.contains(wsl))
			return;
		WebSocketListeners.addElement(wsl);
	}

	/**
	 * 
	 * @param event_type
	 * @param msg
	 */
	private void createWebSocketEvent(int event_type, JSONObject msg) {
		WebSocketEvent wse = new WebSocketEvent(this, event_type, msg);

		Vector vtemp = (Vector) WebSocketListeners.clone();
		for (int x = 0; x < vtemp.size(); x++) {
			WebSocketListener target = null;
			target = (WebSocketListener) vtemp.elementAt(x);

			switch (event_type) {
			case WebSocketEvent.ON_CLOSE:
				target.onClose();
				break;
			case WebSocketEvent.ON_ERROR:
				target.onError(msg);
				break;
			case WebSocketEvent.ON_MESSAGE:
				target.onMessage(msg);
				break;
			case WebSocketEvent.ON_OPEN:
				target.onOpen();
				break;
			}
		}

	}

	public void run() {
		byte[] buffer = new byte[1024];
		int bytes;

		StringBuffer mStringBuffer = new StringBuffer();

		// Keep listening to the InputStream while connected
		while (true) {
			try {
				// Read from the InputStream
				bytes = mInputStream.read(buffer);

				if (bytes > 0) {
					// Add the characters to the stringbuffer
					mStringBuffer.append(new String(buffer, 0, bytes));

					// Get all JSON messages!
					while (hasNext(mStringBuffer)) {
						// Get the next JSONObject
						int[] startAndStop = getNext(mStringBuffer);
						String next = mStringBuffer.substring(startAndStop[0],
								startAndStop[1]);

						// Process the JSON message
						process(next);

						// Delete the selected characters from the buffer.
						mStringBuffer.delete(startAndStop[0], startAndStop[1]);
					}
				}
			} catch (IOException e) {
				// TODO, restart the thing if it failed?
				JSONObject root = new JSONObject();
				try {
					root.put("error", e);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				createWebSocketEvent(WebSocketEvent.ON_ERROR, root);
				break;
			}
		}

	}

	/**
	 * Create and send the JSONObject to the Processing sketch.
	 * 
	 * @param next
	 */
	private void process(String json_formatted) {
		JSONObject json_obj = null;
		try {
			json_obj = new JSONObject(json_formatted);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (json_obj != null)
			createWebSocketEvent(WebSocketEvent.ON_MESSAGE, json_obj);
	}

	/**
	 * Detect if the StringBuffer has another JSON package inside it...
	 * 
	 * @param mStringBuffer
	 * @return
	 */
	private boolean hasNext(StringBuffer mStringBuffer) {
		int start = mStringBuffer.indexOf("{");
		int end = -1;

		if (start != -1) {
			int starts = 1;
			int ends = 0;

			for (int i = 0; i < mStringBuffer.length(); i++) {

				if (mStringBuffer.charAt(i) == '{') {
					starts++;
				} else if (mStringBuffer.charAt(i) == '}') {
					ends++;
					end = i + 1;
				}

				if (starts == ends)
					break;
			}
		}

		if (start != -1 && end != -1 && start < end)
			return true;

		return false;
	}

	private int[] getNext(StringBuffer mStringBuffer) {
		int start = mStringBuffer.indexOf("{");
		int end = -1;

		if (start != -1) {
			int starts = 1;
			int ends = 0;

			for (int i = 0; i < mStringBuffer.length(); i++) {

				if (mStringBuffer.charAt(i) == '{') {
					starts++;
				} else if (mStringBuffer.charAt(i) == '}') {
					ends++;
					end = i + 1;
				}

				if (starts == ends)
					break;
			}
		}

		if (start != -1 && end != -1)
			return new int[] { start, end };

		return null;
	}

	/**
	 * Write to the connected OutStream.
	 * 
	 * @param buffer
	 *          The bytes to write
	 */
	public void write(byte[] buffer) {
		try {
			mOutputStream.write(attachHeaderAndFooter(buffer));
		} catch (IOException e) {
			JSONObject root = new JSONObject();
			try {
				root.put("error", e);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			createWebSocketEvent(WebSocketEvent.ON_ERROR, root);
		}
	}

	public void cancel() {
		try {
			mSocket.close();
		} catch (IOException e) {
			JSONObject root = new JSONObject();
			try {
				root.put("error", e);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			createWebSocketEvent(WebSocketEvent.ON_ERROR, root);
		}

	}

	/**
	 * This adds the required header and footer for the package, without them the
	 * hub won't recognize the message.
	 * 
	 * @param buffer
	 * @return
	 */
	private byte[] attachHeaderAndFooter(byte[] buffer) {
		// In total, 2 bytes longer than the original message!
		byte[] sendbuffer = new byte[buffer.length + 2];

		// Set the first byte (0x000000)
		sendbuffer[0] = (byte) 0x000000;

		// Add the real package (buffer)
		for (int i = 1; i < sendbuffer.length - 1; i++)
			sendbuffer[i] = buffer[i - 1];

		// Add the footer (0xFFFFFD)
		sendbuffer[sendbuffer.length - 1] = (byte) 0xFFFFFD;

		return sendbuffer;
	}
}
