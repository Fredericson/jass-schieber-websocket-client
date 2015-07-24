package ch.jass.connection.mapping;

import ch.jass.model.Trumpf;

public enum ChooseTrumpfModeDataValue {
	TRUMPF,
	OBEABE,
	UNDEUFE,
	SCHIEBE;

	public static final String PROPERTY_NAME = "mode";

	public static ChooseTrumpfModeDataValue getMappedMode(final Trumpf trumpf) {
		if (trumpf == null) {
			return SCHIEBE;
		} else if (trumpf.getColor() != null) {
			return TRUMPF;
		} else if (trumpf == Trumpf.TOPDOWN) {
			return OBEABE;
		} else if (trumpf == Trumpf.BUTTOMUP) {
			return UNDEUFE;
		}

		throw new IllegalArgumentException("Trumpf could not be mapped!");
	}
}
