/**
 * 
 */
package countdowntimer.timer.internal;

import java.util.Date;

import countdowntimer.timer.CountdownTimer;
import countdowntimer.timer.FutureCountdownEvent;

public class FutureCountdownImpl implements FutureCountdownEvent {
	
	private int hour;
	private int minute;
	private int second;
	
	private int day;
	private int month;
	private int year;
	
	private String description;
	private int id;
	
	public int getDay() { return day; }
	public int getHour() { return hour; }
	public int getMinute() { return minute; }
	public int getMonth() { return month; }
	public int getSecond() { return second; }
	public int getYear() { return year; }
	
	public String getDescription() { return description; }
	public int getInternalId() { return id; }
	
	public void programTimer(CountdownTimer timer) {
		long now = new Date().getTime();
		long then = new Date(year-1900,month-1,day,hour,minute,second).getTime();
		long toGo = (then - now) / 1000;
		timer.setSeconds(toGo);
		timer.setTargetSeconds(toGo);
		if (toGo > 0) {
			timer.start();
		} else {
			timer.stop();
			timer.callbackListeners();
		}
		timer.setLinkedEvent(this);
		// Store in prefs needed here?   check doesn't get overwritten
	}
	public void onStarted(CountdownTimer timer) {}
	public void onStopped(CountdownTimer timer) {}
	public void reset() {}
	public void setTargetSeconds(long targetSeconds) {}
	
}