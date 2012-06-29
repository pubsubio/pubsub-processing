package pubsub.io.processing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class for parsing pubsub.io messages, re-written for the Processing library.
 * (Originally written for the Android version)
 * 
 * @author Andreas Goransson
 * 
 */
public class PubsubParser {

	/**
	 * Creates the "sub" message.
	 * 
	 * @param sub
	 * @return
	 * @throws JSONException
	 */
	public static String sub(String sub) throws JSONException {
		
		JSONObject root = new JSONObject();
		root.put("sub", sub);
		
		return root.toString();
	}

	/**
	 * Creates the "subscribe" message.
	 * 
	 * @param json_filter
	 * @param handler_callback
	 * @return
	 * @throws JSONException
	 */
	public static String subscribe(JSONObject json_filter, int callback_id)
			throws JSONException {

		JSONObject root = new JSONObject();
		root.put("name", "subscribe");
		root.put("query", json_filter);
		root.put("id", callback_id);

		return root.toString();
	}

	/**
	 * Creates the "unsubscribe" message.
	 * 
	 * @param sub
	 * @return
	 * @throws JSONException
	 */
	public static String unsubscribe(int handler_callback) throws JSONException {

		JSONObject root = new JSONObject();
		root.put("name", "unsubscribe");
		root.put("id", handler_callback);

		return root.toString();
	}

	/**
	 * Creates the "publish" message.
	 * 
	 * @param doc
	 * @return
	 * @throws JSONException
	 */
	public static String publish(JSONObject doc) throws JSONException {

		JSONObject root = new JSONObject();
		root.put("name", "publish");
		root.put("doc", doc);

		return root.toString();
	}

	/**
	 * Creates the "publish" message.
	 * 
	 * @param doc
	 * @return
	 * @throws JSONException
	 */
	public static String publish(JSONArray doc) throws JSONException {

		JSONObject root = new JSONObject();
		root.put("name", "publish");
		root.put("doc", doc);

		return root.toString();
	}
}
