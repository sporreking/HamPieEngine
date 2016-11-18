package sk.entity;

import java.util.ArrayList;
import java.util.List;

public class Container extends Node {
	
	private ArrayList<Node> nodes;
	
	public Container() {
		nodes = new ArrayList<>();
	}
	
	@Override
	public void update(double delta) {
		for(Node node : nodes)
			node.update(delta);
	}
	
	@Override
	public void draw() {
		for(Node node : nodes)
			node.draw();
	}
	
	public Container add(Node node) {
		nodes.add(node);
		
		return this;
	}
	
	public boolean has(Node node) {
		return nodes.contains(node);
	}
	
	public Node get(int i) {
		return nodes.get(i);
	}
	
	public int getIndex(Node node) {
		for(int i = 0; i < nodes.size(); i++)
			if(nodes.get(i) == node)
				return i;
		
		return -1;
	}
	
	@SuppressWarnings("unchecked")
	public List<Node> getNodes() {
		return (ArrayList<Node>) nodes.clone();
	}
	
	public Container remove(int i) {
		nodes.remove(i);
		
		return this;
	}
	
	public int getNumOfNodes() {
		return nodes.size();
	}
}