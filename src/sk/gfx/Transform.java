package sk.gfx;

import sk.util.vector.Matrix4f;
import sk.util.vector.Vector2f;

public class Transform {
	
	public Vector2f position = new Vector2f();
	public Vector2f scale = new Vector2f(1, 1);
	public float rotation = 0;
	
	public Matrix4f getMatrix() {
		Matrix4f translation = (Matrix4f) new Matrix4f().setIdentity();
		translation.m30 = position.getX();
		translation.m31 = position.getY();
		
		Matrix4f rot = (Matrix4f) new Matrix4f().setIdentity();
		rot.m00 = (float) Math.cos(rotation);
		rot.m10 = (float)-Math.sin(rotation);
		rot.m01 = (float) Math.sin(rotation);
		rot.m11 = (float) Math.cos(rotation);
		
		Matrix4f scale = (Matrix4f) new Matrix4f().setIdentity();
		scale.m00 = this.scale.x;
		scale.m11 = this.scale.y;
		
		return Matrix4f.mul(Matrix4f.mul(rot, translation, null), scale, null);
	}
}