package countdowntimer.preferences;

import java.awt.Rectangle;
import java.util.prefs.Preferences;

import countdowntimer.registration.Registration;
import countdowntimer.timer.internal.SubMode;

public class TimerPreferenceImpl implements TimerPreference {  
	
	private Preferences javaPrefs;
	private int instance;

	public TimerPreferenceImpl(Preferences javaPrefs, int instance) {
		this.javaPrefs = javaPrefs;
		this.instance = instance;
//		try {
//			javaPrefs.removeNode();
//		} catch (BackingStoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} // xxxxxxxxxx
	}
	
	public int getDisplayMode() { 
		if (isRegistered() || isTrial()) { 
			return javaPrefs.getInt("display-mode-"+instance, 5); //$NON-NLS-1$
		}
		return 1;
	}
	
	public void setDisplayMode(int mode) { 
		javaPrefs.putInt("display-mode-"+instance, mode); //$NON-NLS-1$
	}
	
	public boolean isFractionDigitsShown() {
		if (isRegistered() || isTrial()) {
			return javaPrefs.getBoolean("show-fraction-digits-"+instance, true);  //$NON-NLS-1$
		}
		return false;
	}
	
	public void setFractionDigitsShown(boolean on) { javaPrefs.putBoolean("show-fraction-digits-"+instance, on); } //$NON-NLS-1$

	public boolean isTimerLooped() {
		if (isRegistered() || isTrial()) {
			return javaPrefs.getBoolean("timer-looped", false);  //$NON-NLS-1$
		}
		return false;
	}
	public void setTimerLooped(boolean looped) { 
		javaPrefs.putBoolean("timer-looped-"+instance, looped);	 //$NON-NLS-1$
		countdowntimer.preferences.Preferences.getInstance().notifyPreferenceOrTweakChanged(); 
	}
	
	public SubMode getSubMode() {
		if (isRegistered() || isTrial()) { 
			String x = javaPrefs.get("submode-"+instance, SubMode.STOP.name());	 //$NON-NLS-1$
			try {
				return SubMode.valueOf(x);
			} catch (Exception e) {
				return SubMode.STOP;
			}
		}
		return SubMode.STOP;
	}

	public void setSubMode(SubMode subMode) {
		javaPrefs.put("submode-"+instance, subMode.name());	 //$NON-NLS-1$
		countdowntimer.preferences.Preferences.getInstance().notifyPreferenceOrTweakChanged(); 
	}
	
	public Rectangle getLastWindowPosition() {
		if (!javaPrefs.getBoolean("clock-position-"+instance,false)) { //$NON-NLS-1$
			return null;
		}
		int x = javaPrefs.getInt("clock-position-x-"+instance, 150); //$NON-NLS-1$
		int y = javaPrefs.getInt("clock-position-y-"+instance, 100); //$NON-NLS-1$
		int w = javaPrefs.getInt("clock-position-w-"+instance, 700); //$NON-NLS-1$
		int h = javaPrefs.getInt("clock-position-h-"+instance, 460); //$NON-NLS-1$
		
		if (w < 50) w = 50;
		if (h < 50) h = 50;
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		
		return new Rectangle(x,y,w,h);		
	}
	
	public void setLastPosition(Rectangle pos) {
		javaPrefs.putBoolean("clock-position-"+instance,true); //$NON-NLS-1$
		javaPrefs.putInt("clock-position-x-"+instance, pos.x); //$NON-NLS-1$
		javaPrefs.putInt("clock-position-y-"+instance, pos.y); //$NON-NLS-1$
		javaPrefs.putInt("clock-position-w-"+instance, pos.width); //$NON-NLS-1$
		javaPrefs.putInt("clock-position-h-"+instance, pos.height); //$NON-NLS-1$
	}
	
