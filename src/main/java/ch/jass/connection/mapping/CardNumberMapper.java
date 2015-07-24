package ch.jass.connection.mapping;

import java.util.HashMap;
import java.util.Map;

import ch.jass.model.Rank;

public class CardNumberMapper {

	private CardNumberMapper() {

	}

	private enum Numbers {
		SIX(6, Rank.SIX), SEVEN(7, Rank.SEVEN), EIGHT(8, Rank.EIGHT), NINE(9, Rank.NINE), TEN(10, Rank.TEN),
		ELEVEN(11, Rank.JACK), TWELVE(12, Rank.QUEEN), THIRDTEEN(13, Rank.KING), FOURTEEN(14, Rank.ACE);

		private Numbers(final Integer number, final Rank rank) {
			this.number = number;
			this.rank = rank;
		}

		private final Integer number;
		private final Rank rank;
	}

	public static final String PROPERTY_NAME = "number";

	private static Map<Rank, Integer> numberMap;

	public static synchronized Integer getNumber(final Rank rank) {
		if (numberMap == null) {
			initNumberMap();
		}
		return numberMap.get(rank);
	}

	private static void initNumberMap() {
		numberMap = new HashMap<Rank, Integer>();
		for (Numbers number : Numbers.values()) {
			numberMap.put(number.rank, number.number);
		}
	}

	private static Map<Integer, Rank> rankMap;

	public static synchronized Rank getRank(final Integer number) {
		if (rankMap == null) {
			initRankMap();
		}
		Rank rank = rankMap.get(number);
		if (rank == null) {
			throw new IllegalStateException("Did not found Rank for Number: " + number + ", " + rankMap.toString());
		}
		return rank;
	}

	private static void initRankMap() {
		rankMap = new HashMap<Integer, Rank>();
		for (Numbers number : Numbers.values()) {
			rankMap.put(number.number, number.rank);
		}
	}
}
