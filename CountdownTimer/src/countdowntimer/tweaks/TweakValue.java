package countdowntimer.tweaks;

public enum TweakValue  {
	
	ENABLED(Messages.getString("TweakValue.ENABLED")), DISABLED(Messages.getString("TweakValue.DISABLED")), //$NON-NLS-1$ //$NON-NLS-2$
	
	FADE_ALL(Messages.getString("TweakValue.ALL")), FADE_INACTIVE(Messages.getString("TweakValue.INACTIVE")), FADE_NONE(Messages.getString("TweakValue.NONE")),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	COLOR_THEME_ALL(Messages.getString("TweakValue.ALL")), COLOR_THEME_DEFAULT(Messages.getString("TweakValue.DEFAULT")), COLOR_THEME_RED(Messages.getString("TweakValue.RED")), COLOR_THEME_GREEN(Messages.getString("TweakValue.GREEN")), COLOR_THEME_HIGHC(Messages.getString("TweakValue.HIGHC")), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	COLOR_THEME_BLUE(Messages.getString("TweakValue.BLUE")), COLOR_THEME_THEME1(Messages.getString("TweakValue.THEME1")), COLOR_THEME_THEME2(Messages.getString("TweakValue.THEME2")), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	PERF_NORMAL(Messages.getString("TweakValue.NORMAL")), PERF_LOW(Messages.getString("TweakValue.LOW")), PERF_MEDIUM(Messages.getString("TweakValue.MEDIUM")), PERF_HIGH(Messages.getString("TweakValue.HIGH")), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	
	DIGIT_SHOW(Messages.getString("TweakValue.SHOW")), DIGIT_HIDE(Messages.getString("TweakValue.HIDE")), //$NON-NLS-1$ //$NON-NLS-2$
	
	CLOSE_SMALL(Messages.getString("TweakValue.SMALL")), CLOSE_MEDIUM(Messages.getString("TweakValue.MEDIUM")), CLOSE_LARGE(Messages.getString("TweakValue.LARGE")), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	GAMMA_NORMAL(Messages.getString("TweakValue.NORMAL")), GAMMA_VHIGH(Messages.getString("TweakValue.V.HIGH")), GAMMA_HIGH(Messages.getString("TweakValue.HIGH")), GAMMA_MEDIUM(Messages.getString("TweakValue.MEDIUM")), GAMMA_LOW(Messages.getString("TweakValue.LOW")), GAMMA_VLOW(Messages.getString("TweakValue.V.LOW")),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	
	DIGIT_RATIO_FIXED(Messages.getString("TweakValue.FIXED")), DIGIT_RATIO_FREE(Messages.getString("TweakValue.FREE")); //$NON-NLS-1$ //$NON-NLS-2$
	
	private String displayName;
	
	TweakValue(String displayName) {
		this.displayName = displayName;
	}
	
	void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	
}
