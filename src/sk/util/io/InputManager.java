package sk.util.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

import org.lwjgl.glfw.GLFW;

import sk.game.Game;
import sk.util.io.Keyboard.KeyState;

public class InputManager {
	
	static InputManager INSTANCE = new InputManager();
	
	static HashMap<String, KeyState> states = new HashMap<>();
	static HashMap<String, ArrayList<Input>> inputs = new HashMap<>();

	static class Input {
		boolean joystick;
		int joyID = 0;
		int button;
		
		public Input(String id, String key) {
			joystick = true;
			joyID = Integer.parseInt(id);
			button = Joystick.JoyData.ButtonName.valueOf(key.toUpperCase()).ordinal();
		}
		
		public Input(String key) {
			joystick = false;
			
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
			}

			if (key == "_") {
				key = " ";
			}
			
			
			button = key.charAt(0);
		}
	}
	
	public static KeyState get(String name) {
		return states.get(name);
	}
	
	public static boolean pressed(String name) {
		return states.get(name) == KeyState.PRESSED;
	}
	
	public static boolean down(String name) {
		KeyState state = states.get(name);
		return state == KeyState.DOWN || state == KeyState.PRESSED;
	}
	
	public static void update() {
		// Change pressed to released
		for (KeyState state : states.values()) {
			if (state == KeyState.PRESSED) {
				state = KeyState.DOWN;
			}
		}
		
		// Poll all updates
		KeyState state;
		for (String key : inputs.keySet()) {
			ArrayList<Input> bindings = inputs.get(key);
			state = KeyState.RELEASED;
			
			for (Input binding : bindings) {
				if (binding.joystick) {
					// Index out of bounds
					Joystick.JoyData d = Joystick.get(binding.joyID);
					if (d == null) continue;
					
					KeyState s = d.get(binding.button);
					if (s == KeyState.RELEASED) continue;
					state = s;
					
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
			
			if (tokens[1].equals("J")) {
				// It's a joystick
				inputs.get(key).add(new Input(tokens[2], tokens[3]));
			} else if (tokens[1].equals("K")) {
				// It's a Keyboard
				inputs.get(key).add(new Input(tokens[2]));
			}
			
		}	
	}
}
