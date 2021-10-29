package brownshome.unreasonableodds.session;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import brownshome.netcode.BaseSchema;
import brownshome.netcode.Schema;
import brownshome.netcode.udp.UDPConnectionManager;
import brownshome.unreasonableodds.*;
import brownshome.unreasonableodds.entites.*;
import brownshome.unreasonableodds.history.BranchRecord;
import brownshome.unreasonableodds.packets.game.*;
import brownshome.unreasonableodds.packets.session.SessionSchema;
import brownshome.unreasonableodds.player.*;

public class NetworkGameSession extends NetworkSession implements GameSession {
	private final Map<Id, NetworkGamePlayer> players;
	private final AtomicInteger nextUniverseId = new AtomicInteger();
	private final Rules rules;

	private final Map<Id, Builder.UniverseInfo> universes;

	private StaticMap map;

	public static final class NetworkGameSessionConverter extends SessionConverter<NetworkGameSession> { }

	public static NetworkGameSession get() {
		return (NetworkGameSession) NetworkSession.get();
	}

	protected NetworkGameSession(UDPConnectionManager connectionManager,
	                             Rules rules,
	                             Map<Id, NetworkGamePlayer> players,
	                             Map<Id, Builder.UniverseInfo> universes,
	                             Map<InetSocketAddress, SessionId> sessionIds) {
		super(connectionManager, sessionIds);

		this.players = players;
		this.rules = rules;
		this.universes = universes;
	}

	public static class Builder {
		private final UDPConnectionManager connectionManager;
		private final Map<Id, NetworkGamePlayer> players;
		private final Rules rules;

		/**
		 * @param universe the last received universe from the host of this universe, this is null if the universe is hosted
		 *                 locally or the universe has not been received from the host.
		 * @param branchRecord the branch-record for this universe, always populated
		 * @param host the address of the host of this universe, always populated
		 */
		protected record UniverseInfo(SessionId host, Universe universe, BranchRecord branchRecord, Collection<Entity> incomingEntities) {
			public UniverseInfo {
				assert host != null;
				assert branchRecord != null;
				assert incomingEntities.isEmpty();
			}

			public UniverseInfo(SessionId host, BranchRecord branchRecord) {
				this(host, null, branchRecord, new ArrayList<>());
			}

			UniverseInfo withHost(SessionId host) {
				return new UniverseInfo(host, universe, branchRecord, new ArrayList<>());
			}

			UniverseInfo withUniverse(Universe universe) {
				return new UniverseInfo(host, universe, universe.branchRecord(), new ArrayList<>());
			}
		}

		private final SessionId sessionId;
		private final Map<InetSocketAddress, SessionId> sessionIds;

		public Builder(UDPConnectionManager connectionManager, Rules rules, SessionId id, Map<InetSocketAddress, SessionId> sessionIds) {
			this.connectionManager = connectionManager;
			this.rules = rules;
			this.players = new HashMap<>();
			this.sessionId = id;
			this.sessionIds = sessionIds;
		}

		public void addPlayer(NetworkGamePlayer player) {
			assert sessionIds.containsValue(player.id().sessionId()) && player.id().sessionId().address() != null || player.id().sessionId().equals(sessionId);

			players.put(player.id(), player);
		}

		protected void setupSession(NetworkGameSession session) {
			session.sessionId(sessionId.number());
		}

		protected NetworkGameSession build(UDPConnectionManager connectionManager,
		                                   Rules rules,
		                                   Map<Id, NetworkGamePlayer> players,
		                                   Map<InetSocketAddress, SessionId> sessionIds) {
			return new NetworkGameSession(connectionManager, rules, players, new HashMap<>(), sessionIds);
		}

		public final NetworkGameSession build() {
			var session = build(connectionManager,
					rules,
					players,
					sessionIds);
			setupSession(session);
			return session;
		}
	}

	@Override
	public Rules rules() {
		return rules;
	}

	@Override
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

	public final void receiveRemoteUniverse(Universe universe, SessionId host) {
		if (universes.computeIfPresent(universe.id(), (id, universeInfo) -> universeInfo.withUniverse(universe)) == null) {
			throw new IllegalArgumentException("Universe %s has not been registered".formatted(host));
		}
	}

