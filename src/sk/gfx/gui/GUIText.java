package sk.gfx.gui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import sk.gfx.FontTexture;
import sk.gfx.ShaderProgram;
import sk.util.vector.Vector2f;
import sk.util.vector.Vector3f;
import sk.util.vector.Vector4f;

/**
 * GUI text is text that can be drawn on any GUIElement.
 * 
 * The text is renderd on the CPU to a bit-map that later is used as a texture
 * where the red chanel is replaced with the color specified. As sutch, color
 * switching is very cheep. But changing the font a lot is very costly, since
 * the entire image has to be re draw.
 *
 */
public class GUIText {
	protected String text = "";
	protected FontTexture texture = null;
	protected Vector4f color = null;
	protected Font font = null;

	protected int width = -1;
	protected int height = -1;

	protected Vector2f offset = new Vector2f();
	protected GUITextPosition position = GUITextPosition.CENTER;

	private boolean dirty = true;;

	/**
	 * For the person who doesn't want anything written displayed.
	 */
	public GUIText() {
	}

	/**
	 * @param text
	 *            the text we wish to write
	 * @param width
	 *            the width of our new image
	 * @param height
	 *            the height of out new image
	 * @param font
	 *            the font that should be used
	 */
	public GUIText(String text, int width, int height, Font font) {
		this(text, width, height, font, new Vector4f(), GUITextPosition.CENTER, new Vector2f());
	}

	/**
	 * @param text
	 *            the text we wish to write
	 * @param width
	 *            the width of our new image
	 * @param height
	 *            the height of out new image
	 * @param font
	 *            the font that should be used
	 * @param color
	 *            a color that will be used for when the font is displayed
	 */
	public GUIText(String text, int width, int height, Font font, Vector4f color) {
		this(text, width, height, font, color, GUITextPosition.CENTER, new Vector2f());
	}

	/**
	 * @param text
	 *            the text we wish to write
	 * @param width
	 *            the width of our new image
	 * @param height
	 *            the height of out new image
	 * @param font
	 *            the font that should be used
	 * @param position
	 *            the position of the text, enumeration wise
	 */
	public GUIText(String text, int width, int height, Font font, GUITextPosition position) {
		this(text, width, height, font, new Vector4f(), position, new Vector2f());
	}

	/**
	 * The do it all constructor, it constructs everything
	 * 
	 * @param text
	 *            the text we wish to write
	 * @param width
	 *            the width of our new image
	 * @param height
	 *            the height of out new image
	 * @param font
	 *            the font that should be used
	 * @param color
	 *            a color that will be used for when the font is displayed
	 * @param position
	 *            the position of the text, enumeration wise
	 * @param offset
	 *            the padding of the text position in pixels
	 */
	public GUIText(String text, int width, int height, Font font, Vector4f color, GUITextPosition position,
			Vector2f offset) {
		this.text = text;
		this.width = width;
		this.height = height;
		this.font = font;
		this.color = color;
		this.position = position;
		this.offset = offset.clone();
		texture = new FontTexture();
	}

	/**
	 * Binds all the necessities for the text to be drawn properly
	 * 
	 * @return
	 */
	public GUIText bind() {
		// Make sure it doesn't need to re draw
		draw();

		if (texture.getID() != 0) {
			// We have a valid texture so send in all the information
			ShaderProgram.GUI.send1i("b_has_text", 1);

			// Bind the rendered text
			ShaderProgram.GUI.send1i("t_text", 3);
			texture.bind(3);

			// Send in the color
			ShaderProgram.GUI.send4f("v_text_color", color.x, color.y, color.z, color.w);
		} else {
			// Tell the shader it doesn't need to draw text
			ShaderProgram.GUI.send1i("b_has_text", 0);
		}
		return this;
	}

