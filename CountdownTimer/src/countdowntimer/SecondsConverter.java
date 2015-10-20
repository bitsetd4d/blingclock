package countdowntimer;

public class SecondsConverter {
	
	public static String secondsToHHMM(long seconds) {
	  	int h = (int) (seconds / (60 * 60));
    	int left = (int) (seconds - (h * 60 * 60));
    	int m = left / 60;
    	left -= (m * 60);
    	int s = left;
    	StringBuilder sb = new StringBuilder();
    	if (h > 0) {
    		sb.append(h);
    		sb.append(":"); //$NON-NLS-1$
    		if (m < 10) {
    			sb.append("0"); //$NON-NLS-1$
    		}
    	}
    	sb.append(m);
    	sb.append(":"); //$NON-NLS-1$
    	if (s < 10) {
    		sb.append("0"); //$NON-NLS-1$
    	}
    	sb.append(s);
    	return sb.toString();
	}

}
