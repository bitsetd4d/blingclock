package countdowntimer.preferences;

import java.awt.Rectangle;

import countdowntimer.timer.internal.SubMode;

public interface TimerPreference {
	
	int getDisplayMode();
	void setDisplayMode(int mode);

	boolean isFractionDigitsShown();
	void setFractionDigitsShown(boolean on);

//	boolean isTimerLooped();
//	void setTimerLooped(boolean looped);
	
	SubMode getSubMode();
	void setSubMode(SubMode subMode);

	Rectangle getLastWindowPosition();
	void setLastPosition(Rectangle pos);

	void setLastMiniPosition(Rectangle pos);
	Rectangle getLastMiniWindowPosition();

	double getRedRatio();
	void setRedRatio(double ratio);
	
	double getAmberRatio();
	void setAmberRatio(double ratio);

	String getWindowTitle();
	void setWindowTitle(String txt);

	String getTimerTitle();
	void setTimerTitle(String txt);

	boolean isCompactDigitsLayout();
	void setCompactDigitsLayout(boolean b);

	boolean isRemoveTitlebar();
	void setRemoveTitlebar(boolean b);
	
	public boolean isOnTop();
	public void setOnTop(boolean b);

}
