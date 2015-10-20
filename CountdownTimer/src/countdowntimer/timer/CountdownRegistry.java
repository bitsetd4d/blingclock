package countdowntimer.timer;

import java.util.ArrayList;
import java.util.List;

import countdowntimer.preferences.Preferences;
import countdowntimer.preferences.TickingPref;
import countdowntimer.timer.internal.CountdownTimerImpl;
import countdowntimer.timer.internal.FutureCountdownImpl;
import countdowntimer.timer.internal.TickingCountdownImpl;

public class CountdownRegistry {
	
	private static final int SECS_PER_DAY = 60 * 60 * 24;

	private static CountdownRegistry INSTANCE = new CountdownRegistry();
	public static CountdownRegistry getInstance() { return INSTANCE; }
	
	public CountdownTimer createTimer() { return new CountdownTimerImpl(); }
	
	
//	private List<CountdownEvent> allEvents;
	
	public CountdownEvent getTimerEvent(int timerInstance) {
		TickingPref p = Preferences.getInstance().getTickingPref(timerInstance);
		TickingCountdownImpl t = getEventFromPref(p);
		t.log();
		return t;		
	}
	
	public int getNewTimerInstanceId() { 
		return Preferences.getInstance().getNextFreePrefId();
	}
	
	private TickingCountdownImpl getEventFromPref(TickingPref p) {
		TickingCountdownImpl t = new TickingCountdownImpl(p);
		return t;
	}
	
	// TODO -- not complete
//	public List<CountdownEvent> getAllEvents() {
//		if (allEvents == null) {
//			populateTestData1();
//			//populateTestData2();
//		}
//		return allEvents;
//	}
//	
//	private void populateTestData1() { 
//		List<CountdownEvent> events = new ArrayList<CountdownEvent>();
//		
//		TickingCountdownImpl ticking = new TickingCountdownImpl();
//		ticking.description = "";
//		ticking.id = 1;
//		ticking.totalSecondsToCount = 60 * 60;
//		ticking.latestStartedTime = System.currentTimeMillis();
//		ticking.ticking = true;
//		
//		FutureCountdownImpl event1 = new FutureCountdownImpl();
//		event1.description = "Easter Sunday";
//		event1.id = 2;
//		event1.year = 2010;
//		event1.month = 4;
//		event1.day = 12;
//		event1.hour = 0;
//		event1.minute = 0;
//		event1.second = 0;
//		
//		FutureCountdownImpl event2 = new FutureCountdownImpl();
//		event2.description = "Christmas Day";
//		event2.id = 3;
//		event2.year = 2009;
//		event2.month = 12;
//		event2.day = 25;
//		event2.hour = 0;
//		event2.minute = 0;
//		event2.second = 0;
//		
//		FutureCountdownImpl event3 = new FutureCountdownImpl();
//		event3.description = "Dr Who Special";
//		event3.id = 4;
//		event3.year = 2009;
//		event3.month = 4;
//		event3.day = 11;
//		event3.hour = 18;
//		event3.minute = 45;
//		event3.second = 0;
//		
//		FutureCountdownImpl event4 = new FutureCountdownImpl();
//		event4.description = "Days of your life";
//		event4.id = 4;
//		event4.year = 2009 + 40;
//		event4.month = 4;
//		event4.day = 11;
//		event4.hour = 18;
//		event4.minute = 45;
//		event4.second = 0;
//		
//		FutureCountdownImpl event5 = new FutureCountdownImpl();
//		event5.description = "Summer Holiday";
//		event5.id = 4;
//		event5.year = 2009;
//		event5.month = 8;
//		event5.day = 1;
//		event5.hour = 12;
//		event5.minute = 0;
//		event5.second = 0;
//		
//		FutureCountdownImpl event6 = new FutureCountdownImpl();
//		event6.description = "100 Years";
//		event6.id = 4;
//		event6.year = 2109;
//		event6.month = 1;
//		event6.day = 1;
//		event6.hour = 0;
//		event6.minute = 0;
//		event6.second = 0;
//		
//		events.add(ticking);
//		events.add(event1);
//		events.add(event2);
//		events.add(event3);
//		events.add(event4);
//		events.add(event5);
//		events.add(event6);
//		
//		this.allEvents = events;
//	}


}