	/**
	 * Generates the texture for the text, which later can be bound
	 * 
	 * @return itself for chaining
	 */
	public GUIText draw() {
		// If it isn't dirty, don't draw it
		if (!dirty)
			return this;
		
		// Delete the texture if we have it
		if (texture.getID() != 0)
			texture.destroy();

		// If we have no text, just return
		if (text.equals("")) {
			dirty = false;
			return this;
		}

		// Position the text
		Graphics gfx = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics();
		gfx.setFont(font);
		
		FontMetrics fm = gfx.getFontMetrics();
		int fontWidth = fm.stringWidth(text);
		int fontHeight = fm.getHeight();

		// Calculate X and Y
		int x = 0;
		int y = 0;

		if (position.and(GUITextPosition.TOP)) {
			y = fontHeight + (int) offset.y;
		} else if (position.and(GUITextPosition.BOTTOM)) {
			y = height - fontHeight / 2;
		} else {
			y = height / 2 + fontHeight / 2;
		}

		if (position.and(GUITextPosition.LEFT)) {
			x = (int) offset.x;
		} else if (position.and(GUITextPosition.RIGHT)) {
			x = width - fontWidth;
		} else {
			x = width / 2 - fontWidth / 2 ;
		}
		
		x += (int) offset.x;
		y += (int) offset.y;

		texture.generate(text, width, height, x, y, font, new Vector3f(1.0f, 0.0f, 0.0f));

		// Now we're clean, so flip the flag
		dirty = false;
		return this;
	}

	/**
	 * 
	 * The current text.
	 * 
	 * @return the current text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * 
	 * Sets the current text but doesn't update if it is the same.
	 * 
	 * <p>
	 * Note that this is an expensive 
	 * operation since it has to redraw the entire
	 * font texture.
	 * </p>
	 * 
	 * @param text the new text
	 * @return
	 */
	public GUIText setText(String text) {
		if (this.text.equals(text)) return this;
		
		this.text = text;
		dirty = true;
		return this;
	}

	/**
	 * 
	 * The current color of the text.
	 * 
	 * @return the current color of the text.
	 */
	public Vector4f getColor() {
		return color;
	}

	/**
	 * 
	 * Sets the color of the text. This is a
	 * super cheap operation so feel free to
	 * run it every frame.
	 * 
	 * @param color
	 * @return
	 */
	public GUIText setColor(Vector4f color) {
		this.color = color;
		return this;
	}

	/**
	 * Returns the font that is used.
	 * 
	 * @return the font that is used.
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * 
	 * Sets the font of the text.
	 * 
	 * <p>
	 * Note that this is an expensive 
	 * operation since it has to redraw the entire
	 * font texture.
	 * </p>
	 * 
	 * @param font the new font
	 * @return
	 */
	public GUIText setFont(Font font) {
		this.font = font;
		dirty = true;
		return this;
	}

	/**
	 * 
	 * The width of the texture.
	 * 
	 * @return the width of the texture.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * 
	 * Sets the width of the texture. 
	 * 
	 * <p>
	 * Note that this is an expensive 
	 * operation since it has to redraw the entire
	 * font texture.
	 * </p>
	 * 
	 * @param width the width of the font texture.
	 * @return
	 */
	public GUIText setWidth(int width) {
		this.width = width;
		dirty = true;
		return this;
	}

	/***
	 * 
	 * The height of the font texture.
	 * 
	 * @return the height of the font texture.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * 
	 * Sets the height of the font texture. 
	 * 
	 * <p>
	 * Note that this is an expensive 
	 * operation since it has to redraw the entire
	 * font texture.
	 * </p>
	 * 
	 * @param height the height of the font texture
	 * @return
	 */
	public GUIText setHeight(int height) {
		this.height = height;
		dirty = true;
		return this;
	}

	/**
	 * 
	 * The offset of the text position.
	 * 
	 * @return the offset of the text position.
	 */
	public Vector2f getOffset() {
		return offset;
	}

	/**
	 * 
	 * Sets the offset of the text on the 
	 * font texture. This is relative to the
	 * position it has without the offset in
	 * pixels. 
	 * 
	 * <p>
	 * Note that this is an expensive 
	 * operation since it has to redraw the entire
	 * font texture.
	 * </p>
	 * 
	 * @param offset the new position relative to the default.
	 * @return
	 */
	public GUIText setOffset(Vector2f offset) {
		this.offset = offset;
		dirty = true;
		return this;
	}

	/**
	 * 
	 * The position and alignment of the text.
	 * 
	 * @return the position and alignment of the text.
	 */
	public GUITextPosition getPosition() {
		return position; 
	}

	/**
	 * 
	 * Sets the position and alignment of the
	 * text on the font texture.
	 * 
	 * <p>
	 * Note that this is an expensive 
	 * operation since it has to redraw the entire
	 * font texture.
	 * </p>
	 * 
	 * @param position the position and alignment.
	 * @return
	 */
	
	public GUIText setPosition(GUITextPosition position) {
		this.position = position;
		dirty = true;
		return this;
	}
}