	public final InetSocketAddress address() {
		return connectionManager().address();
	}

	public final SessionId hostSessionId(Id id) {
		var result = universes.get(id).host;
		assert result != null;
		return result;
	}

	@Override
	public Id allocateUniverseId() {
		return new Id(sessionId(), nextUniverseId.getAndIncrement());
	}

	public List<Id> allUniverseIds() {
		var comparator = Comparator
				.comparing((Map.Entry<Id, Builder.UniverseInfo> e) -> e.getValue().branchRecord)
				.thenComparingInt(e -> e.getKey().sessionId().number())
				.thenComparingInt(e -> e.getKey().number());

		return universes.entrySet().stream().sorted(comparator).map(Map.Entry::getKey).toList();
	}

	public final void exportEntity(Id universeId, Entity entity) {
		assert !hostSessionId(universeId).equals(sessionId()) : "This method must only be used for external universes";

		if (entity instanceof PlayerCharacter player) {
			var controllingPlayer = (ControllingNetworkPlayer) player(player.playerId());
			ExportedGamePlayer exported = ExportedGamePlayer.exportPlayer(controllingPlayer, universeId);
			players.put(controllingPlayer.id(), exported);
		}

		connectionManager().getOrCreateConnection(hostSessionId(universeId).address()).send(new ExportEntityPacket(null, universeId, entity));
	}

	public final void importEntity(Id universeId, Entity entity) {
		if (entity instanceof PlayerCharacter player) {
			var controller = player(player.playerId());
			ImportedGamePlayer importedGamePlayer = ImportedGamePlayer.create(controller, connectionManager().getOrCreateConnection(controller.id().sessionId().address()));
			players.put(importedGamePlayer.id(), importedGamePlayer);
		}

		universes.get(universeId).incomingEntities.add(entity);
	}

	public final Collection<Entity> newIncomingEntities(Id id) {
		var incoming = universes.get(id).incomingEntities;
		var result = new ArrayList<>(universes.get(id).incomingEntities);
		incoming.clear();
		return result;
	}

	public final void stepExportedPlayers(Multiverse.MultiverseStep step) {
		for (var p : players()) {
			if (p instanceof ExportedGamePlayer exported) {
				var universe = universe(exported.hostUniverseId());

				// We have not received a universe yet
				if (universe == null) {
					continue;
				}

				var newEntities = universe.exportedStep(step, exported);
				var address = hostSessionId(exported.hostUniverseId()).address();

				int idNumber = exported.id().number();
				assert Short.toUnsignedInt((short) idNumber) == idNumber;
				connectionManager().getOrCreateConnection(address).send(new PlayerUpdatePacket(null, (short) idNumber, newEntities));
			}
		}
	}

	public void startGame(Multiverse multiverse) { }

	/**
	 * Called when this session creates a new universe
	 * @param universe the universe to create
	 */
	public final void registerNewUniverse(Universe universe) {
		registerNewUniverse(universe.id(), universe.branchRecord(), sessionId());

		var packet = new RegisterNewUniversePacket(null, universe.id(), universe.branchRecord());
		for (var e : sessionIds().entrySet()) {
			var sessionId = e.getValue();
			var address = e.getKey();

			// Don't send to self
			if (sessionId.equals(sessionId())) {
				continue;
			}

			connectionManager().getOrCreateConnection(address).send(packet);
		}
	}

	public final void registerNewUniverse(Id id, BranchRecord branchRecord, SessionId hostSession) {
		if (universes.putIfAbsent(id, new Builder.UniverseInfo(hostSession, branchRecord)) != null) {
			throw new IllegalArgumentException("Cannot re-register universe %s".formatted(id));
		}
	}

	protected static List<Schema> gameSchema() {
		return List.of(new BaseSchema(), new SessionSchema(), new GameSchema());
	}

	public final StaticMap map() {
		return map;
	}

	public void map(StaticMap map) {
		this.map = map;
	}
}
