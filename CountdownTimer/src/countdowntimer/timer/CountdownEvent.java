package countdowntimer.timer;

public interface CountdownEvent {
	
	int getInternalId();
	String getDescription();
	
	void programTimer(CountdownTimer timer);
	
	void onStarted(CountdownTimer timer);
	void onStopped(CountdownTimer timer);
	void setTargetSeconds(long targetSeconds);
	void reset();
	
}
