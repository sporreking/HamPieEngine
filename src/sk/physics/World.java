package sk.physics;

import java.util.ArrayList;

import sk.util.vector.Vector2f;

public class World {
	
	static ArrayList<Body> bodies = new ArrayList<Body>();
	
	public static Vector2f gravity = new Vector2f(0, 0.00001f);
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
		// Update all bodies
		timer += delta;
		while (stepLength < timer) {
			timer -= stepLength;
			for (Body a : bodies) {
				a.addVelocity((Vector2f) gravity.clone().scale((float) stepLength));
				a.update(stepLength);
			}
			
			// Check for collisions
			for (int i = 0; i < bodies.size(); i++) {
				for (int j = 0; j < i; j++) {
					// Make sure not both are static
					Body a = bodies.get(i);
					Body b = bodies.get(j);
					
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
							
							float floatCorrect = 0.0001f;
							
							// A normalized reversed body
							Vector2f reverse = dynamicBody.getVelocity();
							float dot = Vector2f.dot(dynamicBody.getVelocity(), c.normal);
							reverse.scale(-(c.collisionDepth / dot + floatCorrect));
							
							// Move back it back
							dynamicBody.getTransform().position.add(reverse);
							
							// Change the velocity
							
						}
						
						System.out.println("A collision occured, maybe you should handle it");
					}
				}
			}
		}
	}
}