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
	exports brownshome.unreasonableodds.network;

	requires transitive brownshome.vecmath;

	requires brownshome.netcode;

	// Reflection access to schema names
	opens brownshome.unreasonableodds.network.packets to brownshome.netcode;
	opens brownshome.unreasonableodds.packets to brownshome.netcode;
}