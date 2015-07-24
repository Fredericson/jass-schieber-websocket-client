package ch.jass.connection.outbound;

import java.util.HashMap;
import java.util.Map;

public class SendMessageDataMap {

	private final SendMessageType type;
	private final Map<String, Object> data = new HashMap<String, Object>();

	public SendMessageDataMap(final SendMessageType type) {
		this.type = type;
	}

	public SendMessageType getType() {
		return type;
	}

	public void addData(final String key, final Object value) {
		data.put(key, value);
	}

	public Map<String, Object> getData() {
		return data;
	}
}
