package sk.util.io;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.glfw.GLFWMouseButtonCallback;

import sk.gfx.gui.GUIButton;

public class MouseButton extends GLFWMouseButtonCallback {
	
	public static boolean changes = false;
	private static final HashMap<Integer, KeyState> states = new HashMap<>();

	@Override
	public void invoke(long window, int button, int action, int mods) {
		if(action == GLFW_PRESS) {
			states.put(button, KeyState.PRESSED);
		} else if(action == GLFW_RELEASE) {
			states.put(button, KeyState.RELEASED);
		}
		changes = true;
	}
	
	
	/**
	 * 
	 * Returns whether or not the key is currently down.
	 * This will even return true when the key was pressed, however not when it was released.
	 * 
	 * @param key the key to check on.
	 * @return true if the key is currently down.
	 */
	public static final boolean down(int key) {
		if(states.containsKey(key))
			if(states.get(key) != KeyState.RELEASED)
				return true;
		
		return false;
	}
	
	/**
	 * 
	 * Should not be called by the user.
	 * Changes pressed keys to the down state, and removes released keys.
	 * 
	 */
	public static final void _update() {
		ArrayList<Integer> trash = new ArrayList<>();
		
		for(int key : states.keySet()) {
			if(states.get(key) == KeyState.PRESSED)
				states.put(key, KeyState.DOWN);
			else if(states.get(key) == KeyState.RELEASED)
				trash.add(key);
		}
		
		for(int key : trash)
			states.remove(key);
		
		trash.clear();
		changes = false;
	}
	
	/**
	 * Returns true if the specified key is pressed.
	 * @param key The key to check.
	 * @return If the key is pressed.
	 */
	public static final boolean pressed(int key) {
		if(states.containsKey(key))
			return states.get(key) == KeyState.PRESSED;
		
		return false;
	}
	
	/**
	 * Returns true if the specified key is released.
	 * @param key The key to check.
	 * @return If the key is pressed.
	 */
	public static final boolean released(int key) {
		if(states.containsKey(key))
			if(states.get(key) == KeyState.RELEASED)
				return true;
		
		return false;
	}
	
	private enum KeyState {
		DOWN, PRESSED, RELEASED;
	}
	
	public static final GLFWMouseButtonCallback INSTANCE = new MouseButton();
}
