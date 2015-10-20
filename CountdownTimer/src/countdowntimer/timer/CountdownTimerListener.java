package countdowntimer.timer;

public interface CountdownTimerListener {
	
	void onTick(int remainingDays,long remainingTenths,long hundredths);
	void onCountdownReachedZero();
	void onCountdownZeroRestartingLoop();
	void onCountdownReachedZeroEnteringOverrun();

}
