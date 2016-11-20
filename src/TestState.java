import java.awt.Font;

import org.lwjgl.glfw.GLFW;

import sk.entity.Entity;
import sk.entity.Root;
import sk.game.Game;
import sk.gamestate.GameState;
import sk.gfx.Animation;
import sk.gfx.Camera;
import sk.gfx.FontTexture;
import sk.gfx.Mesh;
import sk.gfx.Renderer;
import sk.gfx.SpriteSheet;
import sk.gfx.Texture;
import sk.gfx.gui.GUIElement;
import sk.sst.SST;
import sk.util.io.Keyboard;
import sk.util.vector.Vector3f;

public class TestState implements GameState {
	
	private Texture t_wood;
	
	private SpriteSheet t_ss;
	
	private FontTexture t_font;
	
	Entity t_entity;
	
	Root t_root;
	
	@Override
	public void init() {		
		//GFX
		t_wood = new Texture("res/texture/wood.png");
		t_ss = new SpriteSheet("res/texture/zombies.png", 4, 4);
		t_font = new FontTexture("Hello World!", 128, 128, 0, 64,
				new Font("Fixedsys", Font.BOLD, 11), new Vector3f(0, 0, 1));
		
		Renderer r = new Renderer(Mesh.QUAD);
		
		//Entity
		t_entity = new Entity();
		t_entity.add(0, new GUIElement(-.5f, 0, 200, 0, 100, 100));
//		t_entity.add(0, new Animation(t_ss, 5.0f, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
		
		//Root
		t_root = new Root().add(0, "Test1", t_entity);
	}
	
	float speed = .01f;
	@Override
	public void update(double delta) {
		if(Keyboard.down(GLFW.GLFW_KEY_W))
			Camera.DEFAULT.position.y += speed;
		if(Keyboard.down(GLFW.GLFW_KEY_A))
			Camera.DEFAULT.position.x -= speed;
		if(Keyboard.down(GLFW.GLFW_KEY_S))
			Camera.DEFAULT.position.y -= speed;
		if(Keyboard.down(GLFW.GLFW_KEY_D))
			Camera.DEFAULT.position.x += speed;
		if(Keyboard.down(GLFW.GLFW_KEY_Q)) {
			Camera.DEFAULT.scale.x += speed;
			Camera.DEFAULT.scale.y += speed;
		}
		if(Keyboard.down(GLFW.GLFW_KEY_E)) {
			Camera.DEFAULT.scale.x -= speed;
			Camera.DEFAULT.scale.y -= speed;
		}
		
		if(Keyboard.released(GLFW.GLFW_KEY_ESCAPE))
			Game.stop();
		t_root.update(delta);
	}
	
	@Override
	public void draw() {
		t_root.draw();
	}
	
	@Override
	public void exit() {
		t_wood.destroy();
		t_ss.destroy();
		t_font.destroy();
	}
}
