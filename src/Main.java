import sk.game.Game;
import sk.game.GameProperties;
import sk.util.vector.Vector4f;

public class Main {
	
	
	
	public static final void main(String[] args) {
		GameProperties gp = new GameProperties();
		
		sk.debug.Debug.setDebugMode(true);;
		
		gp.title = "Test";
		gp.startState = States.TEST_1;
		gp.clearColor = new Vector4f(1, 0, 1, 1);
		gp.vSync = true;
		gp.resizable = true;
		gp.height = 600;
		gp.width = 800;
		gp.icon = "res/texture/mask.png";
		
		Game.start(gp);
	}
}