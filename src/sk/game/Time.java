package sk.game;

public final class Time {
	
	private static long previousTime = System.nanoTime();
	private static double delta;
	
	/**
	 * 
	 * Called each frame by the main loop to update the delta time value. Should only be called by the engine.
	 * 
	 */
	protected static final void update() {
		
		long currentTime = System.nanoTime();
		
		delta = (currentTime - previousTime) / 1000000000d;
		
		previousTime = currentTime;
	}
	
	/**
	 * 
	 * Returns the time passed since the previous frame.
	 * 
	 * @return the time passed since the previous frame.
	 */
	public static final double getDelta() {
		return delta;
	}
}