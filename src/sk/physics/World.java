package sk.physics;

import java.util.ArrayList;

import sk.entity.Entity;
import sk.game.Time;
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
	
	ArrayList<Body> bodies = new ArrayList<Body>();
	
	public Vector2f gravity = new Vector2f(0.001f, 0.1f);
	public float stepLength = 1.0f / 60.0f;
	private float timer = 0.0f;
	
	/**
	 * Adds a body to this horrid world of collision
	 * @param body The body you wish to add
	 */
	public void addBody(Body body) {
		// Make sure there's only one of each body
		if (bodies.contains(body)) return;
		bodies.add(body);
	}
	
	/**
	 * Removes the body from the list of bodies
	 * @param body The body that should be removed
	 */
	public void removeBody(Body body) {
		bodies.remove(body);
	}
	
	/**
	 * Updates the world, checks for collisions and
	 * steps forward through the simulation.
	 * @param delta The time step
	 */
	public void update(double delta) {
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
					// Check if they share a layer
					if (!a.sharesLayer(b)) continue;
					// Make sure not both are static
					if (!a.isDynamic() && !b.isDynamic()) continue;
					// Make sure not both are triggers
					if (a.isTrigger() && b.isTrigger()) continue;
 					
					CollisionData c = null;
					// There are multiple shapes, skipp the bread phase and try the bodies
					Transform ta = a.getTransform();
					Transform tb = b.getTransform();
					for (Shape shapeA : a.getShapes()) {
						for (Shape shapeB : b.getShapes()) {
							float bpRange = (float) Math.pow(
									a.getShape().getBP() * Math.max(
											a.getTransform().scale.x, 
											a.getTransform().scale.y) + 
									b.getShape().getBP() * Math.max(
											b.getTransform().scale.x, 
											b.getTransform().scale.y), 2.0f);
							float distanceSq = Vector2f.sub(
									a.getTransform().position.clone().add(shapeA.getCenter()), 
									b.getTransform().position.clone().add(shapeB.getCenter()), 
									null).lengthSquared();
							
							if (bpRange <= distanceSq) continue;
							
							c = CollisionData.SATtest(a.getShape(), a.getTransform(), b.getShape(), b.getTransform());
							
							if (c == null) continue;
							
							if (a.isDynamic()) {
								c.a = b;
								c.b = a;
							} else {
								c.a = a;
								c.b = b;
							}
							
							handleCollision(c);
						}
					}
				}
			}
		}
	}

	private void handleCollision(CollisionData c) {
		// Add their collisions to the bodies
		c.a.addCollision(c);
		c.b.addCollision(c);
		
		// If one of them is a trigger we are done
		if (c.a.isTrigger() || c.b.isTrigger()) return;
		
		// Now we just solve the collision and everyone is happy
		c.solve();
	}
	
	/**
	 * Adds the entities body to the world
	 * @param entity the entity you whish to add
	 */
	public void addEntity(Entity entity) {
		addBody(entity.get(Body.class));
	}
}