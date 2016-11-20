package sk.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import sk.gfx.Renderer;

public class Entity extends Node {
	
	private TreeMap<Integer, ArrayList<Component>> components;
	
	public Entity() {
		components = new TreeMap<>();
	}
	
	@Override
	public void update(double delta) {
		for(int i : components.keySet())
			for(Component c : components.get(i))
				c.update(delta);
	}
	
	@Override
	public void draw() {
		for(int i : components.keySet())
			for(Component c : components.get(i))
				c.draw();
	}
	
	public <T extends Component> boolean has(Class<T> c) {
		for(List<Component> comps : components.values())
			for(Component comp : comps)
				if(comp.getClass() == c)
					return true;
		
		return false;
	}
	
	public Entity add(int priority, Component comp) {
		if(has(comp.getClass()))
			throw new IllegalArgumentException("The component ("
						+ comp.getClass().getSimpleName() + ") is already part of this entity");
		
		//Check if the prerequisite components are attached
		if(comp.requirements() != null) {
			ArrayList<String> requirements = new ArrayList<>();
			
			for(Class<? extends Component> req : comp.requirements())
				if(!has(req))
					requirements.add(req.getName());
			
			if(!requirements.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				
				sb.append("\"" + comp.getClass().getSimpleName() + "\" requires"
						+ " the following component(s): ");
				
				for(String c : requirements) {
					
					sb.append(c);
					
					if(requirements.indexOf(c) != requirements.size() - 1)
						sb.append(", ");
				}
				
				throw new IllegalStateException(sb.toString());
			}
		}
		
		if(components.get(priority) == null)
			components.put(priority, new ArrayList<Component>());
		
		components.get(priority).add(comp);
		
		comp.setParent(this);
		
		comp.init();
		
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Component> T get(Class<T> c) {
		for(List<Component> comps : components.values())
			for(Component comp : comps)
				if(comp.getClass() == c)
					return (T) comp;
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Component> getComponents(int priority) {
		if(components.get(priority) == null)
			throw new IllegalArgumentException("There are no components with the priority \""
					+ priority + "\"");
		
		return (List<Component>) components.get(priority).clone();
	}
	
	public int getPriority(Class<? extends Component> c) {
		for(int i : components.keySet()) {
			for(Component comp : getComponents(i))
				if(comp.getClass() == c)
					return i;
		}
		
		return -1;
	}
	
	public Entity remove(Class<? extends Component> c) {
		if(!has(c))
			throw new IllegalArgumentException("The component \"" + c.getSimpleName()
					+ "\" is not a part of this entity");
		
		int p = getPriority(c);
		
		for(Component comp : components.get(p)) {
			if(comp.getClass() == c) {
				comp.removeParent();
				components.get(p).remove(comp);
			}
		}
		
		return this;
	}
	
	public Entity clear() {
		components.clear();
		
		return this;
	}
}