package sk.audio;

import java.util.Random;

import sk.util.vector.Vector3f;

public final class AudioManager {
	
	public static final int MAX_SOURCES = 256;
	public static final int MAX_LOOP_SOURCES = 10;
	public static final int MAX_TEMP_SOURCES = MAX_SOURCES - MAX_LOOP_SOURCES;
	private static final float RANDOM_GAIN_RANGE = 0.25f;
	private static final float RANDOM_PITCH_RANGE = 0.1f;
	
	private static AudioHandler audioHandler;
	private static Thread thread;
	private static Random random;
	
	
	/**
	 * 
	 * Must be called before audio usage.
	 * 
	 */
	public static final synchronized void start() {
		audioHandler = new AudioHandler();
		
		thread = new Thread(audioHandler, "Audio Handler");
		
		thread.start();
		
		random = new Random();
		
		while(!audioHandler.ready);
	}
	
	/**
	 * 
	 * Sets the position of the audio listener for 3D sound.
	 * 
	 * @param position the new position of the listener.
	 */
	public static final synchronized void setListenerPosition(Vector3f position) {
		audioHandler.setListenerPosition(position);
	}
	
	/**
	 * @return the globalLoopGain
	 */
	public float getGlobalLoopGain() {
		return audioHandler.getGlobalLoopGain();
	}

	/**
	 * @param globalLoopGain the globalLoopGain to set
	 */
	public void setGlobalLoopGain(float globalLoopGain) {
		audioHandler.setGlobalLoopGain(globalLoopGain);
	}

	/**
	 * @return the globalTempGain
	 */
	public float getGlobalTempGain() {
		return audioHandler.getGlobalTempGain();
	}

	/**
	 * @param globalTempGain the globalTempGain to set
	 */
	public void setGlobalTempGain(float globalTempGain) {
		audioHandler.setGlobalTempGain(globalTempGain);
	}
	/**
	 * 
	 * Returns the thread where the audio handler is running.
	 * 
	 * @return the audio handler thread.
	 */
	public static final synchronized Thread getThread() {
		return thread;
	}
	
	/**
	 * 
	 * Fades the gain of the specified sources to a new one over a duration of time.
	 * 
	 * @param target the gain to fade to.
	 * @param duration the time period to fade during in seconds.
	 * @param source the sources to fade.
	 */
	public static final synchronized void fadeSourceGain(float target,
			float duration, int... source) {
		for (int s : source)
			audioHandler.queue(new AudioEvent(null, true, false,
					AudioEvent.EVENT_FADE_GAIN, new float[] { s, target,
							duration }));
	}
	
	/**
	 * 
	 * Fades the pitch of the specified sources to a new one over a duration of time.
	 * 
	 * @param target the pitch to fade to.
	 * @param duration the time period to fade during in seconds.
	 * @param source the sources to fade.
	 */
	public static final synchronized void fadeSourcePitch(float target,
			float duration, int... source) {
		for (int s : source)
			audioHandler.queue(new AudioEvent(null, true, false,
					AudioEvent.EVENT_FADE_PITCH, new float[] { s, target,
							duration }));
	}
	
	/**
	 * 
	 * Sets the gain of the specified sources.
	 * 
	 * @param target the gain to set.
	 * @param source the sources to change.
	 */
	public static final synchronized void setSourceGain(float target,
			int... source) {
		fadeSourceGain(target, 0, source);
	}
	
	/**
	 * 
	 * Sets the pitch of the specified sources.
	 * 
	 * @param target the pitch to set.
	 * @param source the sources to change.
	 */
	public static final synchronized void setSourcePitch(float target,
			int... source) {
		fadeSourcePitch(target, 0, source);
	}
	
	/**
	 * 
	 * Instantly stops the specified sources.
	 * 
	 * @param source the sources to stop.
	 */
	public static final synchronized void stopSource(int... source) {
		for (int s : source)
			audioHandler.queue(new AudioEvent(null, true, false,
					AudioEvent.EVENT_STOP, new float[] { s }));
	}
	
	/**
	 * 
	 * Fades the specified sources to a stop over a duration of time.
	 * 
	 * @param duration the duration to fade over in seconds.
	 * @param source the sources to stop.
	 */
	public static final synchronized void stopSource(float duration,
			int... source) {
		for (int s : source)
			audioHandler.queue(new AudioEvent(null, true, false,
					AudioEvent.EVENT_STOP_FADE, new float[] { s, duration }));
	}
	
