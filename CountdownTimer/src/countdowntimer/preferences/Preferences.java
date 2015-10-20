package countdowntimer.preferences;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.prefs.BackingStoreException;

import countdowntimer.FullScreenUtil;
import countdowntimer.registration.Registration;
import countdowntimer.tweaks.TweakValue;

public class Preferences {

	private static Preferences INSTANCE = new Preferences();
	public static Preferences getInstance() { return INSTANCE; }
	
	//private List<PreferencesListener> listeners = new CopyOnWriteArrayList<PreferencesListener>();
	private List<WeakReference<PreferencesListener>> listeners = new CopyOnWriteArrayList<WeakReference<PreferencesListener>>();
	private java.util.prefs.Preferences javaPrefs = java.util.prefs.Preferences.userNodeForPackage(Preferences.class);
	
	public void addListener(PreferencesListener l) { 
		WeakReference<PreferencesListener> wr = new WeakReference<PreferencesListener>(l);
		listeners.add(wr); 
	}
	
	public void notifyPreferenceOrTweakChanged() { 
		for (WeakReference<PreferencesListener> l : listeners) {
			PreferencesListener pl = l.get();
			if (pl != null) {
				pl.onPreferencesChanged(); 
			}
		} 
	}
	
	//public int getDelayBeforeButtonRepeat() { return 700; }
	//public int getButtonRepeatInterval() { return 50; }
	//public long getDelayBeforeSpeedRampup() { return 4000; }

//	public int getFadeInTimerInterval() { return 30; }
//	public int getFadeOutTimerInterval() { return 60; }
	//public float getFadeTransparencyStep() { return 0.05f; }
	//public float getFadeTransparencyStop() { return 0.1f; }
	
	public float getButtonPlayPauseDisabledButtonFade() { return 0.5f; }
	
	public boolean getPreserveDigitsRatio() { return true; }
	
	private static final String trialKeyName = "clock-position-w-2"; //$NON-NLS-1$
	private static final String trialRenableKeyName = "throw-color-fg4"; //$NON-NLS-1$
	private static final String fakeRunKey = "clock-position-v-1"; //$NON-NLS-1$
	
