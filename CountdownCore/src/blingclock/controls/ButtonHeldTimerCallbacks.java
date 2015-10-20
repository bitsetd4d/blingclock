package blingclock.controls;

import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Timer;

public class ButtonHeldTimerCallbacks {
	
	private Timer buttonHoldTimer;
	
	private List<GlowButtonListener> listeners = new CopyOnWriteArrayList<GlowButtonListener>();
	
	public ButtonHeldTimerCallbacks() {
		initButtonHeldTimer();
	}
	
	public void addListener(GlowButtonListener listener) {
		listeners.add(listener);
	}
	public void removeListener(GlowButtonListener listener) {
		listeners.remove(listener);
	}
	
	private void initButtonHeldTimer() {
		buttonHoldTimer = new Timer(ButtonConstants.BUTTON_REPEAT_INTERVAL,new ActionListener() { public void actionPerformed(java.awt.event.ActionEvent e) {
			onButtonHeldTimerFired();
		}});
		buttonHoldTimer.setInitialDelay(ButtonConstants.DELAY_BEFORE_BUTTON_REPEAT);
	}
	
	public void onMousePressed() {
		firstCallFromHoldTimer = true;
		buttonHoldTimer.start();
		for (GlowButtonListener l : listeners) {
			l.onClicked();
		}
	}

	public void onMouseReleased() {
		buttonHoldTimer.stop();
		if (!firstCallFromHoldTimer) {
			for (GlowButtonListener l : listeners) {
				l.onStopHeldForPeriod();
			}
		}
	}

	private boolean firstCallFromHoldTimer = false;
	protected void onButtonHeldTimerFired() {
		if (firstCallFromHoldTimer) {
			for (GlowButtonListener l : listeners) {
				l.onStartHeldForPeriod();
			}
			firstCallFromHoldTimer = false;
		}
		for (GlowButtonListener l : listeners) {
			l.onHeldTick();
		}
	}
}
