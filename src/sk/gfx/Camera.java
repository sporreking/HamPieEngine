package sk.gfx;

import sk.game.Window;
import sk.util.vector.Matrix4f;

public class Camera extends Transform {
	
	private Matrix4f projection;
	
	@Override
	public Matrix4f getMatrix() {
		return (Matrix4f) super.getMatrix().invert();
	}
	
	public Matrix4f getProjection() {
		return projection;
	}
	
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