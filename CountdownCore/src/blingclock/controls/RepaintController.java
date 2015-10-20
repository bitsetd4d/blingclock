package blingclock.controls;

import java.awt.Component;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RepaintController {
	
	private static final int PAINT_DELAY = 60;
	private static final int MAX_COMPONENTS = 150;
	private static Component[] toPaint = new Component[MAX_COMPONENTS];
	private static int index = 0;
	
	private static Object lock = new Object();
	private static boolean inFlight = false;
	
	private static Executor exec = Executors.newSingleThreadExecutor();
	private static Runnable repaintTask = new RepaintTask();
	
	public static void repaint(Component c) {
		synchronized (lock) {
			if (index < MAX_COMPONENTS) {
				toPaint[index] = c;
				index++;
				triggerRepaint();
				return;
			}
		}
		c.repaint(0);
	}

	private static void triggerRepaint() {
		if (!inFlight) {
			exec.execute(repaintTask);
			inFlight = true;
		}
	}
	
	private static Component[] toPaintNow = new Component[MAX_COMPONENTS];
	private static void repaintNow() {
		final int count = index;
		synchronized (lock) {
			inFlight = false;
			for (int i=0; i<index; i++) {
				toPaintNow[i] = toPaint[i];
				toPaint[i] = null;
			}
			index = 0;
		}
		for (int i=0; i<count; i++) {
			toPaintNow[i].repaint(0);
			toPaintNow[i] = null;
		}
	}
	
	private static class RepaintTask implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(PAINT_DELAY);
			} catch (InterruptedException e) {}
			repaintNow();
		}
	}
	
}
