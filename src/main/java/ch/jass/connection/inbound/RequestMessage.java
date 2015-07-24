package ch.jass.connection.inbound;


public class RequestMessage {

	private final RequestMessageType type;
	private final String data;

	public RequestMessage(final RequestMessageType type, final String data) {
		this.type = type;
		this.data = data;
	}

	public RequestMessageType getType() {
		return type;
	}

	public String getData() {
		return data;
	}

	@Override
	public String toString() {
		return "RequestMessage [type=" + type + ", data=" + data + "]";
	}
}
