package sk.game;

/*
 * TODO: Add toString() methods to each class
 */

import sk.gamestate.GameState;
import sk.util.vector.Vector4f;

/**
 * 
 * This class is used to supply properties to the engine upon game start.
 * 
 * @author Alfred Sporre
 *
 */
public class GameProperties {

	// Window settings
	public String title = new String();
	public int width = 800;
	public int height = 600;
	public boolean resizable = false;
	public boolean vSync = true;
	public String icon = new String();
	public Vector4f clearColor = new Vector4f(0, 0, 0, 1);
	public boolean fullscreen = false;
	public boolean useDisplayResolution = true;
	public int display = 44;
	public boolean recalculateViewMatrix = true;
	// Initialization settings
	public GameState startState;
}