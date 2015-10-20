package countdowntimer.registration;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Timer;
import java.util.TimerTask;

import d3bug.licensing.SimpleRegistration;
import d3bug.licensing.client.SignedStringLicenseValidator;

public class RegistrationWatcher {
	
	private Timer timer = new Timer();
	private static final int POLL_SECONDS = 5;
	
	private String lastPoll = ""; //$NON-NLS-1$
	
	public void start() {
		RegistrationTimerTask t = new RegistrationTimerTask();
		timer.schedule(t, POLL_SECONDS*1000, POLL_SECONDS*1000);
	}

	protected String cleanLicense(String licence) {
		String trimmed = timeAboveBelowIfPresent(licence);
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<trimmed.length(); i++) {
			char c = trimmed.charAt(i);
			if (isValidChar(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	
	private static final String ABOVE = "KEY ABOVE"; //$NON-NLS-1$
	private static final String BELOW = "KEY BELOW"; //$NON-NLS-1$
	private String timeAboveBelowIfPresent(String licence) {
		String block = licence;
		int idx = block.indexOf(BELOW);
		if (idx >= 0) {
			/* Remove up to next newline */
			block = block.substring(idx);
			int newline = block.indexOf("\n"); //$NON-NLS-1$
			if (newline >= 0) {
				block = block.substring(newline);
			}
		}
		
		idx = block.indexOf(ABOVE);
		if (idx >= 0) {
			block = block.substring(0,idx);
			int newLine = block.lastIndexOf("\n"); //$NON-NLS-1$
			if (newLine >= 0) {
				block = block.substring(0,newLine);
			}
		}
		
		return block;
	}

	private boolean isValidChar(char c) {
		char[] valid = new char[] { 'A', 
			'B', 'C', 'D', 'E', 'F', 'G',
		    'H', 'I', 'J', 'K', 'L', 'M', 'N',
		    'O', 'P', 'Q', 'R', 'S', 'T', 'U', 
		    'V', 'W', 'X', 'Y', 'Z',
		    'a', 'b', 'c', 'd', 'e', 'f', 'g',
		    'h', 'i', 'j', 'k', 'l', 'm', 'n',
		    'o', 'p', 'q', 'r', 's', 't', 'u', 
		    'v', 'w', 'x', 'y', 'z',
		    '0', '1', '2', '3', '4', '5', 
		    '6', '7', '8', '9', '+', '/',
		    '=', 
		};
		for (char x : valid) {
			if (x == c) return true;
		}
		return false;
	}
	
	public boolean isValid(String licence) { 
		try {
			SignedStringLicenseValidator v = new SignedStringLicenseValidator(licence);
			SimpleRegistration r = new SimpleRegistration();
			r.restoreFrom(v);
			return r.isValid();
		} catch (Exception e) {
//			e.printStackTrace();
			return false;
		}
	}

	
	private class RegistrationTimerTask extends TimerTask {

		@Override
		public void run() {
			try {
				if (Registration.getInstance().isRegistered()) return;
				String x = pollClipboard();
				if (x.length() > 0 && !lastPoll.equals(x)) {
					//System.out.println("DEBUG -> "+x);
					String clean = cleanLicense(x);
					//System.out.println("Clean: "+clean);
					boolean isOK = isValid(clean);
					//System.out.println("OK? ..." + (isOK ? "YES!!!!!!!" : "****NON****"));
					lastPoll = x;
					if (isOK) {
						registerLicense(clean);
					}
				}			
			} catch (Exception e) {}
		}
		
		private String pollClipboard() {
			String result = ""; //$NON-NLS-1$
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable contents = clipboard.getContents(null);
			boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
			if (hasTransferableText) {
				try {
					result = (String) contents.getTransferData(DataFlavor.stringFlavor);
					if (result.length() > 1000) {
						result = result.substring(0,1000);
					}
				} catch (Exception e) {}
			}
			return result;
		}
	}


	public void registerLicense(String licence) {
		Registration.getInstance().register(licence);
	}

}
