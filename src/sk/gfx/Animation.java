package sk.gfx;

import sk.entity.Component;

public class Animation extends Component {
	
	private SpriteSheet spriteSheet;
	
	public Animation(SpriteSheet spriteSheet) {
		this.spriteSheet = spriteSheet;
	}
	
	@Override
	public void update(double delta) {
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Class<Component>[] requirements() {
		return new Class[] { Renderer.class };
	}
}