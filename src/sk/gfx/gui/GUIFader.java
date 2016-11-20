package sk.gfx.gui;

import sk.gfx.ShaderProgram;
import sk.gfx.Texture;

public class GUIFader extends GUIElement{

	
	//
	Texture[] textures;
	Texture mask;
	
	float value;
	
	/**
	 * Constructs a GUI element that fades between textures depending on the value, and the mask. 
	 * It draws "textures[0]" if the sample from "mask" is greater than "value" otherwise it draws "texture[1]"
	 * @param anchorX The anchor point in screen coordinates for this specific GUI element on the X-axis. 
	 * @param anchorY The anchor point in screen coordinates for this specific GUI element on the Y-axis.
	 * @param offsetX The pixel offset from the anchor point, in pixels on the X-axis.
	 * @param offsetY The pixel offset from the anchor point, in pixels on the Y-axis.
	 * @param width The width in pixels of this GUI element.
	 * @param height The height in pixels of this GUI element.
	 * @param mask The mask that handles the blending between the two textures.
	 * @param textures The two textures that will have color sampled from it. 
	 */
	public GUIFader(float anchorX, float anchorY, int offsetX, int offsetY, int width, int height, Texture mask, Texture ... textures) {
		super(anchorX, anchorY, offsetX, offsetY, width, height);
		
		if (textures.length != 2) {
			throw new IllegalArgumentException("You did not supply two textures to the GUIFader constructor for textures.");
		}
		
		this.textures = textures;
		this.mask = mask;
		
		value = 0;
	}

	/**
	 * Sets the indexed texture to the new texture.
	 * @param index The index of the texture, 0 or 1.
	 * @param texture The texture.
	 */
	public void setTexture(int index, Texture texture) {
		this.textures[index] = texture;
	}
	
	/**
	 * Sets the value of value, which controls the blending.
	 * @param value The value.
	 */
	public void setValue(float value) {
		value = value < 0.0 ? 0.0f : value;
		value = value > 1.0 ? 1.0f : value;
		this.value = value;
	}
	
	/**
	 * Changes the value of value by delta linearly, which controls the blending.
	 * @param delta The change of "value"
	 */
	public void changeValue(float delta) {
		setValue(value + delta);
	}
	
	/**
	 * Sets the mask texture.
	 * @param mask The mask.
	 */
	public void setMask(Texture mask) {
		this.mask = mask;
	}
	
	/**
	 * Self explanatory.
	 */
	public void draw() {
		setupShader();
		
		//Tell the shader that this is a fader
		ShaderProgram.GUI.send1i("b_is_fader", 1);
		
		//Tell the shader that this is a fader
		ShaderProgram.GUI.send1f("f_value", this.value);
		
		//Send the texture id
		ShaderProgram.GUI.send1i("t_mask", 0);

		//Send the texture id
		ShaderProgram.GUI.send1i("t_sampler", 1);

		//Send the texture id
		ShaderProgram.GUI.send1i("t_sampler_on", 2);
		
		//Bind the mask to the value
		mask.bind(0);
		
		//Bind the sample textures to their values
		textures[0].bind(1);
		textures[1].bind(2);
		
		getMesh().draw();
	}
}
