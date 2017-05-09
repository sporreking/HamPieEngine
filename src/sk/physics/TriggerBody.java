package sk.physics;

/**
 * 
 * A TriggerBody is just like a normal
 * body, but it is by default set to be
 * a trigger. 
 * 
 * Using two bodies on the same entity
 * is undefined behaver if you let them
 * both play in the a physics world.
 * 
 * @author Ed
 *
 */
public class TriggerBody extends Body {
	
	/**
	 * 
	 * Creates a new trigger body, which
	 * is a body that is set to trigger by 
	 * default.
	 * 
	 */
	public TriggerBody() {
		this("", Shape.QUAD);
	}
	
	/**
	 * 
	 * Creates a new trigger body that
	 * can be used for trigger checks. 
	 * It defaults to a unit square.
	 * 
	 * @param tag the tag you want on the object.
	 */
	public TriggerBody(String tag) {
		this(tag, Shape.QUAD);
	}

	/**
	 * 
	 * Creates a new trigger body with
	 * the specifications.
	 * 
	 * @param tag the tag you wish to add.
	 * @param shape the shape you want the collision to have.
	 */
	public TriggerBody(String tag, Shape shape) {
		super(shape);
		setDynamic(false);
		setTrigger(true);
		setTag(tag);
	}	
}
