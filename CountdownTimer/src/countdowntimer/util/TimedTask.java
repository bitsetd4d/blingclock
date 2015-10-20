package countdowntimer.util;

public abstract class TimedTask {

	protected long started;
	protected float seconds = 0;
	boolean stopped = false;
	
	public void stop() {
		stopped = true;
	}
	
	public abstract void run();

	protected void tick() {
		long now = System.currentTimeMillis();
		if (started == 0) {
			started = now;
		} else {
			seconds = (now - started) / 1000.0f;
		}
	}
	
}
