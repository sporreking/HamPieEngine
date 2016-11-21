package sk.entity.component;
import sk.entity.Component;
import sk.entity.Entity;
import sk.gfx.Renderer;
import sk.gfx.Transform;
import sk.util.vector.Vector2f;

public class AABB extends Component {
	
	boolean lazyInit;
	boolean useParent;
	
	float halfWidth, halfHeight;
	Vector2f min, max;
	
	Transform transform;
	
	/**
	 * Initalize a new AABB (Axis Aligned Bounding Box) with the values from 
	 * any attached renderer. This will assume it is a quad.
	 */
	public AABB() {
		lazyInit = true;
		useParent = true;
	}
	
	/**
	 * Initalize a new AABB (Axis Aligned Bounding Box) with the renderer components transform 
	 * with a custom width and heigh.
	 * @param width The width of the AABB
	 * @param height The height of the AABB
	 */
	public AABB(float width, float height) {
		lazyInit = false;
		useParent = true;
		
		setWidth(width);
		setHeight(height);
	}
	
	/**
	 * Initalizes a new AABB (Axis Aligned Bounding Box) with the supplied information.
	 * @param width The width of the box.
	 * @param height The height of the box
	 * @param transform The transform of the box.
	 */
	public AABB(float width, float height, Transform transform) {
		lazyInit = false;
		useParent = false;
		
		setWidth(width);
		setHeight(height);
		
		this.transform = transform;
	}
	
	/**
	 * Sets the width of this bounding box.
	 * @param width The new desired width.
	 */
	public void setWidth(float width) {
		halfWidth = (float) (width / 2.0);
	}

	/**
	 * Sets the height of this bounding box.
	 * @param height The new desired height .
	 */
	public void setHeight(float height) {
		halfHeight = (float) (height / 2.0);
	}
	
	/**
	 * Self descriptive.
	 * @return
	 */
	public float getWidth() {
		return halfWidth * 2;
	}
	
	/**
	 * Self descriptive.
	 * @return
	 */
	public float getHeight() {
		return halfHeight * 2;
	}
	
	/**
	 * Initalizes this component, with things that need to be done
	 */
	public void init() {
		if (lazyInit) {
			Renderer r = getParent().get(Renderer.class);
			if (r == null) {
				throw new IllegalArgumentException("You cannot initalize an AABB component "
						+ "without putting it in an entity which ALLREADY has a renderer, "
						+ "or by manually specifying the values");
			}
			Transform t = r.transform;
			setWidth(t.scale.x);
			setWidth(t.scale.y);
			if (!useParent) {
				transform = t;
			}
		}
	}
	
	/**
	 * Updates the boundaries of this bounding box with respect to the transform.
	 */
	private void calculateMinMax() {
		if (useParent) {
			//If we're using the parents transform, use that.
			transform = getParent().get(Renderer.class).transform;
		}
		
		min.x = -halfWidth * transform.scale.x + transform.position.x;
		max.x =  halfWidth * transform.scale.x + transform.position.x;
		
		min.y = -halfHeight * transform.scale.y + transform.position.y;
		max.y =  halfHeight * transform.scale.y + transform.position.y;
	}
	
	/**
	 * Checks if the specified point lies within this bounding box.
	 * @param point The point to check
	 * @return 
	 */
	public boolean check(Vector2f point) {
		//Make sure the boundaries are up to date
		calculateMinMax();
		
		//This is just an AABB test for a point, without if statements,
		//gotta save those frames! (And kill the animals)
		return 
			(min.x < point.x && point.x < max.x) &&
			(min.y < point.y && point.y < max.y);
	}
	
	/**
	 * Checks weather the entities AABB collides with this AABB.
	 * @param entity The entity we wish to check against.
	 * @return
	 */
	public boolean check(Entity entity) {
		AABB aabb = entity.get(AABB.class);
		if (aabb != null) {
			return check(aabb);
		} else {
			throw new IllegalArgumentException("The entity supplied does not have an AABB.");
		}
	}
	
	/**
	 * Checks weather the other AABB collides with this AABB.
	 * @param aabb The other AABB
	 * @return
	 */
	public boolean check(AABB aabb) {		
		//Make sure the boundaries are up to date
		calculateMinMax();
		aabb.calculateMinMax();

		//This is just an AABB test for a point, without if statements,
		//gotta save those frames! (And kill the animals)	
		return 
			((min.x < aabb.min.x && aabb.min.x < max.x) || 
			 (min.x < aabb.max.x && aabb.max.x < max.x)) &&
			((min.y < aabb.min.y && aabb.min.y < max.y) || 
			 (min.y < aabb.max.y && aabb.max.y < max.y));
	}
}
