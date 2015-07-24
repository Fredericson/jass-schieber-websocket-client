package ch.jass.connection.mapping;

import ch.jass.model.schieber.table.PlayerNumber;

public class PlayerNumberMapper {

	public static PlayerNumber getPlayerNumber(final int playerNumber) {
		if (playerNumber == 0) {
			return PlayerNumber.PLAYER_1;
		} else if (playerNumber == 1) {
			return PlayerNumber.PLAYER_2;
		} else if (playerNumber == 2) {
			return PlayerNumber.PLAYER_3;
		} else if (playerNumber == 3) {
			return PlayerNumber.PLAYER_4;
		}
		throw new IllegalArgumentException("PlayerNumber not available! " + playerNumber);
	}
}
