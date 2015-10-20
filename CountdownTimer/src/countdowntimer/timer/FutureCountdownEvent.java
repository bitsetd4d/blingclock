package countdowntimer.timer;

public interface FutureCountdownEvent extends CountdownEvent {
	
	int getDay();
	int getMonth();
	int getYear();
	
	int getHour();
	int getMinute();
	int getSecond();

}
