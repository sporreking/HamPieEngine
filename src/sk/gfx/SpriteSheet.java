package sk.gfx;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet {
	
	private Texture[] textures;
	
	private int width;
	private int height;
	private int tilesX;
	private int tilesY;
	
	public SpriteSheet() {
		
	}
	
	public SpriteSheet(String path, int tilesX, int tilesY) {
		generate(path, tilesX, tilesY);
	}
	
	public SpriteSheet generate(String path, int tilesX, int tilesY) {
		this.tilesX = tilesX;
		this.tilesY = tilesY;
		
		BufferedImage img = null;
		
		try {
			
			img = ImageIO.read(new File(path));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		textures = new Texture[tilesX * tilesY];
		
		width = img.getWidth();
		height = img.getHeight();
		
		int tw = width / tilesX;
		int th = height / tilesY;
		
		for(int i = 0; i < tilesY; i++) {
			for(int j = 0; j < tilesX; j++) {
				int[] pixels = new int[tw * th];
				
				img.getRGB(j * tw, i * th, tw, th, pixels, 0, tw);
				
				textures[i * tilesX + j] = new Texture().generate(tw, th, pixels);
			}
		}
		
		return this;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getTilesX() {
		return tilesX;
	}
	
	public int getTilesY() {
		return tilesY;
	}
	
	public int getTileWidth() {
		return width / tilesX;
	}
	
	public int getTileHeight() {
		return height / tilesY;
	}
	
	public Texture getTexture(int offset) {
		return textures[offset];
	}
	
	public Texture getTexture(int x, int y) {
		return textures[y * tilesX + x];
	}
	
	public void destroy() {
		for(Texture t : textures)
			t.destroy();
	}
}