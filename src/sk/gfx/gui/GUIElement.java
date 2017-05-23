package sk.gfx.gui;

import sk.entity.component.AABB;
import sk.game.Window;
import sk.gfx.Camera;
import sk.gfx.Mesh;
import sk.gfx.Renderer;
import sk.gfx.ShaderProgram;
import sk.gfx.Transform;
import sk.util.vector.Vector4f;

public class GUIElement extends Renderer {
	
	protected float anchorX;
	protected float anchorY;
	protected int offsetX;
	protected int offsetY;
	protected int width;
	protected int height;
	protected Vector4f hue;

	protected GUIText text;
	protected boolean dirty = true;
	
	/**
	 * Creates a new GUI element, and attaches it to the screen.
	 * 
	 * @param anchorX the x-coordinate of this GUI element's anchor point. 
	 * @param anchorY the y-coordinate of this GUI element's anchor point.
	 * @param offsetX the x-axis offset in pixels from the anchor point.
	 * @param offsetY the y-axis offset in pixels from the anchor point.
	 * @param width the width of this GUI element in pixels.
	 * @param height the height of this GUI element in pixels.
	 */
	public GUIElement(float anchorX, float anchorY, int offsetX, int offsetY, int width, int height) {
		super(Mesh.QUAD);
		camera = Camera.GUI;
		
		this.anchorX = anchorX;
		this.anchorY = anchorY;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = width;
		this.height = height;
		
		text = null;
		hue = new Vector4f(1, 1, 1, 1);
		
		updateTransform();
	}

	@Override
	public void init() {
		if(getParent().has(Transform.class))
			transform = getParent().get(Transform.class);
		
		updateTransform();
	}
	
	/**
	 * 
	 * Updates the transform of this GUI element.
	 * 
	 * @param anchorX the x-coordinate of this GUI element's anchor point. 
	 * @param anchorY the y-coordinate of this GUI element's anchor point.
	 * @param offsetX the x-axis offset in pixels from the anchor point.
	 * @param offsetY the y-axis offset in pixels from the anchor point.
	 * @param width the width of this GUI element in pixels.
	 * @param height the height of this GUI element in pixels.
	 */
	public void updateTransform() {
		
		transform.scale.x = 2.0f * width /  Window.getWidth();
		transform.scale.y = 2.0f * height / Window.getHeight();
		
		transform.position.x = anchorX + 2.0f * offsetX / Window.getWidth();
		transform.position.y = anchorY + 2.0f * offsetY / Window.getHeight();
		
		// If we have an AABB, update that too.
		if (getParent() == null) return;
		if (getParent().has(AABB.class)) {
			AABB aabb = getParent().get(AABB.class);
			aabb.setWidth(2.0f * width / Window.getWidth());
			aabb.setHeight(2.0f * height / Window.getHeight());
		}
	}
	
	/**
	 * 
	 * Translates the GUI element.
	 * 
	 * @param dx the distance to translate on the x-axis.
	 * @param dy The distance to translate on the y-axis.
	 */
	public void translate(float dx, float dy) {
		transform.position.x += dx;
		transform.position.y += dy;
	}
	
	/**
	 * Adds or changes the text that is displayed on top of this GUI
	 * @param text the new GUIText
	 */
	public void setText(GUIText text) {
		this.text = text;
	}
	
	/**
	 * 
	 * Gets the text on this GUIElement.
	 * 
	 * @return the text object bound to this button.
	 */
	public GUIText getText() {
		return text;
	}
	
	@Override
	public void draw() {
		setupShader();
	
		//Send the texture id
		ShaderProgram.GUI.send1i("t_sampler", 0);

		//Tell the shader that this is NOT a fader
		ShaderProgram.GUI.send1i("b_is_fader", 0);

		//Bind the texture
		getTexture().bind(0);

		//Binds the text if it is available
		if (text != null) {
			text.bind();
		}
		
		getMesh().draw();
	}
	
	/**
	 * @return the current hue of the GUIElement.
	 */
	public Vector4f getHue() {
		return hue;
	}
	
	/**
	 * 
	 * Sets the X-anchor.
	 * 
	 * @param x the OpenGL x coordinate this should anchor to.
	 */
	public void setAnchorX(float x) {
		anchorX = x;
		dirty = true;
	}
	
	/**
	 * 
	 * Sets the Y-anchor.
	 * 
	 * @param y the OpenGL y coordinate this should anchor to.
	 */
	public void setAnchorY(float y) {
		anchorY = y;
		dirty = true;
	}
	
	/**
	 * 
	 * Sets the anchor.
	 * 
	 * @param x the OpenGL x coordinate this should anchor to.
	 * @param y the OpenGL y coordinate this should anchor to.
	 */
	public void setAnchor(float x, float y) {
		anchorX = x;
		anchorY = y;
		dirty = true;
	}

	/**
	 * 
	 * The hue of the GUIElement is multiplied with the original color
	 * to give the button a different hue or change it's alpha.
	 * 
	 * @param hue the new hue of the GUIElemet
	 */
	public void setHue(Vector4f hue) {
		this.hue = hue;
	}
	
	/**
	 * 
	 * Called before each draw call to bind the GUI shader program and send it's appropriate matrices.
	 * 
	 */
	protected void setupShader() {
		// Update the transforms if the resolution has changed since last draw call.
		if (Window.resolutionHasChanged() || dirty) {
			updateTransform();
		}

		//Select shader program
		ShaderProgram.GUI.use();
		
		//Send projection matrix
		ShaderProgram.GUI.sendM4("projection", camera.getProjection());
		
		//Send view matrix
		ShaderProgram.GUI.sendM4("view", camera.getMatrix());
		
		//Send model matrix
		ShaderProgram.GUI.sendM4("model", transform.getMatrix());

		//Send in the hue
		ShaderProgram.GUI.send4f("v_hue", hue.x, hue.y, hue.z, hue.w);
	}
}
