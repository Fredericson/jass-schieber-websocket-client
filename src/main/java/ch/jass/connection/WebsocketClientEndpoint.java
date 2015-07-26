package ch.jass.connection;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import ch.jass.connection.inbound.RequestMessageBuilder;
import ch.jass.connection.inbound.RequestMessageType;
import ch.jass.connection.mapping.CardColorMapper;
import ch.jass.connection.mapping.CardNumberMapper;
import ch.jass.connection.mapping.ChooseSessionDataValue;
import ch.jass.connection.mapping.ChooseTrumpfColorDataValue;
import ch.jass.connection.mapping.ChooseTrumpfModeDataValue;
import ch.jass.connection.outbound.SendMessage;
import ch.jass.connection.outbound.SendMessageBuilder;
import ch.jass.connection.outbound.SendMessageDataMap;
import ch.jass.connection.outbound.SendMessageType;
import ch.jass.model.Card;
import ch.jass.model.Color;
import ch.jass.model.SessionType;
import ch.jass.model.Trumpf;
import ch.jass.model.schieber.api.SchieberPlayerCallback;
import ch.jass.model.schieber.api.SchieberServerService;
import ch.jass.model.schieber.table.PlayerNumber;
import ch.jass.model.schieber.table.PlayerOnTable;
import ch.jass.model.schieber.table.SchieberStich;
import ch.jass.model.schieber.table.SchieberTableInfo;

import com.sun.istack.internal.logging.Logger;

/**
 * JassServer Client
 *
 */
@ClientEndpoint
public class WebsocketClientEndpoint implements SchieberServerService {

	private static final Logger LOGGER = Logger.getLogger(WebsocketClientEndpoint.class);

	private final URI endpointURI;
	private Session userSession = null;
	private SchieberPlayerCallback schieberPlayer;

	// Webplatform always choose Player1 as first Trumpfable Player
	private PlayerNumber actualTrumpfablePlayer = PlayerNumber.PLAYER_1;

	public WebsocketClientEndpoint(final URI endpointURI) {
		this.endpointURI = endpointURI;
	}

