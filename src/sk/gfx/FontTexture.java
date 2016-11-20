package sk.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import sk.util.vector.Vector3f;

public class FontTexture extends Texture {
	
	public FontTexture() {
		
	}
	
	public FontTexture(String text, int width, int height, int x, int y) {
		this(text, width, height, x, y, new Font("Arial Black", Font.BOLD, 11));
	}
	
	public FontTexture(String text, int width, int height, int x, int y, Font font) {
		this(text, width, height, x, y, font, new Vector3f());
	}
	
	public FontTexture(String text, int width, int height, int x, int y, Font font, Vector3f color) {
		generate(text, width, height, x, y, font, color);
	}
	
	public FontTexture generate(String text, int width, int height, int x, int y, Font font, Vector3f color) {
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics gfx = img.getGraphics();
		
		float[] hsb = new float[3];
		
		Color.RGBtoHSB((int) Math.floor(color.x * 255),
				(int) Math.floor(color.y * 255), (int) Math.floor(color.z * 255), hsb);
		
		gfx.setColor(Color.getHSBColor(hsb[0], hsb[1], hsb[2]));
		gfx.setFont(font);
		gfx.drawString(text, x, y);
		
		int[] pixels = new int[width * height];
		
		img.getRGB(0, 0, width, height, pixels, 0, width);
		
		generate(width, height, pixels);
		
		return this;
	}
	
}