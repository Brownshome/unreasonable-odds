package brownshome.unreasonableodds.entites;

enum KnownEntities {
	HISTORICAL_CHARACTER,
	JUMP_SCAR,
	PLAYER_CHARACTER,
	STATIC_MAP;

	public int id() {
		return ordinal();
	}
}
