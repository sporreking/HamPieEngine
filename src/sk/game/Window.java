package sk.game;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import sk.util.io.Mouse;
import sk.gfx.Camera;
import sk.gfx.Texture;
import sk.util.io.Keyboard;
import sk.util.vector.Vector4f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;


public final class Window {
	
	private static GLFWVidMode primary;
	
	private static long window;
	private boolean fullscreen = false;
	
	private static Vector4f clearColor;
	
	private static float aspectRatio = 1.0f;
	
	public static final class Resize extends GLFWWindowSizeCallback {
		public static final Resize INSTANCE = new Resize();

		@Override
		public void invoke(long window, int width, int height) {
			Window.setSize(width, height);
		}
	}
	
	/**
	 * 
	 * Creates window, GL-context and sets up callbacks.
	 * 
	 */
	protected static final void create() {
		
		//Setup error callback
		GLFWErrorCallback.createPrint(System.err).set();
		
		//Initialize GLFW
		if(!glfwInit())
			throw new IllegalStateException("Failed to initialize GLFW");
		
		//Hints
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, Game.properties.resizable ? GLFW_TRUE : GLFW_FALSE);
		
		if (!Game.properties.fullscreen) {
			//Create window
			window = glfwCreateWindow(Game.properties.width, Game.properties.height,
					Game.properties.title, MemoryUtil.NULL, MemoryUtil.NULL);
			
			if(window == MemoryUtil.NULL)
				throw new IllegalStateException("Failed to create window");
			
			//Primary screen mode
			primary = glfwGetVideoMode(glfwGetPrimaryMonitor());
			
			//Center window
			glfwSetWindowPos(window, (primary.width() - getWidth()) / 2,
					(primary.height() - getHeight()) / 2);
		} else {
			if (Game.properties.display > getNumberOfDisplays()) {
				Game.properties.display = 0;
			}
			long monitor = glfwGetMonitors().get(Game.properties.display);
			
			if (Game.properties.useDisplayResolution) {
				GLFWVidMode mode = glfwGetVideoMode(monitor);
				Game.properties.width = mode.width();
				Game.properties.height = mode.height();
			}
			
			//Create window
			window = glfwCreateWindow(Game.properties.width, Game.properties.height,
					Game.properties.title, monitor, MemoryUtil.NULL);
		}
		//Make context current
		glfwMakeContextCurrent(window);
		
		//Detect context
		GL.createCapabilities();
		
		//VSync?
		glfwSwapInterval(Game.properties.vSync ? 1 : 0);
		
		//Clear color
		setClearColor(Game.properties.clearColor);
		
		//Viewport
		setSize(Game.properties.width, Game.properties.height);
		
		//Icon, if one is desired
		if (!Game.properties.icon.isEmpty())
			setIcon(Game.properties.icon);
		
