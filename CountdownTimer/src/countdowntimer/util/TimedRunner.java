package countdowntimer.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

public class TimedRunner {
	
	private Timer simpleTimer;
	private List<TimedTask> tasks = new ArrayList<TimedTask>();
	
	private static final TimedRunner INSTANCE = new TimedRunner();
	public static TimedRunner getInstance() { return INSTANCE; }
	
	private void startTimer() {
		simpleTimer = new Timer(100,new ActionListener() { public void actionPerformed(ActionEvent e) {
			onTimerFired();
		}});
		simpleTimer.start();
	}
	
	private void stopTimer() {
		if (simpleTimer != null) {
			simpleTimer.stop();
			simpleTimer = null;
		}
	}

	protected void onTimerFired() {
		List<TimedTask> toStop = new ArrayList<TimedTask>();
		for (TimedTask t : tasks) {
			t.tick();
			t.run();
			if (t.stopped) {
				toStop.add(t);
			}
		}
		tasks.removeAll(toStop);
		if (tasks.isEmpty()) stopTimer();
	}
	
	public void run(TimedTask task) {
		if (simpleTimer == null) {
			startTimer();
		}
		tasks.add(task);
		task.started = 0;
	}
	
}
