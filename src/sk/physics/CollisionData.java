package sk.physics;

import sk.util.vector.Vector2f;

/**
 * A data class that handles collision information
 * @author ed
 */
public class CollisionData {
	// The normal
	public Vector2f normal;
	// The penetration depth
	public float collisionDepth = Float.MAX_VALUE;
	// Which shape owned the normal
	public Shape normalOwner;
	// The bodies involved
	public Body a, b;
	// The other body
	public Body other;
	
	public CollisionData() {}

	/**
	 * Copy constructor
	 * @param c The CollisionData object you wish to copy
	 * 
	 */
	public CollisionData(CollisionData c) {
		normal = c.normal;
		collisionDepth = c.collisionDepth;
		normalOwner = c.normalOwner;
		a = c.a;
		b = c.b;
		other = c.other;
	}
}
