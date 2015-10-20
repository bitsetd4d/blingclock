package countdowntimer.timer.internal;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;

import countdowntimer.preferences.Preferences;
import countdowntimer.preferences.PreferencesListener;
import countdowntimer.timer.CountdownEvent;
import countdowntimer.timer.CountdownTimer;
import countdowntimer.timer.CountdownTimerListener;
import countdowntimer.tweaks.TweakRegistry;

public class CountdownTimerImpl implements CountdownTimer { 
	
	private static final long HUNDREDS_24_HOURS = 24 * 60 * 60 * 100;
	private Timer timer = new Timer();
	private boolean running;
	private TimerTask timerTask;
	//private static final int TIME_FREQ = 100;
	
	private long timerBaseTime = 0;
	private long timerMillisBase; 

	private long countdownTimerHundreths;
	private long targetSeconds;
	private long baseStopwatchMs;
	
	private List<CountdownTimerListener> listeners = new CopyOnWriteArrayList<CountdownTimerListener>();
	private Object timerLock = new Object();	
	private Mode timerMode = Mode.COUNTDOWN;
	
	private CountdownEvent linkedEvent;
	private PreferencesListener prefsListener;
	
	private boolean suspendCallbacks;
	
	//private boolean loopMode; 
	private SubMode subMode = SubMode.STICKY; 
	
	public CountdownTimerImpl() {
		prefsListener = new PreferencesListener() { 
			public void onPreferencesChanged() {
				restartTimer();
			}
		};
		Preferences.getInstance().addListener(prefsListener);
	}

	public void setLinkedEvent(CountdownEvent linkedEvent) {
		this.linkedEvent = linkedEvent;
	}
	
	public int getDays() { 
		return (int)(countdownTimerHundreths / HUNDREDS_24_HOURS);
	}
	
	public void setSeconds(long seconds) {
		if (seconds >= 0) {
			this.countdownTimerHundreths = seconds * 100;
		} else {
			this.countdownTimerHundreths = 0;
		}
	}
	
	public void setSecondsTo(long t) {
		setSeconds(t);
		timerBaseTime = System.currentTimeMillis() - ((targetSeconds - t) * 1000);
	}
	
	public void setTenthsSeconds(long tenths) {
		if (tenths >= 0) {
			this.countdownTimerHundreths = tenths*10;
		} else {
			this.countdownTimerHundreths = 0;
		}
	}
	
	public long getSeconds() { 
		return countdownTimerHundreths / 100;
	}
	
	public long getHundreths() { 
		return countdownTimerHundreths;
	}
	
	public long getSecondsToday() {
		int days = (int) (countdownTimerHundreths / HUNDREDS_24_HOURS);
		long hundreths = (int) (countdownTimerHundreths - days * HUNDREDS_24_HOURS);
		return hundreths / 100;
	}
	
	public void setTargetSeconds(long seconds) {
		if (seconds >= 0) {
			this.targetSeconds = seconds;
		} else {
			this.targetSeconds = 0;
		}
		if (timerMode == Mode.STOPWATCH) {
			timerBaseTime = System.currentTimeMillis();
			baseStopwatchMs = seconds * 1000;
		}
		if (linkedEvent != null && timerMode != Mode.TIME) {
			linkedEvent.setTargetSeconds(targetSeconds);
		}
	}
	
	public long getTargetSeconds() { 
		return targetSeconds;
	}
	
	
	public void start() {
		synchronized (timerLock) {
			timerBaseTime = System.currentTimeMillis();
			timerMillisBase = countdownTimerHundreths * 10;
	     	if (timerTask == null) {
				startTimerTask();
	    		running = true;
	    	}
		}
		if (linkedEvent != null && timerMode != Mode.TIME) {
			linkedEvent.onStarted(this);
		}
	}	
	
	protected void restartTimer() {
		synchronized (timerLock) {
			if (timerTask != null) {
				timerTask.cancel();
				startTimerTask();
			}
			
		}
	}
	
	private void startTimerTask() {
		int interFrameDelay = TweakRegistry.getInstance().getInterFrameDelay();
		timerTask = new MyCountdownTimer();
		timer.schedule(timerTask,interFrameDelay,interFrameDelay);
	}
	
	public void stop() {
		synchronized (timerLock) {
	    	if (timerTask != null) {
	    		timerTask.cancel();
	    		timerTask = null;
	    		running = false;
	    		doTick();	
	    	}
		}
		if (linkedEvent != null && timerMode != Mode.TIME) {
			linkedEvent.onStopped(this);
		}
		baseStopwatchMs = countdownTimerHundreths * 10;
		if (timerMode == Mode.STOPWATCH && subMode == SubMode.OVERRUN) {
			timerMode = Mode.COUNTDOWN;
		}
	}
	
	public boolean isRunning() { return running; }
	
	private Random random = new Random();
	protected void countdownTicked() {
		long now = System.currentTimeMillis();
		long msSinceStarted = now - timerBaseTime;
		int decimalFrig = 0; // make counter look good
		if (running) {
			decimalFrig = random.nextInt(9); // Will get called with 0 when timer stopped
		}
		countdownTimerHundreths = decimalFrig + (timerMillisBase - msSinceStarted) / 10;
		adjustTimers();
		callbackListeners(true);
	}
	
