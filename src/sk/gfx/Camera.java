package sk.gfx;

import sk.game.Window;
import sk.util.vector.Matrix4f;

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
	
	public static final Camera DEFAULT;
	
	static {
		DEFAULT = new Camera().createOrtho(-Window.getAspectRatio(),
				Window.getAspectRatio(), 1, -1);
	}
}