	public void setLastMiniPosition(Rectangle pos) {
		javaPrefs.putBoolean("mini-position-"+instance,true); //$NON-NLS-1$
		javaPrefs.putInt("mini-position-x-"+instance, pos.x); //$NON-NLS-1$
		javaPrefs.putInt("mini-position-y-"+instance, pos.y); //$NON-NLS-1$
		javaPrefs.putInt("mini-position-w-"+instance, pos.width); //$NON-NLS-1$
		javaPrefs.putInt("mini-position-h-"+instance, pos.height); //$NON-NLS-1$
	}
	
	public Rectangle getLastMiniWindowPosition() {
		if (!javaPrefs.getBoolean("mini-position-"+instance,false)) { //$NON-NLS-1$
			return null;
		}
		int x = javaPrefs.getInt("mini-position-x-"+instance, 100); //$NON-NLS-1$
		int y = javaPrefs.getInt("mini-position-y-"+instance, 100); //$NON-NLS-1$
		int w = javaPrefs.getInt("mini-position-w-"+instance, 200); //$NON-NLS-1$
		int h = javaPrefs.getInt("mini-position-h-"+instance, 20); //$NON-NLS-1$
		return new Rectangle(x,y,w,h);		
	}
	
	public double getRedRatio()   { return javaPrefs.getDouble("red-ratio-"+instance, 0.2); } //$NON-NLS-1$
	public double getAmberRatio() { return javaPrefs.getDouble("amber-ratio-"+instance, 0.5); } //$NON-NLS-1$
	
	public void setRedRatio(double ratio)   { javaPrefs.putDouble("red-ratio-"+instance, ratio); } //$NON-NLS-1$
	public void setAmberRatio(double ratio) { javaPrefs.putDouble("amber-ratio-"+instance, ratio); } //$NON-NLS-1$

	public String getWindowTitle() {
		if (isRegistered() || isTrial()) {
			return javaPrefs.get("window-title-"+instance, "Bling Clock"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return "Bling Clock"; //$NON-NLS-1$
	}
	
	public void setWindowTitle(String txt) {
		javaPrefs.put("window-title-"+instance, txt); //$NON-NLS-1$
		countdowntimer.preferences.Preferences.getInstance().notifyPreferenceOrTweakChanged();
	}
	
	public String getTimerTitle() {
		if (isRegistered() || isTrial()) {
			return javaPrefs.get("timer-title-"+instance, ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return ""; //$NON-NLS-1$
	}

	public void setTimerTitle(String txt) {
		javaPrefs.put("timer-title-"+instance, txt);	 //$NON-NLS-1$
		countdowntimer.preferences.Preferences.getInstance().notifyPreferenceOrTweakChanged(); 
	}
	
	public boolean isCompactDigitsLayout() { 
		//if (isRegistered() || isTrial()) {
			return javaPrefs.getBoolean("compact-digits-"+instance, true); //$NON-NLS-1$
		//}
		//return false;
	}
	
	public void setCompactDigitsLayout(boolean b) {
		javaPrefs.putBoolean("compact-digits-"+instance,b); //$NON-NLS-1$
	}
	
	public boolean isRemoveTitlebar() { 
		//if (isRegistered() || isTrial()) {
			return javaPrefs.getBoolean("remove-titlebar-"+instance, false); //$NON-NLS-1$
		//}
		//return false;
	}
	
	public void setRemoveTitlebar(boolean b) {
		javaPrefs.putBoolean("remove-titlebar-"+instance,b); //$NON-NLS-1$
	}
	
	public boolean isOnTop() { 
		return javaPrefs.getBoolean("on-top-"+instance, false);  //$NON-NLS-1$
	}
	public void setOnTop(boolean b) { 
		javaPrefs.putBoolean("on-top-"+instance, b);  //$NON-NLS-1$
	}

	
	private boolean isRegistered() { 
		return Registration.getInstance().isRegistered();
	}
	private boolean isTrial() { 
		return Registration.getInstance().isTrialMode();
	}


}