	/**
	 * 
	 * Instantly pauses the specified sources.
	 * 
	 * @param source the sources to pause.
	 */
	public static final synchronized void pauseSource(int... source) {
		for (int s : source)
			audioHandler.queue(new AudioEvent(null, true, false,
					AudioEvent.EVENT_PAUSE, new float[] { s }));
	}
	
	/**
	 * 
	 * Fades the specified sources to a pause over a duration of time.
	 * 
	 * @param duration the duration to fade over in seconds.
	 * @param source the sources to pause.
	 */
	public static final synchronized void pauseSource(float duration,
			int... source) {
		for (int s : source)
			audioHandler.queue(new AudioEvent(null, true, false,
					AudioEvent.EVENT_PAUSE_FADE, new float[] { s, duration }));
	}
	
	/**
	 * 
	 * Starts playing audio, fading it in over a duration of time.
	 * The audio will be played once only.
	 * 
	 * @param gain the gain to end fading at.
	 * @param pitch the pitch of the audio.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param z the z coordinate.
	 * @param perturb adds just a smidge of random to the sound.
	 * @param audio the audio to play.
	 */
	public static final synchronized void playOne(float gain, float pitch,
			float x, float y, float z, boolean perturb, Audio audio) {
		
		if (perturb) {
			gain *= random.nextFloat() % RANDOM_GAIN_RANGE - RANDOM_GAIN_RANGE / 2 + 1;
			pitch *= random.nextFloat() % RANDOM_PITCH_RANGE - RANDOM_PITCH_RANGE / 2 + 1;
		}
		
		audioHandler.queue(
				new AudioEvent(audio, false, true,
				AudioEvent.EVENT_PLAY_POSITION, new float[] { gain, pitch,
						x, y, z}));
	}
	
	/**
	 * 
	 * Starts playing audio, fading it in over a duration of time.
	 * The audio will be played once only.
	 * 
	 * @param gain the gain to end fading at.
	 * @param pitch the pitch of the audio.
	 * @param duration the duration to fade over in seconds.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param z the z coordinate.
	 * @param perturb adds just a smidge of random to the sound.
	 * @param audio the audio to play.
	 */
	public static final synchronized void playOne(float gain, float pitch,
			float duration, float x, float y, float z, boolean perturb, Audio audio) {
		
		if (perturb) {
			gain *= random.nextFloat() % RANDOM_GAIN_RANGE - RANDOM_GAIN_RANGE / 2 + 1;
			pitch *= random.nextFloat() % RANDOM_PITCH_RANGE - RANDOM_PITCH_RANGE / 2 + 1;
		}
		
		audioHandler.queue(
				new AudioEvent(audio, false, true,
				AudioEvent.EVENT_PLAY_FADE_POSITION, new float[] { gain, pitch,
						duration, x, y, z}));
	}
	
	/**
	 * 
	 * Starts playing audio, fading it in over a duration of time.
	 * The audio will be played once only.
	 * 
	 * @param gain the gain to end fading at.
	 * @param pitch the pitch of the audio.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param z the z coordinate.
	 * @param perturb adds just a smidge of random to the sound.
	 * @param audio the audio to play.
	 */
	public static final synchronized void play(float gain, float pitch,
			float x, float y, float z, boolean perturb, Audio...audio) {
		for (Audio a : audio) {
			playOne(gain, pitch, x, y, z, perturb, a);
		}
	}
	
	/**
	 * 
	 * Starts playing audio, fading it in over a duration of time.
	 * The audio will be played once only.
	 * 
	 * @param gain the gain to end fading at.
	 * @param pitch the pitch of the audio.
	 * @param duration the duration to fade over in seconds.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param z the z coordinate.
	 * @param perturb adds just a smidge of random to the sound.
	 * @param audio the audio to play.
	 */
	public static final synchronized void play(float gain, float pitch,
			float duration, float x, float y, float z, boolean perturb, Audio...audio) {
		for (Audio a : audio) {
			playOne(gain, pitch, duration, x, y, z, perturb, a);
		}
	}

