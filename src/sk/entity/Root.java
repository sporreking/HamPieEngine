package sk.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class Root extends Node {
	
	private TreeMap<Integer, List<String>> priorities;
	private HashMap<String, Node> nodes;
	
	public Root() {
		priorities = new TreeMap<>();
		nodes = new HashMap<>();
	}
	
	@Override
	public void update(double delta) {
		for(List<String> keys : priorities.values())
			for(String key : keys)
				nodes.get(key).update(delta);
	}
	
	@Override
	public void draw() {
		for(List<String> keys : priorities.values())
			for(String key : keys)
				nodes.get(key).draw();
	}
	
	public Root add(int priority, String key, Node node) {
		if(nodes.containsKey(key))
			throw new IllegalArgumentException("A node with the key \""
					+ key + "\" already exists in this root");
		
		if(priorities.get(priority) == null)
			priorities.put(priority, new ArrayList<String>());
		
		priorities.get(priority).add(key);
		nodes.put(key, node);
		
		return this;
	}
	
	public boolean has(String key) {
		return nodes.containsKey(key);
	}
	
	public Node get(String key) {
		return nodes.get(key);
	}
	
	public int getPriority(String key) {
		for(int i : priorities.keySet())
			if(priorities.get(i).contains(key))
				return i;
		
		throw new IllegalArgumentException("There is no node with the key \""
				+ key + "\" in this root");
	}
	
	public List<String> getKeys(int... priorities) {
		List<String> keys = new ArrayList<>();
		
		for(int i : priorities) {
			if(this.priorities.get(i) == null)
				throw new IllegalArgumentException("There is no node with the priority \""
						+ i + "\" in this root");
			
			for(String key : this.priorities.get(i))
				keys.add(key);
		}
		
		return keys;
	}
	
	public List<Node> getNodes(int... priorities) {
		List<Node> nodes = new ArrayList<>();
		
		for(int i : priorities) {
			if(this.priorities.get(i) == null)
				throw new IllegalArgumentException("There is no node with the priority \""
						+ i + "\" in this root");
			
			for(String key : this.priorities.get(i))
				nodes.add(this.nodes.get(key));
		}
		
		return nodes;
	}
	
	public List<String> getKeys() {
		List<String> keys = new ArrayList<>();
		
		for(String key : nodes.keySet())
			keys.add(key);
		
		return keys;
	}
	
	public List<Node> getNodes() {
		List<Node> nodes = new ArrayList<>();
		
		for(Node node : this.nodes.values())
			nodes.add(node);
		
		return nodes;
	}
	
	public Root remove(String key) {
		for(int i = 0; i < priorities.size(); i++) {
			List<String> list = priorities.get(i);
			for(String str : list) {
				if(str.equals(key)) {
					priorities.remove(i);
					nodes.remove(key);
					return this;
				}
			}
		}
		
		throw new IllegalArgumentException("There is no node with the key \""
				+ key + "\" in this root");
	}
	
	public int getNumOfNodes() {
		return nodes.size();
	}
}