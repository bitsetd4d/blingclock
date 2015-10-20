package countdowntimer;

public class Features {

	private static Features INSTANCE = new Features();
	public static Features getInstance() { return INSTANCE; }
	
	public boolean isCheckForUpdatesSupported() { return true; }
	
	public boolean isPrefsEnabled() { return true; }
	public boolean isOnTopEnabled() { return true; }
	public boolean isMuteButtonEnabled() { return true; }
	public boolean isShowHideButtonEnabled() { return true; }
	
	public boolean isHoldDownPlusMinusEnabled() { return true; }
	public boolean fadePlayPauseButtonsWhenMouseStationary() { return true; }
	
	public boolean isMiniViewAvailable() { return true; }
	public boolean isFullViewAvailable() { return true; }
	
}
