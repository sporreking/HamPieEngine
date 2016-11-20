package sk.game;

public final class Time {
	
	private static long previousTime = System.nanoTime();
	private static double delta;
	
	protected static final void update() {
		
		long currentTime = System.nanoTime();
		
		delta = (currentTime - previousTime) / 1000000000d;
		
		previousTime = currentTime;
	}
	
	public static final double getDelta() {
		return delta;
	}
}