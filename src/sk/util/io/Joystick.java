package sk.util.io;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWJoystickCallback;

import sk.util.io.Keyboard.KeyState;
import sk.util.vector.Vector2f;

/**
 * 
 * Handles the connection and disconnections of joysticks (controllers).
 * 
 * Note: The home button doesn't work on windows.
 * 
 * @author ed
 *
 */
public class Joystick extends GLFWJoystickCallback {

	public static final float DEAD_ZONE = 0.05f;
	// The threshold when changeing analog to digita.
	public static final float DIGITAL_THRESHOLD = 0.5f;
	
	// Holds the data of a controller
	public static class JoyData {
		private enum JoyType {
			NONE,
			PS4,
			PS3,
			XBOX,
			XBOX_ONE,
		}
		
		public enum ButtonName {
			X,
			Y,
			A,
			B,
			START,
			SELECT,
			HOME,
			RIGHT_BUMPER,
			LEFT_BUMPER,
			LEFT,
			RIGHT,
			LEFTSTICK_UP,
			LEFTSTICK_DOWN,
			LEFTSTICK_LEFT,
			LEFTSTICK_RIGHT,
			RIGHTSTICK_UP,
			RIGHTSTICK_DOWN,
			RIGHTSTICK_LEFT,
			RIGHTSTICK_RIGHT,
			DPAD_UP,
			DPAD_DOWN,
			DPAD_LEFT,
			DPAD_RIGHT,
			LEFT_TRIGGER,
			RIGHT_TRIGGER,
		}
		
		public JoyType type = JoyType.NONE;

		// Face buttons
		public KeyState x, y, a, b = KeyState.RELEASED;

		// Special buttons
		public KeyState start, select, home  = KeyState.RELEASED;
		
		// "Sticks"
		public Vector2f dpad = new Vector2f();
		public Vector2f leftStick = new Vector2f();
		public Vector2f rightStick = new Vector2f();

		// Shoulder buttons (Bumpers)
		public KeyState rightBumper, leftBumper = KeyState.RELEASED;
		
		// Triggers
		public float rightTrigger, leftTrigger = 0;
		
		// Stick buttons
		public KeyState left, right = KeyState.RELEASED;
		
		public void set(ButtonName name, KeyState state) {
			set(name.ordinal(), state);
		}
		
		/**
		 *
		 * Sets the state of a specified button.
		 *
		 * @param name the identifyer for the specific button.
		 * @param state the state we want to change to.
		 */
		public void set(int name, KeyState state) {
			switch (name) {
			case 0:
				x = state;
				return;
			case 1:
				y = state;
				return;
			case 2:
				a = state;
				return;
			case 3:
				b = state;
				return;
			case 4:
				start = state;
				return;
			case 5:
				select = state;
				return;
			case 6:
				home = state;
				return;
			case 7:
				rightBumper = state;
				return;
			case 8:
				leftBumper = state;
				return;
			case 9:
				left = state;
				return;
			case 10:
				right = state;
				return;
			}
			
		}
		
		/**
		 *
		 * Returns the keystate for the specified button.
		 *
		 * @param name the identifyer for the specific button.
		 *
		 * @return the state of the specified button.
		 */
		public KeyState get(ButtonName name) {
			return get(name.ordinal());
		}
		
