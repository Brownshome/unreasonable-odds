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

	requires static brownshome.netcode.annotation;

	requires brownshome.netcode;

	// Reflection access to schema names
	opens brownshome.unreasonableodds.session.net to brownshome.netcode;
	opens brownshome.unreasonableodds.net to brownshome.netcode;
}