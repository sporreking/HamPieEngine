package sk.util.io;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.LinkedList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWJoystickCallback;

import com.sun.xml.internal.ws.encoding.soap.SOAP12Constants;

import sk.util.io.Keyboard.KeyState;
import sk.util.vector.Vector2f;

/**
 * 
 * Handles the connection and disconnections of joysticks (controllers).
 * 
 * @author ed
 *
 */
public class Joystick extends GLFWJoystickCallback {

	public static final float DEAD_ZONE = 0.05f;
	
	// Holds the data of a controller
	public static class JoyData {
		private enum JoyType {
			None,
			PS4,
			PS3,
			Xbox,
		}
		
		public JoyType type = JoyType.None;

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
	private static final HashMap<Integer, JoyData> inputs;
	
	
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
	 * Adds the joystick and adds a keymap to it.
	 * 
	 * @param joy the joystick id
	 */
	private static void add(int joy) {
		JoyData c = new JoyData();
		
		switch (GLFW.glfwGetJoystickName(joy)) {
		case "Sony PLAYSTATION(R)3 Controller":
			c.type = JoyData.JoyType.PS3;
			break;
		case "Sony Computer Entertainment Wireless Controller":
			c.type = JoyData.JoyType.PS4;
			break;
		default:
			System.out.println(GLFW.glfwGetJoystickName(joy));
			System.out.println("Unkown controller connected!");
		}
		
		inputs.put(joy, c);
	}
	
	/**
	 * 
	 * Cleans up after the joystick
	 * 
	 * @param joy the joystick id
 	 */
	private static void remove(int joy) {
		int i = 0;
		for (int c : inputs.keySet()) {
			if (c == joy) {
				inputs.remove(c);
				break;
			}
			i++;
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
				System.out.println(i + " is connected!");
			}
		}
	}
	
	public static final void pollEvents() {
		// Update each connected controller
		int joy;
		JoyData data;
		for (int i : inputs.keySet()) {
			joy = i;
			data = inputs.get(i);
			FloatBuffer axis = GLFW.glfwGetJoystickAxes(joy);
			ByteBuffer buttons = GLFW.glfwGetJoystickButtons(joy);
			
			switch (data.type) {
			case PS4:
				handlePS4(data, axis, buttons);
				break;
			case PS3:
				handlePS3(data, axis, buttons);
				break;
			case Xbox:
				handleXbox(data, axis, buttons);
				break;
			default:
				break;
			}
		}
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
		
		byte b;
		i = 0;
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
	private static final void handleXbox(JoyData data, FloatBuffer axis, ByteBuffer buttons) {
		System.out.println("FIX ME!");
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
	
	
	public static final Joystick INSTANCE = new Joystick();
	
	static {
		inputs = new HashMap<>();
	}
}
