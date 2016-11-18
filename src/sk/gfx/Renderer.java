package sk.gfx;

import sk.entity.Component;

public class Renderer extends Component {
	
	private Mesh mesh = Mesh.QUAD;
	private Texture texture = Texture.DEFAULT;
	public Camera camera = Camera.DEFAULT;
	public Transform transform = new Transform();
	
	/**
	 * 
	 * Creates a renderer from the supplied mesh.
	 * 
	 * @param mesh the mesh to be used by this renderer.
	 */
	public Renderer(Mesh mesh) {
		this.mesh = mesh;
	}
	
	/**
	 * 
	 * Renders the mesh with associated properties.
	 * 
	 */
	public void draw() {
		
		//Select shader program
		ShaderProgram.ORTHO.use();
		
		//Send projection matrix
		ShaderProgram.ORTHO.sendM4("projection", camera.getProjection());
		
		//Send view matrix
		ShaderProgram.ORTHO.sendM4("view", camera.getMatrix());
		
		//Send model matrix
		ShaderProgram.ORTHO.sendM4("model", transform.getMatrix());
		
		ShaderProgram.ORTHO.send1i("t_sampler", 0);
		
		texture.bind(0);
		
		mesh.draw();
	}
	
	public Mesh getMesh() {
		return mesh;
	}
	
	public Renderer setMesh(Mesh mesh) {
		this.mesh = mesh;
		
		return this;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public Renderer setTexture(Texture texture) {
		this.texture = texture;
		
		return this;
	}
}