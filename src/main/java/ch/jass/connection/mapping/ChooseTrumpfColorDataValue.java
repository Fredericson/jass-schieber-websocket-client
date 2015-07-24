package ch.jass.connection.mapping;

import ch.jass.model.Trumpf;

public enum ChooseTrumpfColorDataValue {
	SPADES, HEARTS, DIAMONDS, CLUBS;

	public static final String PROPERTY_NAME = "trumpfColor";

	public static ChooseTrumpfColorDataValue getMappedColor(final Trumpf trumpf) {
		if (trumpf == null || trumpf.getColor() == null) {
			return null;
		}
		return ChooseTrumpfColorDataValue.valueOf(trumpf.getColor().name());
	}
}
