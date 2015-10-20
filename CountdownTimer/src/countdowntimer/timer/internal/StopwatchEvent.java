package countdowntimer.timer.internal;

import countdowntimer.timer.CountdownEvent;
import countdowntimer.timer.CountdownTimer;

public class StopwatchEvent implements CountdownEvent {

	private long tenthsToGo;
	
	public StopwatchEvent(long tenthsToGo) {
		this.tenthsToGo = tenthsToGo;
	}
	
	public String getDescription() {
		return "Stopwatch"; //$NON-NLS-1$
	}

	public int getInternalId() {
		return 0;
	}

	public void onStarted(CountdownTimer timer) {}
	public void onStopped(CountdownTimer timer) {}

	public void programTimer(CountdownTimer timer) {
		timer.startStopwatchMode();
		timer.setTenthsSeconds(tenthsToGo);
	}

	public void reset() {}

	public void setTargetSeconds(long targetSeconds) {}

}