		/**
		 *
		 * Gets the keystate for the specified button.
		 *
		 * @param name the identifyer for the specified button.
		 *
		 * @return the state of the specified button.
		 */
		public KeyState get(int name) {
			switch (name) {
			case 0:
				return x;
			case 1:
				return y;
			case 2:
				return a;
			case 3:
				return b;
			case 4:
				return start;
			case 5:
				return select;
			case 6:
				return home;
			case 7:
				return rightBumper;
			case 8:
				return leftBumper;
			case 9:
				return left;
			case 10:
				return right;
			case 11:
				return leftStick.y > DIGITAL_THRESHOLD ? KeyState.DOWN : KeyState.RELEASED;
			case 12:
				return leftStick.y < -DIGITAL_THRESHOLD ? KeyState.DOWN : KeyState.RELEASED;
			case 13:
				return leftStick.x < -DIGITAL_THRESHOLD ? KeyState.DOWN : KeyState.RELEASED;
			case 14:
				return leftStick.x > DIGITAL_THRESHOLD ? KeyState.DOWN : KeyState.RELEASED;
			case 15:
				return rightStick.y < -DIGITAL_THRESHOLD ? KeyState.DOWN : KeyState.RELEASED;
			case 16:
				return rightStick.y > DIGITAL_THRESHOLD ? KeyState.DOWN : KeyState.RELEASED;
			case 17:
				return rightStick.x < -DIGITAL_THRESHOLD ? KeyState.DOWN : KeyState.RELEASED;
			case 18:
				return rightStick.x > DIGITAL_THRESHOLD ? KeyState.DOWN : KeyState.RELEASED;
			case 19:
				return dpad.y < -DIGITAL_THRESHOLD ? KeyState.DOWN : KeyState.RELEASED;
			case 20:
				return dpad.y > DIGITAL_THRESHOLD ? KeyState.DOWN : KeyState.RELEASED;
			case 21:
				return dpad.x < -DIGITAL_THRESHOLD ? KeyState.DOWN : KeyState.RELEASED;
			case 22:
				return dpad.x > DIGITAL_THRESHOLD ? KeyState.DOWN : KeyState.RELEASED;
			case 23:
				return leftTrigger > DIGITAL_THRESHOLD ? KeyState.DOWN : KeyState.RELEASED;
			case 24:
				return rightTrigger > DIGITAL_THRESHOLD ? KeyState.DOWN : KeyState.RELEASED;		
			}			
			return null;
		}
		
		/**
		 *
		 * A way to serialize the controller input,
		 * mainly used for debugging since there is
		 * a lot of information in it which can take a 
		 * while to print.
		 *
		 */
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append("Type: ");
			b.append(type.name());
			b.append("\n");
			
			b.append("D-pad: ");
			b.append(dpad.x);
			b.append(", ");
			b.append(dpad.y);
			b.append("\n");

			b.append("Left stick: ");
			b.append(leftStick.x);
			b.append(", ");
			b.append(leftStick.y);
			b.append("\n");
				
			b.append("Right stick: ");
			b.append(rightStick.x);
			b.append(", ");
			b.append(rightStick.y);
			b.append("\n");
			
			b.append("Left trigger: ");
			b.append(leftTrigger);
			b.append(", Right trigger: ");
			b.append(rightTrigger);
			b.append("\n");

			b.append("Face buttons: \n");
			b.append("    ");
			b.append(y);
			b.append("\n");
			b.append(x);
			b.append(" ");
			b.append(this.b);
			b.append("\n    ");
			b.append(a);
			b.append("\n");
			
			b.append("Bumpers: ");
			b.append(leftBumper != KeyState.RELEASED);
			b.append(", ");
			b.append(rightBumper != KeyState.RELEASED);
			b.append("\n");

			b.append("Sticks: ");
			b.append(left != KeyState.RELEASED);
			b.append(", ");
			b.append(right != KeyState.RELEASED);
			b.append("\n");

			b.append("Start: ");
			b.append(start  != KeyState.RELEASED);
			b.append(", Select: ");
			b.append(select != KeyState.RELEASED);
			b.append(", Home: ");
			b.append(home != KeyState.RELEASED);
			b.append("\n");

