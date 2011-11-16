package pubsub.io.processing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

public class MyThread extends Thread {
	private final Socket mmSocket;
	private final InputStream mmInStream;
	private final OutputStream mmOutStream;

	private Vector WebSocketListeners;

	public MyThread(Socket socket) {// , String socketType) {
		System.out.println("create ConnectedThread: ");// + socketType);
		mmSocket = socket;
		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		WebSocketListeners = new Vector();

		// Get the BluetoothSocket input and output streams
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
			System.out.println("temp sockets not created" + e.getMessage());
		}

		mmInStream = tmpIn;
		mmOutStream = tmpOut;

		createWebSocketEvent(WebSocketEvent.ON_OPEN, null);
	}

	public void addWebSocketListener(WebSocketListener wsl) {
		// add main frame to vector of listeners
		if (WebSocketListeners.contains(wsl))
			return;
		WebSocketListeners.addElement(wsl);
	}

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
		System.out.println("BEGIN mConnectedThread");
		byte[] buffer = new byte[1024];
		int bytes;

		// Keep listening to the InputStream while connected
		while (true) {
			try {
				// Read from the InputStream
				bytes = mmInStream.read(buffer);

				if (bytes > -1) {
					// Parse the message and send to the right callback
					String readMessage = new String(buffer, 0, bytes);

					if (readMessage.indexOf('{') != -1
							&& readMessage.lastIndexOf('}') != -1)
						readMessage = readMessage.substring(
								readMessage.indexOf('{'),
								readMessage.lastIndexOf('}') + 1);
					System.out.println(readMessage);
					JSONObject json_obj = null;
					try {
						json_obj = new JSONObject(readMessage);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (json_obj != null)
						createWebSocketEvent(WebSocketEvent.ON_MESSAGE,
								json_obj);
				}

			} catch (IOException e) {
				System.out.println("disconnected" + e.getMessage());
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
	 * Write to the connected OutStream.
	 * 
	 * @param buffer
	 *            The bytes to write
	 */
	public void write(byte[] buffer) {
		try {
			mmOutStream.write(attachHeaderAndFooter(buffer));

			// Share the sent message back to the UI Activity
			// mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1,
			// buffer)
			// .sendToTarget();
		} catch (IOException e) {
			System.out.println("Exception during write" + e.getMessage());
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
			mmSocket.close();
		} catch (IOException e) {
			System.out.println("close() of connect socket failed"
					+ e.getMessage());
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
	 * This adds the required header and footer for the package, without them
	 * the hub won't recognize the message.
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
