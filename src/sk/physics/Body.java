package sk.physics;

import java.util.ArrayList;

import sk.entity.Component;
import sk.gfx.Transform;
import sk.util.vector.Vector2f;

public class Body extends Component {
	
	// Force and velocity, self explanatory
	private Vector2f velocity = new Vector2f();
	private Vector2f force = new Vector2f();
	
	// The mass
	private float mass = 0.0f;
	private float invertedMass = 0.0f;
	
	// The friction coefficient
	private float friction = 0.0f;
	private float invertedFriction = 0.0f;
	
	// The bounce factor
	private float bounce = 0.0f;
	
	// If the body is dynamic
	private boolean dynamic = true;
	
	// If the body should trigger any collision response
	private boolean trigger = false;
	
	// If the body should rotate or not (WIP
	private boolean rotatable = true;
	
	// A reference to the shape
	private Shape shape;
	
	// A quick reference to the transform
	private Transform transform;
	
	// A list of all collisions this frame
	private ArrayList<CollisionData> collisions = new ArrayList<CollisionData>();
	
	public Body() {
		this(1.0f, 1.0f);
	}
	
	public Body(float mass) {
		this(mass, 1.0f);
	}
	
	public Body(float mass, float friction) {
		this(mass, friction, 1.0f);
	}
	
	public Body(float mass, float friction, float bounce) {
		setMass(mass);
		setFriction(friction);
		setBounce(bounce);
		World.addBody(this);
	}

	public CollisionData getCollision(Body b) {
		for (CollisionData c : collisions) {
			//TODO
		}
		return null;
	}
	
	public void addCollision(CollisionData c) {
		c = new CollisionData(c);
		if (c.a == this) {
			c.other = c.b;
		} else {
			c.other = c.a;
		}
		
		collisions.add(c);
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public boolean isDynamic() {
		return dynamic;
	}
	
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}
	
	public void setRotateable(boolean rotatable) {
		this.rotatable = rotatable;
	}
	
	public boolean isRotatable() {
		return rotatable;
	}
	
	public void setFriction(float friction) {
		if (mass <= 0.0f) {
			throw new IllegalArgumentException("Zero or negative friction supplied.");
		}
		this.friction = friction;
		this.invertedFriction = 1.0f / friction;
	}
	
	public float getFriction() {
		return friction;
	}
	
	public float getInvertedFriction() {
		return invertedFriction;
	}
	
	
	public void setMass(float mass) {
		if (mass <= 0.0f) {
			throw new IllegalArgumentException("Zero or negative mass supplied.");
		}
		this.mass = mass;
		this.invertedMass = 1.0f / mass;
	}

	public float getMass() {
		return mass;
	}
	
	public float getInvertedMass() {
		return invertedMass;
	}
	
	public Vector2f getVelocity() {
		return velocity.clone();
	}
	
	public float getBounce() {
		return bounce;
	}
	
	public void setBounce(float bounce) {
		if (bounce < 0.0f) {
			throw new IllegalArgumentException("Negative bounce supplied.");
		}
		this.bounce = bounce;
	}
	
	public void addForce(Vector2f force) {
		Vector2f.add((Vector2f) force, this.force, this.force);
	}
	
	public void addVelocity(Vector2f vel) {
		Vector2f.add(vel, velocity, velocity);
	}
	
	public Transform getTransform() {
		return transform;
	}
	
	@Override
	public void init() {
		shape = getParent().get(Shape.class);
		transform = getParent().get(Transform.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Component>[] requirements() { 
		return (Class<? extends Component>[]) new Class<?>[] {
			Shape.class,
			Transform.class
		}; 
	}
		
	public void step(double delta) {
		if (isDynamic()) {
			Vector2f.add(velocity, (Vector2f) force.scale(invertedMass), velocity);
			force.set(0.0f, 0.0f);			
		}
		Vector2f.add(transform.position, (Vector2f) velocity.clone().scale((float) delta), transform.position);
		collisions.clear();
	}

	public Vector2f getMomentum() {
		return (Vector2f) getVelocity().scale(mass);
	}

	public boolean isTrigger() {
		return trigger;
	}
}