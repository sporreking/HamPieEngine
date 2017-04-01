package sk.physics;

import java.util.ArrayList;

import com.sun.xml.internal.bind.v2.model.util.ArrayInfoUtil;

import sk.entity.Component;
import sk.gfx.Transform;
import sk.gfx.Vertex2D;
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
	public Shape(Vertex2D... points) {
		this.points = new Vector2f[points.length];
		for (int i = 0; i < points.length; i++) {
			this.points[i] = new Vector2f(points[i].getData(0));
		}
		processPoints();
	}
	
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
			float currentLength = points[i].length();
			if (broadPhaseLength < currentLength) {
				broadPhaseLength = currentLength; 
			}
			
			// Calculated of 90 degree rotation matrix
			Vector2f normal = new Vector2f(-edges[i].y, edges[i].x);
			normal.normalise();
			normals[i] = normal;
		}
	}
	
	/**
	 * This value is used for broad phase checks.
	 * The check is a simple circular check,
	 * where the extents are the maximum width of
	 * the Shape in question.
	 * @return The largest distance from origo.
	 */
	public float getBP() {
		return broadPhaseLength;
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
	 * Casts the shape along the normal specified returning the minimi point
	 * @param normal The normal you want to cast along
	 * @return The longest distance along the normal
	 * of all the points
	 */
	public float castAlongMax(Vector2f normal) {
		float maxLength = 0;
		float angle = transform.rotation;
		Vector2f scale = transform.scale;
		Vector2f rotatedPoint = new Vector2f();
		for (Vector2f p : points) {
			rotatedPoint = p.clone();
			// Scale
			rotatedPoint.x *= scale.x;
			rotatedPoint.y *= scale.y;
			// Rotate
			rotatedPoint = Vector2f.rotate(rotatedPoint, (float) angle, null);
			// Cast
			maxLength = Math.max(maxLength, Vector2f.dot(rotatedPoint, normal));
		}
		return maxLength;
	}
	
	/**
	 * Casts the shape along the normal specified returning the minimum point
	 * @param normal The normal you want to cast along
	 * @return The longest distance along the normal
	 * of all the points
	 */
	public float castAlongMin(Vector2f normal) {
		float minLength = 0;
		float angle = transform.rotation;
		Vector2f scale = transform.scale;
		Vector2f rotatedPoint = new Vector2f();
		for (Vector2f p : points) {
			rotatedPoint = p.clone();
			// Scale
			rotatedPoint.x *= scale.x;
			rotatedPoint.y *= scale.y;
			// Rotate
			rotatedPoint = Vector2f.rotate(rotatedPoint, (float) angle, null);
			// Cast
			minLength = Math.min(minLength, Vector2f.dot(rotatedPoint, normal));
		}
		return minLength;
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
		
		float max;
		float min;
		float dotDistance = 0.0f;
		float depth = 0.0f;
		
		// Fuse the arrays into one
		Vector2f[] normals = new Vector2f[a.normals.length + b.normals.length];
		System.arraycopy(a.normals, 0, normals, 0, a.normals.length);
		System.arraycopy(b.normals, 0, normals, a.normals.length, b.normals.length);
		
		Vector2f n;
		final int split = a.normals.length;

		
		for (int i = 0; i < normals.length; i++) {
			n = normals[i];
			if (i < split) {
				n = Vector2f.rotate(n, a.transform.rotation, null);
				max = a.castAlongMax(n);
				min = -b.castAlongMin(n);	
			} else {
				n = Vector2f.rotate(n, b.transform.rotation, null);
				max = b.castAlongMax(n);
				min = -a.castAlongMin(n);				
			}
		
			
			dotDistance = Math.abs(Vector2f.dot(distance, n));
			depth = (max + min) - dotDistance;
			System.out.println(depth);
						
			if (0 < depth) {
				if (depth < collision.collisionDepth) {
					collision.collisionDepth = depth;
					collision.normal = n;
					if (i < split) {
						collision.normalOwner = a;
					} else {
						collision.normalOwner = b;
					}
				}
			} else {
				return null;
			}
		}
		
		// The normal should point from A to B
		// Find a way to write this without if-s and I will buy you
		// an ice-cream
		if (collision.normalOwner == a) {
			if (collision.normal.dot(distance) < 0.0f) {
				collision.normal.negate();
			}
		} else {
			if (collision.normal.dot(distance) > 0.0f) {
				collision.normal.negate();
			}
		}
		return collision;
	}
}