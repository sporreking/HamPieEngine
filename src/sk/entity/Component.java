package sk.entity;

public abstract class Component {
	
	private Entity parent = null;
	
	public void init() {}
	
	public void update(double delta) {}
	
	public void draw() {}
	
	public void exit() {}
	
	public Class<? extends Component>[] requirements() { return null; }
	
	public Entity getParent() {
		return parent;
	}
	
	protected void setParent(Entity parent) {
		if(this.parent != null)
			throw new IllegalStateException("This component already has a parent");
		
		this.parent = parent;
	}
	
	protected void removeParent() {
		parent = null;
	}
}