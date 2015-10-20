package countdowntimer.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import countdowntimer.Branding;
import countdowntimer.registration.Registration;

public class UpdateChecker {
		
	private static String updateUrl = Branding.getUpdateUrl(); 
		
	// Example <!-- VERSION-CHECK(210) -->
	private static String VERSION = "VERSION-CHECK("; //$NON-NLS-1$
	
	public static void openUpdatePageInBrowser() {
		BrowserLaunching.openURL(updateUrl);
	}
	
	// Go look in Branding class
	public static void asyncFindCurrentVersion(final UpdateCheckListener listener) {
		Runnable r = new Runnable() { public void run() {
			try {
				String url = Branding.enrichUrl(updateUrl);
				String page = readPage(url);
				if (page != null)  {
					int i = page.indexOf(VERSION);
					if (i >=0) {
						int j = page.indexOf(")",i); //$NON-NLS-1$
						if (j >=0) {
							String version = page.substring(i + VERSION.length(),j);
							listener.onCurrentVersion(version);
							return;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			listener.onUnableToCheck();
		}};
		new Thread(r).start();
	}
	
	public static void asyncJustRegisteredCheckCurrentVersion() {
		Runnable r = new Runnable() { public void run() {
			try {
				String url = Branding.enrichUrl(updateUrl);
				url += "&userreg="+Registration.getInstance().getRegisteredEmail(); //$NON-NLS-1$
				String page = readPage(url);
//				if (page != null)  {
//					int i = page.indexOf(VERSION);
//					if (i >=0) {
//						int j = page.indexOf(")",i);
//						if (j >=0) {
//							String version = page.substring(i + VERSION.length(),j);
//							listener.onCurrentVersion(version);
//							return;
//						}
//					}
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}};
		new Thread(r).start();
	}
	
	private static String readPage(String url) throws IOException {
		URL page = new URL(url);
		BufferedReader in = new BufferedReader(new InputStreamReader(page.openStream()));
		StringBuilder sb = new StringBuilder();	
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			sb.append(inputLine);
		}
		in.close();
		return sb.toString();
	}
	
}
