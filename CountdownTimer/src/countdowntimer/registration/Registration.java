package countdowntimer.registration;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import countdowntimer.Branding;
import countdowntimer.preferences.Preferences;
import countdowntimer.update.BrowserLaunching;
import d3bug.licensing.Base64;
import d3bug.licensing.SimpleRegistration;
import d3bug.licensing.client.SignedStringLicenseValidator;

public class Registration {
	
	private List<RegistrationListener> listeners = new CopyOnWriteArrayList<RegistrationListener>();
	private SimpleRegistration simpleReg;
	
	private static final int TRIALS = 3;
	private static final int MAX_RUNS_FOR_TRIAL = 40;
	
	public void addListener(RegistrationListener l) { listeners.add(l); }
	public void removeListener(RegistrationListener l) { listeners.remove(l); }
	
	private boolean trialMode = false;
	
	private int runCount;
	private int trialsRemaining;

	private static Registration INSTANCE = new Registration();
	public static Registration getInstance() { return INSTANCE; }

	private boolean SIMULATE_UNREGISTERED = false;
	private boolean SIMULATE_TRIALMODE = false;
	private boolean SIMULATE_TRIALEXPIRED = false;
	private boolean SIMULATE_REGISTERED = true;

	private Registration() {
		if (SIMULATE_TRIALMODE || SIMULATE_UNREGISTERED || SIMULATE_TRIALEXPIRED) {
			System.out.println("WARNING - SIMULATION ACTIVE!!"); //$NON-NLS-1$
			System.out.println(" WARNING - SIMULATION ACTIVE!!"); //$NON-NLS-1$
			System.out.println("  WARNING - SIMULATION ACTIVE!!"); //$NON-NLS-1$
			System.out.println("   WARNING - SIMULATION ACTIVE!!"); //$NON-NLS-1$
			System.out.println("    WARNING - SIMULATION ACTIVE!!"); //$NON-NLS-1$
		}
		Preferences.getInstance().incrementRunCountThatDoesntReset();
		if (isRegistered()) return;
		long now = System.currentTimeMillis();
		final long EIGHT_DAYS = 8L * 24L * 60L * 60L * 1000L;
		long renabled = Preferences.getInstance().getTrialWasRenabledTimewarpCheck();
		if (renabled > now) { 
			trialMode = false;
			// TIMEWARP detection
			// Someone set the clock in the future and re-enabled trial
			// Then set clock back.
			// Lock until 6 days after THAT date
			Preferences.getInstance().setTrialLockedUntil(renabled + EIGHT_DAYS);
			return;
		}

		if (installedRecently()) { 
			trialsRemaining = TRIALS; 
		} else {
			runCount = Preferences.getInstance().getAndIncrementRunCount();
			trialsRemaining = TRIALS - runCount;
		}
		if (trialsRemaining < 0) trialsRemaining = 0;
		if (Preferences.getInstance().getRunCountThatDoesntReset() > MAX_RUNS_FOR_TRIAL) {
			// Stay locked because run count > max runs for trial
			long lock = EIGHT_DAYS + System.currentTimeMillis();
			Preferences.getInstance().setTrialLockedUntil(lock);
			trialMode = false;
			return;
		}
		if (trialsRemaining > 0) {
			trialMode = true;
		} else {
			// Trial just expired
			boolean haveShownTrialPage = Preferences.getInstance().haveShownTrialForVersion(Branding.getCurrentVersion());
			if (!haveShownTrialPage) {
				Preferences.getInstance().setHaveShownTrialForVersion(Branding.getCurrentVersion(),true);
				BrowserLaunching.openURL(Branding.getStoreUrlExpiredTrials());
			}
			// Run out of trials
			// Will allow again in 6 days
			long lockedUntil = Preferences.getInstance().getTrialLockedUntilDate();
			
			if (lockedUntil == 0) { // 1st time used up trials
				long lock = EIGHT_DAYS + System.currentTimeMillis();
				Preferences.getInstance().setTrialLockedUntil(lock);
			} else if (now > lockedUntil) {
				// If time has passed, unlock again
				Preferences.getInstance().setTrialLockedUntil(0);
				Preferences.getInstance().setTrialWasRenabledTimewarpCheck(now); // Remember when we unlocked
				Preferences.getInstance().resetRunCount();
				trialMode = true;
				trialsRemaining = TRIALS;
			}
		}
	}
	
