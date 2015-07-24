package ch.jass.connection.inbound;

import static ch.jass.connection.mapping.BasicMessageFields.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import ch.jass.connection.mapping.CardColorMapper;
import ch.jass.connection.mapping.CardNumberMapper;
import ch.jass.connection.mapping.ChooseTrumpfColorDataValue;
import ch.jass.connection.mapping.ChooseTrumpfModeDataValue;
import ch.jass.connection.mapping.GetPlayerDataValue;
import ch.jass.connection.mapping.GetTeamsDataValue;
import ch.jass.connection.mapping.PlayerNumberMapper;
import ch.jass.connection.mapping.TeamMapper;
import ch.jass.model.Card;
import ch.jass.model.Color;
import ch.jass.model.Rank;
import ch.jass.model.Trumpf;
import ch.jass.model.schieber.table.PlayerOnTable;
import ch.jass.model.schieber.table.SchieberStich;
import ch.jass.model.schieber.table.SchieberTableInfo;
import ch.jass.model.schieber.table.Team;
import ch.jass.model.schieber.table.TeamScore;

public class RequestMessageBuilder {

	public static RequestMessageType toRequestMessageType(final String requestMsg) {

		JsonObject jsonObject = Json.createReader(new StringReader(requestMsg)).readObject();
		String typeString = jsonObject.getString(FIELD_TYPE);
		RequestMessageType type = RequestMessageType.valueOf(typeString);
		if (type == null) {
			throw new IllegalArgumentException("Unknown RequestMessageType: " + typeString);
		}
		return type;
	}

	public static PlayerOnTable getBroadcastSessionJoined(final String message) {
		JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
		JsonObject jsonObjectMap = jsonObject.getJsonObject(FIELD_DATA);
		return getPlayer(null, jsonObjectMap);
	}

	public static SchieberTableInfo getBroadcastTeams(final String message) {
		SchieberTableInfo table = new SchieberTableInfo();
		JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
		JsonArray jsonArray = jsonObject.getJsonArray(FIELD_DATA);
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject jsonObjectMap = jsonArray.getJsonObject(i);
			String teamName = jsonObjectMap.getString(GetTeamsDataValue.NAME);
			Team team = TeamMapper.getTeam(teamName);
			PlayerOnTable[] players = getPlayers(team, jsonObjectMap);
			table.addTeam(players);
		}
		return table;
	}

	private static PlayerOnTable[] getPlayers(final Team team, final JsonObject jsonObject) {
		PlayerOnTable[] players = new PlayerOnTable[2];
		JsonArray jsonArray = jsonObject.getJsonArray(GetPlayerDataValue.PLAYERS);
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject jsonObjectMap = jsonArray.getJsonObject(i);
			players[i] = getPlayer(team, jsonObjectMap);
		}
		return players;
	}

	private static PlayerOnTable getPlayer(final Team team, final JsonObject jsonObjectMap) {
		String playerName = jsonObjectMap.getString(GetPlayerDataValue.NAME);
		int playerNumber = jsonObjectMap.getInt(GetPlayerDataValue.ID) + 1;
		return new PlayerOnTable(playerName, team, playerNumber);
	}

	public static Set<Card> dealCards(final String message) {
		Set<Card> cards = new HashSet<Card>();
		JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
		JsonArray jsonArray = jsonObject.getJsonArray(FIELD_DATA);
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject jsonObjectMap = jsonArray.getJsonObject(i);
			Card card = getCard(jsonObjectMap);
			cards.add(card);
		}
		return cards;
	}

	private static Card getCard(final JsonObject jsonObjectMap) {
		int number = jsonObjectMap.getInt(CardNumberMapper.PROPERTY_NAME);
		Rank rank = CardNumberMapper.getRank(number);
		String colorString = jsonObjectMap.getString(CardColorMapper.PROPERTY_NAME);
		Color color = Color.valueOf(colorString);
		return Card.getCard(color, rank);
	}

	public static boolean getRequestTrumpfData(final String message) {
		JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
		return jsonObject.getBoolean(FIELD_DATA);
	}

	public static Trumpf getBroadcastTrumpfData(final String message) {
		JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
		JsonObject jsonObjectMap = jsonObject.getJsonObject(FIELD_DATA);
		String modeString = jsonObjectMap.getString(ChooseTrumpfModeDataValue.PROPERTY_NAME);
		ChooseTrumpfModeDataValue mode = ChooseTrumpfModeDataValue.valueOf(modeString);
		if (ChooseTrumpfModeDataValue.SCHIEBE.equals(mode)) {
			return null;
		} else if (ChooseTrumpfModeDataValue.OBEABE.equals(mode)) {
			return Trumpf.TOPDOWN;
		} else if (ChooseTrumpfModeDataValue.UNDEUFE.equals(mode)) {
			return Trumpf.BUTTOMUP;
		} else if (ChooseTrumpfModeDataValue.TRUMPF.equals(mode)) {
			String colorString = jsonObjectMap.getString(ChooseTrumpfColorDataValue.PROPERTY_NAME);
			Color color = Color.valueOf(colorString);
			return Trumpf.getTrumpf(color);
		}

		throw new IllegalStateException("Trumpf mode " + mode + " could not be parsed!");
	}

	public static Color getColorOfFirstPlayedCard(final String message) {
		JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
		JsonArray jsonArray = jsonObject.getJsonArray(FIELD_DATA);
		if (jsonArray.size() == 0) {
			return null;
		}
		// the color of the first card this is the color the player has to play
		JsonObject jsonObjectMap = jsonArray.getJsonObject(0);
		String colorString = jsonObjectMap.getString(CardColorMapper.PROPERTY_NAME);
		return Color.valueOf(colorString);
	}

	public static Card getPlayedCard(final String message) {
		JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
		JsonArray jsonArray = jsonObject.getJsonArray(FIELD_DATA);
		JsonObject jsonObjectMap = jsonArray.getJsonObject(jsonArray.size() - 1);
		return getCard(jsonObjectMap);
	}

	public static SchieberStich getStich(final String message) {
		JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
		JsonObject jsonObjectMap = jsonObject.getJsonObject(FIELD_DATA);
		int playerNumber = jsonObjectMap.getInt(GetPlayerDataValue.ID);
		SchieberStich stich = new SchieberStich(PlayerNumberMapper.getPlayerNumber(playerNumber));
		JsonArray jsonArray = jsonObjectMap.getJsonArray(GetTeamsDataValue.TEAMS);
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject jsonTeamMap = jsonArray.getJsonObject(i);
			String teamName = jsonTeamMap.getString(GetTeamsDataValue.NAME);
			Team team = TeamMapper.getTeam(teamName);
			int points = jsonTeamMap.getInt(GetTeamsDataValue.CURRENT_ROUND_POINTS);
			stich.setTeamScore(new TeamScore(points, team));
		}
		return stich;
	}

	public static List<String> getRequestSessionChoice(final String message) {
		List<String> sessions = new ArrayList<String>();
		JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
		JsonArray jsonArray = jsonObject.getJsonArray(FIELD_DATA);
		for (int i = 0; i < jsonArray.size(); i++) {
			sessions.add(jsonArray.getString(i));
		}
		return sessions;
	}

	public static Card getRejectedCard(final String message) {
		JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();
		JsonObject jsonObjectMap = jsonObject.getJsonObject(FIELD_DATA);
		return getCard(jsonObjectMap);
	}
}
