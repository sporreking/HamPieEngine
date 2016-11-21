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
	 * Initalize a new AABB with the renderer components transform 
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
	
	public AABB(float width, float height, Transform transform) {
		lazyInit = false;
		useParent = false;
		
		setWidth(width);
		setHeight(height);
		
		this.transform = transform;
	}
	
	public void setWidth(float width) {
		halfWidth = (float) (width / 2.0);
		min.x = -halfWidth;
		max.x = halfWidth;
	}

	public void setHeight(float height) {
		halfHeight = (float) (height / 2.0);
		min.y = -halfHeight;
		max.y = halfHeight;
	}
	
	public float getWidth() {
		return halfWidth * 2;
	}
	
	public float getHeight() {
		return halfHeight * 2;
	}
	
	public void init() {
		if (lazyInit) {
			Transform t = getParent().get(Renderer.class).transform;
			setWidth(t.scale.x);
			setWidth(t.scale.y);
			if (!useParent) {
				transform = t;
			}
		}
	}
	
	public boolean check(Vector2f point) {
		//TODO: Implement this function!
		return false;
	}
	
	public boolean check(Entity entity) {
		AABB aabb = entity.get(AABB.class);
		if (aabb != null) {
			return check(aabb);
		} else {			
			return false;
		}
	}
	
	public boolean check(AABB aabb) {
		//TODO: Make this function
		return false;
	}
}
