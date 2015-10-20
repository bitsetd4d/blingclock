package blingclock.controls;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ButtonFadingController {
	
	private Object buttonLock = new Object();
	private Object stopLock = new Object();
	
	private List<ButtonFading> fadingInButtons = new ArrayList<ButtonFading>();
	private List<ButtonFading> fadingOutButtons = new ArrayList<ButtonFading>();
	private List<ButtonFading> doneButtons = new ArrayList<ButtonFading>();
	
	private Timer fadeTimer = new Timer("FadeTimer",true);
	private FadeTimerTask myTimerTask = new FadeTimerTask();
	
	private static ButtonFadingController INSTANCE = new ButtonFadingController();
	public static ButtonFadingController getInstance() { return INSTANCE; }
	
	public ButtonFadingController() {
		start();
	}

	public void start() { 
		fadeTimer.scheduleAtFixedRate(myTimerTask,100,100);
	}
	
	public void fadeIn(ButtonFading glowButton) {
		synchronized (buttonLock) {
			fadingInButtons.add(glowButton);
			fadingOutButtons.remove(glowButton);
		}
	}
	public void fadeOut(ButtonFading glowButton) {
		synchronized (buttonLock) {
			fadingOutButtons.add(glowButton);
			fadingInButtons.remove(glowButton);
		}
	}

	public void stop(ButtonFading glowButton) {
		synchronized (stopLock) {
			doneButtons.add(glowButton);
		}
	}
	
	public void onTimerFired() {
		synchronized (buttonLock) {
			for (ButtonFading b : fadingInButtons) {
				b.onFadeInTimerFired();
			}
			for (ButtonFading b : fadingOutButtons) {
				b.onFadeOutTimerFired();
			}
		}
		synchronized (stopLock) {
			fadingInButtons.removeAll(doneButtons);
			fadingOutButtons.removeAll(doneButtons);
			doneButtons.clear();
		}		
	}
	
	private class FadeTimerTask extends TimerTask {
		@Override
		public void run() {
			try {
				onTimerFired();
			} catch (Exception e) {
				// ignore
			}
		}
	}

}
