package countdowntimer.timer;

import countdowntimer.timer.internal.SubMode;


public interface CountdownTimer {
	
	void start();
	void stop();
	boolean isRunning();

	void setTenthsSeconds(long toGo);
	void setSeconds(long secs);
	void setTargetSeconds(long secs);
	void setSecondsTo(long t);
	
	void increaseTime(int t);
	void reduceTime(int t);
	void resetTime();
	
	long getSeconds();
	long getSecondsToday();
	int getDays();
	long getTargetSeconds();
	long getCountdownTenths();
	long getHundreths();
	
	void addListener(CountdownTimerListener listener);
	void removeListener(CountdownTimerListener listener);
	void callbackListeners();
	
	boolean isTimeMode();
	boolean isStopwatchMode();
	boolean isCountdownMode();
	boolean isOverrunningNow();
	
//	void toggleTimeMode();
//	void toggleStopwatchMode();
	
	void startTimeMode();
	void startStopwatchMode();
	void startCountdownMode();
	
//	void setLoopMode(boolean loop);
//	boolean isLoop();
	
	void setSubMode(SubMode subMode);
	SubMode getSubMode();
	
	void setLinkedEvent(CountdownEvent event);
	CountdownEvent takeLinkedEvent();
	void pushLinkedEvent(CountdownEvent ev);
	void copyStopwatch(CountdownTimer countdownTimer);
	void cloneTimer(CountdownTimer countdownTimer);

	
}