	private void adjustTimers() {
		if (countdownTimerHundreths < 0) {
			countdownTimerHundreths = 0;
		}
		if (targetSeconds < 0) {
			targetSeconds = 0;
		}
	}
	
	private long lastCalledSeconds = 0;
	protected void timeTick() { 
		Date date = new Date();
		int secondsToday = (date.getHours() * 60 * 60) + (date.getMinutes() * 60) + date.getSeconds(); 
		countdownTimerHundreths = secondsToday * 100;
		if (secondsToday != lastCalledSeconds) {
			callbackListeners(false);
			lastCalledSeconds = secondsToday;
		}
	}
	
	protected void stopwatchTick() {
		long now = System.currentTimeMillis();
		long msSinceStarted = now - timerBaseTime;
		int decimalFrig = 0; // make counter look good
		if (running) {
			decimalFrig = random.nextInt(9); // Will get called with 0 when timer stopped
		}
		countdownTimerHundreths = (baseStopwatchMs +  msSinceStarted) / 10 + decimalFrig;
		adjustTimers();
		callbackListeners(false);
	}
	
	public void callbackListeners() {
		callbackListeners(false);
	}
	
	private void callbackListeners(boolean informZero) {
		if (suspendCallbacks) return;
		for (CountdownTimerListener listener : listeners) {
			try {
				int days = (int) (countdownTimerHundreths / HUNDREDS_24_HOURS);
				long hundredths = (countdownTimerHundreths - days * HUNDREDS_24_HOURS);
				long tenths = hundredths/10;
				listener.onTick(days,tenths,hundredths);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (informZero && countdownTimerHundreths == 0) {
			if (subMode == SubMode.LOOP) {
				informListenersZeroAndLoop();
			} else if (subMode == SubMode.OVERRUN) {
				informListenersZeroAndStopwatch();
			} else {
				informListenersZeroAndStop();
			}
		}
	}		
	
	private void informListenersZeroAndStop() { 
		for (CountdownTimerListener listener : listeners) {
			try {
				listener.onCountdownReachedZero();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		stop();
	}
	
	private void informListenersZeroAndLoop() {
		for (CountdownTimerListener listener : listeners) {
			try {
				listener.onCountdownZeroRestartingLoop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		countdownTimerHundreths = targetSeconds * 100;
		timerBaseTime = System.currentTimeMillis();
		timerMillisBase = countdownTimerHundreths * 10;
		countdownTimerHundreths -= 10;  // to adjust seconds eg. if timer is 60 we should show 59 99 (ish) not 60 (ish)
	}
	
	private void informListenersZeroAndStopwatch() {
		for (CountdownTimerListener listener : listeners) {
			try {
				listener.onCountdownReachedZeroEnteringOverrun();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		startStopwatchMode();
		subMode = SubMode.OVERRUN;
		start();
	}

	
	// --------------------------------------------
	// Listeners
	// --------------------------------------------
	public void addListener(CountdownTimerListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(CountdownTimerListener listener) {
		listeners.remove(listener);
	}

	// --------------------------------------------
	// Listeners
	// --------------------------------------------
	private class MyCountdownTimer extends TimerTask {
		@Override
		public void run() {
			try {
				SwingUtilities.invokeAndWait(new Runnable() { public void run() {
					doTick();
				}});
			} catch (Exception e) {
				e.printStackTrace();
			} 		
		}

	}
	
	private void doTick() {
		switch (timerMode) {
			case TIME: 
				timeTick();
				return;
			case STOPWATCH:
				stopwatchTick();
				return;
			case COUNTDOWN:
				countdownTicked();
		}
	}

	public void reduceTime(int t) {
		countdownTimerHundreths -= t * 100;
		timerBaseTime -= t * 1000;
		if (isRunning()) {
			targetSeconds -= t;
		} else {
			targetSeconds = countdownTimerHundreths / 100;
		}
		adjustTimers();
		if (targetSeconds < 0) targetSeconds = 0;
		if (linkedEvent != null) {
			linkedEvent.setTargetSeconds(targetSeconds);
		}
	}

	public void increaseTime(int t) {
		countdownTimerHundreths += t * 100;
		timerBaseTime += t * 1000;
		if (isRunning()) {
			targetSeconds += t;
		} else {
			targetSeconds = countdownTimerHundreths / 100;
		}
		adjustTimers();
		if (linkedEvent != null) {
			linkedEvent.setTargetSeconds(targetSeconds);
		}
	}

	public void resetTime() {
		if (timerMode == Mode.STOPWATCH) {
			baseStopwatchMs = 0;
			countdownTimerHundreths = 0;
			callbackListeners(false);
			return;
		}

		if (countdownTimerHundreths != targetSeconds * 100) {
			countdownTimerHundreths = targetSeconds * 100;
		} else {
			countdownTimerHundreths = 60 * 100;
			targetSeconds = 60;
		}
		if (linkedEvent != null) {
			linkedEvent.setTargetSeconds(targetSeconds);
			linkedEvent.reset();
		}
	}

	public long getCountdownTenths() { return countdownTimerHundreths / 10; }

	// ---------------------------------------------------
	// Time mode support
	// ---------------------------------------------------
	public boolean isTimeMode() {
		return timerMode == Mode.TIME;
	}
	public boolean isStopwatchMode() {
		return timerMode == Mode.STOPWATCH; 
	}
	
	public boolean isCountdownMode() {
		return timerMode == Mode.COUNTDOWN; 
	}
	
	public boolean isOverrunningNow() { 
		return timerMode == Mode.STOPWATCH && subMode == SubMode.OVERRUN;
	}
	

//	public void toggleTimeMode() {
//		setTimeMode(!timeMode);
//	}
	
	public void startCountdownMode() {
		if (timerMode != Mode.COUNTDOWN) {
			stopPreviousMode();
			timerMode = Mode.COUNTDOWN;
		}
	}

	public void startStopwatchMode() {
		if (timerMode != Mode.STOPWATCH) {
			stopPreviousMode();
			timerMode = Mode.STOPWATCH;
			subMode = SubMode.STOP;
			beginStopwatchMode();
		}
	}
	
	public void startTimeMode() {
		if (timerMode != Mode.TIME) {
			stopPreviousMode();
			timerMode = Mode.TIME;
			beginTimeMode();
		}
	}
	
	private void stopPreviousMode() { 
		switch (timerMode) {
			case TIME: 
				restoreFromTimeMode();
				return;
			case STOPWATCH:
				restoreFromStopwatchMode();
				return;
			case COUNTDOWN:
				// Nothing
		}
	}
	
//	public void setTimeMode(boolean mode) {
//		timeMode = mode;
//		if (timeMode) {
//			startTimeMode();
//		} else {
//			restoreFromTimeMode();
//		}
//	}
	
	private long previousHundreds = 0;
	private CountdownEvent savedLinkedEvent;
	private void beginTimeMode() {
		previousHundreds = countdownTimerHundreths;
		savedLinkedEvent = linkedEvent;
		linkedEvent = null;
		start();
	}
	
	private void restoreFromTimeMode() {
		stop();
		linkedEvent = savedLinkedEvent;
		savedLinkedEvent = null;
		if (linkedEvent != null) {
			linkedEvent.programTimer(this);
		} else {
			countdownTimerHundreths = previousHundreds;	
		}
		callbackListeners();
	}
	
	public void restoreFromStopwatchMode() {
		try {
			suspendCallbacks = true;
			restoreFromTimeMode();
		} finally {
			suspendCallbacks = false;
		}
		callbackListeners();
	}


	public CountdownEvent takeLinkedEvent() {
		CountdownEvent event = linkedEvent;
		linkedEvent = null;
		return event;
	}

	public void pushLinkedEvent(CountdownEvent event) {
		timerBaseTime = 0;
		timerMillisBase = 0; 
		countdownTimerHundreths = 0;
		targetSeconds = 0;
		if (event != null) {
			event.programTimer(this);
		}
	}
	
	public void copyStopwatch(CountdownTimer countdownTimer) {
		if (countdownTimer instanceof CountdownTimerImpl) {
			CountdownTimerImpl impl = (CountdownTimerImpl)countdownTimer;
			timerMode = Mode.STOPWATCH;
			timerBaseTime = impl.timerBaseTime;
			timerMillisBase = impl.timerMillisBase;
			baseStopwatchMs = impl.baseStopwatchMs;
		}
	}
	
	public void cloneTimer(CountdownTimer countdownTimer) {
		if (countdownTimer instanceof CountdownTimerImpl) {
			CountdownTimerImpl impl = (CountdownTimerImpl)countdownTimer;
			timerBaseTime = impl.timerBaseTime;
			timerMillisBase = impl.timerMillisBase;
			countdownTimerHundreths = impl.countdownTimerHundreths;
			targetSeconds = impl.targetSeconds;
			baseStopwatchMs = impl.baseStopwatchMs;
			subMode = impl.subMode;
			if (impl.running) {
	    		running = true;
	    		startTimerTask();
			}
		}
	}

	
	// ---------------------------------------------------
	// Loop mode
	// ---------------------------------------------------
//	public boolean isLoop() {
//		return subMode == SubMode.LOOP;
//	}
//
//	public void setLoopMode(boolean loopMode) {
//		this.subMode = loopMode ? SubMode.LOOP : SubMode.STOP;
//	}
	
	public void setSubMode(SubMode subMode) {
		this.subMode = subMode;
	}
	public SubMode getSubMode() {
		return subMode;
	}
	

//	public void toggleStopwatchMode() {
//		stopwatchMode = !stopwatchMode;
//		if (stopwatchMode) {
//			startStopwatchMode();
//		} else {
//			restoreFromStopwatchMode();
//		}
//	}
	
	
	public void beginStopwatchMode() { 
		timerMode = Mode.STOPWATCH;
		previousHundreds = countdownTimerHundreths;
		savedLinkedEvent = linkedEvent;
		linkedEvent = null;
		stop();
		resetTime();
	}



}