		//Setup callbacks
		Keyboard.INSTANCE.set(window);
		Mouse.INSTANCE.set(window);
		Mouse.Cursor.INSTANCE.set(window);
		Resize.INSTANCE.set(window);
	}
	
	/**
	 * 
	 * Fetches the current window width.
	 * 
	 * @return the width of the window in pixels.
	 */
	public static final int getWidth() {
		int[] width = new int[1];
		
		glfwGetWindowSize(window, width, null);
		
		return width[0];
	}
	
	/**
	 * 
	 * Sets the height of the window.
	 * 
	 * @param height the new height for the window.
	 */
	public static final void setHeight(int height) {
		int width = getWidth();
		setSize(width, height);
	}
	
	/**
	 * 
	 * Sets the width of the window.
	 * 
	 * @param width the new width for the window.
	 */
	public static final void setWidth(int width) {
		int height = getHeight();
		setSize(width, height);
	}
	
	/**
	 * 
	 * Queries GLFW for the number of displays it finds.
	 * 
	 * @return the number of displays connected.
	 */
	public static final int getNumberOfDisplays() {
		return glfwGetMonitors().limit();
	}
	
	/**
	 * 
	 * Enters borderless full screen on the primary display width its default width and height.
	 *	
	 */
	public static final void enterBorderless() {
		enterBorderless(0, -1, -1);
	}
	
	/**
	 * 
	 * Enters borderless full screen on the specified monitor with the displays width and height.
	 * 
	 * @param display the display you want to change to.
	 */
	public static final void enterBorderless(int display) {
		enterBorderless(display, -1, -1);
	}
	
	/**
	 * 
	 * Enters borderless full screen on the specified monitor.
	 * 
	 * @param display the display index you want to use.
	 * @param width the width of the new window. -1 for the displays width.
	 * @param width the height of the new window. -1 for the displays height.
	 */
	public static final void enterBorderless(int display, int width, int height) {
		PointerBuffer monitors = glfwGetMonitors();
		
		GLFWVidMode mode = glfwGetVideoMode(monitors.get(display));
		
		if (width == -1) {
			width = mode.width();
		}
		
		if (height == -1) {
			height = mode.height();
		}
		
		// Without these line, you can't switch displays while in full screen
		glfwWindowHint(GLFW_DECORATED, 1);
		glfwSetWindowMonitor(window, monitors.get(display), 0, 0, width, height, mode.refreshRate());
		glfwWindowHint(GLFW_DECORATED, 0);
		setSize(width, height);
	}
	
	/**
	 * 
	 * Changes the window to be floating if it was full screen, nothing happens otherwise.
	 * 
	 * @param x the x position of the window.
	 * @param y the y position of the window.
	 * @param width the width of the window.
	 * @param height the height of the window.
	 */
	public static final void enterFloating(int x, int y, int width, int height) {
		GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		
		// Without these line, you can't switch displays while in full screen
		glfwWindowHint(GLFW_DECORATED, 1);
		glfwSetWindowMonitor(window, 0, (mode.width() - width) / 2, (mode.height() - height) / 2, width, height, mode.refreshRate());
		setSize(width, height);
	}
	
	/**
	 * 
	 * Resizes the GLViewport to fir with the new size of display.
	 * 
	 * @param width the new width of the viewport.
	 * @param height the new height of the viewport.
	 */
	public static final void setSize(int width, int height) {
		glViewport(0, 0, width, height);
		aspectRatio = ((float) width) / ((float) height);

		if (Game.properties.recalculateViewMatrix)
			Camera.DEFAULT.updateViewMatrix();
	}
	
	/**
	 * 
	 * Fetches the current window height.
	 * 
	 * @return the height of the window in pixels.
	 */
	public static final int getHeight() {
		int[] height = new int[1];
		
		glfwGetWindowSize(window, null, height);
		
		return height[0];
	}
	
	/**
	 * 
	 * Sets the icon for the application.
	 * 
	 * @param path the relative path to the icon you want to use.
	 */
	public static final void setIcon(String path) {
		BufferedImage img = null;
		
		try {
			img = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		int[] pixels = new int[img.getWidth() * img.getHeight()];
		
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
		
		ByteBuffer bytes = BufferUtils.createByteBuffer(pixels.length * 4);
		bytes.asIntBuffer().put(pixels);
		
		GLFWImage glfwImg =GLFWImage.malloc();
		glfwImg.set(img.getWidth(), img.getHeight(), bytes);
		GLFWImage.Buffer buffer = GLFWImage.malloc(1);
		buffer.put(0, glfwImg);
		
		glfwSetWindowIcon(window, buffer);

		buffer.free();
		glfwImg.free();
	}
	
	/**
	 * 
	 * Returns the aspect ratio of the window.
	 * 
	 * @return the aspect ratio of the window.
	 */
	public static final float getAspectRatio() {
		return aspectRatio;
	}
	
	/**
	 * 
	 * Clears the window with the pre-selected color.
	 * 
	 */
	public static final void clear() {
		glClear(GL_COLOR_BUFFER_BIT);
	}
	
	/**
	 * 
	 * Sets the clear color of the window.
	 * 
	 * @param clearColor the new clear color
	 */
	public static final void setClearColor(Vector4f clearColor) {
		Window.clearColor = clearColor;
		glClearColor(clearColor.getX(), clearColor.getY(), clearColor.getZ(), clearColor.getW());
	}
	
	/**
	 * 
	 * Gets the current clear color.
	 * 
	 * @return the current clear color.
	 */
	public static final Vector4f getClearColor() {
		return new Vector4f(clearColor);
	}
	
	/**
	 * 
	 * Swaps buffers with OpenGL and displays what has been rendered.
	 * 
	 */
	public static final void swapBuffers() {
		glfwSwapBuffers(window);
	}
	
	/**
	 * 
	 * Returns whether or not the window's close button has been pressed.
	 * 
	 * @return true if the close button has been pressed.
	 */
	protected static final boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}
	
	/**
	 * 
	 * Makes the window visible.
	 * 
	 */
	protected static final void show() {
		glfwShowWindow(window);
	}
	
	/**
	 * 
	 * Frees callbacks and destroys window.
	 * 
	 */
	protected static final void destroy() {
		Callbacks.glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
	}
}