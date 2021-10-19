package brownshome.unreasonableodds.entites;

public enum KnownEntities {
	HISTORICAL_CHARACTER,
	JUMP_SCAR,
	PLAYER_CHARACTER,
	STATIC_MAP;

	public int id() {
		return ordinal();
	}
}
