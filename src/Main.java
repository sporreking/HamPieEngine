import sk.game.Game;
import sk.game.GameProperties;
import sk.util.vector.Vector4f;

public class Main {
	
	
	
	public static final void main(String[] args) {
		GameProperties gp = new GameProperties();
		
		gp.title = "Test";
		gp.startState = States.TEST_1;
		gp.clearColor = new Vector4f(1, 0, 1, 1);
		gp.vSync = false;
		gp.height = 600;
		gp.width = 800;
		
		Game.start(gp);
	}
}