	private boolean installedRecently() {
		if (getUsage() > 60) return false;
		List<String> okCodes = Branding.getNowCodesForDays(5);
		return okCodes.contains(Branding.getRunCodeWhenInstalled());
	}
	
	public int getUsage() {
		return Preferences.getInstance().getRunCountThatDoesntReset();
	}
	
	public boolean areAllTrialsRemaining() { return trialsRemaining == TRIALS; }
	public int getTrialRunsRemaining() { 
		return trialsRemaining; 
	}
	
	public int getDaysTillUnlockAgainApprox() {
		long lockedUntil = Preferences.getInstance().getTrialLockedUntilDate() - System.currentTimeMillis();
		if (lockedUntil <= 0) return 0;
		return 1 + (int)(lockedUntil / (24L * 60L * 60L * 1000L));
	}
	
	public boolean isValid(String licence) { 
		try {
			SignedStringLicenseValidator v = new SignedStringLicenseValidator(licence);
			SimpleRegistration r = new SimpleRegistration();
			r.restoreFrom(v);
			return r.isValid();
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	public boolean isRegistered() {
		if (SIMULATE_REGISTERED) {
			if (simpleReg == null) {
				simpleReg = new SimpleRegistration();
				simpleReg.setName("Blinglock User");
			}
			return true;
		}
		if (SIMULATE_UNREGISTERED) return false;
		if (simpleReg == null) {
			 simpleReg = new SimpleRegistration();
			 String wrappedCode = Preferences.getInstance().getLicenseCode();
			 String code = unwrap(wrappedCode);
			 SignedStringLicenseValidator v = new SignedStringLicenseValidator(code);
			 simpleReg.restoreFrom(v);
		}
		return simpleReg.isValid(); 
	}
	
	public boolean isTrialMode() {
		if (SIMULATE_TRIALMODE) return true;
		if (SIMULATE_TRIALEXPIRED) return false;
		return trialMode;
	}
	
	public boolean isNotRegisteredAndNotInTrialMode() {
		return !isRegistered() && !isTrialMode();
	}
	
	public boolean isTrialModeOrRegistered() {
		return isRegistered() || isTrialMode();
	}
	
	public String getRegisteredName() {
		if (isRegistered()) return simpleReg.getName();
		return Messages.getString("Registration.UNREGISTERED");  //$NON-NLS-1$
	}
	
	public String getRegisteredEmail() { 
		if (isRegistered()) return simpleReg.getEmail();
		return "";  //$NON-NLS-1$
	}
	
	public String getFeatureCodes() {
		if (isRegistered()) return simpleReg.getFeatureCodes();
		return ""; //$NON-NLS-1$
	}
	
	public String getSpecialMessage1() {
		if (isRegistered()) return simpleReg.getSpecial1();
		return "";		 //$NON-NLS-1$
	}
	public String getSpecialMessage2() {
		if (isRegistered()) return simpleReg.getSpecial2();
		return "";		 //$NON-NLS-1$
	}
	public void register(String licence) {
		String wrapped = wrap(licence);
		Preferences.getInstance().setLicenseCode(wrapped);
		simpleReg = null;
		for (RegistrationListener l : listeners) {
			l.onRegistrationStatusChanged();
		}
	}
	
	private static String wrap(String y) {
		String x = reverse(y);
		return Base64.encodeBytes(x.getBytes());
	}

	private static String unwrap(String y) {
		String d = new String(Base64.decode(y));
		return reverse(d);
	}
	
	private static String reverse(String x) {
		StringBuilder sb = new StringBuilder();
		for (int i=x.length()-1; i>=0; i--) {
			sb.append(x.charAt(i));
		}
		return sb.toString();
	}

}
