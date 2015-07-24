package ch.jass.connection.outbound;

import static ch.jass.connection.mapping.BasicMessageFields.*;

import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

public class SendMessageBuilder {

	public static String toJSONString(final SendMessage sendMessage) {
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder().add(FIELD_TYPE, sendMessage.getType().name());
		if (sendMessage.getData() != null) {
			jsonBuilder = jsonBuilder.add(FIELD_DATA, sendMessage.getData());
		}
		return jsonBuilder.build().toString();
	}

	public static String toJSONString(final SendMessageDataMap sendMessage) {
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder().add(FIELD_TYPE, sendMessage.getType().name())
				.add(FIELD_DATA, createJsonObjBuilder(sendMessage.getData()));
		return jsonBuilder.build().toString();

	}

	private static JsonObjectBuilder createJsonObjBuilder(final Map<String, Object> map) {
		JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
		for (Entry<String, Object> entry : map.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof Integer) {
				jsonObjBuilder.add(entry.getKey(), (int) value);
			} else {
				jsonObjBuilder.add(entry.getKey(), (String) value);
			}
		}
		return jsonObjBuilder;
	}
}
