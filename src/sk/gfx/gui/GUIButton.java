package sk.gfx.gui;

import java.util.ArrayList;
import java.util.List;

import sk.entity.Component;
import sk.entity.component.AABB;
import sk.util.io.Mouse;
import sk.util.io.MouseButton;

public class GUIButton extends GUIElement {

	int mouseButton = 0;
	boolean isDown = false;
	boolean isOver = false;
	
	Event onClick, onRelease, onHover, onUnhover;
	
	
	public GUIButton(float anchorX, float anchorY, int offsetX, int offsetY, int width, int height) {
		super(anchorX, anchorY, offsetX, offsetY, width, height);
	}
	
	public GUIButton(float anchorX, float anchorY, int offsetX, int offsetY, int width, int height, int mouseButton) {
		super(anchorX, anchorY, offsetX, offsetY, width, height);
		this.mouseButton = mouseButton;
	}
	
	@Override
	/**
	 * Add an AABB if there isn't one already.
	 */
	public void init() {
		getParent().add(0, (Component) (new AABB()));
	}
	
	@Override
	public void update(double delta) {
		if (MouseButton.changes) {
			System.out.println("A");
			if (getParent().get(AABB.class).check(Mouse.getMousePosition())) {
				if (!isOver) {
					System.out.println("B");
					onHover.fire();
				}
				isOver = true;
			} else {
				if (isOver) {
					onRelease.fire();
				}
				isOver = false;
			}
			
			if (MouseButton.pressed(mouseButton) && isOver) {
				onClick.fire();
			}
			
			if (MouseButton.released(mouseButton) || !isOver) {
				onRelease.fire();
			}
		}
	}

	public int getMouseButton() {
		return mouseButton;
	}

	public void setMouseButton(int mouseButton) {
		this.mouseButton = mouseButton;
	}

	public Event getOnClick() {
		return onClick;
	}

	public void setOnClick(Event onClick) {
		this.onClick = onClick;
	}

	public Event getOnRelease() {
		return onRelease;
	}

	public void setOnRelease(Event onRelease) {
		this.onRelease = onRelease;
	}

	public Event getOnHover() {
		return onHover;
	}

	public void setOnHover(Event onHover) {
		this.onHover = onHover;
	}

	public Event getOnUnhover() {
		return onUnhover;
	}

	public void setOnUnhover(Event onUnhover) {
		this.onUnhover = onUnhover;
	}

	public boolean isDown() {
		return isDown;
	}

	public boolean isOver() {
		return isOver;
	}
	
}
