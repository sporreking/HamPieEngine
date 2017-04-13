import java.awt.Font;

import org.lwjgl.glfw.GLFW;

import sk.audio.Audio;
import sk.audio.AudioManager;
import sk.audio.SineAudio;
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
import sk.gfx.Transform;
import sk.gfx.Vertex2D;
import sk.gfx.gui.GUIButton;
import sk.gfx.gui.GUIElement;
import sk.gfx.gui.GUIFader;
import sk.physics.Body;
import sk.physics.Shape;
import sk.physics.World;
import sk.sst.SST;
import sk.util.io.Keyboard;
import sk.util.vector.Vector2f;
import sk.util.vector.Vector3f;

public class TestState implements GameState {
	
	private Texture t_wood, t_mask, t_on, t_off;
	
	private SpriteSheet t_ss;
	
	private FontTexture t_font;
	
	Entity t_entity;
	
	Root t_root;
	
	Audio t_psych;
	
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
/*		//t_entity.add(0, new GUIElement(-.5f, 0, 200, 0, 100, 100).setTexture(t_font));
		GUIButton button = new GUIButton(-.5f, 0, 200, 0, 100, 100);
		button.setOnClick(() -> System.out.println("Click"));
		t_entity.add(0, new Transform());
		t_entity.add(0, button);
//		t_entity.add(0, new Animation(t_ss, 5.0f, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));
*/
		
		Shape s_shape = new Shape(
				new Vector2f(-0.5f, -0.5f),
				new Vector2f(-0.5f,  0.5f),
				new Vector2f( 0.5f,  0.5f),
				new Vector2f( 0.5f, -0.5f));
		
		Transform t = new Transform();
		t.position.y = -1.0f;
		//t.scale.x = 1.5f;
		t_entity.add(0, t);
		t_entity.add(0,	new Renderer(Mesh.QUAD));
		t_entity.add(0, new Body(s_shape, 1.0f, 1.0f, 1.0f));
		//Root
		t_root = new Root().add(0, "Test1", t_entity);

		t_entity = new Entity();
		t = new Transform();
		//t.rotation = (float) (Math.PI * 1.0f / 4.0f);
		t.position.y = 1.0f;
		t.scale.x = 2.0f;
		t_entity.add(0, t);
		t_entity.add(0,	new Renderer(Mesh.QUAD));
		t_entity.add(0, new Body(s_shape, 1.0f, 1.0f, 0.2f));
		t_entity.get(Body.class).setDynamic(false);
		t_entity.get(Body.class).addVelocity(new Vector2f(0.1f, -0.01f));
		
		t_root.add(0, "Test2", t_entity);
		
		t_entity = new Entity();
		t = new Transform();
		//t.rotation = (float) (Math.PI * 1.0f / 4.0f);
		t.position.y = 0.0f;
		t.position.x = 1.0f;
		t.scale.x = 2.0f;
		t_entity.add(0, t);
		t_entity.add(0,	new Renderer(Mesh.QUAD));
		t_entity.add(0, new Body(s_shape, 1.0f, 1.0f, 0.2f));
		t_entity.get(Body.class).setDynamic(true);
		
		

		t_root.add(0, "Test3", t_entity);
		
		//Audio
		AudioManager.start();
		
		t_psych = new Audio("res/audio/elevator.wav");
		
		//AudioManager.playSource(0, 1.0f, 1.0f, 5, t_psych, true);
	}
	
	float speed = .01f;
	float nd = 1.0f;
	@Override
	public void update(double delta) {
		
		World.update(delta);
		
		//((Entity) t_root.get("Test1")).get(Transform.class).rotation += delta * 0.1;
		
		if(Keyboard.down(GLFW.GLFW_KEY_Z)) {
			((Entity) t_root.get("Test1")).get(Body.class).addForce(new Vector2f((float) delta, 0.0f));
		}
		
		if(Keyboard.down(GLFW.GLFW_KEY_X)) {
			((Entity) t_root.get("Test1")).get(Body.class).addForce(new Vector2f((float) -delta, 0.0f));
		}
		
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
			((Entity) t_root.get("Test1")).get(GUIFader.class).changeThreshold(speed);
		}
		if(Keyboard.down(GLFW.GLFW_KEY_E)) {
			Camera.DEFAULT.scale.x -= speed;
			Camera.DEFAULT.scale.y -= speed;
			((Entity) t_root.get("Test1")).get(GUIFader.class).changeThreshold(-speed);
		}
		if(Keyboard.down(GLFW.GLFW_KEY_O)) {
			nd += speed * .1f;
			AudioManager.setSourcePitch(nd, 0);
			t_entity.get(GUIButton.class).transform.position.x += speed;
		}
		if(Keyboard.down(GLFW.GLFW_KEY_I)) {
			nd -= speed * .1f;
			AudioManager.setSourcePitch(nd, 0);
			t_entity.get(GUIButton.class).transform.position.x -= speed;
		}
		
		if(Keyboard.released(GLFW.GLFW_KEY_ESCAPE))
			Game.stop();
		t_root.update(delta * nd);
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
		t_psych.destroy();
	}
}
