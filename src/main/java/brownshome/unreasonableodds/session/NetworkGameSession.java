package brownshome.unreasonableodds.session;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import brownshome.netcode.BaseSchema;
import brownshome.netcode.Schema;
import brownshome.netcode.udp.UDPConnection;
import brownshome.netcode.udp.UDPConnectionManager;
import brownshome.unreasonableodds.*;
import brownshome.unreasonableodds.entites.StaticMap;
import brownshome.unreasonableodds.packets.game.GameSchema;
import brownshome.unreasonableodds.packets.game.SetUniverseHostPacket;
import brownshome.unreasonableodds.packets.session.SessionSchema;
import brownshome.unreasonableodds.player.*;

public class NetworkGameSession extends NetworkSession implements GameSession {
	private final Map<Id, NetworkGamePlayer> players;
	private final AtomicInteger nextUniverseId = new AtomicInteger();
	private final Rules rules;

	protected record UniverseInfo(Universe universe, InetSocketAddress hostAddress) {
		UniverseInfo withHostAddress(InetSocketAddress hostAddress) {
			return new UniverseInfo(universe, hostAddress);
		}

		UniverseInfo withUniverse(Universe universe) {
			return new UniverseInfo(universe, hostAddress);
		}

	}

	private final Map<Id, UniverseInfo> universes;

	private final UDPConnection universeRegistrar;
	private final Collection<UDPConnection> connections;

	private StaticMap map;

	public static final class NetworkGameSessionConverter extends SessionConverter<NetworkGameSession> { }

	public static NetworkGameSession get() {
		return (NetworkGameSession) NetworkSession.get();
	}

	protected NetworkGameSession(UDPConnectionManager connectionManager,
	                             Rules rules,
	                             Map<Id, NetworkGamePlayer> players,
	                             Map<Id, UniverseInfo> universes,
	                             Collection<UDPConnection> connections,
	                             UDPConnection universeRegistrar) {
		super(connectionManager);

		// More complex topologies can be supported later, at the moment we are using a host - client system
		assert connections.isEmpty() || universeRegistrar == null;

		this.players = players;
		this.rules = rules;
		this.universes = universes;
		this.universeRegistrar = universeRegistrar;
		this.connections = connections;
	}

	public static final class Builder {
		private final UDPConnectionManager connectionManager;
		private final Map<Id, NetworkGamePlayer> players;
		private final Map<Id, UniverseInfo> universes;
		private final Rules rules;

		/**
		 * All connected addresses
		 */
		private final Collection<UDPConnection> connections = new ArrayList<>();
		private final int sessionId;

		private UDPConnection universeRegistrar;

		public Builder(NetworkLobbySession lobbySession) {
			this.connectionManager = lobbySession.connectionManager();
			this.rules = lobbySession.rules();
			this.players = new HashMap<>();
			this.universes = new HashMap<>();
			this.sessionId = lobbySession.sessionId();
		}

		public void makeClientSession(UDPConnection universeRegistrar) {
			this.universeRegistrar = universeRegistrar;
		}

		public void addConnection(UDPConnection connection) {
			assert universeRegistrar == null;
			connections.add(connection);
			connection.connect(gameSchema());
		}

		public void addPlayer(NetworkGamePlayer player) {
			players.put(player.id(), player);
		}

		public NetworkGameSession build() {
			var session = new NetworkGameSession(connectionManager, rules, players, universes, connections, universeRegistrar);
			session.sessionId(sessionId);
			session.markThreadAsSessionThread();
			return session;
		}
	}

	protected final UDPConnection universeRegistrar() {
		return universeRegistrar;
	}

	protected boolean isUniverseRegistrar() {
		return universeRegistrar == null;
	}

	@Override
	public Rules rules() {
		return rules;
	}

	public final NetworkGamePlayer player(Id id) {
		return players.get(id);
	}

	@Override
	public Collection<? extends NetworkGamePlayer> players() {
		return players.values();
	}

	public final Universe universe(Id id) {
		return universes.get(id).universe;
	}

	public final void universe(Universe universe, InetSocketAddress sender) {
		universes.compute(universe.id(), (id, universeInfo) -> universeInfo == null
				? new UniverseInfo(universe, sender)
				: universeInfo.withUniverse(universe));
	}

	public final InetSocketAddress address() {
		return connectionManager().address();
	}

	public final InetSocketAddress hostAddress(Id id) {
		return universes.get(id).hostAddress;
	}

	@Override
	public Id allocateUniverseId() {
		return new Id(sessionId(), nextUniverseId.getAndIncrement());
	}

	public final void startGame(Multiverse multiverse) { }

	/**
	 * Called when this session creates a new universe
	 * @param universe the universe to create
	 */
	public final void registerNewUniverse(Universe universe) {
		setUniverseHost(universe.id(), connectionManager().address());

		if (!isUniverseRegistrar()) {
			universeRegistrar.send(new SetUniverseHostPacket(null, universe.id(), connectionManager().address()));
		}
	}

	public final void setUniverseHost(Id id, InetSocketAddress hostAddress) {
		universes.compute(id, (i, universeInfo) -> universeInfo == null ? new UniverseInfo(null, hostAddress) : universeInfo.withHostAddress(hostAddress));

		if (isUniverseRegistrar()) {
			var packet = new SetUniverseHostPacket(null, id, hostAddress);

			for (var c : connections) {
				c.send(packet);
			}
		}
	}

	protected static List<Schema> gameSchema() {
		/*
		 * TODO james.brown [13-10-2021] Move the lobby specific schema into its own schema
		 */
		return List.of(new BaseSchema(), new SessionSchema(), new GameSchema());
	}

	public StaticMap computeStaticMapIfAbsent(Supplier<StaticMap> func) {
		if (map == null) {
			map = func.get();
		}

		return map;
	}
}
