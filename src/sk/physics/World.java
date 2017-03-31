package sk.physics;

import java.util.ArrayList;

import sk.util.vector.Vector2f;

public class World {
	
	static ArrayList<Body> bodies = new ArrayList<Body>();
	
	public static Vector2f gravity = new Vector2f(0.01f, 0.1f);
	public static final float FLOAT_CORRECT = 0.00001f;
	public static float stepLength = 1.0f / 60.0f;
	private static float timer = 0.0f;
	
	static public void addBody(Body body) {
		// Make sure there's only one of each body
		if (bodies.contains(body)) return;
		bodies.add(body);
	}
	
	/**
	 * Removes the body from the list of bodies
	 * @param body The body that should be removed
	 */
	static public void removeBody(Body body) {
		bodies.remove(body);
	}
	
	/**
	 * Updates the world, checks for collisions and
	 * steps forward through the simulation.
	 * @param delta The time step
	 */
	static public void update(double delta) {
		timer += delta;
		// Make sure we only step if we need to
		while (stepLength < timer) {
			timer -= stepLength;
			// Update all bodies
			Vector2f deltaGravity = (Vector2f) gravity.clone().scale((float) stepLength);
			for (Body a : bodies) {
				a.addVelocity(deltaGravity);
				a.update(stepLength);
			}
			
			// Check for collisions
			for (int i = 0; i < bodies.size(); i++) {
				for (int j = 0; j < i; j++) {
					Body a = bodies.get(i);
					Body b = bodies.get(j);
					// Check if they're in roughly the same area
					float bpRange = (float) Math.pow(a.getShape().getBP() + b.getShape().getBP(), 2.0f);
					float distanceSq = a.getTransform().position.clone().sub(b.getTransform().position).lengthSquared();
					if (bpRange <= distanceSq)
					// Make sure not both are static
					if (!a.isDynamic() && !b.isDynamic()) return;
					CollisionData c = Shape.SATtest(a.getShape(), b.getShape());
					
					// If there was a collision, handle it
					if (c != null) {
						// If both are dynamic
						if (a.isDynamic() && b.isDynamic()) {
							
						} else {
							// Else, one of them is dynamic, and one is static.
							Body staticBody = a.isDynamic() ? b : a;
							Body dynamicBody = a.isDynamic() ? a : b;
							
							if (staticBody.getShape() == c.normalOwner) {
								c.normal.negate();
							}
							
							// A normalized reversed body
							Vector2f reverse = dynamicBody.getVelocity();
							float dot = Vector2f.dot(dynamicBody.getVelocity(), c.normal);
							reverse.scale(-(c.collisionDepth / dot + FLOAT_CORRECT));
							
							// Move back it back
							dynamicBody.getTransform().position.add(reverse);
							
							// Change the velocity
							Vector2f deltaVel = new Vector2f();
							// TODO: THIS!!!
							dynamicBody.addVelocity(deltaVel);;
						}
					}
				}
			}
		}
	}
}