	@Override
	public void connectToServer(final SchieberPlayerCallback schieberPlayer) {
		this.schieberPlayer = schieberPlayer;
		try {
			WebSocketContainer container = ContainerProvider
					.getWebSocketContainer();
			container.connectToServer(this, endpointURI);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Callback hook for Connection open events.
	 *
	 * @param userSession
	 *            the userSession which is opened.
	 */
	@OnOpen
	public void onOpen(final Session userSession) {
		System.out.println("opening websocket");
		this.userSession = userSession;
	}

	/**
	 * Callback hook for Connection close events.
	 *
	 * @param userSession
	 *            the userSession which is getting closed.
	 * @param reason
	 *            the reason for connection close
	 */
	@OnClose
	public void onClose(final Session userSession, final CloseReason reason) {
		System.out.println("closing websocket");
		this.userSession = null;
	}

	/**
	 * Callback hook for Message Events. This method will be invoked when a
	 * client send a message.
	 *
	 * @param message
	 *            The text message
	 */
	@OnMessage
	public void onMessage(final String message) {

		try {
			System.out.println(schieberPlayer.getName() + ": " + message);
			RequestMessageType requestMsgType = RequestMessageBuilder.toRequestMessageType(message);
			// System.out.println(JassBot.this.getRepresentationString() + " " + requestMsg.toString());
			if (RequestMessageType.REQUEST_PLAYER_NAME.equals(requestMsgType)) {
				sendPlayerName(schieberPlayer.getName());
			} else if (RequestMessageType.REQUEST_SESSION_CHOICE.equals(requestMsgType)) {
				List<String> sessions = RequestMessageBuilder.getRequestSessionChoice(message);
				schieberPlayer.requestSessionChoice(sessions);
			} else if (RequestMessageType.BROADCAST_SESSION_JOINED.equals(requestMsgType)) {
				PlayerOnTable playerOnTable = RequestMessageBuilder.getBroadcastSessionJoined(message);
				schieberPlayer.broadcastJoinedPlayer(playerOnTable);
			} else if (RequestMessageType.BROADCAST_TEAMS.equals(requestMsgType)) {
				SchieberTableInfo schieberTableInfo = RequestMessageBuilder.getBroadcastTeams(message);
				schieberPlayer.broadcastTeams(schieberTableInfo);
			} else if (RequestMessageType.DEAL_CARDS.equals(requestMsgType)) {
				Set<Card> cards = RequestMessageBuilder.dealCards(message);
				schieberPlayer.dealCards(cards);
			} else if (RequestMessageType.REQUEST_TRUMPF.equals(requestMsgType)) {
				boolean geschoben = RequestMessageBuilder.getRequestTrumpfData(message);
				schieberPlayer.requestTrumpf(geschoben);
			} else if (RequestMessageType.BROADCAST_TRUMPF.equals(requestMsgType)) {
				Trumpf trumpf = RequestMessageBuilder.getBroadcastTrumpfData(message);
				schieberPlayer.broadcastTrumpfForGame(trumpf);
				schieberPlayer.broadcastTrumpfablePlayer(actualTrumpfablePlayer);
				incrementTrumpfablePlayer();
			} else if (RequestMessageType.REQUEST_CARD.equals(requestMsgType)) {
				Color color = RequestMessageBuilder.getColorOfFirstPlayedCard(message);
				schieberPlayer.requestCard(color);
			} else if (RequestMessageType.REJECT_CARD.equals(requestMsgType)) {
				// This case should never happen.
				Card card = RequestMessageBuilder.getRejectedCard(message);
				LOGGER.warning("Card_Rejected: " + card);
				schieberPlayer.requestCardRejected(card);
			} else if (RequestMessageType.PLAYED_CARDS.equals(requestMsgType)) {
				Card card = RequestMessageBuilder.getPlayedCard(message);
				schieberPlayer.broadcastPlayedCard(card);
			} else if (RequestMessageType.BROADCAST_STICH.equals(requestMsgType)) {
				SchieberStich stich = RequestMessageBuilder.getStich(message);
				schieberPlayer.broadcastStich(stich);
			} else if (RequestMessageType.BROADCAST_GAME_FINISHED.equals(requestMsgType)) {
				schieberPlayer.broadcastGameFinished(null);
			} else if (RequestMessageType.BROADCAST_WINNER_TEAM.equals(requestMsgType)) {
				schieberPlayer.broadcastWinnerTeam(null);
			}
		} catch (Exception ex) {
			LOGGER.info(schieberPlayer.getName() + ": Error while receiving message: " + message, ex);
		}
	}

	private void incrementTrumpfablePlayer() {
		this.actualTrumpfablePlayer = PlayerNumber.next(actualTrumpfablePlayer);
	}

	/**
	 * Send a message.
	 *
	 * @param message
	 */
	private void sendMessage(final SendMessage sendMessage) {
		sendMessageString(SendMessageBuilder.toJSONString(sendMessage));
	}

	/**
	 * Send a message.
	 *
	 * @param message
	 */
	private void sendMessage(final SendMessageDataMap sendMessageDataMap) {
		sendMessageString(SendMessageBuilder.toJSONString(sendMessageDataMap));
	}

	private void sendMessageString(final String msg) {
		System.out.println("send: " + msg);
		this.userSession.getAsyncRemote().sendText(msg);
	}

	/**
	 * Send a playerName.
	 *
	 * @param name
	 * @param message
	 */
	private void sendPlayerName(final String playerName) {
		SendMessage sendMsg = new SendMessage(SendMessageType.CHOOSE_PLAYER_NAME, playerName);
		sendMessage(sendMsg);
	}

	@Override
	public void sendChooseSession(final SessionType sessionType, final String existingSession) {
		SendMessageDataMap sendMsg = new SendMessageDataMap(SendMessageType.CHOOSE_SESSION);
		ChooseSessionDataValue chooseSession = ChooseSessionDataValue.getMappedChooseSession(sessionType);
		sendMsg.addData(ChooseSessionDataValue.DATA_SESSION_CHOICE, chooseSession.name());
		if (existingSession != null) {
			sendMsg.addData(ChooseSessionDataValue.DATA_SESSION_NAME, existingSession);
		}
		sendMessage(sendMsg);
	}

	/**
	 * Null means geschoben.
	 * 
	 * @param trumpf
	 */
	@Override
	public void sendChooseTrumpf(final Trumpf trumpf) {
		SendMessageDataMap sendMsg = new SendMessageDataMap(SendMessageType.CHOOSE_TRUMPF);
		ChooseTrumpfModeDataValue mappedMode = ChooseTrumpfModeDataValue.getMappedMode(trumpf);
		sendMsg.addData(ChooseTrumpfModeDataValue.PROPERTY_NAME, mappedMode.name());
		ChooseTrumpfColorDataValue mappedColor = ChooseTrumpfColorDataValue.getMappedColor(trumpf);
		if (mappedColor != null) {
			sendMsg.addData(ChooseTrumpfColorDataValue.PROPERTY_NAME, mappedColor.name());
		}
		sendMessage(sendMsg);
	}

	@Override
	public void sendChooseCard(final Card card) {
		SendMessageDataMap sendMsg = new SendMessageDataMap(SendMessageType.CHOOSE_CARD);
		sendMsg.addData(CardNumberMapper.PROPERTY_NAME, CardNumberMapper.getNumber(card.getRank()));
		sendMsg.addData(CardColorMapper.PROPERTY_NAME, card.getColor().name());
		sendMessage(sendMsg);
	}
}