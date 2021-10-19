module brownshome.unreasonableodds.gdx {
	requires brownshome.unreasonableodds;
	requires browngu.logging;

	requires com.badlogicgames.gdx;
	requires com.badlogicgames.gdx.backend;

	// Needed for game-rule lookup
	opens brownshome.unreasonableodds.gdx to brownshome.unreasonableodds;

	requires java.desktop;
}