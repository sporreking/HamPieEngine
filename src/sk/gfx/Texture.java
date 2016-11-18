package sk.gfx;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;

public class Texture {
	
	private int id;
	
	private int width;
	private int height;
	
	public Texture() {
		
	}
	
	public Texture(String path) {
		generate(path);
	}
	
	public Texture generate(String path) {
		
		BufferedImage img = null;
		
		try {
			img = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int[] pixels = new int[img.getWidth() * img.getHeight()];
		
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
		
		generate(img.getWidth(), img.getHeight(), pixels);
		
		return this;
	}
	
	public Texture generate(int width, int height, int[] pixels) {
		
		this.width = width;
		this.height = height;
		
		id = glGenTextures();
		
		bind();
		
		IntBuffer buffer = ByteBuffer.allocateDirect(pixels.length << 2)
				.order(ByteOrder.nativeOrder()).asIntBuffer();

		buffer.put(pixels);
		buffer.flip();
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
				GL_BGRA, GL_UNSIGNED_BYTE, buffer);
		
		return this;
	}
	
	public Texture bind() {
		return bind(0);
	}
	
	public Texture bind(int target) {
		glActiveTexture(GL_TEXTURE0 + target);
		glBindTexture(GL_TEXTURE_2D, id);
		
		return this;
	}
	
	public int getID() {
		return id;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void destroy() {
		glDeleteTextures(id);
	}
	
	public static final Texture DEFAULT;
	
	static {
		DEFAULT = new Texture().generate(1, 1, new int[] {0xffffffff});
	}
	
	public static final void destroyAll() {
		DEFAULT.destroy();
	}
}