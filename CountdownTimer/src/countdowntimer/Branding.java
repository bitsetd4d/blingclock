package countdowntimer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import countdowntimer.preferences.Preferences;
import countdowntimer.registration.Registration;


public class Branding {
	
	private static long MILLIS_PER_DAY = 24L * 60 * 60 * 1000;
	
	private static String localeDir = "";
	
	public static int getCurrentVersion() { return 224; } 
	public static String getSpecialMessageUnregistered() { return ""; } //$NON-NLS-1$
	public static String getSpecialMessageRegistered() { return ""; } //$NON-NLS-1$
	
	public static String getStoreUrl() { return "http://www.blingclock.com/"+localeDir+"buy_license.html"; } //$NON-NLS-1$
	public static String getUpdateUrl() { return "http://www.blingclock.com/"+localeDir+"current_version.html"; } //$NON-NLS-1$
	public static String getStoreUrlBuyButton() { 
		return enrichUrl("http://www.blingclock.com/"+localeDir+"buy_blingclock_license.html");  //$NON-NLS-1$
	}
	public static String getStoreUrlExpiredTrials() { 
		return enrichUrl("http://www.blingclock.com/"+localeDir+"why_buy_blingclock.html");  //$NON-NLS-1$
	}
	
	public static String enrichUrl(String url) {
		try {
			int trials = Registration.getInstance().getTrialRunsRemaining();
			int runs = Registration.getInstance().getUsage();
			boolean registered = Registration.getInstance().isRegistered();
			String os = getOsName();
			String runCode = getRunCodeWhenInstalled();
			String timeCode = getTimeCode();
//			Locale l = Locale.getDefault();
//			String country = "";
//			if (l != null) {
//				country = l.getCountry();
//			}
			return url + "?version="+Branding.getCurrentVersion()+"&trials="+trials+"&runs="+runs+"&registered="+registered+"&os="+os+"&inst="+runCode+"&tc="+timeCode; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ 
		} catch (Exception e) {
			return url;
		}
	}
	
	public static String getRunCodeWhenInstalled() {
		try {
			long t = Preferences.getInstance().getInstallDate();
			Date dt = new Date(t);
			return getCode(dt);
		} catch (Exception e) {
			return "00"; //$NON-NLS-1$
		}
	}
	
	private static String getTimeCode() {
		try {
			long t = Preferences.getInstance().getInstallDate();
			Date dt = new Date(t);
			return getTimeCode(dt);
		} catch (Exception e) {
			return "00"; //$NON-NLS-1$
		}
	}
	
	public static String getNowCode() {
		try {
			Date dt = new Date();
			return getCode(dt);
		} catch (Exception e) {
			return "00"; //$NON-NLS-1$
		}
	}
	
	public static List<String> getNowCodesForDays(int days) {  
		List<String> codes = new ArrayList<String>();
		for (int i=0; i<=days; i++) {
			try {
				Date dt = new Date(System.currentTimeMillis() - i*MILLIS_PER_DAY);
				codes.add(getCode(dt));
			} catch (Exception e) {
				return codes;
			}			
		}
		return codes;
	}
	
	private static String getCode(Date dt) {
		try {
			int m = dt.getMonth(); // 0-11
			int d = dt.getDate(); // 1-31
			int y = dt.getYear(); // year - 1900
			char monthCode = (char)(m + 'A');
			return (y - 100)+String.valueOf(monthCode) + d;	
		} catch (Exception e) {
			return "00"; //$NON-NLS-1$
		}
	}
	
	private static String getTimeCode(Date dt) {
		try {
			int h = dt.getHours(); 
			int m = dt.getMinutes();
			char hourCode = (char)(h + 'A');
			return String.valueOf(hourCode) + m;
		} catch (Exception e) {
			return "Z0"; //$NON-NLS-1$
		}
	}
	
	private static String getOsName() { 
		String osName = System.getProperty("os.name"); //$NON-NLS-1$
		if (osName == null) return "unknown"; //$NON-NLS-1$
		return osName.replace(' ','_').toLowerCase();
	}
	
	public static void setLocale(String targetLocale) {
		localeDir = targetLocale + "/";
	}
	
	
}
