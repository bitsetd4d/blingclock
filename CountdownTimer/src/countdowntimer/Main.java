/*
 * Main.java
 *
 * Created on July 22, 2007, 6:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package countdowntimer;

import java.util.Locale;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import blingclock.digits.DigitPrefs;

import countdowntimer.preferences.Preferences;
import countdowntimer.registration.Registration;

/**
 *
 * @author paul
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	if (args.length > 0) {
    		setMyLocale(args[0]);
    	}
    	System.setProperty("com.apple.mrj.application.apple.menu.about.name", Messages.getString("Main.1")); //$NON-NLS-1$ //$NON-NLS-2$
 	 	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		DigitPrefs.setInstance(new DigitPrefs() {

			@Override
			public boolean getShow24Clock() {
				return Preferences.getInstance().getShow24Clock();
			}

			@Override
			public boolean isRegistered() {
				return Registration.getInstance().isRegistered();
			}

			@Override
			public boolean getHideHourDigitsIfPossible() {
				return Preferences.getInstance().getHideHourDigitsIfPossible();
			}

			@Override
			public boolean isTrialMode() {
				return Registration.getInstance().isTrialMode();
			} 
			
		});
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
        		boolean noDecs = Preferences.getInstance().getTimerPreference(1).isRemoveTitlebar();
        		boolean timeMode = Preferences.getInstance().getStartupDisplay().equals("CLOCK"); //$NON-NLS-1$
        		boolean stopwatchMode = Preferences.getInstance().getStartupDisplay().equals("STOPWATCH"); //$NON-NLS-1$
            	new CountdownWindow(false,noDecs,1,timeMode,stopwatchMode).start();
        }});
	}
    
    private static void setMyLocale(String languageCode) {
    	String targetLocale = "gb";
    	if (languageCode.equals("3082")) targetLocale="es";
    	for (Locale locale : Locale.getAvailableLocales()) {
    		if (locale.getLanguage().equalsIgnoreCase(targetLocale)) {
    			System.out.println("Setting Locale "+locale.getLanguage());
    			Locale.setDefault(locale);
    			Branding.setLocale(targetLocale);
    			return;
    		}
    	}
    	Locale.setDefault(Locale.UK);
    }
    
}
