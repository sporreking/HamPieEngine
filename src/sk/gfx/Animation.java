package sk.gfx;

import sk.entity.Component;

public class Animation extends Component {
	
	private SpriteSheet spriteSheet;
	
	private int[] frames;
	
	private int offset;
	
	private float speed;
	
	private float stack;
	
	public Animation(SpriteSheet spriteSheet, float speed, int... frames) {
		this.spriteSheet = spriteSheet;
		this.frames = frames;
		this.speed = speed;
		
		offset = 0;
		stack = 0;
	}
	
	@Override
	public void init() {
		getParent().get(Renderer.class).setTexture(spriteSheet.getTexture(frames[offset]));
	}
	
	@Override
	public void update(double delta) {
		stack += delta * speed;
		
		if(stack >= 1.0f) {
			stack -= 1.0f;
			increment();
			getParent().get(Renderer.class).setTexture(spriteSheet.getTexture(frames[offset]));
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Class<Component>[] requirements() {
		return new Class[] { Renderer.class };
	}
	
	public Animation setFrames(int... frames) {
		this.frames = frames;
		
		return this;
	}
	
	public Animation addFrames(int... frames) {
		int[] newFrames = new int[this.frames.length + frames.length];
		
		for(int i = 0; i < this.frames.length; i++)
			newFrames[i] = this.frames.length;
		
		for(int i = 0; i < frames.length; i++)
			newFrames[this.frames.length + i] = frames[i];
		
		this.frames = newFrames;
		
		return this;
	}
	
	public Animation clearFrames() {
		frames = new int[0];
		return this;
	}
	
	public int getNumOfFrames() {
		return frames.length;
	}
	
	public int[] getFrames() {
		return frames;
	}
	
	public Animation setOffset(int offset) {
		this.offset = offset % frames.length;
		
		return this;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getCurrentFrame() {
		return frames[offset];
	}
	
	public Animation increment() {
		offset++;
		offset %= frames.length;
		
		return this;
	}
	
	public Animation decrement() {
		offset--;
		offset %= frames.length;
		
		return this;
	}
}