	public int getAndIncrementRunCount() {
		try {
			int x = javaPrefs.getInt(fakeRunKey, 0);
			javaPrefs.putInt(fakeRunKey, x+1);
			flush();
			return x;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 99; 
	}
	
	public long getInstallDate() { 
		try {
			long t = javaPrefs.getLong("date-installed", 0); //$NON-NLS-1$
			if (t == 0) {
				t = System.currentTimeMillis();
				javaPrefs.putLong("date-installed", t); //$NON-NLS-1$
				flush();
			}
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0; 
	}
	
	public void incrementRunCountThatDoesntReset() {
		try {
			int x = javaPrefs.getInt("run-count", 0); //$NON-NLS-1$
			javaPrefs.putInt("run-count", x+1); //$NON-NLS-1$
			flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getRunCountThatDoesntReset() {
		return javaPrefs.getInt("run-count", -1); //$NON-NLS-1$
	}

	
	public void resetRunCount() {
		javaPrefs.remove(fakeRunKey);
	}
	
	
	public int getTimeUnit() { return javaPrefs.getInt("alarm-time-unit",60); } //$NON-NLS-1$
	public void setTimeUnit(int amount) { javaPrefs.putInt("alarm-time-unit",amount); } //$NON-NLS-1$

	public boolean getWindowToFrontOnAlarm() { 
		if (isRegistered()) {
			return javaPrefs.getBoolean("restore-on-alarm", true); //$NON-NLS-1$
		}
		return false;
	}
	public void setWindowToFrontOnAlarm(boolean b) { 
		javaPrefs.putBoolean("restore-on-alarm", b);  //$NON-NLS-1$
		notifyPreferenceOrTweakChanged();
	}
	
	public TimerPreference getTimerPreference(int instance) {
		return new TimerPreferenceImpl(javaPrefs,instance);
	}

	
	public boolean isAlarmSoundMuted() 		   { return javaPrefs.getBoolean("alarm-sound-muted", false); } //$NON-NLS-1$
	public void setAlarmSoundMuted(boolean on) { javaPrefs.putBoolean("alarm-sound-muted", on);	notifyPreferenceOrTweakChanged(); } //$NON-NLS-1$
		
	public boolean getHideHourDigitsIfPossible() 		{ return javaPrefs.getBoolean("hide-hour-if-poss", true); } //$NON-NLS-1$
	public void setHideHourDigitsIfPossible(boolean b)  { javaPrefs.putBoolean("hide-hour-if-poss", b);	notifyPreferenceOrTweakChanged(); } //$NON-NLS-1$
	
	public boolean getRememberPositionOnScreen() {
		return javaPrefs.getBoolean("clock-remember-position",true);  //$NON-NLS-1$
	}
	public void setRememberPositionOnScreen(boolean b) { javaPrefs.putBoolean("clock-remember-position",b); notifyPreferenceOrTweakChanged(); } //$NON-NLS-1$

	
	public String getMiniMode() { 
		return javaPrefs.get("mini-mode","PROPORTIONAL"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void setMiniMode(String x) {
		javaPrefs.put("mini-mode",x); //$NON-NLS-1$
	}


//	public boolean getShowMessagebarSecondsRemaining() { return getRegisteredProperty("messagebar-seconds", false); }
//	public void setShowMessagebarSecondsRemaining(boolean b) {  javaPrefs.putBoolean("messagebar-seconds",b); notifyPreferenceOrTweakChanged(); }
//	
//	public boolean getShowMessagebarTimeRemaining() { return getRegisteredProperty("messagebar-time", false); }
//	public void setShowMessagebarTimeRemaining(boolean b) {  javaPrefs.putBoolean("messagebar-time",b); notifyPreferenceOrTweakChanged(); }
	
	public boolean getIncludeTimeInTitle() {
		if (isRegistered() || isTrial()) {
			return javaPrefs.getBoolean("time-in-title", false);  //$NON-NLS-1$
		}
		return true;
	}
	
	public void setIncludeTimeInTitle(boolean b) {
		javaPrefs.putBoolean("time-in-title",b); notifyPreferenceOrTweakChanged(); //$NON-NLS-1$
	}
	
	private boolean getRegisteredProperty(String name,boolean def) {
		if (isRegistered()) {
			return javaPrefs.getBoolean(name, def);
		}
		return def;		
	}
	
	private static String DEFAULT = "Warning+Alarm.wav"; //$NON-NLS-1$
	public String getSoundFile() {
		if (isRegistered()) {
			return javaPrefs.get("alarm-sound", DEFAULT); //$NON-NLS-1$
		}
		return DEFAULT;
		 
	}
	public void setSoundFile(String sound) { javaPrefs.put("alarm-sound",sound); } //$NON-NLS-1$

	public void setSoundRepeat(int repeat) {
		javaPrefs.putInt("alarm-sound-repeat", repeat);  //$NON-NLS-1$
	}
	public void setSoundDelay(int repeatDelay) {
		javaPrefs.putInt("alarm-sound-delay", repeatDelay);  //$NON-NLS-1$
	}
	
	public int getSoundRepeat() { 
		if (isRegistered()) {
			return javaPrefs.getInt("alarm-sound-repeat", 1); //$NON-NLS-1$
		}
		return 1;
	}
	public int getSoundDelay() {
		if (isRegistered()) {
			return javaPrefs.getInt("alarm-sound-delay", 1000); //$NON-NLS-1$
		}
		return 1000;
	}
	
	private boolean isRegistered() { 
		return Registration.getInstance().isRegistered();
	}
	private boolean isTrial() { 
		return Registration.getInstance().isTrialMode();
	}
	
	public void resetToDefault() {
		javaPrefs.remove("restore-on-alarm"); //$NON-NLS-1$
		javaPrefs.remove("alarm-sound-muted"); //$NON-NLS-1$
		javaPrefs.remove("clock-remember-position"); //$NON-NLS-1$
		javaPrefs.remove("clock-position-1"); //$NON-NLS-1$
		javaPrefs.remove("clock-position-x-1"); //$NON-NLS-1$
		javaPrefs.remove("clock-position-y-1"); //$NON-NLS-1$
		javaPrefs.remove("clock-position-w-1"); //$NON-NLS-1$
		javaPrefs.remove("clock-position-h-1"); //$NON-NLS-1$
		javaPrefs.remove("mini-position-1"); //$NON-NLS-1$
		javaPrefs.remove("mini-position-x-1"); //$NON-NLS-1$
		javaPrefs.remove("mini-position-y-1"); //$NON-NLS-1$
		javaPrefs.remove("mini-position-w-1"); //$NON-NLS-1$
		javaPrefs.remove("mini-position-h-1"); //$NON-NLS-1$
		javaPrefs.remove("messagebar-seconds"); //$NON-NLS-1$
		javaPrefs.remove("messagebar-time"); //$NON-NLS-1$
		javaPrefs.remove("alarm-sound"); //$NON-NLS-1$
		javaPrefs.remove("click-timer-panel"); //$NON-NLS-1$
		javaPrefs.remove("alarm-time-unit"); //$NON-NLS-1$
		javaPrefs.remove("timer-looped"); //$NON-NLS-1$
		javaPrefs.remove("display-mode"); //$NON-NLS-1$
		javaPrefs.remove("currentTheme"); //$NON-NLS-1$
		javaPrefs.remove("red-ratio"); //$NON-NLS-1$
		javaPrefs.remove("amber-ratio"); //$NON-NLS-1$
		javaPrefs.remove("alarm-sound-repeat"); //$NON-NLS-1$
		javaPrefs.remove("alarm-sound-delay"); //$NON-NLS-1$
		javaPrefs.remove("mini-mode"); //$NON-NLS-1$
		javaPrefs.remove("window-title"); //$NON-NLS-1$
		javaPrefs.remove("time-in-title"); //$NON-NLS-1$
		javaPrefs.remove("set-time-keyboard"); //$NON-NLS-1$
		javaPrefs.remove("inter-frame-delay"); //$NON-NLS-1$
		javaPrefs.remove("hide-mouse"); //$NON-NLS-1$
		javaPrefs.remove("timer-title"); //$NON-NLS-1$
		javaPrefs.remove("hide-hour-if-poss"); //$NON-NLS-1$
		javaPrefs.remove("remove-titlebar"); //$NON-NLS-1$
		javaPrefs.remove("compact-digits"); //$NON-NLS-1$
		notifyPreferenceOrTweakChanged();
	}
		
//	public int getInterFrameDelay() {
//		if (isRegistered()) {
//			return javaPrefs.getInt("inter-frame-delay", 75);
//		}
//		return 75;
//	}
//	
//	public void setInterFrameDelay(int frameDelay)  { 
//		javaPrefs.putInt("inter-frame-delay", frameDelay); 
//		notifyPreferenceOrTweakChanged();
//	}
	
	public boolean isHideMouseEnabled() { return javaPrefs.getBoolean("hide-mouse", true); } //$NON-NLS-1$
	public void setHideMouseEnabled(boolean b) { javaPrefs.putBoolean("hide-mouse", b); } //$NON-NLS-1$
	
	public String getUpdateOption() {
		return javaPrefs.get("update-check", "WEEKLY"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void setUpdateOption(String option) {
		javaPrefs.put("update-check", option);		 //$NON-NLS-1$
	}
	
	public boolean shouldCheckForNewVersion() {
		String update = getUpdateOption();
		if (update.equals("NONE")) return false; //$NON-NLS-1$
		long lastChecked = javaPrefs.getLong("update-last-checked", 0); //$NON-NLS-1$
		long AGO = 24 * 60 * 1000;
		if (update.equals("WEEKLY")) { //$NON-NLS-1$
			AGO = 7 * AGO;
		}
		long now = System.currentTimeMillis();
		if (lastChecked + AGO < now) {
			javaPrefs.putLong("update-last-checked", now); //$NON-NLS-1$
			return true;
		}
		return false;
	}
	
	public String getLicenseCode() { 
		return javaPrefs.get("regcode", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
	public void setLicenseCode(String code) { 
		javaPrefs.put("regcode", code); //$NON-NLS-1$
		flush();
	}
	
	public boolean isAllowClickGridToSetTime() {
		if (isRegistered()) {
			return true;
//			return getRegisteredProperty("click-timer-panel", true); //$NON-NLS-1$
		}
		if (isTrial()) return true;
		return false;
	}
	public void setAllowClickGridToSetTime(boolean b) {
		javaPrefs.putBoolean("click-timer-panel",b);  //$NON-NLS-1$
		notifyPreferenceOrTweakChanged(); 
	}
	
	public boolean isAllowSetTimeWithKeyboard() {
		if (isRegistered()) {
			return getRegisteredProperty("set-time-keyboard", true); //$NON-NLS-1$
		}
		if (isTrial()) return true;
		return false;
	}

	public void setAllowSetTimeWithKeyboard(boolean b) {
		javaPrefs.putBoolean("set-time-keyboard",b);  //$NON-NLS-1$
	}
	
	
//	public String getCurrentTheme() {
//		if (isRegistered() || isTrial()) {
//			return javaPrefs.get("currentTheme","default");
//		}
//		return "default";
//	}
//	public void setCurrentTheme(String option) {
//		javaPrefs.put("currentTheme",option);
//	}
//	
	public long getTrialLockedUntilDate() {
		long t = javaPrefs.getLong(trialKeyName, 0);
		return t;
		// Don't call key trial-locked-until to make slightly less obvious
	}
	public void setTrialLockedUntil(long ts) {
		javaPrefs.putLong(trialKeyName, ts);
		flush();
	}
	
	public void setTrialWasRenabledTimewarpCheck(long now) { 
		javaPrefs.putLong(trialRenableKeyName,now); 
		flush();
	}
	public long getTrialWasRenabledTimewarpCheck() { 
		return javaPrefs.getLong(trialRenableKeyName, 0); 
	}
	

	public int getNextFreePrefId() {
		int id=1;
		while (true) {
			long t = javaPrefs.getLong("t.totalSecondsToCount."+id,-999); //$NON-NLS-1$
			if (t == -999) return id;
			id++;
		}
	}

	
	public TickingPref getTickingPref(int id) {
		TickingPref p = new TickingPref();
		p.id = id;
		p.description = ""; //$NON-NLS-1$
		p.totalSecondsToCount = javaPrefs.getLong("t.totalSecondsToCount."+id, 60); //$NON-NLS-1$
		p.latestStartedTime = javaPrefs.getLong("t.latestStartedTime."+id, 0); //$NON-NLS-1$
		p.tenthsUsedAlready = javaPrefs.getLong("t.tenthsUsedAlready."+id, 0); //$NON-NLS-1$
		p.ticking = javaPrefs.getBoolean("t.ticking."+id, false); //$NON-NLS-1$
		System.out.println("PREF: <-read- Reading timer : "+id+", ticking="+p.ticking); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("PREF: <-read-  Latest Start : "+new Date(p.latestStartedTime)); //$NON-NLS-1$
		System.out.println("PREF: <-read--   10ths used : "+p.tenthsUsedAlready+", secs to count: "+p.totalSecondsToCount); //$NON-NLS-1$ //$NON-NLS-2$
		return p;
	}
	
	public void storeTickingPref(TickingPref pref) {
		int id = pref.id;
		javaPrefs.putLong("t.totalSecondsToCount."+id, pref.totalSecondsToCount); //$NON-NLS-1$
		javaPrefs.putLong("t.latestStartedTime."+id, pref.latestStartedTime); //$NON-NLS-1$
		javaPrefs.putLong("t.tenthsUsedAlready."+id, pref.tenthsUsedAlready); //$NON-NLS-1$
		javaPrefs.putBoolean("t.ticking."+id, pref.ticking); //$NON-NLS-1$
		asyncFlush();
		System.out.println("PREF: -store-> Storing timer : "+id+", ticking="+pref.ticking); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("PREF: -store->  Latest Start : "+new Date(pref.latestStartedTime)); //$NON-NLS-1$
		System.out.println("PREF: -store->    10ths used : "+pref.tenthsUsedAlready+", secs to count: "+pref.totalSecondsToCount); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private Object flushLock = new Object();
	private volatile boolean flushing = true;
	private void asyncFlush() {
		synchronized (flushLock) {
			if (flushing) return;
			flushing = true;			
		}
		new Thread() { public void run() {
			synchronized (flushLock) {
				flushing = false;
			}
			try {
				javaPrefs.flush();
			} catch (BackingStoreException e) {}		
		}}.start();
	}
	
	public void flush() { 
		try {
			javaPrefs.flush();
		} catch (BackingStoreException e) {}		
	}

	public boolean haveShownTrialForVersion(int currentVersion) {
		return javaPrefs.getBoolean("offered-buy-page-"+currentVersion,false);  //$NON-NLS-1$
	}

	public void setHaveShownTrialForVersion(int currentVersion, boolean haveShown) {
		javaPrefs.putBoolean("offered-buy-page-"+currentVersion,haveShown);  //$NON-NLS-1$
	}

	public TweakValue getTweakProperty(String name, TweakValue defaultValue) {
		String v = javaPrefs.get("tweak-"+name,null); //$NON-NLS-1$
		if (v == null) return defaultValue;
		try {
			TweakValue tv = TweakValue.valueOf(v);
			return tv;
		} catch (Exception e) {}
		return defaultValue;
	}
	
	public void setTweakProperty(String name, TweakValue value) {
		javaPrefs.put("tweak-"+name,value.name()); //$NON-NLS-1$
	}
	
	public void setGuruTweakProperty(String key, boolean value) {
		javaPrefs.put("guru-"+key, value ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public boolean getGuruTweakProperty(String key) {
		return "true".equals(javaPrefs.get("guru-"+key, "false")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public String getStartupDisplay() {
		return javaPrefs.get("startup-display","COUNTDOWN"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	public void setStartupDisplay(String value) {
		javaPrefs.put("startup-display",value); //$NON-NLS-1$
	}

	public boolean getShow24Clock() {
		return javaPrefs.getBoolean("clock-24h",false); //$NON-NLS-1$
	}
	public void setShow24Clock(boolean value) {
		javaPrefs.putBoolean("clock-24h",value);		 //$NON-NLS-1$
	}

	public boolean getBlinkSeparatorsInTimeMode() {
		return javaPrefs.getBoolean("blink-time-mode", true); //$NON-NLS-1$
	}

	public void setBlinkSeparatorsInTimeMode(boolean value) {
		javaPrefs.putBoolean("blink-time-mode", value); //$NON-NLS-1$
		notifyPreferenceOrTweakChanged();
	}

	public boolean getUseFullScreenAPI() {
		return javaPrefs.getBoolean("full-screen-api", FullScreenUtil.isOSX()); //$NON-NLS-1$
	}

	public void setUseFullScreenAPI(boolean b) {
		javaPrefs.putBoolean("full-screen-api", b); //$NON-NLS-1$
	}

	public boolean getFlashDigitsIfOverrunning() {
		return javaPrefs.getBoolean("flash-on-overrun", true); //$NON-NLS-1$
	}

	public void setFlashDigitsIfOverrunning(boolean b) {
		javaPrefs.putBoolean("flash-on-overrun", b); //$NON-NLS-1$
	}

	public void setCustomSoundName(String name) {
		javaPrefs.put("custom-sound-name", name);
	}
	public String getCustomSoundFileName() {
		return javaPrefs.get("custom-sound-name",null);
	}
	
	public void setStoredSoundName(String name) {
		javaPrefs.put("custom-sound-filename", name);
	}
	public String getStoredSoundFileName() {
		return javaPrefs.get("custom-sound-filename",null);
	}

	
}
