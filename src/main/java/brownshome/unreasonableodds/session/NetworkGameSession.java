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
	private final Map<NetworkPlayer.Id, NetworkGamePlayer> players;
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
	private final Map<Universe.Id, UniverseInfo> universes;
	private final UDPConnection universeRegistrar;

	private StaticMap map;

	public static final class NetworkGameSessionConverter extends SessionConverter<NetworkGameSession> { }

	public static NetworkGameSession get() {
		return (NetworkGameSession) NetworkSession.get();
	}

	protected NetworkGameSession(UDPConnectionManager connectionManager,
	                             Rules rules,
	                             Map<NetworkPlayer.Id, NetworkGamePlayer> players,
	                             Map<Universe.Id, UniverseInfo> universes,
	                             UDPConnection universeRegistrar) {
		super(connectionManager);

		this.players = players;
		this.rules = rules;
		this.universes = universes;
		this.universeRegistrar = universeRegistrar;
	}

	public static final class Builder {
		private final UDPConnectionManager connectionManager;
		private final Map<NetworkPlayer.Id, NetworkGamePlayer> players;
		private final Map<Universe.Id, UniverseInfo> universes;
		private final Rules rules;

		/**
		 * The address of the universe registrar, this address will be null if we are the registrar
		 */
		private UDPConnection universeRegistrar;

		public Builder(UDPConnectionManager connectionManager, Rules rules) {
			this.connectionManager = connectionManager;
			this.rules = rules;
			this.players = new HashMap<>();
			this.universes = new HashMap<>();
		}

		public void makeClientSession(UDPConnection universeRegistrar) {
			this.universeRegistrar = universeRegistrar;
		}

		public void addPlayer(NetworkGamePlayer player) {
			players.put(player.id(), player);
		}

		public NetworkGameSession build() {
			return new NetworkGameSession(connectionManager, rules, players, universes, universeRegistrar);
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

	public final NetworkGamePlayer player(NetworkPlayer.Id id) {
		return players.get(id);
	}

	@Override
	public Collection<? extends NetworkGamePlayer> players() {
		return players.values();
	}

	public final Universe universe(Universe.Id id) {
		return universes.get(id).universe;
	}

	public final void universe(Universe universe) {
		universes.compute(universe.id(), (id, universeInfo) -> universeInfo == null
				? new UniverseInfo(universe, universe.id().creator())
				: universeInfo.withUniverse(universe));
	}

	public final InetSocketAddress address() {
		return connectionManager().address();
	}

	public final InetSocketAddress hostAddress(Universe.Id id) {
		return universes.get(id).hostAddress;
	}

	@Override
	public Universe.Id allocateUniverseId() {
		return new Universe.Id(address(), nextUniverseId.getAndIncrement());
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

	public final void setUniverseHost(Universe.Id id, InetSocketAddress hostAddress) {
		universes.compute(id, (i, universeInfo) -> universeInfo == null ? new UniverseInfo(null, hostAddress) : universeInfo.withHostAddress(hostAddress));

		if (isUniverseRegistrar()) {
			var packet = new SetUniverseHostPacket(null, id, hostAddress);

			for (var player : players.values()) {
				if (!(player instanceof LocalGamePlayer)) {
					// Not us
					connectionManager().getOrCreateConnection(player.id().address()).send(packet);
				}
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
