package pubsub.io.processing;

import java.util.EventObject;

import org.json.simple.JSONObject;

public class WebSocketEvent extends EventObject {

	public static final int ON_MESSAGE = 0;
	public static final int ON_OPEN = 1;
	public static final int ON_CLOSE = 2;
	public static final int ON_ERROR = 3;

	private int event_type;

	private JSONObject message;

	public WebSocketEvent(Object source, int event_type, JSONObject message) {
		super(source);
		this.event_type = event_type;
		this.message = message;
	}

	public JSONObject getMessage() {
		return message;
	}

}
