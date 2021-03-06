package sk.physics;

import java.util.ArrayList;

import sk.entity.Entity;
import sk.gfx.Transform;
import sk.util.vector.Vector2f;

/**
 * The World class handles all the collisions in the world, 
 * literally. This is the object that should be updated
 * to update all bodies in the world.
 * @author Ed
 *
 */
public class World {
	
	// Recommended is 1 / 60
	public float stepLength = 1.0f / 60.0f;

	ArrayList<Body> bodies = new ArrayList<Body>();
	public Vector2f gravity = new Vector2f(0.0f, -0.5f);
	private float timer = 0.0f;
	
	/**
	 * Adds a physics body to this world.
	 * 
	 * @param body the body you wish to add.
	 */
	public void addBody(Body body) {
		// Make sure there's only one of each body
		if (body == null) throw new IllegalArgumentException("Body passed to world is null");
		if (bodies.contains(body)) return;
		bodies.add(body);
	}
	
	/**
	 * Removes the specified body from this world if it is contained.
	 * 
	 * @param body the body that should be removed.
	 */
	public void removeBody(Body body) {
		bodies.remove(body);
	}
	
	/**
	 * Updates the world, checks for collisions and
	 * steps forward through the simulation.
	 * 
	 * @param delta the time passed since the previous frame.
	 */
	public void update(double delta) {
		timer += delta;
		// Make sure we only step if we need to
		while (stepLength < timer) {
			timer -= stepLength;
			// Update all bodies
			Vector2f deltaGravity = (Vector2f) gravity.clone().scale((float) stepLength);
			for (Body a : bodies) {
				if (a.isDynamic() && !a.isOnlyOverlap())
					a.addVelocity(deltaGravity);
				a.step(stepLength);
			}
			
			// Check for collisions
			for (int i = 1; i < bodies.size(); i++) {
				for (int j = 0; j < i; j++) {
					Body a = bodies.get(i);
					Body b = bodies.get(j);
					// Check if they share a layer
					if (!a.sharesLayer(b)) continue;
					// Make sure not both are static
					if (!a.isDynamic() && !b.isDynamic()) continue;
					// Make sure not both are triggers
					if (a.isTrigger() && b.isTrigger()) continue;
					// Make sure they don't share parent
					if (a.getParent() == b.getParent() && a.getParent() != null) continue;
 					
					
					Collision c = null;
					Transform ta = a.getTransform();
					Transform tb = b.getTransform();
					for (Shape shapeA : a.getShapes()) {
						for (Shape shapeB : b.getShapes()) {
							float bpRange = (float) Math.pow(
									shapeA.getBP(ta) + shapeB.getBP(tb), 
									2.0f);
							
							float distanceSq = 
									shapeA.getCenter(ta)
									.sub(shapeB.getCenter(tb))
									.lengthSquared();
							
							if (bpRange <= distanceSq) continue;
							c = Collision.SATtest(shapeA, ta, shapeB, tb);
							
							if (c == null) continue;
							
							
							// Skip the collision if the normal is the wrong way
							if (!b.oneWayCheck(c.collisionDepth, a.getVelocity(), c.normal) || 
								!a.oneWayCheck(c.collisionDepth, b.getVelocity(), c.normal.clone().negate())) 
								continue;

							// Now we sort them
							if (a.isDynamic()) {
								c.a = b;
								c.b = a;
							} else {
								c.a = a;
								c.b = b;
							}

							// Add their collisions to the bodies
							a.addCollision(c);
							b.addCollision(c);
							
							// If one of them is a trigger we are done
							if (a.isTrigger() || b.isTrigger()) continue;
						
							c.solve(stepLength);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Adds the body of an entity.
	 * 
	 * @param entity the entity whose body you wish to add.
	 */
	public void addEntity(Entity entity) {
		addBody(entity.get(Body.class));
	}
}
