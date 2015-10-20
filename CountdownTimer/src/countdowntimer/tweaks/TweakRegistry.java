package countdowntimer.tweaks;

import countdowntimer.Styling;
import countdowntimer.preferences.Preferences;
import countdowntimer.registration.Registration;

public class TweakRegistry {
	
	private static TweakRegistry INSTANCE = new TweakRegistry();
	public static TweakRegistry getInstance() { return INSTANCE; }
	
	public boolean isRegisteredOnly(String tweak) {
		if (tweak.equals(TweakConstants.COLOR_THEME)) return true;
		if (tweak.equals(TweakConstants.PERFORMANCE)) return true;
		//if (tweak.equals(TRANSPARENCY)) return true;
		if (tweak.equals(TweakConstants.DIGIT_TITLES)) return true;
		if (tweak.equals(TweakConstants.DIGIT_RATIO)) return true;
		return false; 
	}
	
	
	TweakValue getValue(String name) {
		TweakValue[] valid = getValidValues(name);
		if (Registration.getInstance().isNotRegisteredAndNotInTrialMode() && isRegisteredOnly(name)) {
			return valid[0];
		}
		TweakValue value = Preferences.getInstance().getTweakProperty(name,valid[0]);
		for (TweakValue validValue : valid) {
			if (value.equals(validValue)) return validValue;
		}
		return valid[0];
	}
	
	public TweakValue setToNextValidValue(String tweakName) {
		TweakValue now = getValue(tweakName);
		TweakValue[] all = getValidValues(tweakName);
		TweakValue newValue = all[0];
		for (int i=0; i<all.length; i++) {
			if (all[i].equals(now)) {
				int next = (i+1) % all.length;
				newValue = all[next];
				break;
			}
		}
		Preferences.getInstance().setTweakProperty(tweakName,newValue);
		if (tweakName.equals(TweakConstants.COLOR_THEME) || tweakName.equals(TweakConstants.GAMMA)){
			applyThemeToGui();
		} else if (tweakName.equals(TweakConstants.PERFORMANCE) || tweakName.equals(TweakConstants.DIGIT_TITLES) || tweakName.equals(TweakConstants.CLOSE_BUTTONS) ||  tweakName.equals(TweakConstants.DIGIT_RATIO) || tweakName.equals(TweakConstants.LAYOUT_TITLES)) {
			Preferences.getInstance().notifyPreferenceOrTweakChanged();
		} else if (tweakName.equals(TweakConstants.DIGIT_RATIO)) {
			Preferences.getInstance().notifyPreferenceOrTweakChanged();
		}
		return newValue;
	}

	private void applyThemeToGui() {
		Styling.hotApplyTheme(getValue(TweakConstants.COLOR_THEME),getGamma());
	}

	public int getInterFrameDelay() {
		TweakValue v = getValue(TweakConstants.PERFORMANCE);
		if (v == TweakValue.PERF_LOW) return 100;
		if (v == TweakValue.PERF_NORMAL) return 75;
		if (v == TweakValue.PERF_MEDIUM) return 40;
		if (v == TweakValue.PERF_HIGH) return 20;
		return 75;
	}
	
	//public float getGamma() { return 1f; }
	public float getGamma() { 
		TweakValue v = getValue(TweakConstants.GAMMA);
		if (v == TweakValue.GAMMA_VHIGH) return 2f;
		if (v == TweakValue.GAMMA_HIGH) return 1.5f;
		if (v == TweakValue.GAMMA_MEDIUM) return 0.8f;
		if (v == TweakValue.GAMMA_LOW) return 0.5f;
		if (v == TweakValue.GAMMA_VLOW) return 0f;
		return 1f; 
	}
	
	public int[] getCloseButtonMetrics() {
		TweakValue size = TweakRegistry.getInstance().getValue(TweakConstants.CLOSE_BUTTONS);
		int w = 10;
		int g = 1;
		if (size == TweakValue.CLOSE_MEDIUM) { 
			w = 15;
			g = 2;
		} else if (size == TweakValue.CLOSE_LARGE) { 
			w = 20;
			g = 3;
		}
		return new int[] { w, g };
	}
		
