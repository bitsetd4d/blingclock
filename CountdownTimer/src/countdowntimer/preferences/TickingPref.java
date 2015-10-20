package countdowntimer.preferences;

/*
 * Store/retrieve in preferences
 */
public class TickingPref {
	
	public String description = ""; //$NON-NLS-1$
	public int id;
	public long totalSecondsToCount;
	public long latestStartedTime;
	public long tenthsUsedAlready;
	public boolean ticking;
	public boolean stopwatch;

}
