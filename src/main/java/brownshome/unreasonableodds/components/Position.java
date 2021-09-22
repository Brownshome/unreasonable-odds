package brownshome.unreasonableodds.components;

import brownshome.vecmath.Rot3;
import brownshome.vecmath.Vec2;

/**
 * A component that represents a position and orientation
 */
public record Position(Vec2 position, Rot3 orientation) { }
