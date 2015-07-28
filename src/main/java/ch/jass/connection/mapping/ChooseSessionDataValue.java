package ch.jass.connection.mapping;

import ch.jass.model.SessionType;

public enum ChooseSessionDataValue {
	undefined, CREATE_NEW, AUTOJOIN, JOIN_EXISTING;

	public static final String DATA_SESSION_CHOICE = "sessionChoice";
	public static final String DATA_SESSION_NAME = "sessionName";

	public static ChooseSessionDataValue getMappedChooseSession(final SessionType sessionType) {
		if (sessionType == SessionType.AUTO_JOIN) {
			return AUTOJOIN;
		} else if (sessionType == SessionType.CREATE_NEW) {
			return CREATE_NEW;
		} else if (sessionType == SessionType.JOIN_EXISTING) {
			return JOIN_EXISTING;
		}

		throw new IllegalArgumentException("SessionType not found!");
	}

}
