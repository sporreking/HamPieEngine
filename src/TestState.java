import org.lwjgl.glfw.GLFW;

import sk.entity.Entity;
import sk.entity.Root;
import sk.game.Game;
import sk.gamestate.GameState;
import sk.gfx.Animation;
import sk.gfx.Camera;
import sk.gfx.Mesh;
import sk.gfx.Renderer;
import sk.gfx.Texture;
import sk.sst.SST;
import sk.util.io.Input;

public class TestState implements GameState {
	
	private Renderer r;
	
	private Texture t_wood;
	
	Entity t_entity;
	
	Root t_root;
	
	@Override
	public void init() {		
		//GFX
		t_wood = new Texture("res/texture/wood.png");
		
		r = new Renderer(Mesh.QUAD).setTexture(t_wood);
		
		//Entity
		t_entity = new Entity();
		t_entity.add(0, new SST());
		
		t_entity.get(SST.class).store("t1", 1);
		t_entity.get(SST.class).store("t2", "hej");
		
		t_entity.add(1, r);
		t_entity.add(0, new Animation());
		
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
		}
		if(Input.down(GLFW.GLFW_KEY_E)) {
			Camera.DEFAULT.scale.x -= speed;
			Camera.DEFAULT.scale.y -= speed;
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
	}
}
