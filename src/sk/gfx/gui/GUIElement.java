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
	
	public GUIElement(float anchorX, float anchorY, int offsetX, int offsetY, int width, int height) {
		super(Mesh.QUAD);
		
		this.width = width;
		this.height = height;
		
		transform.scale.x = 2.0f * width /  Window.getWidth();
		transform.scale.y = 2.0f * height / Window.getHeight();
		
		camera = new Camera().createOrtho(-1, 1, 1, -1);
		transform.position.x = anchorX + 2.0f * offsetX / Window.getWidth();
		transform.position.y = anchorY + 2.0f * offsetY / Window.getHeight();
	}
	
	@Override
	public void update(double delta) {
		
	}
	
	public void draw() {
		
		//Select shader program
		ShaderProgram.GUI.use();
		
		//Send projection matrix
		ShaderProgram.GUI.sendM4("projection", camera.getProjection());
		
		//Send view matrix
		ShaderProgram.GUI.sendM4("view", camera.getMatrix());
		
		//Send model matrix
		ShaderProgram.GUI.sendM4("model", transform.getMatrix());
		
		ShaderProgram.GUI.send1i("t_sampler", 0);
		
		getTexture().bind(0);
		
		getMesh().draw();
	}
}