
package sk.util.io;

import org.lwjgl.glfw.GLFWCursorPosCallback;

import sk.gfx.gui.GUIButton;
import sk.util.vector.Vector2f;

public class Mouse extends GLFWCursorPosCallback {

	static double x, y;
	
	@Override
	public void invoke(long window, double xPos, double yPos) {	
		x = xPos;
		y = yPos;
		MouseButton.changes = true;
	}
	
	/**
	 * Gets the current mouse position.
	 * @return Make a guess.
	 */
	public static Vector2f getMousePosition() {
		return new Vector2f((float) x, (float) y);
	}
	
	public static final GLFWCursorPosCallback INSTANCE = new Mouse();
}