			return b.toString();
		}
	}
	
	// A list of all connected controllers
	private static final HashMap<Integer, JoyData> joysticks;
	
	
	@Override
	public void invoke(int joy, int event) {
		if (event == GLFW.GLFW_CONNECTED) {
			add(joy);
		} else if (event == GLFW.GLFW_DISCONNECTED) {
			remove(joy);
		}
	}
	
	/**
	 *
	 * Returns the joydata related to the specified joystick.
	 *
	 * @param joy the id of the joystick.
	 *
	 */
	public static JoyData get(int joy) {
		int i = 0;
		for (int k : joysticks.keySet()) {
			if (i == joy) {
				return joysticks.get(k);
			}
			i++;
		}
		return null;
	}
	
	/**
	 * 
	 * Adds the joystick and adds a keymap to it.
	 * 
	 * @param joy the joystick id
	 */
	private static void add(int joy) {
		JoyData c = new JoyData();
		
		String name = GLFW.glfwGetJoystickName(joy); 
		System.out.println(name);
		if (name.contains("Xbox")) {
			// It's an Xbox!
			if (name.contains("One")) {
				// #Xbone
				c.type = JoyData.JoyType.XBOX_ONE;
			} else {
				// THIS IS NOT SUPPORTED YET!
				c.type = JoyData.JoyType.XBOX;
			}
		} else if (name.contains("Sony") || name.contains("Wireless")) {
			if (name.contains("3")) {
				// PS3
				c.type = JoyData.JoyType.PS3;
			} else {
				// PS4
				c.type = JoyData.JoyType.PS4;
			}
		} else {
			/*
			System.out.println(GLFW.glfwGetJoystickName(joy));
			System.out.println("Unkown controller connected!");
			 */
			return;
		}
		
		joysticks.put(joy, c);
	}
	
	/**
	 * 
	 * Cleans up after the joystick
	 * 
	 * @param joy the joystick id
 	 */
	private static void remove(int joy) {
		for (int c : joysticks.keySet()) {
			if (c == joy) {
				joysticks.remove(c);
				break;
			}
		}
	}
	
	/**
	 * 
	 * This checks which controllers are connected, since
	 * we don't know this when we start without checking.
	 * 
	 * Controllers that were connected before the start 
	 * apparently don't generate a connection event, 
	 * don't know why.
	 * 
	 * This is called in the window class, you don't need
	 * to care about this method.
	 * 
	 */
	public static final void init() {
		for (int i = GLFW.GLFW_JOYSTICK_1; i < GLFW.GLFW_JOYSTICK_LAST; i++) {
			if (isConnected(i)) {
				add(i);
			}
		}
	}
	
	/**
	 *
	 * Updates all the keystates.
	 *
	 */
	public static final void _update() {
		for (JoyData data : joysticks.values()) {
			for (int i = 0; i < JoyData.ButtonName.values().length; i++) {
				KeyState state = data.get(i);
				if (state == KeyState.PRESSED) {
					data.set(i, KeyState.DOWN);
				}
			}
		}
	}
	
	/**
	 *
	 * Joystick data has to be polled manually, that is what this method does.
	 *
	 */
	public static final void pollEvents() {
		// Update each connected controller
		int joy;
		JoyData data;
		try {
			for (int i : joysticks.keySet()) {
				joy = i;
				data = joysticks.get(i);
				FloatBuffer axis = GLFW.glfwGetJoystickAxes(joy);
				ByteBuffer buttons = GLFW.glfwGetJoystickButtons(joy);
				
				switch (data.type) {
				case PS4:
					handlePS4(data, axis, buttons);
					break;
				case PS3:
					handlePS3(data, axis, buttons);
					break;
				case XBOX_ONE:
					handleXboxOne(data, axis, buttons);
					break;
				case XBOX:
					handleXbox(data, axis, buttons);
				default:
					break;
				}
				
				//System.out.println(data);
			}
		} catch (Exception e) {/* SILENTLY IGNORE */}
	}

	/**
	 * 
	 * Handles the controller and formats the data into something more generic.
	 * 
	 * @param data the data that is relevant to this controller.
	 * @param axis the buffer of axis.
	 * @param buttons the buffer of buttons.
	 */
	private static final void handlePS4(JoyData data, FloatBuffer axis, ByteBuffer buttons) {
		
		data.dpad.scale(0);
		
		byte b;
		int i = 0;
		try {
			while (buttons.hasRemaining()) {
				b = buttons.get();
				
				switch(i) {
				case 0:
					if (b == 1 && data.x == KeyState.RELEASED) {
						data.x = KeyState.PRESSED;
					} else if (b != 1) {
						data.x = KeyState.RELEASED;
					}
					break;
				case 1:
					if (b == 1 && data.a == KeyState.RELEASED) {
						data.a = KeyState.PRESSED;
					} else if (b != 1) {
						data.a = KeyState.RELEASED;
					}
					break;
				case 2:
					if (b == 1 && data.b == KeyState.RELEASED) {
						data.b = KeyState.PRESSED;
					} else if (b != 1) {
						data.b = KeyState.RELEASED;
					}
					break;
				case 3:
					if (b == 1 && data.y == KeyState.RELEASED) {
						data.y = KeyState.PRESSED;
					} else if (b != 1) {
						data.y = KeyState.RELEASED;
					}
					break;
				case 5:
					if (b == 1 && data.rightBumper == KeyState.RELEASED) {
						data.rightBumper = KeyState.PRESSED;
					} else if (b != 1) {
						data.rightBumper = KeyState.RELEASED;
					}
					break;
				case 4:
					if (b == 1 && data.leftBumper == KeyState.RELEASED) {
						data.leftBumper = KeyState.PRESSED;
					} else if (b != 1) {
						data.leftBumper = KeyState.RELEASED;
					}
					break;
				case 8:
					if (b == 1 && data.select == KeyState.RELEASED) {
						data.select = KeyState.PRESSED;
					} else if (b != 1) {
						data.select = KeyState.RELEASED;
					}
					break;
				case 9:
					if (b == 1 && data.start == KeyState.RELEASED) {
						data.start = KeyState.PRESSED;
					} else if (b != 1) {
						data.start = KeyState.RELEASED;
					}
					break;
				case 10:
					if (b == 1 && data.left == KeyState.RELEASED) {
						data.left = KeyState.PRESSED;
					} else if (b != 1) {
						data.left = KeyState.RELEASED;
					}
					break;
				case 11:
					if (b == 1 && data.right == KeyState.RELEASED) {
						data.right = KeyState.PRESSED;
					} else if (b != 1) {
						data.right = KeyState.RELEASED;
					}
					break;
				case 12:
					if (b == 1 && data.home == KeyState.RELEASED) {
						data.home = KeyState.PRESSED;
					} else if (b != 1) {
						data.home = KeyState.RELEASED;
					}
					break;
					/* Windows stuff, IDK */
				case 14:
					if (b == 1)
						data.dpad.y += 1;
					break;
				case 15:
					if (b == 1)
						data.dpad.x += 1;
					break;
				case 16:
					if (b == 1)
						data.dpad.y -= 1;
					break;
				case 17:
					if (b == 1)
						data.dpad.x -= 1;
					break;
				}
				i++;
			}
		} catch (Exception e) {/* SILENTLY IGNORE */}
		
		/* AXIS */
		float a;
		i = 0;
		try {
			while (axis.hasRemaining()) {
				a = axis.get();

				
				switch(i) {
				case 0:
					data.leftStick.x = Math.abs(a) < DEAD_ZONE ? 0 : a;
					break;
				case 1:
					data.leftStick.y = Math.abs(a) < DEAD_ZONE ? 0 : -a;
					break;
				case 2:
					data.rightStick.x = Math.abs(a) < DEAD_ZONE ? 0 : a;
					break;
				case 5:
					data.rightStick.y = Math.abs(a) < DEAD_ZONE ? 0 : -a;
					break;
				case 6:
					data.dpad.x = Math.abs(a) < DEAD_ZONE ? 0 : Math.signum(a);
					break;
				case 7:
					data.dpad.y = Math.abs(a) < DEAD_ZONE ? 0 : -Math.signum(a);
					break;
				case 3:
					data.leftTrigger = (float) (Math.abs(a) < DEAD_ZONE ? 0 : a * 0.5f + 0.5);
					break;
				case 4:
					data.rightTrigger = (float) (Math.abs(a) < DEAD_ZONE ? 0 : a * 0.5f + 0.5);
					break;
				}
				i++;
			}
		} catch (Exception e) {/* SILENTLY IGNORE */}
		
		
	}
	
	/**
	 * 
	 * Handles the controller and formats the data into something more generic.
	 * 
	 * @param data the data that is relevant to this controller.
	 * @param axis the buffer of axis.
	 * @param buttons the buffer of buttons.
	 */
	private static final void handlePS3(JoyData data, FloatBuffer axis, ByteBuffer buttons) {
		/* AXIS */
		float a;
		int i = 0;
		try {
			while (axis.hasRemaining()) {
				a = axis.get();
				
				switch(i) {
				case 0:
					data.leftStick.x = Math.abs(a) < DEAD_ZONE ? 0 : a;
					break;
				case 1:
					data.leftStick.y = Math.abs(a) < DEAD_ZONE ? 0 : -a;
					break;
				case 2:
					data.rightStick.x = Math.abs(a) < DEAD_ZONE ? 0 : a;
					break;
				case 3:
					data.rightStick.y = Math.abs(a) < DEAD_ZONE ? 0 : -a;
					break;
				case 8:
					data.dpad.x = Math.abs(a) < DEAD_ZONE ? 0 : Math.signum(a);
					break;
				case 9:
					data.dpad.y = Math.abs(a) < DEAD_ZONE ? 0 : -Math.signum(a);
					break;
				case 10:
					data.dpad.y = Math.abs(a) < DEAD_ZONE ? 0 : -Math.signum(a);
					break;
				case 12:
					data.leftTrigger = (float) (Math.abs(a) < DEAD_ZONE ? 0 : a * 0.5f + 0.5);
					break;
				case 13:
					data.rightTrigger = (float) (Math.abs(a) < DEAD_ZONE ? 0 : a * 0.5f + 0.5);
					break;
				}
				i++;
			}
		} catch (Exception e) {/* SILENTLY IGNORE */}
		
		// Reset that d-pad
		data.dpad.scale(0);
		
		byte b;
		i = 0;
		try {
			while (buttons.hasRemaining()) {
				b = buttons.get();
				
				switch(i) {
				case 12:
					if (b == 1 && data.y == KeyState.RELEASED) {
						data.y = KeyState.PRESSED;
					} else if (b != 1) {
						data.y = KeyState.RELEASED;
					}
					break;
				case 13:
					if (b == 1 && data.b == KeyState.RELEASED) {
						data.b = KeyState.PRESSED;
					} else if (b != 1) {
						data.b = KeyState.RELEASED;
					}
					break;
				case 14:
					if (b == 1 && data.a == KeyState.RELEASED) {
						data.a = KeyState.PRESSED;
					} else if (b != 1) {
						data.a = KeyState.RELEASED;
					}
					break;
				case 15:
					if (b == 1 && data.x == KeyState.RELEASED) {
						data.x = KeyState.PRESSED;
					} else if (b != 1) {
						data.x = KeyState.RELEASED;
					}
					break;
				case 10:
					if (b == 1 && data.leftBumper == KeyState.RELEASED) {
						data.leftBumper = KeyState.PRESSED;
					} else if (b != 1) {
						data.leftBumper = KeyState.RELEASED;
					}
					break;
				case 11:
					if (b == 1 && data.rightBumper == KeyState.RELEASED) {
						data.rightBumper = KeyState.PRESSED;
					} else if (b != 1) {
						data.rightBumper = KeyState.RELEASED;
					}
					break;
				case 0:
					if (b == 1 && data.select == KeyState.RELEASED) {
						data.select = KeyState.PRESSED;
					} else if (b != 1) {
						data.select = KeyState.RELEASED;
					}
					break;
				case 3:
					if (b == 1 && data.start == KeyState.RELEASED) {
						data.start = KeyState.PRESSED;
					} else if (b != 1) {
						data.start = KeyState.RELEASED;
					}
					break;
				case 1:
					if (b == 1 && data.left == KeyState.RELEASED) {
						data.left = KeyState.PRESSED;
					} else if (b != 1) {
						data.left = KeyState.RELEASED;
					}
					break;
				case 2:
					if (b == 1 && data.right == KeyState.RELEASED) {
						data.right = KeyState.PRESSED;
					} else if (b != 1) {
						data.right = KeyState.RELEASED;
					}
					break;
				case 16:
					if (b == 1 && data.home == KeyState.RELEASED) {
						data.home = KeyState.PRESSED;
					} else if (b != 1) {
						data.home = KeyState.RELEASED;
					}
					break;
				// D-pad
				case 4:
					data.dpad.y += b != 0 ? 1 : 0;
					break;
				case 5:
					data.dpad.x += b != 0 ? 1 : 0;
					break;
				case 6:
					data.dpad.y -= b != 0 ? 1 : 0;
					break;
				case 7:
					data.dpad.x -= b != 0 ? 1 : 0;
					break;
				}
				
				i++;
			}
		} catch (Exception e) {/* SILENTLY IGNORE */}
	}
	
	/**
	 * 
	 * Handles the controller and formats the data into something more generic.
	 * 
	 * @param data the data that is relevant to this controller.
	 * @param axis the buffer of axis.
	 * @param buttons the buffer of buttons.
	 */
	private static final void handleXboxOne(JoyData data, FloatBuffer axis, ByteBuffer buttons) {
		/* AXIS */
		float a;
		int i = 0;
		try {
			while (axis.hasRemaining()) {
				a = axis.get();
				
				switch(i) {
				case 0:
					data.leftStick.x = Math.abs(a) < DEAD_ZONE ? 0 : a;
					break;
				case 1:
					data.leftStick.y = Math.abs(a) < DEAD_ZONE ? 0 : -a;
					break;
				case 3:
					data.rightStick.x = Math.abs(a) < DEAD_ZONE ? 0 : a;
					break;
				case 4:
					data.rightStick.y = Math.abs(a) < DEAD_ZONE ? 0 : -a;
					break;
				case 6:
					data.dpad.x = Math.abs(a) < DEAD_ZONE ? 0 : Math.signum(a);
					break;
				case 7:
					data.dpad.y = Math.abs(a) < DEAD_ZONE ? 0 : -Math.signum(a);
					break;
				case 2:
					data.leftTrigger = (float) (Math.abs(a) < DEAD_ZONE ? 0 : a * 0.5f + 0.5);
					break;
				case 5:
					data.rightTrigger = (float) (Math.abs(a) < DEAD_ZONE ? 0 : a * 0.5f + 0.5);
					break;
				}
				
				i++;
			}
		} catch (Exception e) {/* SILENTLY IGNORE */}
	
		byte b;
		i = 0;
		try {
			while (buttons.hasRemaining()) {
				b = buttons.get();
				
				switch(i) {
				case 0:
					if (b == 1 && data.a == KeyState.RELEASED) {
						data.a = KeyState.PRESSED;
					} else if (b != 1) {
						data.a = KeyState.RELEASED;
					}
					break;
				case 1:
					if (b == 1 && data.b == KeyState.RELEASED) {
						data.b = KeyState.PRESSED;
					} else if (b != 1) {
						data.b = KeyState.RELEASED;
					}
					break;
				case 2:
					if (b == 1 && data.x == KeyState.RELEASED) {
						data.x = KeyState.PRESSED;
					} else if (b != 1) {
						data.x = KeyState.RELEASED;
					}
					break;
				case 3:
					if (b == 1 && data.y == KeyState.RELEASED) {
						data.y = KeyState.PRESSED;
					} else if (b != 1) {
						data.y = KeyState.RELEASED;
					}
					break;
				case 4:
					if (b == 1 && data.leftBumper == KeyState.RELEASED) {
						data.leftBumper = KeyState.PRESSED;
					} else if (b != 1) {
						data.leftBumper = KeyState.RELEASED;
					}
					break;
				case 5:
					if (b == 1 && data.rightBumper == KeyState.RELEASED) {
						data.rightBumper = KeyState.PRESSED;
					} else if (b != 1) {
						data.rightBumper = KeyState.RELEASED;
					}
					break;
				case 6:
					if (b == 1 && data.select == KeyState.RELEASED) {
						data.select = KeyState.PRESSED;
					} else if (b != 1) {
						data.select = KeyState.RELEASED;
					}
					break;
				case 7:
					if (b == 1 && data.start == KeyState.RELEASED) {
						data.start = KeyState.PRESSED;
					} else if (b != 1) {
						data.start = KeyState.RELEASED;
					}
					break;
				case 8:
					if (b == 1 && data.home == KeyState.RELEASED) {
						data.home = KeyState.PRESSED;
					} else if (b != 1) {
						data.home = KeyState.RELEASED;
					}
					break;
				case 9:
					if (b == 1 && data.left == KeyState.RELEASED) {
						data.left = KeyState.PRESSED;
					} else if (b != 1) {
						data.left = KeyState.RELEASED;
					}
					break;
				case 10:
					if (b == 1 && data.right == KeyState.RELEASED) {
						data.right = KeyState.PRESSED;
					} else if (b != 1) {
						data.right = KeyState.RELEASED;
					}
					break;
				}
				i++;
			}
		} catch (Exception e) {/* SILENTLY IGNORE */}
	}
	
	/**
	 * 
	 * Handles the controller and formats the data into something more generic.
	 * 
	 * @param data the data that is relevant to this controller.
	 * @param axis the buffer of axis.
	 * @param buttons the buffer of buttons.
	 */
	private static final void handleXbox(JoyData data, FloatBuffer axis, ByteBuffer buttons) {
		data.dpad.scale(0);
		
		float a;
		int i = 0;
		try {
			while (axis.hasRemaining()) {
				a = axis.get();
				
				switch(i) {
				case 0:
					data.leftStick.x = Math.abs(a) < DEAD_ZONE ? 0 : a;
					break;
				case 1:
					data.leftStick.y = Math.abs(a) < DEAD_ZONE ? 0 : -a;
					break;
				case 2:
					data.rightStick.x = Math.abs(a) < DEAD_ZONE ? 0 : a;
					break;
				case 3:
					data.rightStick.y = Math.abs(a) < DEAD_ZONE ? 0 : -a;
					break;
				case 6:
					data.dpad.x = Math.abs(a) < DEAD_ZONE ? 0 : Math.signum(a);
					break;
				case 7:
					data.dpad.y = Math.abs(a) < DEAD_ZONE ? 0 : -Math.signum(a);
					break;
				case 4:
					data.leftTrigger = (float) (Math.abs(a) < DEAD_ZONE ? 0 : a * 0.5f + 0.5);
					break;
				case 5:
					data.rightTrigger = (float) (Math.abs(a) < DEAD_ZONE ? 0 : a * 0.5f + 0.5);
					break;
				}
				
				i++;
			}
		} catch (Exception e) {/* SILENTLY IGNORE */}
	
		byte b;
		i = 0;
		boolean isWindows = buttons.limit() > 11;
		try {
			while (buttons.hasRemaining()) {
				b = buttons.get();
				
				
				System.out.println(i + ", " + b);
				
				switch(i) {
				case 0:
					if (b == 1 && data.a == KeyState.RELEASED) {
						data.a = KeyState.PRESSED;
					} else if (b != 1) {
						data.a = KeyState.RELEASED;
					}
					break;
				case 1:
					if (b == 1 && data.b == KeyState.RELEASED) {
						data.b = KeyState.PRESSED;
					} else if (b != 1) {
						data.b = KeyState.RELEASED;
					}
					break;
				case 2:
					if (b == 1 && data.x == KeyState.RELEASED) {
						data.x = KeyState.PRESSED;
					} else if (b != 1) {
						data.x = KeyState.RELEASED;
					}
					break;
				case 3:
					if (b == 1 && data.y == KeyState.RELEASED) {
						data.y = KeyState.PRESSED;
					} else if (b != 1) {
						data.y = KeyState.RELEASED;
					}
					break;
				case 4:
					if (b == 1 && data.leftBumper == KeyState.RELEASED) {
						data.leftBumper = KeyState.PRESSED;
					} else if (b != 1) {
						data.leftBumper = KeyState.RELEASED;
					}
					break;
				case 5:
					if (b == 1 && data.rightBumper == KeyState.RELEASED) {
						data.rightBumper = KeyState.PRESSED;
					} else if (b != 1) {
						data.rightBumper = KeyState.RELEASED;
					}
					break;
				case 6:
					if (b == 1 && data.select == KeyState.RELEASED) {
						data.select = KeyState.PRESSED;
					} else if (b != 1) {
						data.select = KeyState.RELEASED;
					}
					break;
				case 7:
					if (b == 1 && data.start == KeyState.RELEASED) {
						data.start = KeyState.PRESSED;
					} else if (b != 1) {
						data.start = KeyState.RELEASED;
					}
					break;
				case 8: // Woop, windows...
					if (isWindows) {
						if (b == 1 && data.home == KeyState.RELEASED) {
							data.left= KeyState.PRESSED;
						} else if (b != 1) {
							data.right = KeyState.RELEASED;
						}
					} else {
						if (b == 1 && data.home == KeyState.RELEASED) {
							data.home = KeyState.PRESSED;
						} else if (b != 1) {
							data.home = KeyState.RELEASED;
						}
					}
					break;
				case 9:
					if (isWindows) {
						if (b == 1 && data.right == KeyState.RELEASED) {
							data.right = KeyState.PRESSED;
						} else if (b != 1) {
							data.right = KeyState.RELEASED;
						}
					} else {
						if (b == 1 && data.left == KeyState.RELEASED) {
							data.left = KeyState.PRESSED;
						} else if (b != 1) {
							data.left = KeyState.RELEASED;
						}
					}
					break;
				case 10:
					if (isWindows) {
						if (b == 1) {
							data.dpad.y += 1;
						}
					} else {
						if (b == 1 && data.right == KeyState.RELEASED) {
							data.right = KeyState.PRESSED;
						} else if (b != 1) {
							data.right = KeyState.RELEASED;
						}
					}
					break;
				case 11:
					if (b == 1) {
						data.dpad.x += 1;
					}
					break;
				case 12:
					if (b == 1) {
						data.dpad.y -= 1;
					}
					break;
				case 13:
					if (b == 1) {
						data.dpad.x -= 1;
					}
					break;
				}
				i++;
			}
		} catch (Exception e) {/* SILENTLY IGNORE */}
	}
	
	/**
	 * 
	 * Checks if the joy with the GLFW_JOYSTICK_n is connected.
	 * 
	 * @param joy the id of the joystick you want to check. (0 to 15)
	 * @return if it is connected.
	 */
	public static final boolean isConnected(int joy) {
		return GLFW.glfwJoystickPresent(joy);
	}
	
	
	public static final Joystick INSTANCE;
	
	static {
		joysticks = new HashMap<>();
		INSTANCE = new Joystick();
	}
}