	private TweakValue[] getValidValues(String name) {
		if (name == TweakConstants.HOVER_HELP) return new TweakValue[] {  TweakValue.ENABLED, TweakValue.DISABLED };
		if (name == TweakConstants.LAYOUT_TITLES) return new TweakValue[] {  TweakValue.ENABLED, TweakValue.DISABLED };
		if (name == TweakConstants.FADE_BUTTONS) return new TweakValue[] { TweakValue.FADE_ALL, TweakValue.FADE_INACTIVE, TweakValue.FADE_NONE };
		if (name == TweakConstants.COLOR_THEME) return new TweakValue[] { 
				TweakValue.COLOR_THEME_DEFAULT, 
				TweakValue.COLOR_THEME_RED, 
				TweakValue.COLOR_THEME_GREEN, 
				TweakValue.COLOR_THEME_BLUE, 
				TweakValue.COLOR_THEME_THEME1, 
				TweakValue.COLOR_THEME_THEME2,
				TweakValue.COLOR_THEME_HIGHC
		}; 
		if (name == TweakConstants.PERFORMANCE) return new TweakValue[] { TweakValue.PERF_NORMAL, TweakValue.PERF_LOW, TweakValue.PERF_MEDIUM, TweakValue.PERF_HIGH }; 
		if (name == TweakConstants.DIGIT_TITLES) return new TweakValue[] { TweakValue.DIGIT_SHOW, TweakValue.DIGIT_HIDE };
		if (name == TweakConstants.CLOSE_BUTTONS) return new TweakValue[] { TweakValue.CLOSE_SMALL, TweakValue.CLOSE_MEDIUM, TweakValue.CLOSE_LARGE };
		if (name == TweakConstants.GAMMA) return new TweakValue[] {
				TweakValue.GAMMA_NORMAL,
				TweakValue.GAMMA_VHIGH,
				TweakValue.GAMMA_HIGH,
				TweakValue.GAMMA_MEDIUM,
				TweakValue.GAMMA_LOW,
				TweakValue.GAMMA_VLOW
		};
		if (name == TweakConstants.DIGIT_RATIO) return new TweakValue[] { TweakValue.DIGIT_RATIO_FIXED, TweakValue.DIGIT_RATIO_FREE };
		return new TweakValue[] {  TweakValue.ENABLED, TweakValue.DISABLED };
	}

	public String getTweakTitle(String tweakName) {
		if (tweakName == TweakConstants.HOVER_HELP) return Messages.getString("TweakRegistry.Title_HoverHelp"); //$NON-NLS-1$
		if (tweakName == TweakConstants.LAYOUT_TITLES) return Messages.getString("TweakRegistry.Title_LayoutTitles"); //$NON-NLS-1$
		if (tweakName == TweakConstants.FADE_BUTTONS) return Messages.getString("TweakRegistry.Title_FadeButtons"); //$NON-NLS-1$
		if (tweakName == TweakConstants.COLOR_THEME) return Messages.getString("TweakRegistry.Title_ColorTheme"); //$NON-NLS-1$
		if (tweakName == TweakConstants.PERFORMANCE) return Messages.getString("TweakRegistry.Title_RefreshRate"); //$NON-NLS-1$
		if (tweakName == TweakConstants.DIGIT_TITLES) return Messages.getString("TweakRegistry.Title_DigitHeadings"); //$NON-NLS-1$
		if (tweakName == TweakConstants.CLOSE_BUTTONS) return Messages.getString("TweakRegistry.Title_CloseButtons"); //$NON-NLS-1$
//		if (tweakName == TRANSPARENCY) return "Transparency";
		if (tweakName == TweakConstants.GAMMA) return Messages.getString("TweakRegistry.Title_DigitGamma"); //$NON-NLS-1$
		if (tweakName == TweakConstants.DIGIT_RATIO) return Messages.getString("TweakRegistry.Title_DigitRatio"); //$NON-NLS-1$
		return tweakName;
	}

	public void setGuruTweak(String tweakName,boolean enabled) {
		Preferences.getInstance().setGuruTweakProperty(tweakName,enabled);
		Preferences.getInstance().notifyPreferenceOrTweakChanged();
	}
	
	public boolean isGuruTweakOn(String tweakName) {
		return Preferences.getInstance().getGuruTweakProperty(tweakName);
	}

	public boolean isHoverHelpEnabled() {
		return getValue(TweakConstants.HOVER_HELP) == TweakValue.ENABLED;
	}

	public boolean isShowDigitTitles() {
		return getValue(TweakConstants.DIGIT_TITLES) == TweakValue.DIGIT_SHOW;
	}

	public boolean isDigitRatioFree() {
		return getValue(TweakConstants.DIGIT_RATIO) == TweakValue.DIGIT_RATIO_FREE;
	}
	
	public boolean isFadeButtonsDisabled() {
		return getValue(TweakConstants.FADE_BUTTONS) == TweakValue.FADE_NONE;
	}
	
	public boolean isFadeAllEnabled() {
		return getValue(TweakConstants.FADE_BUTTONS) == TweakValue.FADE_ALL;
	}
	
	public boolean isLayoutTitlesEnabled() { 
		return getValue(TweakConstants.LAYOUT_TITLES) == TweakValue.ENABLED;
	}

	public TweakValue getColorTheme() {
		return getValue(TweakConstants.COLOR_THEME);
	}

}
