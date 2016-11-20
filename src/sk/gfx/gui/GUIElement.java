package sk.gfx.gui;

import sk.entity.Component;
import sk.game.Window;
import sk.gfx.Camera;
import sk.gfx.Mesh;
import sk.gfx.Renderer;
import sk.gfx.ShaderProgram;

public class GUIElement extends Renderer {
	
	private int width;
	private int height;
	
	/**
	 * Constructs a GUI element, which is attached to the screen.
	 * @param anchorX The anchor point in screen coordinates for this specific GUI element on the X-axis. 
	 * @param anchorY The anchor point in screen coordinates for this specific GUI element on the Y-axis.
	 * @param offsetX The pixel offset from the anchor point, in pixels on the X-axis.
	 * @param offsetY The pixel offset from the anchor point, in pixels on the Y-axis.
	 * @param width The width in pixels of this GUI element.
	 * @param height The height in pixels of this GUI element.
	 */
	public GUIElement(float anchorX, float anchorY, int offsetX, int offsetY, int width, int height) {
		super(Mesh.QUAD);
		camera = new Camera().createOrtho(-1, 1, 1, -1);
		
		updateTransform(anchorX, anchorY, offsetX, offsetY, width, height);
	}
	
	
	/**
	 * Updates the transform of the GUI element
	 * @param anchorX The anchor point in screen coordinates for this specific GUI element on the X-axis. 
	 * @param anchorY The anchor point in screen coordinates for this specific GUI element on the Y-axis.
	 * @param offsetX The pixel offset from the anchor point, in pixels on the X-axis.
	 * @param offsetY The pixel offset from the anchor point, in pixels on the Y-axis.
	 * @param width The width in pixels of this GUI element.
	 * @param height The height in pixels of this GUI element.
	 */
	public void updateTransform(float anchorX, float anchorY, int offsetX, int offsetY, int width, int height) {
		this.width = width;
		this.height = height;
		
		transform.scale.x = 2.0f * width /  Window.getWidth();
		transform.scale.y = 2.0f * height / Window.getHeight();
		
		transform.position.x = anchorX + 2.0f * offsetX / Window.getWidth();
		transform.position.y = anchorY + 2.0f * offsetY / Window.getHeight();
	}

	/**
	 * Updates the transform of the GUI element
	 * @param anchorX The anchor point in screen coordinates for this specific GUI element on the X-axis. 
	 * @param anchorY The anchor point in screen coordinates for this specific GUI element on the Y-axis.
	 * @param offsetX The pixel offset from the anchor point, in pixels on the X-axis.
	 * @param offsetY The pixel offset from the anchor point, in pixels on the Y-axis.
	 */
	public void updateTransform(float anchorX, float anchorY, int offsetX, int offsetY) {
		updateTransform(anchorX, anchorY, offsetX, offsetY, width, height);
	}
	
	/**
	 * Moves the GUI element.
	 * @param deltaOffsetX The distance to move on the X-axis.
	 * @param deltaOffsetY The distance to move on the Y-axis.
	 */
	public void move(float deltaOffsetX, float deltaOffsetY) {
		transform.position.x += deltaOffsetX;
		transform.position.y += deltaOffsetY;
	}
	
	@Override	
	public void update(double delta) {
		
	}
	
	/**
	 * Self descriptive.
	 */
	public void draw() {
		setupShader();
	
		//Send the texture id
		ShaderProgram.GUI.send1i("t_sampler", 0);

		//Tell the shader that this is NOT a fader
		ShaderProgram.GUI.send1i("b_is_fader", 0);
		
		//Bind the texture
		getTexture().bind(0);
		
		getMesh().draw();
	}
	
	protected void setupShader() {
		//Select shader program
		ShaderProgram.GUI.use();
		
		//Send projection matrix
		ShaderProgram.GUI.sendM4("projection", camera.getProjection());
		
		//Send view matrix
		ShaderProgram.GUI.sendM4("view", camera.getMatrix());
		
		//Send model matrix
		ShaderProgram.GUI.sendM4("model", transform.getMatrix());
	}
}