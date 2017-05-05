package sk.gfx;

import sk.game.Window;
import sk.util.vector.Matrix4f;
import sk.util.vector.Vector2f;

public class Camera extends Transform {
	
	private Matrix4f projection;
	
	/**
	 * 
	 * Returns a matrix representation of this camera's transform.
	 * 
	 */
	@Override
	public Matrix4f getMatrix() {
		return (Matrix4f) super.getMatrix().invert();
	}
	
	/**
	 * 
	 * Returns the projection of this camera.
	 * 
	 * @return the projection of this camera.
	 */
	public Matrix4f getProjection() {
		return projection;
	}
	
	/**
	 * 
	 * Initializes this camera with a orthographic projection.
	 * 
	 * @param left the left-most coordinate of the projection.
	 * @param right the right-most coordinate of the projection.
	 * @param top the top-most coordinate of the projection.
	 * @param bottom the bottom-most coordinate of the projection.
	 * @return
	 */
	public Camera createOrtho(float left, float right, float top, float bottom) {
		projection = (Matrix4f) new Matrix4f().setIdentity();
		projection.m00 = 2f / (right - left);
		projection.m30 = -((right + left) / (right - left));
		projection.m11 = 2f / (top - bottom);
		projection.m31 = -((top + bottom) / (top - bottom));
		
		return this;
	}
	
	/**
	 * Projects the specified coordinates to model space
	 * @param x The x-component of the vector that we want to convert.
	 * @param y The y-component of the vector that we want to convert. 
	 * @return A new converted vector.
	 */
	public Vector2f projectCoordinates(float x, float y) {
		return projectCoordinates(new Vector2f(x, y));
	}
	
	/**
	 * Projects the specified coordinates to model space
	 * @param a The vector that we want to convert. 
	 * @return A new converted vector.
	 */
	public Vector2f projectCoordinates(Vector2f a) {
		Vector2f v = new Vector2f(a);
		v.translate(-0.5f, -0.5f);
		v.scale(2.0f);
		//Flip the Y-axis to make it match OpenGL.
		v.y *= -1;

		//Project it.
		//Manual multiplication, which assumes it's an orthographic matrix generated by the function "createOrtho"
		v.x = v.x * projection.m00 + projection.m30;
		v.y = v.y * projection.m11 + projection.m31;
		return v;
	}
	
	/**
	 * 
	 * Refreshes the view matrix of the camera when the screen is resized.
	 * 
	 * @return
	 */
	public Camera updateViewMatrix() {
		float ratio = Window.getAspectRatio();
		
		this.createOrtho(-ratio, ratio, 1, -1);
		
		return this;
	}
	
	public static final Camera DEFAULT;
	public static final Camera GUI;
	
	static {
		DEFAULT = new Camera();
		DEFAULT.updateViewMatrix();
		GUI = new Camera().createOrtho(-1, 1, 1, -1);
	}
}