	/**
	 * 
	 * Starts playing audio, fading it in over a duration of time.
	 * The audio will be played once only.
	 * 
	 * @param gain the gain to end fading at.
	 * @param pitch the pitch of the audio.
	 * @param duration the duration to fade over in seconds.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param z the z coordinate.
	 * @param perturb adds just a smidge of random to the sound.
	 * @param audio the audio to play.
	 */
	public static final synchronized void playSource(int source, float gain, float pitch,
		float duration, float x, float y, float z, boolean perturb, Audio audio) {
		
		if (perturb) {
			gain *= random.nextFloat() % RANDOM_GAIN_RANGE - RANDOM_GAIN_RANGE / 2 + 1;
			pitch *= random.nextFloat() % RANDOM_PITCH_RANGE - RANDOM_PITCH_RANGE / 2 + 1;
		}
		
		audioHandler.queue(
			new AudioEvent(audio, false, true,
			AudioEvent.EVENT_PLAY_FADE_POSITION, new float[] { source, gain, pitch,
						duration, x, y, z}));
	}

	/**
	 * 
	 * Starts playing audio, fading it in over a duration of time.
	 * The audio will be played once only.
	 * 
	 * @param gain the gain to end fading at.
	 * @param pitch the pitch of the audio.
	 * @param duration the duration to fade over in seconds.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param z the z coordinate.
	 * @param audio the audio to play.
	 */
	public static final synchronized void playSource(int source, float gain, float pitch,
			float x, float y, float z, boolean loop, Audio audio) {
		audioHandler.queue(
			new AudioEvent(audio, loop, false,
			AudioEvent.EVENT_PLAY_POSITION, new float[] { source, gain, pitch,
						x, y, z}));
	}

	/**
	 * 
	 * Starts playing audio, fading it in over a duration of time.
	 * The audio will be played once only.
	 * 
	 * @param gain the gain to end fading at.
	 * @param pitch the pitch of the audio.
	 * @param duration the duration to fade over in seconds.
	 * @param perturb adds just a smidge of random to the sound.
	 * @param audio the audio to play.
	 */
	public static final synchronized void play(float gain, float pitch,
			float duration, boolean perturb, Audio... audio) {
		
		if (perturb) {
			gain *= random.nextFloat() % RANDOM_GAIN_RANGE - RANDOM_GAIN_RANGE / 2 + 1;
			pitch *= random.nextFloat() % RANDOM_PITCH_RANGE - RANDOM_PITCH_RANGE / 2 + 1;
		}
		
		for (Audio a : audio) {
			audioHandler.queue(new AudioEvent(a, false, true,
					AudioEvent.EVENT_PLAY_FADE, new float[] { gain, pitch,
							duration }));
		}
	}
	
	/**
	 * 
	 * Starts playing audio at the given source, fading it in over a duration of time.
	 * 
	 * @param source the source to play the audio at.
	 * @param gain the gain to end fading at.
	 * @param pitch the pitch of the audio.
	 * @param duration the duration to fade over in seconds.
	 * @param audio the audio to play.
	 * @param loop {@code true} if the audio should loop.
	 */
	public static final synchronized void playSource(int source, float gain,
			float pitch, float duration, Audio audio, boolean loop) {
		audioHandler.queue(new AudioEvent(audio, loop, false,
				AudioEvent.EVENT_PLAY_FADE, new float[] { source, gain, pitch,
						duration }));
	}
	
	/**
	 * 
	 * Instantly plays audio once.
	 * 
	 * @param gain the gain of the audio.
	 * @param pitch the pitch of the audio.
	 * @param perturb adds just a smidge of random to the sound.
	 * @param audio the audio to play.
	 */
	public static final synchronized void play(float gain, float pitch,
			boolean perturb, Audio... audio) {
		for (Audio a : audio) {
			playOne(gain, pitch, 0, 0, 0, perturb, a);
		}
	}
	
	/**
	 * 
	 * Instantly plays audio at the given source.
	 * 
	 * @param source the source to play at.
	 * @param gain the gain of the audio.
	 * @param pitch the pitch of the audio.
	 * @param audio the audio to play.
	 * @param loop {@code true} if the audio should loop.
	 */
	public static final synchronized void playSource(int source, float gain,
			float pitch, Audio audio, boolean loop) {
		audioHandler.queue(new AudioEvent(audio, loop, false,
				AudioEvent.EVENT_PLAY, new float[] { source, gain, pitch }));
	}
	
	/**
	 * 
	 * Called when the audio manager should be destroyed. Should only be called by the engine.
	 * 
	 */
	public static final synchronized void destroy() {
		destroy(0);
	}
	
	/**
	 * 
	 * Called when the audio manager should be destroyed.s
	 * 
	 * @param error the error code to exit with.
	 */
	public static final synchronized void destroy(int error) {
		audioHandler.setError(error);
		audioHandler.running = false;
	}
}