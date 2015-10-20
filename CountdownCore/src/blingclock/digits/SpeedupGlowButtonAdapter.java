package blingclock.digits;

import blingclock.controls.ButtonConstants;
import blingclock.controls.GlowButtonAdapter;

public class SpeedupGlowButtonAdapter extends GlowButtonAdapter {
	
	long speedUp;
	long rampUp = ButtonConstants.DELAY_BEFORE_SPEED_RAMPUP;
	
	@Override
	public void onStartHeldForPeriod() {
		speedUp = System.currentTimeMillis();
	}
	
	@Override
	public void onStopHeldForPeriod() {
		speedUp = 0;
	}
	
	public void onHeldTick() {
		onClicked();
		if (speedUp == 0) return;
		if (System.currentTimeMillis() > speedUp + rampUp) {
			onClicked();
		}
		if (System.currentTimeMillis() > speedUp + 2 * rampUp) {
			onClicked();
		}
		if (System.currentTimeMillis() > speedUp + 3 * rampUp) {
			onClicked();
		}
		if (System.currentTimeMillis() > speedUp + 4 * rampUp) {
			onClicked();
			onClicked();
			onClicked();
		}
	}

}
