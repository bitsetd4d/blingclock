package blingclock.digits;

public abstract class DigitPrefs {
	
	private static DigitPrefs INSTANCE = new NullDigitPrefs();
	public static DigitPrefs getInstance() { return INSTANCE; }
	public static void setInstance(DigitPrefs prefs) { INSTANCE = prefs; }
	
	public abstract boolean getShow24Clock();
	public abstract boolean isRegistered();
	public abstract boolean getHideHourDigitsIfPossible();
	public abstract boolean isTrialMode();
		
	private static class NullDigitPrefs extends DigitPrefs {

		@Override
		public boolean getShow24Clock() {
			return false;
		}
		@Override
		public boolean getHideHourDigitsIfPossible() {
			return false;
		}
		@Override
		public boolean isRegistered() {
			return false;
		}
		@Override
		public boolean isTrialMode() {
			return false;
		}
		
	}
	
}
