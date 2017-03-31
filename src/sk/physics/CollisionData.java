package sk.physics;

import sk.util.vector.Vector2f;

/**
 * A data class that handles collision information
 * @author ed
 */
public class CollisionData {
	public Vector2f normal;
	public float collisionDepth = Float.MAX_VALUE;
	public Shape normalOwner;
	public Body other;
	
	public CollisionData() {}
}
