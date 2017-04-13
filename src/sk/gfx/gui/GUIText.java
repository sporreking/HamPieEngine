package sk.gfx.gui;

import sk.game.Window;
import sk.gfx.Texture;
import sk.util.vector.Vector2f;
import sk.util.vector.Vector3f;

enum GUITextPosition {
	TOP,
	BOTTOM,
	RIGHT, 
	TOP_RIGHT,
	BOTTOM_RIGHT,
	LEFT,
	TOP_LEFT,
	BOTTOM_LEFT	
}

enum GUITextAlign {
	LEFT,
	CENTER,
	RIGHT
}

/**
 * 
 * GUI text is text that can be drawn on any GUIElement.
 *
 */
public class GUIText {
	protected String text = "";
	protected Texture texture = null;
	protected Vector3f color = null;
	
	protected Vector2f textureOffset = new Vector2f();
	
	protected int width = -1;
	protected int height = -1;
	
	protected float fontScale = 10;
	protected int spacing = 10;
	
	GUITextAlign align;
	GUITextPosition position;
	
	public GUIText() {}
	
	public GUIText(String text, Vector3f color, GUITextAlign align, GUITextPosition position) {
		this.text = text;
		this.color = color;
		this.align = align;
		this.position = position;
	}
}
