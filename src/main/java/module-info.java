/**
 * A module containing the core logic for the game
 */
module brownshome.unreasonableodds {
	exports brownshome.unreasonableodds;
	exports brownshome.unreasonableodds.collision;
	exports brownshome.unreasonableodds.components;
	exports brownshome.unreasonableodds.entites;
	exports brownshome.unreasonableodds.generation;
	exports brownshome.unreasonableodds.history;
	exports brownshome.unreasonableodds.tile;
	exports brownshome.unreasonableodds.session;

	requires transitive brownshome.vecmath;
	requires transitive brownshome.netcode;

	// Reflection access to schema names
	opens brownshome.unreasonableodds.packets.lobby to brownshome.netcode;
	opens brownshome.unreasonableodds.packets.session to brownshome.netcode;
	opens brownshome.unreasonableodds.packets.game to brownshome.netcode;
	exports brownshome.unreasonableodds.player;
}