/**
 * 
 */
package countdowntimer.timer.internal;

import java.util.Date;

import countdowntimer.SecondsConverter;
import countdowntimer.preferences.Preferences;
import countdowntimer.preferences.TickingPref;
import countdowntimer.timer.CountdownTimer;
import countdowntimer.timer.TickingCountdown;

public class TickingCountdownImpl implements TickingCountdown {
	
	private long latestStartedTime;
	private long tenthsUsedAlready;
	private long totalSecondsToCount;
	private boolean ticking;
	
	private String description;
	private int id;

	public TickingCountdownImpl(TickingPref p) {
		description = p.description;
		id = p.id;
		totalSecondsToCount = p.totalSecondsToCount;
		tenthsUsedAlready = p.tenthsUsedAlready;
		ticking = p.ticking;
		if (ticking) {
			latestStartedTime = p.latestStartedTime;
		} else {
			latestStartedTime = 0;
		}
	}
	
	public String getDescription() { return description; }
	public int getInternalId() { return id; }
	
	public void programTimer(CountdownTimer timer) {
		timer.setLinkedEvent(null);     // Unhook from previous timer - we're leaving it running
		timer.setTargetSeconds((int)totalSecondsToCount);
		int tenthsSinceStarted = 0;
		if (ticking) {
			tenthsSinceStarted = (int)(System.currentTimeMillis() - latestStartedTime) / 100;
		}
		long toGo = totalSecondsToCount*10 - tenthsSinceStarted - tenthsUsedAlready;
		timer.setTenthsSeconds(toGo);
		timer.setTargetSeconds(totalSecondsToCount);
		if (toGo > 0 && ticking) {
			timer.start();
		} else {
			timer.stop();
			timer.callbackListeners();
		}
		timer.setLinkedEvent(this);
	}
	
	public void onStarted(CountdownTimer timer) {
		latestStartedTime = System.currentTimeMillis();
		ticking = true;
		storeInPrefs();
		log();
	}
	
	public void onStopped(CountdownTimer timer) {
		latestStartedTime = 0;
		tenthsUsedAlready = timer.getTargetSeconds() * 10 - timer.getCountdownTenths();
		ticking = false;
		storeInPrefs();
		log();
	}
	
	public void setTargetSeconds(long targetSeconds) {
		totalSecondsToCount = targetSeconds;
		storeInPrefs();
		log();
	}
	
	public void reset() {
		tenthsUsedAlready = 0;
		storeInPrefs();
		log();
	}
	
	private void storeInPrefs() { 
		TickingPref p = new TickingPref();
		p.description = description;
		p.id = id;
		p.totalSecondsToCount = totalSecondsToCount;
		p.latestStartedTime = latestStartedTime;
		p.tenthsUsedAlready = tenthsUsedAlready;
		p.ticking = ticking;
		Preferences.getInstance().storeTickingPref(p);
	}
	
	public void log() {
		System.out.println("************************************"); //$NON-NLS-1$
		System.out.println("***> ID : "+id); //$NON-NLS-1$
		System.out.println("***> Total Seconds : "+totalSecondsToCount); //$NON-NLS-1$
		System.out.println("***> Latest Start  : "+new Date(latestStartedTime)); //$NON-NLS-1$
		System.out.println("***> Used already  : "+(tenthsUsedAlready/10.0)); //$NON-NLS-1$
		System.out.println("***> Ticking?      : "+ticking); //$NON-NLS-1$
		System.out.println("***> ..............................."); //$NON-NLS-1$
		int tenthsSinceStarted = 0;
		if (ticking) {
			tenthsSinceStarted = (int)(System.currentTimeMillis() - latestStartedTime) / 100;
		}
		long toGo = totalSecondsToCount*10 - tenthsSinceStarted - tenthsUsedAlready;
		System.out.println("***> Time to go    : "+(toGo / 10.0)+"  ~ "+SecondsConverter.secondsToHHMM(toGo/10)); //$NON-NLS-1$ //$NON-NLS-2$
		System.out.println("***> Implied total : "+((toGo + tenthsUsedAlready) / 10.0));			 //$NON-NLS-1$
		System.out.println("************************************\n"); //$NON-NLS-1$
	}
}