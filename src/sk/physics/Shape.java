package sk.physics;

import java.util.ArrayList;

import com.sun.xml.internal.bind.v2.model.util.ArrayInfoUtil;

import sk.entity.Component;
import sk.gfx.Transform;
import sk.util.vector.Vector2f;

public class Shape extends Component {
	
	private Vector2f[] points;
	private Vector2f[] normals;
	private Vector2f[] edges;
	
	private Transform transform;
	
	private float broadPhaseLength = 0.0f;
	
	/**
	 * 
	 * @param points The points that make up the shape.
	 * The points will be joined in the order you supplied, 
	 * where clockwise is expected
	 * Note that they are not allowed to be concave.
	 */
	public Shape(Vector2f... points) {
		this.points = points;
		processPoints();
	}
	
	/**
	 * Processes the points in the object and generates the
	 * appropriate normal and edge data. This speeds up the 
	 * collision check by doing some preprocessing
	 */
	private void processPoints() {
		normals = new Vector2f[points.length];
		edges = new Vector2f[points.length];
		
		for (int i = 0; i < points.length; i++) {
			edges[i] = new Vector2f();
			
			Vector2f.sub(points[(i + 1) % points.length], points[i], edges[i]);
			
			// Check if the current point is further away then the current
			float currentLength = points[i].lengthSquared();
			if (broadPhaseLength < currentLength) {
				broadPhaseLength = currentLength; 
			}
			
			// Calculated of 90 degree rotation matrix
			Vector2f normal = new Vector2f(-edges[i].y, edges[i].x);
			normal.normalise();
			normals[i] = normal;
		}
	}
	
	@Override
	public void init() {
		transform = getParent().get(Transform.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Component>[] requirements() { 
		return (Class<? extends Component>[]) new Class<?>[] {
			Transform.class
		}; 
	}
	
	/**
	 * Casts the shape along the normal specified
	 * @param normal The normal you want to cast along
	 * @return The longest distance along the normal
	 * of all the points
	 */
	public float castAlong(Vector2f normal) {
		float maxLength = 0;
		float angle = transform.rotation;
		Vector2f rotatedPoint = new Vector2f();
		for (Vector2f p : points) {
			Vector2f.rotate(p, angle, rotatedPoint);
			maxLength = Math.max(maxLength, Vector2f.dot(rotatedPoint, normal));
		}
		return maxLength;
	}
	
	/**
	 * Does a Separate Axis Theorem test on the two bodies
	 * @param a The first shape you want to check collision against
	 * @param b The second body you want to check collision against
	 * @return The collision object with the appropriate 
	 * data for the collision, returns null if no collision
	 */
	static public CollisionData SATtest(Shape a, Shape b) {
		Vector2f distance = new Vector2f();
		Vector2f.sub(a.transform.position, b.transform.position, distance);
		
		CollisionData collision = new CollisionData();
		
		float lengthA = 0.0f;
		float lengthB = 0.0f;
		float dotDistance = 0.0f;
		float depth = 0.0f;
		
		// Fuse the arrays into one
		Vector2f[] normals = new Vector2f[a.normals.length + b.normals.length];
		System.arraycopy(a.normals, 0, normals, 0, a.normals.length);
		System.arraycopy(b.normals, 0, normals, a.normals.length, b.normals.length);
		
		for (Vector2f n : normals) {
			lengthA = Math.abs(a.castAlong(n));
			lengthB = Math.abs(b.castAlong(n));
			dotDistance = Math.abs(Vector2f.dot(distance, n));
			depth = dotDistance - (lengthA + lengthB); 
			if (0 > depth) {
				depth = Math.abs(depth);
				if (depth < collision.collisionDepth) {
					collision.collisionDepth = depth;
					collision.normal = n;
				}
			} else {
				return null;
			}
		}
		
		// There was a collision
		return collision;
	}
}