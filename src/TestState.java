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
import sk.gfx.gui.GUIFader;
import sk.sst.SST;
import sk.util.io.Input;
import sk.util.vector.Vector3f;

public class TestState implements GameState {
	
	private Texture t_wood, t_mask, t_on, t_off;
	
	private SpriteSheet t_ss;
	
	private FontTexture t_font;
	
	Entity t_entity;
	
	Root t_root;
	
	@Override
	public void init() {		
		//GFX
		t_mask = new Texture("res/texture/mask.png");
		t_on = new Texture("res/texture/on.png");
		t_off = new Texture("res/texture/off.png");
		t_wood = new Texture("res/texture/wood.png");
		t_ss = new SpriteSheet("res/texture/zombies.png", 4, 4);
		t_font = new FontTexture("Hello World!", 128, 128, 0, 64,
				new Font("Fixedsys", Font.BOLD, 11), new Vector3f(0, 0, 1));
		
		Renderer r = new Renderer(Mesh.QUAD);
		
		//Entity
		t_entity = new Entity();
		//t_entity.add(0, new GUIElement(-.5f, 0, 200, 0, 100, 100).setTexture(t_font));
		t_entity.add(0, new GUIFader(-.5f, 0, 200, 0, 100, 100, t_mask, t_off, t_on));
//		t_entity.add(0, new Animation(t_ss, 5.0f, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
		
		//Root
		t_root = new Root().add(0, "Test1", t_entity);
	}
	
	float speed = .01f;
	@Override
	public void update(double delta) {
		if(Input.down(GLFW.GLFW_KEY_W))
			Camera.DEFAULT.position.y += speed;
		if(Input.down(GLFW.GLFW_KEY_A))
			Camera.DEFAULT.position.x -= speed;
		if(Input.down(GLFW.GLFW_KEY_S))
			Camera.DEFAULT.position.y -= speed;
		if(Input.down(GLFW.GLFW_KEY_D))
			Camera.DEFAULT.position.x += speed;
		if(Input.down(GLFW.GLFW_KEY_Q)) {
			Camera.DEFAULT.scale.x += speed;
			Camera.DEFAULT.scale.y += speed;
			((Entity) t_root.get("Test1")).get(GUIFader.class).changeValue(speed);
		}
		if(Input.down(GLFW.GLFW_KEY_E)) {
			Camera.DEFAULT.scale.x -= speed;
			Camera.DEFAULT.scale.y -= speed;
			((Entity) t_root.get("Test1")).get(GUIFader.class).changeValue(-speed);
		}
		
		if(Input.released(GLFW.GLFW_KEY_ESCAPE))
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
