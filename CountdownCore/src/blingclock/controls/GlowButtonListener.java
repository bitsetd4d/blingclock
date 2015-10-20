package blingclock.controls;

public interface GlowButtonListener {

	void onClicked();
	void onStartHeldForPeriod();
	void onHeldTick();
	void onStopHeldForPeriod();

}
