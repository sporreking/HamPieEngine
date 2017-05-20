package sk.util.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.lwjgl.glfw.GLFW;

import sk.game.Game;
import sk.util.io.Keyboard.KeyState;

/**
 *
 * This is the general way to handle input in the engine.
 *
 * There are some things that are missing, like rebindable keys
 * from inside the game. 
 *
 * But this does offer a way to interface with controllers and 
 * keyboards without caring which is which.
 *
 * @author Ed
 *
 */
public class InputManager {
	
	static HashMap<String, KeyState> states = new HashMap<>();
	static HashMap<String, ArrayList<Input>> inputs = new HashMap<>();

	/**
	 * 
	 * A data object that holds information related to input...
	 *
	 */
	static class Input {
		// If it is a joystick
		boolean joystick;
		// The ID of the Joystick
		int joyID = 0;
		// Which button it is
		int button;
		
		/**
		 *
		 * Creates a new input object that looks for 
		 * joystick events.
		 *
		 * @param id the id of the controller.
		 * @param key the key/button that activates this input.
		 */
		public Input(String id, String key) {
			joystick = true;
			joyID = Integer.parseInt(id);
			button = Joystick.JoyData.ButtonName.valueOf(key.toUpperCase()).ordinal();
		}
		
		/**
		 *
		 * Creates a new input object that looks for
		 * keyboard events.
		 *
		 * @param key the key we should look for.
		 */
		public Input(String key) {
			joystick = false;
			
			// GLFW uses the keycodes, but the arrow keys doesn't have them, so here we go.
			if (key.toUpperCase().equals("LEFT")) {
				button = GLFW.GLFW_KEY_LEFT;
				return;
			} else if (key.toUpperCase().equals("RIGHT")) {
				button = GLFW.GLFW_KEY_RIGHT;
				return;
			} else if (key.toUpperCase().equals("UP")) {
				button = GLFW.GLFW_KEY_UP;
				return;
			} else if (key.toUpperCase().equals("DOWN")) {
				button = GLFW.GLFW_KEY_DOWN;
				return;
			} else if (key.toUpperCase().equals("ESCAPE")) {
				button = GLFW.GLFW_KEY_ESCAPE;
				return;
			} else if (key.toUpperCase().equals("ENTER")) {
				button = GLFW.GLFW_KEY_ENTER;
				return;
			}

			if (key == "_") {
				key = " ";
			}
			
			
			button = key.charAt(0);
		}
	}
	
	/**
	 *
	 * The state of the input.
	 *
	 * @param name the name of the input.
	 */
	public static KeyState get(String name) {
		return states.get(name);
	}

	/**
	 * 
	 * If the input was pressed this frame.
	 *
	 * @param name the name of the input.
	 */
	public static boolean pressed(String name) {
		return states.get(name) == KeyState.PRESSED;
	}
	
	/**
	 * 
	 * If the input is being held down. 
	 *
	 * @param name the name of the input.
	 */
	public static boolean down(String name) {
		KeyState state = states.get(name);
		return state == KeyState.DOWN || state == KeyState.PRESSED;
	}
	
	/**
	 * 
	 * Polls the events from the window manager.
	 *
	 * This should not be called by you.
	 */
	public static void update() {
		// Change pressed to released
		for (KeyState state : states.values()) {
			if (state == KeyState.PRESSED) {
				state = KeyState.DOWN;
			}
		}
		
		// Poll all updates
		KeyState lastState;
		KeyState state;
		for (String key : inputs.keySet()) {
			ArrayList<Input> bindings = inputs.get(key);
			lastState = states.get(key);
			state = KeyState.RELEASED;
			
			for (Input binding : bindings) {
				if (binding.joystick) {
					// Index out of bounds
					Joystick.JoyData d = Joystick.get(binding.joyID);
					if (d == null) continue;
					
					KeyState s = d.get(binding.button);
					
					if (s == KeyState.RELEASED) continue;					
					if (s == KeyState.DOWN && lastState == KeyState.RELEASED) {
						state = KeyState.PRESSED;
						break;
					} else {
						state = s;
					}
				} else {
					if (Keyboard.pressed(binding.button)) {
						state = KeyState.PRESSED;
					} else if (Keyboard.down(binding.button)) {
						state = KeyState.DOWN;
					}
				}
				if (state == KeyState.DOWN) {
					break;
				}
			}
			states.put(key, state);
		}
	}
	
	/**
	 *
	 * Initalizes the inputManager, this should
	 * not be called by you.
	 *
	 */
	public static void init() {
		File file = new File(Game.properties.inputMapPath);
		if (!file.exists()) {
			return;
		}
		
		Scanner stream = null;
		try {
			stream = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// NAME TYPE (K/J) [id] key
		// # is comment
		
		// Parse the file
		while (stream.hasNextLine()) {
			String line = stream.nextLine();
			String[] tokens = line.split(" ");
			
			// Invalid line
			if (tokens.length < 3 || tokens.length > 4) continue;
			
			String key = tokens[0];
			
			if (key == "#") continue;
			
			if (!states.containsKey(key)) {
				states.put(key, KeyState.RELEASED);
				inputs.put(key, new ArrayList<>());
			}
			
			if (tokens[1].charAt(0) == 'J') {
				// It's a joystick
				inputs.get(key).add(new Input(tokens[2], tokens[3]));
			} else if (tokens[1].charAt(0) == 'K') {
				// It's a Keyboard
				inputs.get(key).add(new Input(tokens[2]));
			}
		}	
	}
}
