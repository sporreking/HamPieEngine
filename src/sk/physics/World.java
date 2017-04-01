package sk.physics;

import java.util.ArrayList;

import sk.game.Time;
import sk.util.vector.Vector2f;

public class World {
	
	static ArrayList<Body> bodies = new ArrayList<Body>();
	
	public static Vector2f gravity = new Vector2f(0.001f, 0.1f);
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
				if (a.isDynamic())
					a.addVelocity(deltaGravity);
				a.step(stepLength);
			}
			
			// Check for collisions
			for (int i = 0; i < bodies.size(); i++) {
				for (int j = 0; j < i; j++) {
					Body a = bodies.get(i);
					Body b = bodies.get(j);
					// Make sure not both are static
					if (!a.isDynamic() && !b.isDynamic()) return;
					// Make sure not both are triggers
					if (a.isTrigger() && b.isTrigger()) return;
					// Check if they're in roughly the same area
					float bpRange = (float) Math.pow(a.getShape().getBP() * Math.max(a.getTransform().scale.x, a.getTransform().scale.y)  + b.getShape().getBP() * Math.max(b.getTransform().scale.x, b.getTransform().scale.y), 2.0f);
					float distanceSq = a.getTransform().position.clone().sub(b.getTransform().position).lengthSquared();
					// Does it pass the broad phase test
					if (bpRange <= distanceSq) return;
					CollisionData c = Shape.SATtest(a.getShape(), b.getShape());
					
					// If there was a collision, handle it
					if (c != null) {
						c.a = a;
						c.b = b;
						// Add their collision events
						a.addCollision(c);
						b.addCollision(c);
						
						// If one of them is a trigger we are done
						if (a.isTrigger() || b.isTrigger()) continue;
						// So we can refer to them as dynamic or static, if they are
						Body staticBody = b.isDynamic() ? a : b;
						Body dynamicBody = b.isDynamic() ? b : a;
						// If both bodies are dynamic
						boolean dynamicCollision = (a.isDynamic() && b.isDynamic());
						
						// If one is static
							
						// The normal should point from the static body
						if (staticBody.getShape() == c.normalOwner) {
							c.normal.negate();
						}
						
						// Move it back
						Vector2f reverse;
						if (dynamicCollision) {
							reverse = (Vector2f) c.normal.clone().scale(0.5f * c.collisionDepth + FLOAT_CORRECT);
							staticBody.getTransform().position.sub(reverse);
						} else {
							reverse = (Vector2f) c.normal.clone().scale(c.collisionDepth + FLOAT_CORRECT);
						}
						
						dynamicBody.getTransform().position.add(reverse);
						
						
						// Change the velocity
						Vector2f relativeVelocity = new Vector2f();
						Vector2f.sub(staticBody.getVelocity(), dynamicBody.getVelocity(), relativeVelocity);
						
						float normalVelocity = Vector2f.dot(relativeVelocity, c.normal);
						// Make sure we're not moving away
						if (0.0f < normalVelocity) {
							// Velocity correction
							float bounce = Math.min(a.getBounce(), b.getBounce());
							float bounceImpulse = normalVelocity * (bounce + 1.0f);
							if (dynamicCollision) {
								bounceImpulse = normalVelocity * (bounce + 1.0f);
								bounceImpulse /= a.getInvertedMass() + b.getInvertedMass();
								Vector2f bounceForce = c.normal.clone().scale(bounceImpulse);
								dynamicBody.addForce(bounceForce.scale(a.getMass()));
								staticBody.addForce(bounceForce.scale(-a.getInvertedMass() * b.getMass()));
							} else {
								bounceImpulse *= dynamicBody.getMass();
								Vector2f bounceForce = c.normal.clone().scale(bounceImpulse);
								dynamicBody.addForce(bounceForce);
							}
							
							// Friction
							float mu = Math.min(a.getFriction(), b.getFriction());
							if (mu != 0.0f) {
								float frictionImpulse = Math.abs(bounceImpulse * mu);
								// Super fast manual rotation and creation
								Vector2f tangent = new Vector2f(c.normal.y, -c.normal.x);
								// Make sure we're slowing down in the right direction
								float frictionDirection = relativeVelocity.dot(tangent);
								if (0.0f > frictionDirection) {
									// If they're pointing the same way, flip them
									tangent.negate();
								}
								
								frictionDirection = Math.abs(frictionDirection);
								
								// If the friction force will slow us down too much, clamp it
								if (frictionDirection < frictionImpulse) {
									tangent.scale(frictionDirection * dynamicBody.getMass());
								} else {
									tangent.scale(frictionImpulse * dynamicBody.getMass());
								}

								// Add it to the body
								if (dynamicCollision) {
									dynamicBody.addForce(tangent.scale(0.5f));
									dynamicBody.addForce(tangent.scale(-1.0f));
								} else {
									dynamicBody.addForce(tangent);									
								}
							}
						}
					}
				}
			}
		}
	}
}