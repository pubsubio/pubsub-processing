package pubsub.io.processing;

import org.json.JSONObject;

public interface WebSocketListener {

	public void onMessage(JSONObject msg);

	public void onOpen();

	public void onClose();

	public void onError(JSONObject msg);
}
