package ch.jass.connection.inbound;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.jass.model.schieber.table.PlayerOnTable;

public class BroadcastSessionJoinedTest {

	// {
	// "type" : "SESSION_JOINED",
	// "data" : {
	// "name" : "Player 1",
	// "id" : "0"
	// }
	// }
	private static final String message = "{\"type\":\"SESSION_JOINED\",\"data\":{\"name\":\"bot1\",\"id\":0}}";

	private static PlayerOnTable playerOnTable;

	@BeforeClass
	public static void setup() {
		playerOnTable = RequestMessageBuilder.getBroadcastSessionJoined(message);
	}

	@Test
	public void verifyPlayer() {
		Assert.assertEquals("bot1", playerOnTable.getName());
		Assert.assertNull(playerOnTable.getTeam());
		Assert.assertEquals(1, playerOnTable.getPlayerNumber());
	}
}
